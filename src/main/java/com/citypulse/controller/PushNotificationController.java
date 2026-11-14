package com.citypulse.controller;

import com.citypulse.dto.PushSubscriptionRequest;
import com.citypulse.model.PushSubscription;
import com.citypulse.repository.PushSubscriptionRepository;
import com.citypulse.service.PushNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ============================================================
 * CITYPULSE — PUSH NOTIFICATION ENDPOINTS
 * ============================================================
 *
 * GET  /api/push/vapid-public-key  → returns the public VAPID key
 * POST /api/push/subscribe         → saves a browser subscription
 * POST /api/push/unsubscribe       → removes a subscription
 * POST /api/push/test              → sends a test push to this user
 * POST /api/push/broadcast         → sends to ALL active subscribers
 * GET  /api/push/stats             → count of active subscriptions
 */
@RestController
@RequestMapping("/api/push")
public class PushNotificationController {

    private final PushSubscriptionRepository repository;
    private final PushNotificationService pushService;

    public PushNotificationController(
            PushSubscriptionRepository repository,
            PushNotificationService pushService) {
        this.repository = repository;
        this.pushService = pushService;
    }

    @GetMapping("/vapid-public-key")
    public ResponseEntity<Map<String, String>> getVapidPublicKey() {
        Map<String, String> result = new LinkedHashMap<>();
        result.put("publicKey", pushService.getVapidPublicKey());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<Map<String, Object>> subscribe(
            @RequestBody PushSubscriptionRequest request) {

        Map<String, Object> result = new LinkedHashMap<>();

        if (request == null
                || request.getEndpoint() == null
                || request.getEndpoint().isBlank()) {
            result.put("success", false);
            result.put("message", "endpoint is required");
            return ResponseEntity.badRequest().body(result);
        }

        PushSubscription sub = repository
                .findByEndpoint(request.getEndpoint())
                .orElseGet(PushSubscription::new);

        sub.setEndpoint(request.getEndpoint());
        sub.setP256dh(request.getP256dh());
        sub.setAuth(request.getAuth());
        sub.setUserId(request.getUserId());
        sub.setActive(true);
        sub.setLastSeenAt(LocalDateTime.now());

        if (sub.getCreatedAt() == null) {
            sub.setCreatedAt(LocalDateTime.now());
        }

        PushSubscription saved = repository.save(sub);

        result.put("success", true);
        result.put("subscriptionId", saved.getId());
        result.put("activeSubscriptions", repository.countByActiveTrue());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/unsubscribe")
    public ResponseEntity<Map<String, Object>> unsubscribe(
            @RequestBody Map<String, String> body) {

        Map<String, Object> result = new LinkedHashMap<>();
        String endpoint = body != null ? body.get("endpoint") : null;

        if (endpoint == null || endpoint.isBlank()) {
            result.put("success", false);
            result.put("message", "endpoint is required");
            return ResponseEntity.badRequest().body(result);
        }

        repository.findByEndpoint(endpoint).ifPresent(sub -> {
            sub.setActive(false);
            repository.save(sub);
        });

        result.put("success", true);
        result.put("activeSubscriptions", repository.countByActiveTrue());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> sendTest(
            @RequestBody(required = false) Map<String, String> body) {

        String title = body != null && body.get("title") != null
                ? body.get("title")
                : "CityPulse Test";
        String message = body != null && body.get("body") != null
                ? body.get("body")
                : "Push notifications are working!";

        int sent = pushService.broadcast(title, message, "/");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("sent", sent);
        result.put("activeSubscriptions", repository.countByActiveTrue());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/broadcast")
    public ResponseEntity<Map<String, Object>> broadcast(
            @RequestBody Map<String, String> body) {

        String title = body != null && body.get("title") != null
                ? body.get("title")
                : "CityPulse Alert";
        String message = body != null && body.get("body") != null
                ? body.get("body")
                : "Traffic alert";

        int sent = pushService.broadcast(title, message, "/");

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", true);
        result.put("sent", sent);
        result.put("activeSubscriptions", repository.countByActiveTrue());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> stats() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("totalSubscriptions", repository.count());
        result.put("activeSubscriptions", repository.countByActiveTrue());
        return ResponseEntity.ok(result);
    }
}