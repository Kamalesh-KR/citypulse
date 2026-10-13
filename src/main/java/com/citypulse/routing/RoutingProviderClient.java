package com.citypulse.routing;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RoutingProviderClient {

    private final RestClient restClient;

    public RoutingProviderClient(
            @Value("${citypulse.routing.base-url:http://localhost:8081}")
            String baseUrl) {

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public RestClient getClient() {
        return restClient;
    }
}
