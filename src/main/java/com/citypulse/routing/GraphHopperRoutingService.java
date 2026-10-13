package com.citypulse.routing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Service
public class GraphHopperRoutingService {

    private final RestClient restClient;

    public GraphHopperRoutingService(
            @Value("${citypulse.routing.graphhopper-url:http://localhost:8989}")
            String graphHopperUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(graphHopperUrl)
                .build();
    }

    public Map<?, ?> calculateRoute(
            double fromLatitude,
            double fromLongitude,
            double toLatitude,
            double toLongitude) {

        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/route")
                        .queryParam(
                                "point",
                                fromLatitude + "," + fromLongitude
                        )
                        .queryParam(
                                "point",
                                toLatitude + "," + toLongitude
                        )
                        .queryParam("profile", "car")
                        .queryParam("locale", "en")
                        .queryParam("calc_points", "true")
                        .queryParam("instructions", "true")
                        .queryParam("points_encoded", "false")
                        .build())
                .retrieve()
                .body(Map.class);
    }

    public Map<?, ?> calculateAlternativeRoutes(
            double fromLatitude,
            double fromLongitude,
            double toLatitude,
            double toLongitude) {

        return restClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/route")
                        .queryParam(
                                "point",
                                fromLatitude + "," + fromLongitude
                        )
                        .queryParam(
                                "point",
                                toLatitude + "," + toLongitude
                        )
                        .queryParam("profile", "car")
                        .queryParam("locale", "en")
                        .queryParam("calc_points", "true")
                        .queryParam("instructions", "true")
                        .queryParam("points_encoded", "false")
                        .queryParam(
                                "algorithm",
                                "alternative_route"
                        )
                        .build())
                .retrieve()
                .body(Map.class);
    }
}