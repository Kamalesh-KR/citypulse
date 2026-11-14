/* ============================================================
   CityPulse — Live Traffic Pulse Widget
   ============================================================
   Displays a live-updating vehicle counter on the dashboard.

   - Every second: smooth fluctuation (±2-8 vehicles) for live feel
   - Every 5 min (WebSocket /topic/traffic): re-anchor to real DB value
   - If WebSocket disconnects: show OFFLINE
   ============================================================ */

(function () {
    "use strict";

    const Pulse = {

        currentValue: 0,
        lastDbValue: 0,
        lastDbSyncAt: 0,
        minuteAgoValue: 0,
        minuteAgoSnapshotAt: 0,
        intervalId: null,
        wsConnected: false,

        init() {

            if (!document.getElementById("live-pulse-vehicle-count")) {
                console.warn("[Pulse] Widget not found in DOM");
                return;
            }

            console.log("[Pulse] Initializing");

            this.anchorFromDb();
            this.startTicking();
            this.startDbRefresh();
            this.listenToWebSocket();

            // Update "last sync" label every 5 seconds
            setInterval(() => this.updateSyncLabel(), 5000);
        },

        /* ------------------------------------------------
           Load current total from DB
           ------------------------------------------------ */
        async anchorFromDb() {

            try {

                if (typeof CityPulseApp === "undefined" ||
                    typeof CityPulseApp.apiRequest !== "function") {
                    return;
                }

                const records = await CityPulseApp.apiRequest("/api/traffic/live");

                if (!Array.isArray(records) || records.length === 0) {
                    return;
                }

                let total = 0;
                records.forEach(r => {
                    total += (Number(r.vehicleCount) || 0);
                });

                this.applyDbAnchor(total);

            } catch (error) {
                console.warn("[Pulse] DB anchor failed:", error.message);
            }
        },

        /* ------------------------------------------------
           Refresh DB value every 5 minutes
           ------------------------------------------------ */
        startDbRefresh() {
            setInterval(() => this.anchorFromDb(), 5 * 60 * 1000);
        },

        /* ------------------------------------------------
           Apply a real value (from DB or WebSocket)
           ------------------------------------------------ */
        applyDbAnchor(newTotal) {

            // Track minute-ago comparison
            if (this.lastDbSyncAt > 0) {
                this.minuteAgoValue = this.lastDbValue;
                this.minuteAgoSnapshotAt = this.lastDbSyncAt;
            }

            this.lastDbValue = newTotal;
            this.currentValue = newTotal;
            this.lastDbSyncAt = Date.now();

            this.render();
            this.updateSyncLabel();
        },

        /* ------------------------------------------------
           Tick every 1 second — realistic fluctuation
           ------------------------------------------------ */
        startTicking() {

            if (this.intervalId) {
                clearInterval(this.intervalId);
            }

            this.intervalId = setInterval(() => {
                if (!this.wsConnected && this.currentValue === 0) return;

                // Fluctuation is smaller when vehicle count is larger
                const maxDrift = Math.max(2, Math.min(10, Math.round(this.lastDbValue * 0.04)));
                const drift = (Math.random() * 2 - 1) * maxDrift;

                // Keep it near the real DB value (±15% band)
                const minBound = Math.max(0, this.lastDbValue * 0.85);
                const maxBound = this.lastDbValue * 1.15;

                let next = this.currentValue + drift;

                if (next < minBound) next = minBound + Math.random() * 3;
                if (next > maxBound) next = maxBound - Math.random() * 3;

                this.currentValue = Math.round(next);
                this.render();

            }, 1000);
        },

        /* ------------------------------------------------
           Listen to WebSocket traffic topic
           ------------------------------------------------ */
        listenToWebSocket() {

            // Poll for the WS connected flag — the WS IIFE in app.js
            // sets textContent of #traffic-live-indicator.
            // We mirror its state loosely.

            setInterval(() => {

                const indicator = document.getElementById("traffic-live-indicator");
                const isLive = indicator &&
                    indicator.textContent &&
                    indicator.textContent.indexOf("LIVE") >= 0;

                this.wsConnected = !!isLive;
                this.updateStatusDot();

            }, 2000);

            // Also hook into real traffic updates if app.js exposes them
            // (it doesn't expose a hook, so we do a WebSocket-independent
            // refresh every 30 seconds as a safety net)
            setInterval(() => this.anchorFromDb(), 30 * 1000);
        },

        /* ------------------------------------------------
           Render current value + change indicator
           ------------------------------------------------ */
        render() {

            const valueEl = document.getElementById("live-pulse-vehicle-count");
            const changeEl = document.getElementById("live-pulse-change");
            const velocityEl = document.getElementById("live-pulse-velocity");

            if (valueEl) {
                valueEl.textContent = this.currentValue.toLocaleString("en-IN");
            }

            if (changeEl) {

                if (this.minuteAgoSnapshotAt === 0) {
                    changeEl.textContent = "—";
                    changeEl.className = "live-pulse-change";
                } else {
                    const diff = this.lastDbValue - this.minuteAgoValue;
                    const absDiff = Math.abs(diff);

                    if (absDiff < 2) {
                        changeEl.textContent = "stable";
                        changeEl.className = "live-pulse-change stable";
                    } else if (diff > 0) {
                        changeEl.textContent = `+${absDiff} ↑`;
                        changeEl.className = "live-pulse-change rising";
                    } else {
                        changeEl.textContent = `−${absDiff} ↓`;
                        changeEl.className = "live-pulse-change falling";
                    }
                }
            }

            if (velocityEl) {
                const pct = this.lastDbValue > 0
                    ? Math.round((this.currentValue / this.lastDbValue) * 100)
                    : 100;
                velocityEl.textContent = `${pct}%`;
            }
        },

        /* ------------------------------------------------
           Update "last DB sync" label
           ------------------------------------------------ */
        updateSyncLabel() {

            const el = document.getElementById("live-pulse-last-sync");
            if (!el) return;

            if (this.lastDbSyncAt === 0) {
                el.textContent = "waiting…";
                return;
            }

            const seconds = Math.round((Date.now() - this.lastDbSyncAt) / 1000);

            if (seconds < 5) {
                el.textContent = "just now";
            } else if (seconds < 60) {
                el.textContent = `${seconds}s ago`;
            } else {
                const minutes = Math.round(seconds / 60);
                el.textContent = `${minutes}m ago`;
            }
        },

        /* ------------------------------------------------
           Update status dot (LIVE / OFFLINE)
           ------------------------------------------------ */
        updateStatusDot() {

            const dot = document.getElementById("live-pulse-status-dot");
            const label = document.getElementById("live-pulse-status-label");

            if (!dot || !label) return;

            if (this.wsConnected) {
                dot.className = "live-pulse-dot live";
                label.textContent = "LIVE";
                label.className = "live-pulse-status-label live";
            } else {
                dot.className = "live-pulse-dot offline";
                label.textContent = "SYNCING";
                label.className = "live-pulse-status-label offline";
            }
        }

    };

    window.CityPulsePulse = Pulse;

    document.addEventListener("DOMContentLoaded", () => {
        setTimeout(() => Pulse.init(), 1500);
    });

})();