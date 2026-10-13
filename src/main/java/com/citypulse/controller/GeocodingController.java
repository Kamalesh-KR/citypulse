package com.citypulse.controller;

import com.citypulse.dto.AddressLocationResponse;
import com.citypulse.dto.GeocodeRequest;
import com.citypulse.routing.GeocodingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/geocoding")
public class GeocodingController {

    private final GeocodingService geocodingService;

    public GeocodingController(
            GeocodingService geocodingService) {

        this.geocodingService = geocodingService;
    }

    @PostMapping("/search")
    public ResponseEntity<AddressLocationResponse> searchAddress(
            @Valid @RequestBody GeocodeRequest request) {

        AddressLocationResponse response =
                geocodingService.geocode(
                        request.getAddress());

        return ResponseEntity.ok(response);
    }
}