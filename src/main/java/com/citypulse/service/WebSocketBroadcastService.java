package com.citypulse.service;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class WebSocketBroadcastService {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketBroadcastService(
            SimpMessagingTemplate messagingTemplate) {

        this.messagingTemplate = messagingTemplate;
    }

    // ============================================
    // BROADCAST: new traffic record added
    // ============================================
    public void broadcastTrafficUpdate(Object trafficPayload) {

        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("event", "TRAFFIC_UPDATED");
        envelope.put("timestamp", LocalDateTime.now().toString());
        envelope.put("payload", trafficPayload);

        messagingTemplate.convertAndSend(
                "/topic/traffic",
                (Object) envelope);
    }

    // ============================================
    // BROADCAST: new prediction generated
    // ============================================
    public void broadcastPredictionUpdate(Object predictionPayload) {

        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("event", "PREDICTION_UPDATED");
        envelope.put("timestamp", LocalDateTime.now().toString());
        envelope.put("payload", predictionPayload);

        messagingTemplate.convertAndSend(
                "/topic/predictions",
                (Object) envelope);
    }

    // ============================================
    // BROADCAST: new alert created
    // ============================================
    public void broadcastAlertUpdate(Object alertPayload) {

        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("event", "ALERT_CREATED");
        envelope.put("timestamp", LocalDateTime.now().toString());
        envelope.put("payload", alertPayload);

        messagingTemplate.convertAndSend(
                "/topic/alerts",
                (Object) envelope);
    }

    // ============================================
    // BROADCAST: new citizen report submitted
    // ============================================
    public void broadcastReportUpdate(Object reportPayload) {

        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("event", "REPORT_CREATED");
        envelope.put("timestamp", LocalDateTime.now().toString());
        envelope.put("payload", reportPayload);

        messagingTemplate.convertAndSend(
                "/topic/reports",
                (Object) envelope);
    }
}