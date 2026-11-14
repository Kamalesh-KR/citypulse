package com.citypulse.routing;

import com.citypulse.dto.AddressLocationResponse;
import com.citypulse.model.Location;
import com.citypulse.repository.LocationRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class GeocodingService {

    private static final Logger log =
            LoggerFactory.getLogger(GeocodingService.class);

    private final RestClient restClient;
    private final LocationRepository locationRepository;

    public GeocodingService(
            @Value("${citypulse.geocoding.photon-url:http://localhost:2322}")
            String photonUrl,
            LocationRepository locationRepository) {

        this.restClient = RestClient.builder()
                .baseUrl(photonUrl)
                .build();

        this.locationRepository = locationRepository;
    }

    public AddressLocationResponse geocode(String address) {

        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException(
                    "Address is required.");
        }

        String trimmedAddress = address.trim();

        /* =========================================================
           1. DB-FIRST lookup — match against locations table
           ========================================================= */

        Optional<AddressLocationResponse> dbMatch =
                lookupInDatabase(trimmedAddress);

        if (dbMatch.isPresent()) {
            return dbMatch.get();
        }

        /* =========================================================
           2. Photon geocoding (for addresses not in the DB)
           ========================================================= */

        try {

            return geocodeWithPhoton(trimmedAddress);

        } catch (RuntimeException photonFailure) {

            log.warn(
                    "Photon geocoding unavailable ({}); will try "
                            + "fuzzy DB match for: {}",
                    photonFailure.getMessage(),
                    trimmedAddress);

            /* =====================================================
               3. Fuzzy DB fallback — last resort before failing
               ===================================================== */

            Optional<AddressLocationResponse> fuzzyMatch =
                    fuzzyDatabaseMatch(trimmedAddress);

            if (fuzzyMatch.isPresent()) {
                return fuzzyMatch.get();
            }

            throw new IllegalArgumentException(
                    "Address could not be found: "
                            + trimmedAddress
                            + " (geocoding service unavailable "
                            + "and no matching location in database)");
        }
    }

    /* =========================================================
       DB LOOKUP HELPERS
       ========================================================= */

    /**
     * Exact (case-insensitive) name match against the locations table.
     * This is the fast path for all 44 CityPulse dropdown locations.
     */
    private Optional<AddressLocationResponse> lookupInDatabase(
            String address) {

        try {

            List<Location> allLocations =
                    locationRepository.findAll();

            if (allLocations == null || allLocations.isEmpty()) {
                return Optional.empty();
            }

            // Pass 1: exact case-insensitive name match
            for (Location location : allLocations) {

                if (location.getName() == null
                        || location.getLatitude() == null
                        || location.getLongitude() == null) {
                    continue;
                }

                if (location.getName()
                        .trim()
                        .equalsIgnoreCase(address)) {

                    return Optional.of(
                            toResponse(location));
                }
            }

            // Pass 2: address contains location name (e.g. "MG Road, Bangalore")
            for (Location location : allLocations) {

                if (location.getName() == null
                        || location.getLatitude() == null
                        || location.getLongitude() == null) {
                    continue;
                }

                String name = location.getName().trim();

                if (name.length() >= 4
                        && address.toLowerCase().contains(
                                name.toLowerCase())) {

                    return Optional.of(
                            toResponse(location));
                }
            }

            return Optional.empty();

        } catch (RuntimeException dbFailure) {

            log.warn(
                    "DB location lookup failed: {}",
                    dbFailure.getMessage());

            return Optional.empty();
        }
    }

    /**
     * Very loose last-resort match when both Photon and exact DB
     * lookup fail. Matches on individual words.
     */
    private Optional<AddressLocationResponse> fuzzyDatabaseMatch(
            String address) {

        try {

            List<Location> allLocations =
                    locationRepository.findAll();

            if (allLocations == null || allLocations.isEmpty()) {
                return Optional.empty();
            }

            String[] words =
                    address.toLowerCase().split("\\s+");

            for (Location location : allLocations) {

                if (location.getName() == null
                        || location.getLatitude() == null
                        || location.getLongitude() == null) {
                    continue;
                }

                String name =
                        location.getName().toLowerCase();

                for (String word : words) {

                    if (word.length() >= 4
                            && name.contains(word)) {

                        return Optional.of(
                                toResponse(location));
                    }
                }
            }

            return Optional.empty();

        } catch (RuntimeException dbFailure) {

            return Optional.empty();
        }
    }

    private AddressLocationResponse toResponse(
            Location location) {

        return new AddressLocationResponse(
                location.getName(),
                location.getLatitude().doubleValue(),
                location.getLongitude().doubleValue());
    }

    /* =========================================================
       PHOTON GEOCODING (unchanged behavior)
       ========================================================= */

    private AddressLocationResponse geocodeWithPhoton(
            String address) {

        Map<?, ?> response = restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api")
                        .queryParam("q", address)
                        .queryParam("limit", 1)
                        .build())
                .retrieve()
                .body(Map.class);

        if (response == null) {
            throw new IllegalStateException(
                    "No response was received from Photon.");
        }

        Object featuresObject =
                response.get("features");

        if (!(featuresObject instanceof List<?> features)
                || features.isEmpty()) {

            throw new IllegalArgumentException(
                    "Address could not be found: "
                            + address);
        }

        Object firstFeatureObject =
                features.get(0);

        if (!(firstFeatureObject instanceof Map<?, ?> firstFeature)) {

            throw new IllegalStateException(
                    "Invalid geocoding response received from Photon.");
        }

        Object geometryObject =
                firstFeature.get("geometry");

        if (!(geometryObject instanceof Map<?, ?> geometry)) {

            throw new IllegalStateException(
                    "Photon response does not contain coordinates.");
        }

        Object coordinatesObject =
                geometry.get("coordinates");

        if (!(coordinatesObject instanceof List<?> coordinates)
                || coordinates.size() < 2) {

            throw new IllegalStateException(
                    "Photon response contains invalid coordinates.");
        }

        /*
         * GeoJSON coordinates are returned as:
         *   [longitude, latitude]
         * CityPulse stores them as:
         *   latitude, longitude
         */

        Double longitude =
                convertToDouble(coordinates.get(0));

        Double latitude =
                convertToDouble(coordinates.get(1));

        if (latitude == null || longitude == null) {

            throw new IllegalStateException(
                    "Photon returned invalid coordinates.");
        }

        String resolvedAddress =
                buildResolvedAddress(
                        address,
                        firstFeature);

        return new AddressLocationResponse(
                resolvedAddress,
                latitude,
                longitude);
    }

    private String buildResolvedAddress(
            String originalAddress,
            Map<?, ?> feature) {

        Object propertiesObject =
                feature.get("properties");

        if (!(propertiesObject instanceof Map<?, ?> properties)) {
            return originalAddress;
        }

        String name =
                getString(properties.get("name"));

        String street =
                getString(properties.get("street"));

        String locality =
                getString(properties.get("locality"));

        String district =
                getString(properties.get("district"));

        String city =
                getString(properties.get("city"));

        String state =
                getString(properties.get("state"));

        String country =
                getString(properties.get("country"));

        String postcode =
                getString(properties.get("postcode"));

        StringBuilder builder =
                new StringBuilder();

        appendUniquePart(builder, name);
        appendUniquePart(builder, street);
        appendUniquePart(builder, locality);
        appendUniquePart(builder, district);
        appendUniquePart(builder, city);
        appendUniquePart(builder, state);
        appendUniquePart(builder, postcode);
        appendUniquePart(builder, country);

        if (builder.isEmpty()) {
            return originalAddress;
        }

        return builder.toString();
    }

    private void appendUniquePart(
            StringBuilder builder,
            String value) {

        if (value == null || value.isBlank()) {
            return;
        }

        String cleanedValue =
                value.trim();

        if (cleanedValue.isBlank()) {
            return;
        }

        if (builder.length() == 0) {
            builder.append(cleanedValue);
            return;
        }

        String existing =
                builder.toString();

        if (existing.equalsIgnoreCase(cleanedValue)
                || existing.contains(
                        ", " + cleanedValue + ",")
                || existing.endsWith(
                        ", " + cleanedValue)) {
            return;
        }

        builder.append(", ");
        builder.append(cleanedValue);
    }

    private String getString(Object value) {

        if (value instanceof String string
                && !string.isBlank()) {

            return string.trim();
        }

        return null;
    }

    private Double convertToDouble(Object value) {

        if (value instanceof Number number) {
            return number.doubleValue();
        }

        if (value instanceof String string) {

            try {
                return Double.parseDouble(string);

            } catch (NumberFormatException exception) {

                return null;
            }
        }

        return null;
    }
}