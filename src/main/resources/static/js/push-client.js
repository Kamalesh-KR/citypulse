/* ============================================================
   CityPulse — Push Client (Feature: Push Notifications)
   ============================================================
   - Registers service worker
   - Fetches VAPID public key
   - Requests permission + subscribes
   - POSTs subscription to backend
   ============================================================ */

(function () {
    "use strict";

    const PushClient = {

        swRegistration: null,
        vapidPublicKey: null,

        async init() {

            if (!("serviceWorker" in navigator) || !("PushManager" in window)) {
                console.warn("[PushClient] Push not supported in this browser");
                return;
            }

            try {
                await this.registerServiceWorker();
                console.log("[PushClient] Ready");

                const state = await this.getState();
                this.updateButtonUI(state);

            } catch (error) {
                console.error("[PushClient] Init failed:", error);
            }
        },

        async registerServiceWorker() {

            if (!("serviceWorker" in navigator)) {
                throw new Error("Service Worker not supported");
            }

            this.swRegistration = await navigator.serviceWorker.register("/sw.js", {
                scope: "/"
            });

            await navigator.serviceWorker.ready;
            console.log("[PushClient] SW registered:", this.swRegistration.scope);
        },

        async fetchVapidKey() {

            if (this.vapidPublicKey) return this.vapidPublicKey;

            const res = await fetch("/api/push/vapid-public-key");
            if (!res.ok) throw new Error("Cannot fetch VAPID key");

            const json = await res.json();
            this.vapidPublicKey = json.publicKey;
            return this.vapidPublicKey;
        },

        urlBase64ToUint8Array(base64String) {

            const padding = "=".repeat((4 - base64String.length % 4) % 4);
            const base64 = (base64String + padding)
                .replace(/\-/g, "+")
                .replace(/_/g, "/");

            const rawData = window.atob(base64);
            const outputArray = new Uint8Array(rawData.length);

            for (let i = 0; i < rawData.length; ++i) {
                outputArray[i] = rawData.charCodeAt(i);
            }

            return outputArray;
        },

        async getState() {

            if (Notification.permission === "denied") {
                return "denied";
            }

            if (Notification.permission === "default") {
                return "default";
            }

            const sub = await this.swRegistration.pushManager.getSubscription();

            if (!sub) {
                return "granted-not-subscribed";
            }

            return "subscribed";
        },

        updateButtonUI(state) {

            const button = document.getElementById("enable-notifications-button");
            const label = document.getElementById("notification-label");
            const icon = document.getElementById("notification-icon");

            if (!button || !label) return;

            switch (state) {
                case "subscribed":
                    button.style.background = "#16a34a";
                    if (label) label.textContent = "🔔 Push Active";
                    if (icon) icon.textContent = "🔔";
                    button.onclick = () => this.sendTestPush();
                    break;

                case "granted-not-subscribed":
                    button.style.background = "#0ea5e9";
                    if (label) label.textContent = "🔔 Enable Push";
                    button.onclick = () => this.subscribeAndSend();
                    break;

                case "denied":
                    button.style.background = "#dc2626";
                    if (label) label.textContent = "🔕 Blocked";
                    break;

                default:
                    button.style.background = "#334155";
                    if (label) label.textContent = "🔔 Enable Push";
                    button.onclick = () => this.requestPermissionAndSubscribe();
                    break;
            }
        },

        async requestPermissionAndSubscribe() {

            try {
                const permission = await Notification.requestPermission();

                if (permission !== "granted") {
                    this.updateButtonUI("denied");
                    alert("Notifications were blocked. Enable them in your browser settings to receive push alerts.");
                    return;
                }

                await this.subscribeAndSend();

            } catch (error) {
                console.error("[PushClient] Permission request failed:", error);
                alert("Could not enable push notifications: " + error.message);
            }
        },

        async subscribeAndSend() {

            try {

                const vapidKey = await this.fetchVapidKey();

                let sub = await this.swRegistration.pushManager.getSubscription();

                if (!sub) {
                    sub = await this.swRegistration.pushManager.subscribe({
                        userVisibleOnly: true,
                        applicationServerKey: this.urlBase64ToUint8Array(vapidKey)
                    });
                }

                console.log("[PushClient] Subscription:", sub.endpoint);

                const auth = sub.toJSON();

                const token = localStorage.getItem("citypulse.token");
                const headers = { "Content-Type": "application/json" };
                if (token) headers["Authorization"] = "Bearer " + token;

                const res = await fetch("/api/push/subscribe", {
                    method: "POST",
                    headers: headers,
                    body: JSON.stringify({
                        endpoint: auth.endpoint,
                        p256dh: auth.keys.p256dh,
                        auth: auth.keys.auth
                    })
                });

                if (!res.ok) {
                    throw new Error("Backend rejected subscription: " + res.status);
                }

                this.updateButtonUI("subscribed");

                await this.sendTestPush();

            } catch (error) {
                console.error("[PushClient] Subscribe failed:", error);
                alert("Could not subscribe to push: " + error.message);
            }
        },

        async sendTestPush() {

            try {

                const token = localStorage.getItem("citypulse.token");
                const headers = { "Content-Type": "application/json" };
                if (token) headers["Authorization"] = "Bearer " + token;

                const res = await fetch("/api/push/test", {
                    method: "POST",
                    headers: headers,
                    body: JSON.stringify({
                        title: "CityPulse",
                        body: "Push notifications are working! 🎉"
                    })
                });

                if (res.ok) {
                    const data = await res.json();
                    console.log("[PushClient] Test push sent to " + data.sent + " subscriber(s)");
                }

            } catch (error) {
                console.error("[PushClient] Test push failed:", error);
            }
        }
    };

    window.CityPulsePushClient = PushClient;

    document.addEventListener("DOMContentLoaded", () => {
        setTimeout(() => PushClient.init(), 500);
    });

})();