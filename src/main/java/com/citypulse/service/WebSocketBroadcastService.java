package com.citypulse.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class WebSocketBroadcastService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketBroadcastService(
            SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }


    public void broadcastTrafficUpdate(Object trafficPayload) {

        messagingTemplate.convertAndSend(
                "/topic/traffic",
                trafficPayload
        );
    }


    public void broadcastPredictionUpdate(Object predictionPayload) {

        messagingTemplate.convertAndSend(
                "/topic/predictions",
                predictionPayload
        );
    }


    public void broadcastAlertUpdate(Object alertPayload) {

        messagingTemplate.convertAndSend(
                "/topic/alerts",
                alertPayload
        );
    }


    public void broadcastReportUpdate(Object reportPayload) {

        messagingTemplate.convertAndSend(
                "/topic/reports",
                reportPayload
        );
    }


    /**
     * Broadcasts an emergency dispatch to all connected clients.
     *
     * Frontend subscribes to /topic/emergency and displays:
     *   - Native OS notification
     *   - Red banner at top of page
     *   - Entry in live incident board
     */
    public void broadcastEmergencyDispatch(Object emergencyPayload) {

        messagingTemplate.convertAndSend(
                "/topic/emergency",
                emergencyPayload
        );
    }
}