package com.citypulse.service;

import com.citypulse.model.PushSubscription;
import com.citypulse.repository.PushSubscriptionRepository;
import jakarta.annotation.PostConstruct;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.util.List;

/**
 * ============================================================
 * CITYPULSE — PUSH NOTIFICATION SERVICE
 * ============================================================
 *
 * Sends Web Push notifications to all subscribed browsers.
 * Uses VAPID keys from application.properties.
 */
@Service
public class PushNotificationService {

    private final PushSubscriptionRepository repository;

    @Value("${citypulse.push.vapid-public-key:}")
    private String vapidPublicKey;

    @Value("${citypulse.push.vapid-private-key:}")
    private String vapidPrivateKey;

    @Value("${citypulse.push.vapid-subject:mailto:admin@citypulse.app}")
    private String vapidSubject;

    private PushService pushService;

    public PushNotificationService(PushSubscriptionRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    public void init() {
        try {
            if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
                Security.addProvider(new BouncyCastleProvider());
            }

            if (vapidPublicKey == null || vapidPublicKey.isBlank()
                    || vapidPrivateKey == null || vapidPrivateKey.isBlank()) {
                System.err.println(
                        "[CityPulse Push] VAPID keys not configured. "
                        + "Push notifications will be disabled.");
                return;
            }

            this.pushService = new PushService(
                    vapidPublicKey, vapidPrivateKey, vapidSubject);

            System.out.println("[CityPulse Push] Push service initialized.");
        } catch (Exception e) {
            System.err.println(
                    "[CityPulse Push] Failed to initialize push service: "
                    + e.getMessage());
        }
    }

    public String getVapidPublicKey() {
        return vapidPublicKey != null ? vapidPublicKey : "";
    }

    public int broadcast(String title, String body, String url) {
        if (pushService == null) {
            System.err.println(
                    "[CityPulse Push] Cannot send: push service not initialized.");
            return 0;
        }

        List<PushSubscription> subs = repository.findByActiveTrue();
        int sent = 0;

        for (PushSubscription sub : subs) {
            if (send(sub, title, body, url)) {
                sent++;
            }
        }

        System.out.println(
                "[CityPulse Push] Broadcast sent to " + sent + "/" + subs.size());

        return sent;
    }

    public boolean send(PushSubscription sub, String title, String body, String url) {
        if (pushService == null) return false;

        try {
            String payload = buildPayload(title, body, url);

            Notification notification = new Notification(
                    sub.getEndpoint(),
                    sub.getP256dh(),
                    sub.getAuth(),
                    payload
            );

            pushService.send(notification);
            return true;

        } catch (Exception e) {
            System.err.println(
                    "[CityPulse Push] Send failed for " + sub.getId()
                    + ": " + e.getMessage());

            // Mark subscription inactive if it's stale (410 Gone, etc.)
            String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            if (msg.contains("410") || msg.contains("gone")
                    || msg.contains("404") || msg.contains("not found")) {
                sub.setActive(false);
                repository.save(sub);
            }

            return false;
        }
    }

    private String buildPayload(String title, String body, String url) {
        String safeTitle = escapeJson(title);
        String safeBody = escapeJson(body);
        String safeUrl = escapeJson(url != null ? url : "/");

        return "{"
                + "\"title\":\"" + safeTitle + "\","
                + "\"body\":\"" + safeBody + "\","
                + "\"url\":\"" + safeUrl + "\","
                + "\"icon\":\"/favicon.svg\","
                + "\"badge\":\"/favicon.svg\""
                + "}";
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}