package com.citypulse.dto;

import java.util.List;

public class WorldwideRouteResponse {

    private AddressLocationResponse from;
    private AddressLocationResponse to;
    private List<RouteAlternativeResponse> routes;
    private Integer routeCount;

    public WorldwideRouteResponse() {
    }

    public WorldwideRouteResponse(
            AddressLocationResponse from,
            AddressLocationResponse to,
            List<RouteAlternativeResponse> routes,
            Integer routeCount) {

        this.from = from;
        this.to = to;
        this.routes = routes;
        this.routeCount = routeCount;
    }

    public AddressLocationResponse getFrom() {
        return from;
    }

    public void setFrom(AddressLocationResponse from) {
        this.from = from;
    }

    public AddressLocationResponse getTo() {
        return to;
    }

    public void setTo(AddressLocationResponse to) {
        this.to = to;
    }

    public List<RouteAlternativeResponse> getRoutes() {
        return routes;
    }

    public void setRoutes(List<RouteAlternativeResponse> routes) {
        this.routes = routes;
    }

    public Integer getRouteCount() {
        return routeCount;
    }

    public void setRouteCount(Integer routeCount) {
        this.routeCount = routeCount;
    }
}