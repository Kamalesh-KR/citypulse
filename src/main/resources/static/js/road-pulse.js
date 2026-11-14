/* ============================================================
   CityPulse — Per-Road Live Pulse Widget
   Shows vehicle count per road, updating every second.
   Supports city filter (Bengaluru / Mysuru / Mangaluru / Hubballi)
   ============================================================ */

(function () {
    "use strict";

    const RoadPulse = {

        roads: {},
        intervalId: null,
        wsConnected: false,
        cityFilter: null,

        async init() {

            const container = document.getElementById("road-pulse-roads");
            if (!container) {
                console.warn("[RoadPulse] Container not found");
                return;
            }

            console.log("[RoadPulse] Initializing");

            await this.loadFromDb();
            this.startTicking();
            this.pollConnectionState();
            setInterval(() => this.loadFromDb(), 60 * 1000);
        },

        async loadFromDb() {

            try {

                if (typeof CityPulseApp === "undefined" ||
                    typeof CityPulseApp.apiRequest !== "function") {
                    return;
                }

                const records = await CityPulseApp.apiRequest("/api/traffic/live");

                if (!Array.isArray(records) || records.length === 0) {
                    return;
                }

                records.forEach(r => {

                    const roadId = r.road && r.road.id !== undefined
                        ? r.road.id
                        : r.roadId;

                    const roadName = r.road && r.road.name
                        ? r.road.name
                        : (r.roadName || `Road ${roadId}`);

                    const city = (r.location && r.location.city)
                        || (r.road && r.road.city)
                        || null;

                    const count = Number(r.vehicleCount) || 0;

                    if (roadId === undefined || roadId === null) return;

                    this.roads[roadId] = {
                        id: roadId,
                        name: roadName,
                        city: city,
                        lastDbValue: count,
                        currentValue: this.roads[roadId] && this.roads[roadId].currentValue
                            ? this.roads[roadId].currentValue
                            : count,
                        lastDbSyncAt: Date.now(),
                        level: r.trafficLevel || "UNKNOWN"
                    };
                });

                this.render();
                this.updateFooter();

            } catch (error) {
                console.warn("[RoadPulse] DB load failed:", error.message);
            }
        },

        startTicking() {

            if (this.intervalId) clearInterval(this.intervalId);

            this.intervalId = setInterval(() => {

                Object.values(this.roads).forEach(road => {

                    if (!road || road.lastDbValue === 0) return;

                    const maxDrift = Math.max(2, Math.min(8, Math.round(road.lastDbValue * 0.04)));
                    const drift = (Math.random() * 2 - 1) * maxDrift;

                    const minBound = Math.max(0, road.lastDbValue * 0.88);
                    const maxBound = road.lastDbValue * 1.12;

                    let next = road.currentValue + drift;
                    if (next < minBound) next = minBound + Math.random() * 2;
                    if (next > maxBound) next = maxBound - Math.random() * 2;

                    road.currentValue = Math.round(next);
                });

                this.render();

            }, 1000);
        },

        render() {

            const container = document.getElementById("road-pulse-roads");
            if (!container) return;

            let roads = Object.values(this.roads)
                .filter(r => r && r.id !== undefined);

            if (this.cityFilter) {
                roads = roads.filter(r =>
                    String(r.city || "").toLowerCase() === this.cityFilter
                );
            }

            if (roads.length === 0) {
                container.innerHTML = `<div class="road-pulse-empty">Waiting for traffic data…</div>`;
                return;
            }

            roads.sort((a, b) => b.currentValue - a.currentValue);

            container.innerHTML = roads.map(road => {

                const levelClass = (road.level || "").toLowerCase();
                const pct = road.lastDbValue > 0
                    ? Math.min(100, Math.round((road.currentValue / road.lastDbValue) * 100))
                    : 100;

                return `
                    <div class="road-pulse-row">
                        <div class="road-pulse-name">${this.escapeHtml(road.name)}</div>
                        <div class="road-pulse-bar">
                            <div class="road-pulse-bar-fill level-${levelClass}" style="width: ${pct}%;"></div>
                        </div>
                        <div class="road-pulse-count">${road.currentValue.toLocaleString("en-IN")}</div>
                        <div class="road-pulse-level level-${levelClass}">${this.escapeHtml(road.level || "")}</div>
                    </div>
                `;
            }).join("");
        },

        updateFooter() {

            const totalEl = document.getElementById("road-pulse-total");
            if (!totalEl) return;

            let total = 0;
            Object.values(this.roads).forEach(r => {
                if (this.cityFilter) {
                    if (String(r.city || "").toLowerCase() !== this.cityFilter) return;
                }
                total += (r.currentValue || 0);
            });

            totalEl.textContent = total.toLocaleString("en-IN");
        },

        setCityFilter(cityName) {

            if (!cityName) {
                this.cityFilter = null;
            } else {
                this.cityFilter = String(cityName).toLowerCase();
            }

            this.render();
            this.updateFooter();
        },

        pollConnectionState() {

            setInterval(() => {

                const indicator = document.getElementById("traffic-live-indicator");
                const isLive = indicator &&
                    indicator.textContent &&
                    indicator.textContent.indexOf("LIVE") >= 0;

                this.wsConnected = !!isLive;

                const dot = document.getElementById("road-pulse-status-dot");
                const label = document.getElementById("road-pulse-status-label");

                if (dot && label) {
                    if (isLive) {
                        dot.className = "live-pulse-dot live";
                        label.textContent = "LIVE";
                        label.className = "live-pulse-status-label live";
                    } else {
                        dot.className = "live-pulse-dot offline";
                        label.textContent = "SYNCING";
                        label.className = "live-pulse-status-label offline";
                    }
                }

            }, 2000);
        },

        escapeHtml(s) {
            if (s === null || s === undefined) return "";
            return String(s)
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;");
        }

    };

    window.CityPulseRoadPulse = RoadPulse;

    document.addEventListener("DOMContentLoaded", () => {
        setTimeout(() => RoadPulse.init(), 1500);
    });

})();