package com.citypulse.routing;

import com.citypulse.dto.AddressLocationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;

@Service
public class GeocodingService {

    private final RestClient restClient;

    public GeocodingService(
            @Value("${citypulse.geocoding.photon-url:http://localhost:2322}")
            String photonUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(photonUrl)
                .build();
    }

    public AddressLocationResponse geocode(String address) {

        if (address == null || address.isBlank()) {
            throw new IllegalArgumentException(
                    "Address is required.");
        }

        Map<?, ?> response = restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api")
                        .queryParam("q", address.trim())
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
         *
         * [longitude, latitude]
         *
         * CityPulse stores them as:
         *
         * latitude, longitude
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
                        address.trim(),
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