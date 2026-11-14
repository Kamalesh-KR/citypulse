/* ============================================================
   CityPulse GLOBAL — Peak Hour Heatmap (Feature E1)
   ============================================================
   Calls GET /api/traffic/analytics/heatmap and renders a
   24-hour × roads matrix as a CSS-grid heatmap.
   ============================================================ */

(function () {
    "use strict";

    const Heatmap = {

        data: null,
        container: null,

        init() {

            this.container = document.getElementById("heatmap-grid-container");
            if (!this.container) return;

            const refreshBtn = document.getElementById("heatmap-refresh-button");
            if (refreshBtn) {
                refreshBtn.addEventListener("click", () => this.load());
            }

            const exportBtn = document.getElementById("heatmap-export-button");
            if (exportBtn) {
                exportBtn.addEventListener("click", () => this.exportJson());
            }

            this.installTooltip();

        },

        installTooltip() {

            if (document.getElementById("heatmap-tooltip")) return;

            const tip = document.createElement("div");
            tip.id = "heatmap-tooltip";
            tip.className = "heatmap-tooltip";
            document.body.appendChild(tip);

        },

        async load() {

            const refreshBtn = document.getElementById("heatmap-refresh-button");

            try {

                if (refreshBtn) {
                    refreshBtn.disabled = true;
                    refreshBtn.textContent = "Loading...";
                }

                this.showMessage("Loading peak hour heatmap...", "info");

                const response = await this.fetchData();

                if (!response || !Array.isArray(response.roads)) {
                    throw new Error("Invalid heatmap response from server.");
                }

                this.data = response;
                this.render(response);

                this.showMessage(
                    `Heatmap loaded — ${response.totalRoads || 0} road(s), max congestion ${response.maxCongestion ?? "N/A"}.`,
                    "success"
                );

            } catch (error) {

                console.error("Heatmap loading failed:", error);
                this.showMessage(
                    "Unable to load heatmap. " + (error.message || ""),
                    "error"
                );
                this.container.innerHTML =
                    `<div class="heatmap-empty">Unable to load heatmap data.</div>`;

            } finally {

                if (refreshBtn) {
                    refreshBtn.disabled = false;
                    refreshBtn.textContent = "Refresh";
                }

            }

        },

        async fetchData() {

            // Prefer the global app's apiRequest so auth is attached
            if (typeof CityPulseApp !== "undefined" &&
                typeof CityPulseApp.apiRequest === "function") {
                return await CityPulseApp.apiRequest(
                    "/api/traffic/analytics/heatmap"
                );
            }

            // Fallback: direct fetch with token from localStorage
            const token = localStorage.getItem("citypulse.token");
            const headers = { "Content-Type": "application/json" };
            if (token) headers["Authorization"] = "Bearer " + token;

            const res = await fetch("/api/traffic/analytics/heatmap", { headers });
            if (!res.ok) throw new Error("API " + res.status);
            return await res.json();

        },

        render(payload) {

            if (!this.container) return;

            const hours = Array.isArray(payload.hours)
                ? payload.hours
                : Array.from({ length: 24 }, (_, i) => i);

            const roads = Array.isArray(payload.roads) ? payload.roads : [];

            if (roads.length === 0) {
                this.container.innerHTML =
                    `<div class="heatmap-empty">No traffic data available yet. Once traffic records exist, this heatmap will populate automatically.</div>`;
                this.renderStats(payload);
                return;
            }

            this.renderStats(payload);

            // Build: 1 corner cell + 24 hour labels + 1 corner + roads × (label + 24 cells)
            const cols = hours.length;
            const gridStyle =
                `grid-template-columns: 130px repeat(${cols}, minmax(44px, 1fr));`;

            let html = `<div class="heatmap-grid" style="${gridStyle}">`;

            // Header row
            html += `<div class="heatmap-hour-label heatmap-corner">Road \\ Hour</div>`;
            hours.forEach(h => {
                html += `<div class="heatmap-hour-label">${String(h).padStart(2, "0")}:00</div>`;
            });

            // Road rows
            roads.forEach(road => {

                const cells = Array.isArray(road.cells) ? road.cells : [];
                const cellByHour = {};
                cells.forEach(c => { cellByHour[c.hour] = c; });

                html += `<div class="heatmap-road-label" title="${this.escapeHtml(road.roadName)}">${this.escapeHtml(road.roadName)}</div>`;

                hours.forEach(h => {

                    const cell = cellByHour[h];
                    const avg = cell && cell.avgCongestion !== null
                        ? Number(cell.avgCongestion)
                        : null;
                    const count = cell ? (cell.sampleCount || 0) : 0;

                    let cls = "no-data";
                    let label = "—";

                    if (avg !== null) {
                        label = Math.round(avg);
                        if (avg < 25) cls = "low";
                        else if (avg < 50) cls = "moderate";
                        else if (avg < 75) cls = "high";
                        else cls = "severe";
                    }

                    const tooltip =
                        `${road.roadName} — ${String(h).padStart(2, "0")}:00` +
                        (avg !== null ? ` — ${avg.toFixed(1)} avg (${count} sample${count !== 1 ? "s" : ""})` : " — no data");

                    html += `<div class="heatmap-cell ${cls}"
                                  data-road="${this.escapeHtml(road.roadName)}"
                                  data-hour="${h}"
                                  data-avg="${avg !== null ? avg : ""}"
                                  data-count="${count}"
                                  data-tip="${this.escapeHtml(tooltip)}">
                                  ${label}
                             </div>`;

                });

            });

            html += `</div>`;
            this.container.innerHTML = html;

            this.attachTooltips();

        },

        renderStats(payload) {

            this.setElement("heatmap-total-roads", payload.totalRoads ?? "—");
            this.setElement("heatmap-max-congestion",
                payload.maxCongestion !== null && payload.maxCongestion !== undefined
                    ? Number(payload.maxCongestion).toFixed(1)
                    : "—");

            // Busiest hour across all roads
            let busiestHour = null;
            let busiestSum = 0;
            let busiestCount = 0;

            const hourSum = {};
            const hourCnt = {};

            (payload.roads || []).forEach(road => {
                (road.cells || []).forEach(c => {
                    if (c.avgCongestion === null) return;
                    hourSum[c.hour] = (hourSum[c.hour] || 0) + c.avgCongestion;
                    hourCnt[c.hour] = (hourCnt[c.hour] || 0) + 1;
                });
            });

            Object.keys(hourSum).forEach(h => {
                const avg = hourSum[h] / hourCnt[h];
                if (avg > busiestSum) {
                    busiestSum = avg;
                    busiestHour = h;
                }
            });

            this.setElement("heatmap-busiest-hour",
                busiestHour !== null
                    ? `${String(busiestHour).padStart(2, "0")}:00 (${busiestSum.toFixed(1)})`
                    : "—");

            this.setElement("heatmap-generated-at",
                payload.generatedAt
                    ? new Date(payload.generatedAt).toLocaleString("en-IN", {
                        dateStyle: "medium",
                        timeStyle: "short"
                    })
                    : "—");

        },

        setElement(id, value) {
            const el = document.getElementById(id);
            if (el) el.textContent = value;
        },

        attachTooltips() {

            const tip = document.getElementById("heatmap-tooltip");
            if (!tip) return;

            this.container.querySelectorAll(".heatmap-cell[data-tip]").forEach(cell => {

                cell.addEventListener("mouseenter", e => {
                    tip.textContent = cell.dataset.tip || "";
                    tip.style.display = "block";
                });

                cell.addEventListener("mousemove", e => {
                    const x = e.clientX + 14;
                    const y = e.clientY + 14;
                    tip.style.left = x + "px";
                    tip.style.top = y + "px";
                });

                cell.addEventListener("mouseleave", () => {
                    tip.style.display = "none";
                });

                cell.addEventListener("click", () => {
                    const road = cell.dataset.road;
                    const hour = cell.dataset.hour;
                    const avg = cell.dataset.avg;
                    const count = cell.dataset.count;
                    alert(
                        `Road: ${road}\n` +
                        `Hour: ${String(hour).padStart(2, "0")}:00\n` +
                        `Average congestion: ${avg !== "" ? avg : "N/A"}\n` +
                        `Samples: ${count}`
                    );
                });

            });

        },

        showMessage(text, type) {

            const el = document.getElementById("heatmap-message");
            if (!el) return;
            el.textContent = text;
            el.className = "heatmap-message " + (type || "info");
            el.style.display = "block";

            clearTimeout(this._msgTimer);
            if (type === "success" || type === "info") {
                this._msgTimer = setTimeout(() => { el.style.display = "none"; }, 6000);
            }

        },

        exportJson() {

            if (!this.data) {
                this.showMessage("Load the heatmap before exporting.", "info");
                return;
            }

            const blob = new Blob(
                [JSON.stringify(this.data, null, 2)],
                { type: "application/json" }
            );

            const url = URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = "citypulse-heatmap-" + Date.now() + ".json";
            document.body.appendChild(a);
            a.click();
            document.body.removeChild(a);
            URL.revokeObjectURL(url);

            this.showMessage("Heatmap JSON exported.", "success");

        },

        escapeHtml(s) {
            if (s === null || s === undefined) return "";
            return String(s)
                .replace(/&/g, "&amp;")
                .replace(/</g, "&lt;")
                .replace(/>/g, "&gt;")
                .replace(/"/g, "&quot;")
                .replace(/'/g, "&#039;");
        }

    };

    window.CityPulseHeatmap = Heatmap;

    document.addEventListener("DOMContentLoaded", () => {
        if (typeof CityPulseApp !== "undefined" &&
            typeof CityPulseApp.setupNavigation === "function") {
            // Wait for app to init, then wire the heatmap
            setTimeout(() => Heatmap.init(), 100);
        } else {
            Heatmap.init();
        }
    });

    // Auto-load when Analytics section is shown
    document.addEventListener("click", (e) => {
        const nav = e.target.closest(".nav-item");
        if (!nav) return;
        if (nav.dataset.section === "analytics") {
            setTimeout(() => {
                if (Heatmap.container) Heatmap.load();
            }, 400);
        }
    });

})();