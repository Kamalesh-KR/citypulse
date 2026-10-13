package com.citypulse.dto;

import jakarta.validation.constraints.NotBlank;

public class WorldwideRouteRequest {

    @NotBlank(message = "From address is required")
    private String fromAddress;

    @NotBlank(message = "To address is required")
    private String toAddress;

    public WorldwideRouteRequest() {
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }
}