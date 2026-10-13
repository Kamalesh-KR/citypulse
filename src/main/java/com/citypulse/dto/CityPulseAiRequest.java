package com.citypulse.dto;

import jakarta.validation.constraints.NotBlank;

public class CityPulseAiRequest {

    @NotBlank(message = "Question is required")
    private String question;

    public CityPulseAiRequest() {
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}