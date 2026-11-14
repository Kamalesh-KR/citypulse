/* ============================================================
   CityPulse — Service Worker
   Handles Web Push notifications (Feature: Push Notifications)
   ============================================================ */

const SW_VERSION = "citypulse-push-v1";

self.addEventListener("install", (event) => {
    console.log("[CityPulse SW] Installing version", SW_VERSION);
    self.skipWaiting();
});

self.addEventListener("activate", (event) => {
    console.log("[CityPulse SW] Activated version", SW_VERSION);
    event.waitUntil(self.clients.claim());
});

/* Push event — fires when server sends a Web Push */
self.addEventListener("push", (event) => {
    console.log("[CityPulse SW] Push received");

    let data = {
        title: "CityPulse",
        body: "New traffic alert",
        url: "/",
        icon: "/favicon.svg",
        badge: "/favicon.svg"
    };

    if (event.data) {
        try {
            const payload = event.data.json();
            data = { ...data, ...payload };
        } catch (e) {
            try {
                data.body = event.data.text() || data.body;
            } catch (e2) {
                console.warn("[CityPulse SW] Could not parse push payload");
            }
        }
    }

    const options = {
        body: data.body,
        icon: data.icon || "/favicon.svg",
        badge: data.badge || "/favicon.svg",
        data: { url: data.url || "/" },
        vibrate: [200, 100, 200],
        requireInteraction: false,
        tag: "citypulse-traffic"
    };

    event.waitUntil(
        self.registration.showNotification(data.title, options)
    );
});

/* Notification click — open / focus the app */
self.addEventListener("notificationclick", (event) => {
    console.log("[CityPulse SW] Notification clicked");
    event.notification.close();

    const targetUrl = (event.notification.data && event.notification.data.url)
        ? event.notification.data.url
        : "/";

    event.waitUntil(
        self.clients.matchAll({ type: "window", includeUncontrolled: true })
            .then((clientList) => {
                for (const client of clientList) {
                    if (client.url.includes(targetUrl) && "focus" in client) {
                        return client.focus();
                    }
                }
                if (self.clients.openWindow) {
                    return self.clients.openWindow(targetUrl);
                }
            })
    );
});