package com.citypulse.controller;

import com.citypulse.dto.LocationRequest;
import com.citypulse.dto.LocationResponse;
import com.citypulse.model.Location;
import com.citypulse.service.LocationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/locations")
public class LocationController {

    private final LocationService locationService;

    public LocationController(LocationService locationService) {
        this.locationService = locationService;
    }

    @GetMapping
    public ResponseEntity<List<LocationResponse>> getAllLocations() {

        List<LocationResponse> locations = locationService
                .getAllLocations()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(locations);
    }

    @GetMapping("/{id}")
    public ResponseEntity<LocationResponse> getLocationById(
            @PathVariable Long id) {

        return locationService.getLocationById(id)
                .map(this::convertToResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/city")
    public ResponseEntity<List<LocationResponse>> getLocationsByCity(
            @RequestParam String city) {

        List<LocationResponse> locations = locationService
                .getLocationsByCity(city)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(locations);
    }

    @GetMapping("/type")
    public ResponseEntity<List<LocationResponse>> getLocationsByType(
            @RequestParam String locationType) {

        List<LocationResponse> locations = locationService
                .getLocationsByType(locationType)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(locations);
    }

    @PostMapping
    public ResponseEntity<LocationResponse> createLocation(
            @Valid @RequestBody LocationRequest request) {

        Location location = convertToEntity(request);

        Location savedLocation =
                locationService.createLocation(location);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(convertToResponse(savedLocation));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LocationResponse> updateLocation(
            @PathVariable Long id,
            @Valid @RequestBody LocationRequest request) {

        Location location = convertToEntity(request);

        Location updatedLocation =
                locationService.updateLocation(id, location);

        return ResponseEntity.ok(
                convertToResponse(updatedLocation)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLocation(
            @PathVariable Long id) {

        locationService.deleteLocation(id);

        return ResponseEntity.noContent().build();
    }

    private Location convertToEntity(LocationRequest request) {

        Location location = new Location();

        location.setName(request.getName());
        location.setAddress(request.getAddress());
        location.setCity(request.getCity());
        location.setState(request.getState());

        if (request.getLatitude() != null) {
            location.setLatitude(
                    BigDecimal.valueOf(request.getLatitude())
            );
        }

        if (request.getLongitude() != null) {
            location.setLongitude(
                    BigDecimal.valueOf(request.getLongitude())
            );
        }

        location.setLocationType(request.getLocationType());

        location.setActive(
                request.getActive() != null
                        ? request.getActive()
                        : true
        );

        return location;
    }

    private LocationResponse convertToResponse(Location location) {

        return new LocationResponse(
                location.getId(),
                location.getName(),
                location.getAddress(),
                location.getCity(),
                location.getState(),
                location.getLatitude() != null
                        ? location.getLatitude().doubleValue()
                        : null,
                location.getLongitude() != null
                        ? location.getLongitude().doubleValue()
                        : null,
                location.getLocationType(),
                location.getActive()
        );
    }
}
