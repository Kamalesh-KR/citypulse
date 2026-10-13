package com.citypulse.controller;

import com.citypulse.dto.CityPulseAiRequest;
import com.citypulse.dto.CityPulseAiResponse;
import com.citypulse.service.CityPulseAiService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class CityPulseAiController {

    private final CityPulseAiService cityPulseAiService;

    public CityPulseAiController(
            CityPulseAiService cityPulseAiService) {

        this.cityPulseAiService = cityPulseAiService;
    }

    @PostMapping("/ask")
    public ResponseEntity<CityPulseAiResponse> ask(
            @Valid @RequestBody CityPulseAiRequest request) {

        return ResponseEntity.ok(
                cityPulseAiService.processQuestion(
                        request.getQuestion()));
    }
}