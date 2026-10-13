package com.citypulse.dto;

import jakarta.validation.constraints.NotBlank;

public class GeocodeRequest {

    @NotBlank(message = "Address is required")
    private String address;

    public GeocodeRequest() {
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}