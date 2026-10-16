"use strict";

/*
 * ============================================
 * CITYPULSE FRONTEND APPLICATION
 * ============================================
 */

const CityPulseApp = {

    roads: [],

    locations: [],

    trafficRecords: [],

    predictions: [],

    routeResults: [],

    citizenReports: [],

    countries: [],

    filteredCountries: [],

    currentUserId: 1,

    currentSection: "dashboard",

    apiBaseUrl: "",

    trafficMap: null,

    trafficMarkers: [],

    globalMap: null,

    globalCountryMarker: null,

    globalCountryCircle: null,

    globalSelectedCountryCode: null,

    globalCityMarkers: [],

    globalClockTimer: null,

    currentLanguage: "en",


    init() {

        console.log("CityPulse frontend initializing...");

        this.setupNavigation();

        this.setupRouteControls();

        this.setupEmergencyControls();

        this.setupAlertControls();

        this.setupAnalyticsControls();

        this.setupPredictionControls();

        this.setupCitizenReportControls();

        this.setupCityPulseAiControls();

        this.setupGlobalWorldMapControls();

        this.setupLanguageControls();

        this.loadDashboard();

        console.log("CityPulse frontend initialized.");

    },


    setupNavigation() {

        const navigationItems =
            document.querySelectorAll(".nav-item");

        navigationItems.forEach(item => {

            item.addEventListener("click", event => {

                event.preventDefault();

                const section = item.dataset.section;

                if (section) this.showSection(section);

            });

        });


        const sectionButtons =
            document.querySelectorAll("[data-section-target]");

        sectionButtons.forEach(button => {

            button.addEventListener("click", () => {

                const section = button.dataset.sectionTarget;

                if (section) this.showSection(section);

            });

        });

    },


    showSection(sectionName) {

        const sections =
            document.querySelectorAll(".page-section");

        sections.forEach(section => {
            section.classList.remove("active-section");
        });


        const navigationItems =
            document.querySelectorAll(".nav-item");

        navigationItems.forEach(item => {
            item.classList.remove("active");
        });


        const targetSection =
            document.getElementById(`${sectionName}-section`);

        if (!targetSection) {
            console.warn(`Section not found: ${sectionName}`);
            return;
        }


        targetSection.classList.add("active-section");


        const activeNavigationItem =
            document.querySelector(
                `.nav-item[data-section="${sectionName}"]`
            );

        if (activeNavigationItem) {
            activeNavigationItem.classList.add("active");
        }


        this.currentSection = sectionName;

        this.updatePageHeader(sectionName);


        if (sectionName === "traffic") this.loadLiveTrafficPage();
        if (sectionName === "emergency") this.loadEmergencyPage();
        if (sectionName === "alerts") this.loadAlertsPage();
        if (sectionName === "analytics") this.loadAnalyticsPage();
        if (sectionName === "prediction") this.loadPredictionPage();
        if (sectionName === "routes") this.loadSmartRoutesPage();
        if (sectionName === "reports") this.loadCitizenReportsPage();
        if (sectionName === "assistant") this.loadCityPulseAiPage();
        if (sectionName === "global") this.loadGlobalWorldMapPage();

    },


    updatePageHeader(sectionName) {

        const pageTitle =
            document.getElementById("page-title");

        const pageSubtitle =
            document.getElementById("page-subtitle");


        const pageInformation = {

            dashboard: {
                title: "Traffic Intelligence Dashboard",
                subtitle: "Real-time urban mobility overview"
            },

            traffic: {
                title: "Live Traffic",
                subtitle: "Real-time traffic conditions across monitored roads"
            },

            prediction: {
                title: "Traffic Prediction",
                subtitle: "Future congestion intelligence"
            },

            routes: {
                title: "Smart Routes",
                subtitle: "Traffic-aware route analysis"
            },

            emergency: {
                title: "Emergency Mode",
                subtitle: "Emergency route simulation"
            },

            alerts: {
                title: "Smart Alerts",
                subtitle: "Intelligent traffic and anomaly alerts"
            },

            analytics: {
                title: "Traffic Analytics",
                subtitle: "Historical traffic intelligence"
            },

            reports: {
                title: "Citizen Traffic Reports",
                subtitle: "Community-powered traffic information"
            },

            assistant: {
                title: "CityPulse AI",
                subtitle: "Traffic intelligence assistant"
            },

            global: {
                title: "Global World Map",
                subtitle: "Explore CityPulse coverage across the world"
            }

        };


        const information = pageInformation[sectionName];

        if (!information) return;

        if (pageTitle) pageTitle.textContent = information.title;

        if (pageSubtitle) pageSubtitle.textContent = information.subtitle;

    },


    async apiRequest(endpoint, options = {}) {

        const url = `${this.apiBaseUrl}${endpoint}`;

        const basicCredentials =
            btoa("kamalesh@gmail.com:Kamalesh@123");


        const requestOptions = {

            method: options.method || "GET",

            headers: {

                "Content-Type": "application/json",

                "Authorization": `Basic ${basicCredentials}`,

                ...(options.headers || {})

            },

            credentials: options.credentials || "same-origin"

        };


        if (options.body !== undefined) {

            requestOptions.body = JSON.stringify(options.body);

        }


        try {

            const response = await fetch(url, requestOptions);


            if (!response.ok) {

                const errorText = await response.text();

                throw new Error(
                    `API ${response.status}: ${errorText}`
                );

            }


            const contentType =
                response.headers.get("content-type");


            if (
                contentType &&
                contentType.includes("application/json")
            ) {

                return await response.json();

            }


            return await response.text();

        } catch (error) {

            console.error(
                `API request failed: ${endpoint}`,
                error
            );

            throw error;

        }

    },


    async loadDashboard() {

        try {

            await Promise.all([

                this.loadRoads(),

                this.loadLocations(),

                this.loadLiveTraffic()

            ]);

            this.updateDashboardStatistics();

            this.updateTrafficOverview();

            this.updateApiConnectionStatus(true);

        } catch (error) {

            console.error("Dashboard loading failed:", error);

            this.updateApiConnectionStatus(false);

            this.showDashboardError();

        }

    },


    async loadRoads() {

        const data = await this.apiRequest("/api/roads");

        this.roads = Array.isArray(data) ? data : [];

        return this.roads;

    },


    async loadLocations() {

        const data = await this.apiRequest("/api/locations");

        this.locations = Array.isArray(data) ? data : [];

        return this.locations;

    },


    async loadLiveTraffic() {

        const data = await this.apiRequest("/api/traffic/live");

        this.trafficRecords = Array.isArray(data) ? data : [];

        return this.trafficRecords;

    },


    updateDashboardStatistics() {

        const activeRoads =
            this.roads.filter(road => road.active === true).length;

        const activeLocations =
            this.locations.filter(location => location.active === true).length;

        const severeTraffic =
            this.trafficRecords.filter(record =>
                String(record.trafficLevel).toUpperCase() === "SEVERE"
            ).length;


        this.setElementText("active-roads", activeRoads);

        this.setElementText("traffic-records", this.trafficRecords.length);

        this.setElementText("severe-traffic", severeTraffic);

        this.setElementText("locations-count", activeLocations);

    },


    updateTrafficOverview() {

        const container =
            document.getElementById("traffic-overview");

        if (!container) return;


        if (this.trafficRecords.length === 0) {

            container.innerHTML = `
                <div class="empty-state">
                    No live traffic data available.
                </div>
            `;

            return;

        }


        container.innerHTML =
            this.trafficRecords.slice(0, 8).map(record => {

                const road = this.findRoad(record.roadId);

                const roadName = road
                    ? road.name
                    : `Road ${record.roadId}`;

                const trafficLevel =
                    String(record.trafficLevel || "UNKNOWN").toUpperCase();

                const levelClass =
                    this.getTrafficLevelClass(trafficLevel);

                return `
                    <div class="traffic-item">

                        <div>

                            <div class="traffic-item-name">
                                ${this.escapeHtml(roadName)}
                            </div>

                            <div class="traffic-item-details">
                                ${this.formatNumber(record.vehicleCount)}
                                vehicles
                                &nbsp;•&nbsp;
                                ${this.formatNumber(record.averageSpeed)}
                                km/h
                                &nbsp;•&nbsp;
                                Density
                                ${this.formatNumber(record.trafficDensity)}%
                            </div>

                        </div>

                        <span class="traffic-level ${levelClass}">
                            ${this.escapeHtml(trafficLevel)}
                        </span>

                    </div>
                `;

            }).join("");

    },


    async loadLiveTrafficPage() {

        try {

            await Promise.all([

                this.loadRoads(),

                this.loadLocations(),

                this.loadLiveTraffic()

            ]);

            this.updateDashboardStatistics();

            this.renderLiveTrafficTable();

            this.initializeTrafficMap();

            this.updateApiConnectionStatus(true);

        } catch (error) {

            console.error("Live traffic page loading failed:", error);

            this.updateApiConnectionStatus(false);

            this.renderTrafficPageError();

        }

    },


    renderLiveTrafficTable() {

        const tableBody =
            document.getElementById("traffic-table-body");

        const emptyState =
            document.getElementById("traffic-empty-state");

        if (!tableBody) return;


        if (!this.trafficRecords || this.trafficRecords.length === 0) {

            tableBody.innerHTML = "";

            if (emptyState) emptyState.style.display = "block";

            return;

        }

        if (emptyState) emptyState.style.display = "none";


        tableBody.innerHTML =
            this.trafficRecords.map(record => {

                const road = this.findRoad(record.roadId);

                const location = this.findLocation(record.locationId);

                const level =
                    String(record.trafficLevel || "UNKNOWN").toUpperCase();

                return `
                    <tr>

                        <td>
                            ${this.escapeHtml(
                                road?.name || `Road ${record.roadId}`
                            )}
                        </td>

                        <td>
                            ${this.escapeHtml(location?.name || "N/A")}
                        </td>

                        <td>
                            ${this.formatNumber(record.vehicleCount)}
                        </td>

                        <td>
                            ${this.formatNumber(record.averageSpeed)}
                            km/h
                        </td>

                        <td>
                            ${this.formatNumber(record.trafficDensity)}%
                        </td>

                        <td>
                            <span class="traffic-level ${this.getTrafficLevelClass(level)}">
                                ${this.escapeHtml(level)}
                            </span>
                        </td>

                        <td>
                            ${this.formatDateTime(record.recordedAt)}
                        </td>

                    </tr>
                `;

            }).join("");

    },


    initializeTrafficMap() {

        const mapElement =
            document.getElementById("traffic-map");

        if (!mapElement) return;

        if (typeof L === "undefined") {
            console.warn("Leaflet is not available.");
            return;
        }

        if (this.trafficMap) {

            this.trafficMap.invalidateSize();

            this.updateTrafficMapMarkers();

            return;

        }


        this.trafficMap =
            L.map(mapElement).setView([12.9716, 77.5946], 12);


        L.tileLayer(
            "https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png",
            {
                maxZoom: 19,
                attribution:
                    "&copy; OpenStreetMap contributors"
            }
        ).addTo(this.trafficMap);


        this.updateTrafficMapMarkers();

    },


    updateTrafficMapMarkers() {

        if (!this.trafficMap) return;


        this.trafficMarkers.forEach(
            marker => this.trafficMap.removeLayer(marker)
        );

        this.trafficMarkers = [];


        const locationsWithTraffic =
            this.trafficRecords.map(record => {

                const location = this.findLocation(record.locationId);

                return { record, location };

            }).filter(item =>
                item.location &&
                item.location.latitude !== null &&
                item.location.longitude !== null
            );


        const latestByLocation = new Map();


        locationsWithTraffic.forEach(item => {

            const locationId = Number(item.location.id);

            const existing = latestByLocation.get(locationId);

            if (!existing) {

                latestByLocation.set(locationId, item);

                return;

            }


            const currentDate =
                new Date(item.record.recordedAt || 0);

            const existingDate =
                new Date(existing.record.recordedAt || 0);

            if (currentDate >= existingDate) {

                latestByLocation.set(locationId, item);

            }

        });


        const markerColors = {

            LOW: {
                color: "#16a34a",
                fillColor: "#22c55e"
            },

            MODERATE: {
                color: "#ca8a04",
                fillColor: "#eab308"
            },

            HIGH: {
                color: "#ea580c",
                fillColor: "#f97316"
            },

            SEVERE: {
                color: "#dc2626",
                fillColor: "#ef4444"
            },

            UNKNOWN: {
                color: "#64748b",
                fillColor: "#94a3b8"
            }

        };


        latestByLocation.forEach(item => {

            const record = item.record;

            const location = item.location;

            const level =
                String(record.trafficLevel || "UNKNOWN").toUpperCase();

            const selectedColor =
                markerColors[level] || markerColors.UNKNOWN;


            const marker =
                L.circleMarker(
                    [
                        Number(location.latitude),
                        Number(location.longitude)
                    ],
                    {
                        radius: 10,
                        color: selectedColor.color,
                        fillColor: selectedColor.fillColor,
                        weight: 3,
                        opacity: 1,
                        fillOpacity: 0.85
                    }
                );


            marker.bindPopup(`
                <strong>${this.escapeHtml(location.name)}</strong>
                <br>
                Traffic: <strong>${this.escapeHtml(level)}</strong>
                <br>
                Vehicles: ${this.formatNumber(record.vehicleCount)}
                <br>
                Speed: ${this.formatNumber(record.averageSpeed)} km/h
                <br>
                Density: ${this.formatNumber(record.trafficDensity)}%
                <br>
                Congestion: ${this.formatNumber(record.congestionScore)}
                <br>
                Updated:
                ${this.escapeHtml(this.formatDateTime(record.recordedAt))}
            `);


            marker.addTo(this.trafficMap);

            this.trafficMarkers.push(marker);

        });

    },


    setupEmergencyControls() {

        const button =
            document.getElementById("simulate-emergency-button");

        if (!button) return;

        button.addEventListener(
            "click",
            () => this.simulateEmergencyRoute()
        );

    },


    async loadEmergencyPage() {

        try {

            if (this.locations.length === 0) {
                await this.loadLocations();
            }

            this.populateEmergencyLocationSelects();

            this.clearEmergencyResults();

            this.showEmergencyMessage(
                "Select an emergency type, start location, and destination, then run the simulation.",
                "info"
            );

        } catch (error) {

            console.error("Emergency page loading failed:", error);

            this.showEmergencyMessage(
                "Unable to load emergency route data.",
                "error"
            );

        }

    },


    populateEmergencyLocationSelects() {

        const startSelect =
            document.getElementById("emergency-start");

        const destinationSelect =
            document.getElementById("emergency-destination");

        if (!startSelect || !destinationSelect) return;


        const activeLocations =
            this.locations.filter(location => location.active !== false);


        const options = placeholder => {

            let html = `<option value="">${placeholder}</option>`;

            activeLocations.forEach(location => {

                html += `
                    <option value="${location.id}">
                        ${this.escapeHtml(location.name)}
                    </option>
                `;

            });

            return html;

        };


        startSelect.innerHTML = options("Select start location");

        destinationSelect.innerHTML = options("Select destination");

    },


    async simulateEmergencyRoute() {

        const emergencyType =
            document.getElementById("emergency-type")?.value;

        const startLocationId =
            document.getElementById("emergency-start")?.value;

        const destinationLocationId =
            document.getElementById("emergency-destination")?.value;

        const button =
            document.getElementById("simulate-emergency-button");


        if (!emergencyType) {

            this.showEmergencyMessage(
                "Please select an emergency type.",
                "error"
            );

            return;

        }

        if (!startLocationId || !destinationLocationId) {

            this.showEmergencyMessage(
                "Please select both a start location and destination.",
                "error"
            );

            return;

        }

        if (startLocationId === destinationLocationId) {

            this.showEmergencyMessage(
                "Start and destination locations must be different.",
                "error"
            );

            return;

        }


        if (button) {
            button.disabled = true;
            button.textContent = "Simulating...";
        }


        try {

            const data =
                await this.apiRequest(
                    "/api/emergency-routes/simulate",
                    {
                        method: "POST",
                        body: {
                            emergencyType,
                            startLocationId:
                                Number(startLocationId),
                            destinationLocationId:
                                Number(destinationLocationId)
                        }
                    }
                );


            this.renderEmergencyResults(data);

            this.showEmergencyMessage(
                "Emergency route simulation completed successfully.",
                "success"
            );

        } catch (error) {

            console.error("Emergency simulation failed:", error);

            this.showEmergencyMessage(
                error.message ||
                "Emergency route simulation failed.",
                "error"
            );

        } finally {

            if (button) {
                button.disabled = false;
                button.textContent = "Simulate Emergency Route";
            }

        }

    },


    renderEmergencyResults(data) {

        const panel =
            document.getElementById("emergency-results-panel");

        if (!panel) return;


        this.setElementText(
            "emergency-distance",
            `${this.formatNumber(data.routeDistanceKm, 2)} km`
        );

        this.setElementText(
            "emergency-normal-time",
            `${this.formatNumber(data.normalTravelTimeMinutes, 2)} min`
        );

        this.setElementText(
            "emergency-time",
            `${this.formatNumber(data.emergencyTravelTimeMinutes, 2)} min`
        );

        this.setElementText(
            "emergency-traffic-level",
            data.trafficLevel || "UNKNOWN"
        );

        this.setElementText(
            "emergency-congestion",
            data.congestionScore !== null &&
            data.congestionScore !== undefined
                ? this.formatNumber(data.congestionScore, 2)
                : "N/A"
        );

        this.setElementText(
            "emergency-priority",
            data.priorityStatus || "N/A"
        );

        this.setElementText(
            "emergency-signal",
            data.signalPriority || "N/A"
        );

        this.setElementText(
            "emergency-simulation-status",
            data.simulationStatus || "N/A"
        );

        this.setElementText(
            "emergency-result-message",
            data.message || "Emergency route simulation completed."
        );


        panel.style.display = "block";

    },


    showEmergencyMessage(message, type = "info") {

        const element =
            document.getElementById("emergency-analysis-message");

        if (!element) return;

        element.textContent = message || "";

        element.className = "route-message";

        element.classList.add(`route-message-${type}`);

    },


    clearEmergencyResults() {

        const panel =
            document.getElementById("emergency-results-panel");

        if (panel) panel.style.display = "none";

    },


    setupPredictionControls() {

        const roadSelect =
            document.getElementById("prediction-road-select");

        const generateButton =
            document.getElementById("generate-predictions-button");

        const refreshButton =
            document.getElementById("refresh-predictions-button");


        if (generateButton) {

            generateButton.addEventListener(
                "click",
                () => this.generatePredictionsForSelectedRoad()
            );

        }

        if (refreshButton) {

            refreshButton.addEventListener(
                "click",
                () => this.loadPredictionPage()
            );

        }

        if (roadSelect) {

            roadSelect.addEventListener(
                "change",
                () => this.loadPredictionDataForSelectedRoad()
            );

        }

    },


    async loadPredictionPage() {

        try {

            this.showPredictionMessage(
                "Loading traffic prediction intelligence...",
                "info"
            );

            if (this.roads.length === 0) await this.loadRoads();

            this.populatePredictionRoadSelect();


            const select =
                document.getElementById("prediction-road-select");

            if (select && !select.value && this.roads.length > 0) {

                select.value = String(this.roads[0].id);

            }

            if (!select || !select.value) {

                this.showPredictionMessage(
                    "No roads are available for traffic prediction.",
                    "info"
                );

                return;

            }

            await this.loadPredictionDataForSelectedRoad();

        } catch (error) {

            console.error("Prediction page failed:", error);

            this.clearPredictionResults();

            this.showPredictionMessage(
                "Unable to load Traffic Prediction.",
                "error"
            );

        }

    },


    populatePredictionRoadSelect() {

        const select =
            document.getElementById("prediction-road-select");

        if (!select) return;

        const current = select.value;

        select.innerHTML = `
            <option value="">
                Select road
            </option>
        `;

        this.roads.forEach(road => {

            const option = document.createElement("option");

            option.value = String(road.id);

            option.textContent =
                road.name || `Road ${road.id}`;

            select.appendChild(option);

        });


        if (
            current &&
            this.roads.some(road => String(road.id) === String(current))
        ) {

            select.value = current;

        }

    },


    async loadPredictionDataForSelectedRoad() {

        const select =
            document.getElementById("prediction-road-select");

        if (!select || !select.value) {

            this.clearPredictionResults();

            return;

        }

        const roadId = Number(select.value);


        try {

            const [predictions, traffic] =
                await Promise.all([

                    this.apiRequest(
                        `/api/predictions/road/${roadId}`
                    ),

                    this.apiRequest("/api/traffic")

                ]);


            this.predictions =
                Array.isArray(predictions) ? predictions : [];

            this.trafficRecords =
                Array.isArray(traffic) ? traffic : [];


            const roadTraffic =
                this.trafficRecords
                    .filter(record =>
                        Number(record.roadId) === roadId
                    )
                    .sort((a, b) =>
                        new Date(b.recordedAt || 0) -
                        new Date(a.recordedAt || 0)
                    );

            const latest = roadTraffic[0] || null;


            let weather = null;

            if (latest && latest.locationId) {

                try {

                    weather =
                        await this.apiRequest(
                            `/api/weather/impact/${latest.locationId}`
                        );

                } catch (error) {

                    console.warn(
                        "Weather impact unavailable:",
                        error
                    );

                }

            }


            this.renderPredictionCurrentTraffic(latest);

            this.renderPredictionWeather(weather);

            this.renderPredictionResults(this.predictions);


            this.showPredictionMessage(
                this.predictions.length > 0
                    ? "Traffic predictions loaded successfully."
                    : "No predictions are available for this road.",
                "info"
            );

        } catch (error) {

            console.error("Prediction loading failed:", error);

            this.clearPredictionResults();

            this.showPredictionMessage(
                "Unable to load prediction data.",
                "error"
            );

        }

    },


    async generatePredictionsForSelectedRoad() {

        const select =
            document.getElementById("prediction-road-select");

        if (!select || !select.value) {

            this.showPredictionMessage(
                "Select a road before generating predictions.",
                "error"
            );

            return;

        }

        const button =
            document.getElementById("generate-predictions-button");


        try {

            if (button) {
                button.disabled = true;
                button.textContent = "Generating...";
            }


            await this.apiRequest(
                `/api/predictions/generate/${Number(select.value)}`,
                { method: "POST" }
            );


            await this.loadPredictionDataForSelectedRoad();


            this.showPredictionMessage(
                "Traffic predictions generated successfully.",
                "success"
            );

        } catch (error) {

            console.error("Prediction generation failed:", error);

            this.showPredictionMessage(
                "Unable to generate traffic predictions.",
                "error"
            );

        } finally {

            if (button) {
                button.disabled = false;
                button.textContent = "Generate Predictions";
            }

        }

    },

    renderPredictionCurrentTraffic(traffic) {

        this.setElementText(
            "prediction-current-level",
            traffic ? traffic.trafficLevel || "UNKNOWN" : "NOT AVAILABLE"
        );

    },


    renderPredictionWeather(weather) {

        if (!weather) {

            this.setElementText(
                "prediction-weather-score",
                "NOT AVAILABLE"
            );

            this.setElementText(
                "prediction-weather-level",
                "NOT AVAILABLE"
            );

            this.setElementText(
                "prediction-model",
                "CITYPULSE-TREND-V1"
            );

            this.setElementText(
                "prediction-weather-summary",
                "Weather data is not available for the selected road."
            );

            return;

        }


        this.setElementText(
            "prediction-weather-score",
            this.formatPredictionNumber(weather.weatherImpactScore, 2)
        );

        this.setElementText(
            "prediction-weather-level",
            weather.weatherImpactLevel || "UNKNOWN"
        );

        this.setElementText(
            "prediction-model",
            weather.weatherImpactScore > 0
                ? "CITYPULSE-TREND-WEATHER-V1"
                : "CITYPULSE-TREND-V1"
        );


        const condition =
            weather.weatherCondition || "UNKNOWN";

        const temperature =
            this.formatPredictionNumber(weather.temperature, 1);

        const rainfall =
            this.formatPredictionNumber(weather.rainfall, 1);

        const visibility =
            this.formatPredictionNumber(weather.visibility, 1);


        this.setElementText(
            "prediction-weather-summary",
            `${condition} • ${temperature}°C • Rainfall ${rainfall} mm • Visibility ${visibility} km • ${weather.impactSummary || "Weather impact analyzed."}`
        );

    },


    renderPredictionResults(predictions) {

        const container =
            document.getElementById("prediction-results");

        const empty =
            document.getElementById("prediction-empty-state");

        if (!container) return;


        const latest =
            this.getLatestPredictionsByHorizon(predictions);


        if (latest.size === 0) {

            container.innerHTML = "";

            if (empty) empty.style.display = "block";

            return;

        }

        if (empty) empty.style.display = "none";


        container.innerHTML =
            [15, 30, 45, 60]
                .filter(horizon => latest.has(horizon))
                .map(horizon =>
                    this.buildPredictionCard(latest.get(horizon))
                )
                .join("");

    },


    getLatestPredictionsByHorizon(predictions) {

        const result = new Map();

        (predictions || []).forEach(prediction => {

            const horizon =
                Number(prediction.predictionHorizonMinutes);

            if (!Number.isFinite(horizon)) return;

            const existing = result.get(horizon);

            if (!existing) {

                result.set(horizon, prediction);

                return;

            }


            const currentDate =
                new Date(
                    prediction.createdAt ||
                    prediction.predictionTime ||
                    0
                );

            const existingDate =
                new Date(
                    existing.createdAt ||
                    existing.predictionTime ||
                    0
                );


            if (currentDate >= existingDate) {

                result.set(horizon, prediction);

            }

        });

        return result;

    },


    buildPredictionCard(prediction) {

        const horizon =
            Number(prediction.predictionHorizonMinutes);


        return `
            <div class="prediction-card">

                <div class="prediction-card-header">

                    <div>

                        <span class="prediction-horizon">
                            +${horizon} MIN
                        </span>

                        <h4>
                            ${this.escapeHtml(
                                prediction.predictedTrafficLevel ||
                                "UNKNOWN"
                            )}
                        </h4>

                    </div>

                    <span class="prediction-model">
                        ${this.escapeHtml(
                            prediction.modelVersion || "UNKNOWN"
                        )}
                    </span>

                </div>

                <div class="prediction-card-grid">

                    <div class="prediction-metric">
                        <span>VEHICLES</span>
                        <strong>
                            ${
                                prediction.predictedVehicleCount !== null &&
                                prediction.predictedVehicleCount !== undefined
                                    ? Number(
                                        prediction.predictedVehicleCount
                                    ).toLocaleString()
                                    : "N/A"
                            }
                        </strong>
                    </div>

                    <div class="prediction-metric">
                        <span>AVG SPEED</span>
                        <strong>
                            ${this.formatPredictionNumber(
                                prediction.predictedAverageSpeed
                            )} km/h
                        </strong>
                    </div>

                    <div class="prediction-metric">
                        <span>DENSITY</span>
                        <strong>
                            ${this.formatPredictionNumber(
                                prediction.predictedDensity
                            )}%
                        </strong>
                    </div>

                    <div class="prediction-metric">
                        <span>CONGESTION</span>
                        <strong>
                            ${this.formatPredictionNumber(
                                prediction.predictedCongestionScore
                            )}
                        </strong>
                    </div>

                    <div class="prediction-metric">
                        <span>CONFIDENCE</span>
                        <strong>
                            ${this.formatPredictionNumber(
                                prediction.confidenceScore
                            )}%
                        </strong>
                    </div>

                </div>

            </div>
        `;

    },


    formatPredictionNumber(value, decimals = 2) {

        if (value === null || value === undefined || value === "") {
            return "N/A";
        }

        const number = Number(value);

        if (!Number.isFinite(number)) return "N/A";

        return number.toFixed(decimals);

    },


    showPredictionMessage(message, type = "info") {

        const element =
            document.getElementById("prediction-analysis-message");

        if (!element) return;

        element.textContent = message;

        element.className = `analysis-message ${type}`;

        element.style.display = "block";

    },


    clearPredictionResults() {

        const result =
            document.getElementById("prediction-results");

        if (result) result.innerHTML = "";

        const empty =
            document.getElementById("prediction-empty-state");

        if (empty) empty.style.display = "block";

        this.renderPredictionCurrentTraffic(null);

        this.renderPredictionWeather(null);

    },


    setupAnalyticsControls() {

        const button =
            document.getElementById("refresh-analytics-button");

        if (!button) return;

        button.addEventListener(
            "click",
            () => this.loadAnalyticsPage()
        );

    },


    async loadAnalyticsPage() {

        try {

            this.showAnalyticsMessage(
                "Loading historical traffic analytics...",
                "info"
            );

            const analytics =
                await this.apiRequest(
                    "/api/traffic/analytics/historical"
                );

            this.renderAnalytics(analytics);

            this.showAnalyticsMessage(
                `${analytics?.totalRecords || 0} historical traffic record(s) analyzed successfully.`,
                "success"
            );

        } catch (error) {

            console.error("Analytics loading failed:", error);

            this.clearAnalyticsResults();

            this.showAnalyticsMessage(
                "Unable to load Traffic Analytics.",
                "error"
            );

        }

    },


    renderAnalytics(analytics) {

        const values = {

            "analytics-total-records":
                this.formatNumber(analytics?.totalRecords),

            "analytics-average-vehicles":
                this.formatNumber(analytics?.averageVehicleCount),

            "analytics-average-speed":
                `${this.formatNumber(analytics?.averageSpeed)} km/h`,

            "analytics-average-density":
                `${this.formatNumber(analytics?.averageDensity)}%`,

            "analytics-average-congestion":
                this.formatNumber(analytics?.averageCongestionScore),

            "analytics-min-speed":
                `${this.formatNumber(analytics?.minimumSpeed)} km/h`,

            "analytics-max-speed":
                `${this.formatNumber(analytics?.maximumSpeed)} km/h`,

            "analytics-min-density":
                `${this.formatNumber(analytics?.minimumDensity)}%`,

            "analytics-max-density":
                `${this.formatNumber(analytics?.maximumDensity)}%`,

            "analytics-analysis-start":
                this.formatDateTime(analytics?.analysisStart),

            "analytics-analysis-end":
                this.formatDateTime(analytics?.analysisEnd)

        };


        Object.entries(values).forEach(([id, value]) => {
            this.setElementText(id, value);
        });


        this.renderAnalyticsDistribution(
            analytics?.trafficLevelDistribution
        );

    },


    renderAnalyticsDistribution(distribution) {

        const container =
            document.getElementById("analytics-traffic-distribution");

        if (!container) return;

        const entries = Object.entries(distribution || {});


        if (entries.length === 0) {

            container.innerHTML = `
                <div class="analytics-distribution-empty">
                    No traffic-level distribution data available.
                </div>
            `;

            return;

        }


        const total =
            entries.reduce(
                (sum, item) => sum + Number(item[1] || 0),
                0
            );


        container.innerHTML =
            entries.map(([level, count]) => {

                const percentage =
                    total > 0
                        ? (Number(count) / total) * 100
                        : 0;

                return `
                    <div class="analytics-distribution-row">

                        <div class="analytics-distribution-header">
                            <span>
                                ${this.escapeHtml(
                                    String(level).toUpperCase()
                                )}
                            </span>
                            <span>
                                ${this.formatNumber(count)}
                            </span>
                        </div>

                        <div class="analytics-distribution-track">
                            <div
                                class="analytics-distribution-fill"
                                style="width:${Math.max(
                                    0,
                                    Math.min(100, percentage)
                                )}%;">
                            </div>
                        </div>

                        <span>
                            ${this.formatNumber(percentage)}%
                        </span>

                    </div>
                `;

            }).join("");

    },


    showAnalyticsMessage(message, type = "info") {

        const element =
            document.getElementById("analytics-analysis-message");

        if (!element) return;

        element.textContent = message;

        element.className = `analysis-message ${type}`;

        element.style.display = "block";

    },


    clearAnalyticsResults() {

        [
            "analytics-total-records",
            "analytics-average-vehicles",
            "analytics-average-speed",
            "analytics-average-density",
            "analytics-average-congestion",
            "analytics-min-speed",
            "analytics-max-speed",
            "analytics-min-density",
            "analytics-max-density",
            "analytics-analysis-start",
            "analytics-analysis-end"
        ].forEach(id => this.setElementText(id, "—"));


        const distribution =
            document.getElementById("analytics-traffic-distribution");

        if (distribution) distribution.innerHTML = "";

    },


    setupAlertControls() {

        const button =
            document.getElementById("refresh-alerts-button");

        if (!button) return;

        button.addEventListener(
            "click",
            () => this.loadAlertsPage()
        );

    },


    async loadAlertsPage() {

        try {

            this.showAlertsMessage(
                "Loading Smart Alerts...",
                "info"
            );

            const alerts = await this.apiRequest("/api/alerts");

            const safeAlerts =
                Array.isArray(alerts) ? alerts : [];

            this.renderAlerts(safeAlerts);

            this.showAlertsMessage(
                `${safeAlerts.length} smart alert(s) loaded successfully.`,
                "success"
            );

        } catch (error) {

            console.error("Smart Alerts loading failed:", error);

            this.clearAlertsResults();

            this.showAlertsMessage(
                "Unable to load Smart Alerts.",
                "error"
            );

        }

    },


    renderAlerts(alerts) {

        const list = document.getElementById("alerts-list");

        const empty = document.getElementById("alerts-empty-state");

        if (!list) return;


        const safeAlerts = Array.isArray(alerts) ? alerts : [];

        const critical =
            safeAlerts.filter(alert =>
                String(alert?.severity || "").toUpperCase() === "CRITICAL"
            ).length;

        const high =
            safeAlerts.filter(alert =>
                String(alert?.severity || "").toUpperCase() === "HIGH"
            ).length;

        const active =
            safeAlerts.filter(alert => alert?.active === true).length;


        this.setElementText("alerts-total", safeAlerts.length);

        this.setElementText("alerts-critical", critical);

        this.setElementText("alerts-high", high);

        this.setElementText("alerts-active", active);


        if (safeAlerts.length === 0) {

            list.innerHTML = "";

            if (empty) empty.style.display = "block";

            return;

        }

        if (empty) empty.style.display = "none";


        list.innerHTML =
            safeAlerts
                .map(alert => this.renderAlertCard(alert))
                .join("");

    },


    renderAlertCard(alert) {

        const severity =
            String(alert?.severity || "UNKNOWN").toUpperCase();


        return `
            <article class="alert-card">

                <div class="alert-card-header">

                    <div>

                        <span class="alert-type">
                            ${this.escapeHtml(
                                String(
                                    alert?.alertType || "ALERT"
                                ).toUpperCase()
                            )}
                        </span>

                        <h4 class="alert-card-title">
                            ${this.escapeHtml(
                                alert?.title || "Traffic Alert"
                            )}
                        </h4>

                    </div>

                    <span class="alert-severity alert-severity-${severity.toLowerCase()}">
                        ${this.escapeHtml(severity)}
                    </span>

                </div>

                <p class="alert-card-message">
                    ${this.escapeHtml(
                        alert?.message ||
                        "No alert details available."
                    )}
                </p>

                <div class="alert-card-details">

                    <div>
                        <span class="alert-detail-label">Road</span>
                        <span class="alert-detail-value">
                            ${this.escapeHtml(
                                alert?.road?.name || "Unavailable"
                            )}
                        </span>
                    </div>

                    <div>
                        <span class="alert-detail-label">Location</span>
                        <span class="alert-detail-value">
                            ${this.escapeHtml(
                                alert?.location?.name || "Unavailable"
                            )}
                        </span>
                    </div>

                    <div>
                        <span class="alert-detail-label">Status</span>
                        <span class="alert-detail-value">
                            ${alert?.active === true ? "ACTIVE" : "INACTIVE"}
                        </span>
                    </div>

                    <div>
                        <span class="alert-detail-label">Created</span>
                        <span class="alert-detail-value">
                            ${this.escapeHtml(
                                this.formatDateTime(alert?.createdAt)
                            )}
                        </span>
                    </div>

                    <div>
                        <span class="alert-detail-label">Expires</span>
                        <span class="alert-detail-value">
                            ${this.escapeHtml(
                                this.formatDateTime(alert?.expiresAt)
                            )}
                        </span>
                    </div>

                </div>

            </article>
        `;

    },


    showAlertsMessage(message, type = "info") {

        const element =
            document.getElementById("alerts-analysis-message");

        if (!element) return;

        element.textContent = message;

        element.className = `analysis-message ${type}`;

        element.style.display = "block";

    },


    clearAlertsResults() {

        const list = document.getElementById("alerts-list");

        const empty = document.getElementById("alerts-empty-state");

        if (list) list.innerHTML = "";

        if (empty) empty.style.display = "none";

    },


    setupRouteControls() {

        const button =
            document.getElementById("find-routes-button");

        if (!button) return;

        button.addEventListener(
            "click",
            () => this.findSmartRoutes()
        );

    },


    async loadSmartRoutesPage() {

        try {

            if (this.locations.length === 0) {
                await this.loadLocations();
            }

            this.populateRouteLocationSelects();

            this.clearRouteResults();

        } catch (error) {

            console.error("Smart Routes loading failed:", error);

        }

    },


    populateRouteLocationSelects() {

        const start =
            document.getElementById("route-start-location");

        const destination =
            document.getElementById("route-destination-location");

        if (!start || !destination) return;


        const options = placeholder => {

            let html = `<option value="">${placeholder}</option>`;

            this.locations
                .filter(location => location.active !== false)
                .forEach(location => {

                    html += `
                        <option value="${location.id}">
                            ${this.escapeHtml(location.name)}
                        </option>
                    `;

                });

            return html;

        };


        start.innerHTML = options("Select start location");

        destination.innerHTML = options("Select destination");

    },


    async findSmartRoutes() {

        const start =
            document.getElementById("route-start-location")?.value;

        const destination =
            document.getElementById("route-destination-location")?.value;

        const button =
            document.getElementById("find-routes-button");


        if (!start || !destination) {

            this.showRouteMessage(
                "Please select both start and destination locations.",
                "error"
            );

            return;

        }

        if (start === destination) {

            this.showRouteMessage(
                "Start and destination locations must be different.",
                "error"
            );

            return;

        }


        try {

            if (button) {
                button.disabled = true;
                button.textContent = "Analyzing...";
            }

            this.showRouteMessage(
                "Analyzing traffic-aware routes...",
                "info"
            );


            const data =
                await this.apiRequest(
                    "/api/worldwide-routes/traffic-route",
                    {
                        method: "POST",
                        body: {
                            fromAddress:
                                this.getLocationAddress(Number(start)),
                            toAddress:
                                this.getLocationAddress(Number(destination))
                        }
                    }
                );


            this.routeResults =
                Array.isArray(data)
                    ? data
                    : (
                        Array.isArray(data?.routes)
                            ? data.routes
                            : []
                    );


            this.renderSmartRouteResults(this.routeResults);

            this.showRouteMessage(
                "Traffic-aware route analysis completed.",
                "success"
            );

        } catch (error) {

            console.error("Smart route analysis failed:", error);

            this.showRouteMessage(
                error.message ||
                "Unable to analyze smart routes.",
                "error"
            );

        } finally {

            if (button) {
                button.disabled = false;
                button.textContent = "Find Smart Routes";
            }

        }

    },


    getLocationAddress(locationId) {

        const location = this.findLocation(locationId);

        if (!location) return String(locationId);

        return [
            location.name,
            location.address,
            location.city,
            location.state
        ].filter(Boolean).join(", ");

    },


    renderSmartRouteResults(routes) {

        const container =
            document.getElementById("routes-results");

        const empty =
            document.getElementById("routes-empty-state");

        if (!container) return;


        if (!routes || routes.length === 0) {

            container.innerHTML = "";

            if (empty) empty.style.display = "block";

            return;

        }

        if (empty) empty.style.display = "none";


        container.innerHTML =
            routes.map((route, index) => {

                const recommended = route.recommended === true;

                return `
                    <article class="route-result-card">

                        <div class="route-result-header">

                            <div>

                                <span class="section-label">
                                    ROUTE ${index + 1}
                                </span>

                                <h4>
                                    ${this.escapeHtml(
                                        route.routeName ||
                                        route.routeId ||
                                        `Route ${index + 1}`
                                    )}
                                </h4>

                            </div>

                            ${
                                recommended
                                    ? `<span class="route-recommended-badge">
                                            RECOMMENDED
                                       </span>`
                                    : ""
                            }

                        </div>

                        <div class="route-result-grid">

                            <div>
                                <span>DISTANCE</span>
                                <strong>
                                    ${this.formatNumber(
                                        route.distanceKm, 2
                                    )} km
                                </strong>
                            </div>

                            <div>
                                <span>TIME</span>
                                <strong>
                                    ${this.formatNumber(
                                        route.estimatedTimeMinutes, 2
                                    )} min
                                </strong>
                            </div>

                            <div>
                                <span>CURRENT TRAFFIC</span>
                                <strong>
                                    ${this.escapeHtml(
                                        route.currentTrafficLevel ||
                                        "UNKNOWN"
                                    )}
                                </strong>
                            </div>

                            <div>
                                <span>PREDICTED TRAFFIC</span>
                                <strong>
                                    ${this.escapeHtml(
                                        route.predictedTrafficLevel ||
                                        "UNKNOWN"
                                    )}
                                </strong>
                            </div>

                            <div>
                                <span>CONGESTION</span>
                                <strong>
                                    ${this.formatNumber(
                                        route.congestionScore
                                    )}
                                </strong>
                            </div>

                            <div>
                                <span>ROUTE SCORE</span>
                                <strong>
                                    ${this.formatNumber(
                                        route.routeScore
                                    )}
                                </strong>
                            </div>

                        </div>

                        <p>
                            ${this.escapeHtml(
                                route.recommendationReason ||
                                "Traffic-aware route analyzed by CityPulse."
                            )}
                        </p>

                    </article>
                `;

            }).join("");

    },


    showRouteMessage(message, type = "info") {

        const element =
            document.getElementById("route-analysis-message");

        if (!element) return;

        element.textContent = message;

        element.className = "route-message";

        element.classList.add(`route-message-${type}`);

    },


    clearRouteResults() {

        const container =
            document.getElementById("routes-results");

        if (container) container.innerHTML = "";

        const empty =
            document.getElementById("routes-empty-state");

        if (empty) empty.style.display = "block";

    },


    setupCitizenReportControls() {

        const form =
            document.getElementById("citizen-report-form");

        if (!form) return;

        form.addEventListener(
            "submit",
            event => {

                event.preventDefault();

                this.submitCitizenReport();

            }
        );

    },


    async loadCitizenReportsPage() {

        try {

            if (this.roads.length === 0) {
                await this.loadRoads();
            }

            if (this.locations.length === 0) {
                await this.loadLocations();
            }

            this.populateCitizenReportRoads();

            this.populateCitizenReportLocations();

            await this.loadCitizenReports();

        } catch (error) {

            console.error("Citizen Reports loading failed:", error);

        }

    },


    populateCitizenReportRoads() {

        const select = document.getElementById("report-road");

        if (!select) return;

        select.innerHTML = `
            <option value="">
                Select road
            </option>
        `;

        this.roads.forEach(road => {

            const option = document.createElement("option");

            option.value = String(road.id);

            option.textContent = road.name;

            select.appendChild(option);

        });

    },


    populateCitizenReportLocations() {

        const select = document.getElementById("report-location");

        if (!select) return;

        select.innerHTML = `
            <option value="">
                Select location
            </option>
        `;

        this.locations.forEach(location => {

            const option = document.createElement("option");

            option.value = String(location.id);

            option.textContent = location.name;

            select.appendChild(option);

        });

    },


    async submitCitizenReport() {

        const form =
            document.getElementById("citizen-report-form");

        if (!form) return;

        const reportType =
            document.getElementById("report-type")?.value;

        const roadId =
            document.getElementById("report-road")?.value;

        const locationId =
            document.getElementById("report-location")?.value;

        const description =
            document.getElementById("report-description")?.value;


        if (!reportType || !description) {

            this.showCitizenReportMessage(
                "Please select a report type and enter a description.",
                "error"
            );

            return;

        }


        const button =
            document.getElementById("submit-report-button");


        try {

            if (button) {
                button.disabled = true;
                button.textContent = "Submitting...";
            }


            await this.apiRequest(
                "/api/reports",
                {
                    method: "POST",
                    body: {
                        userId: this.currentUserId,
                        roadId: roadId ? Number(roadId) : null,
                        locationId:
                            locationId ? Number(locationId) : null,
                        reportType,
                        description,
                        status: "PENDING"
                    }
                }
            );


            this.showCitizenReportMessage(
                "Traffic report submitted successfully and is now pending review.",
                "success"
            );

            form.reset();

            await this.loadCitizenReports();

        } catch (error) {

            console.error("Citizen report submission failed:", error);

            this.showCitizenReportMessage(
                error.message ||
                "Unable to submit traffic report.",
                "error"
            );

        } finally {

            if (button) {
                button.disabled = false;
                button.textContent = "Submit Report";
            }

        }

    },


    async loadCitizenReports() {

        try {

            const reports =
                await this.apiRequest(
                    `/api/reports/user/${this.currentUserId}`
                );

            this.citizenReports =
                Array.isArray(reports) ? reports : [];

            this.renderCitizenReports();

        } catch (error) {

            console.error("Citizen reports retrieval failed:", error);

        }

    },


    renderCitizenReports() {

        const container =
            document.getElementById("reports-list");

        const empty =
            document.getElementById("reports-empty-state");

        if (!container) return;


        if (this.citizenReports.length === 0) {

            container.innerHTML = "";

            if (empty) empty.style.display = "block";

            return;

        }

        if (empty) empty.style.display = "none";


        container.innerHTML =
            this.citizenReports.map(report => `
                <article class="report-card">

                    <div class="report-card-header">

                        <div>

                            <span class="section-label">
                                ${this.escapeHtml(
                                    report.reportType || "REPORT"
                                )}
                            </span>

                            <h4>
                                ${this.escapeHtml(
                                    report.description ||
                                    "Traffic report"
                                )}
                            </h4>

                        </div>

                        <span>
                            ${this.escapeHtml(
                                report.status || "PENDING"
                            )}
                        </span>

                    </div>

                    <div class="report-card-details">

                        <div>
                            <span>ROAD</span>
                            <strong>
                                ${this.escapeHtml(
                                    report.road?.name || "Not specified"
                                )}
                            </strong>
                        </div>

                        <div>
                            <span>LOCATION</span>
                            <strong>
                                ${this.escapeHtml(
                                    report.location?.name || "Not specified"
                                )}
                            </strong>
                        </div>

                        <div>
                            <span>REPORTED</span>
                            <strong>
                                ${this.escapeHtml(
                                    this.formatDateTime(report.reportedAt)
                                )}
                            </strong>
                        </div>

                    </div>

                </article>
            `).join("");

    },


    showCitizenReportMessage(message, type = "info") {

        const element =
            document.getElementById("report-analysis-message");

        if (!element) return;

        element.textContent = message;

        element.className = `analysis-message ${type}`;

        element.style.display = "block";

    },


    setupCityPulseAiControls() {

        const form =
            document.getElementById("ai-question-form");

        const questionInput =
            document.getElementById("ai-question");

        const quickQuestionButtons =
            document.querySelectorAll(".ai-question-button");


        if (form) {

            form.addEventListener(
                "submit",
                event => {

                    event.preventDefault();

                    this.askCityPulseAi();

                }
            );

        }


        quickQuestionButtons.forEach(button => {

            button.addEventListener("click", () => {

                const question =
                    button.dataset.question || "";

                if (!question) return;

                if (questionInput) questionInput.value = question;

                this.askCityPulseAi(question);

            });

        });

    },


    loadCityPulseAiPage() {

        const input = document.getElementById("ai-question");

        const chatContainer =
            document.getElementById("ai-chat-container");


        if (
            chatContainer &&
            chatContainer.children.length === 0
        ) {

            const welcome = document.createElement("div");

            welcome.id = "ai-welcome-message";

            welcome.className = "empty-state";

            welcome.innerHTML = `
                <h4>Welcome to CityPulse AI</h4>
                <p>
                    Ask about traffic conditions,
                    congestion predictions, weather
                    impact, alerts or citizen reports.
                </p>
            `;

            chatContainer.appendChild(welcome);

        }

        if (input) input.focus();

    },


    async askCityPulseAi(questionOverride = null) {

        const input = document.getElementById("ai-question");

        const button = document.getElementById("ask-ai-button");

        const container = document.getElementById("ai-chat-container");

        const welcome = document.getElementById("ai-welcome-message");


        if (!container) return;


        const question =
            questionOverride !== null
                ? String(questionOverride).trim()
                : (input ? input.value.trim() : "");


        if (!question) {

            this.showAiMessage("Please enter a question.", "error");

            return;

        }

        if (question.length > 500) {

            this.showAiMessage(
                "Your question is too long. Please keep it within 500 characters.",
                "error"
            );

            return;

        }


        if (button) {
            button.disabled = true;
            button.textContent = "Thinking...";
        }

        this.showAiMessage("", "info", false);


        if (welcome) welcome.style.display = "none";

        this.appendAiMessage(question, "user");

        if (input) input.value = "";


        try {

            const response =
                await this.apiRequest(
                    "/api/ai/ask",
                    {
                        method: "POST",
                        body: { question }
                    }
                );


            if (!response || !response.answer) {

                throw new Error(
                    "CityPulse AI returned an empty response."
                );

            }


            this.appendAiMessage(
                response.answer,
                "assistant",
                response
            );

            this.showAiMessage(
                "CityPulse AI response generated using CityPulse data.",
                "success"
            );

        } catch (error) {

            console.error("CityPulse AI request failed:", error);

            this.appendAiMessage(
                "Unable to get a response from CityPulse AI. Please try again.",
                "assistant"
            );

            this.showAiMessage(
                error.message ||
                "CityPulse AI request failed.",
                "error"
            );

        } finally {

            if (button) {
                button.disabled = false;
                button.textContent = "Ask CityPulse AI";
            }

            if (input) input.focus();

        }

    },


    appendAiMessage(message, sender, responseData = null) {

        const container =
            document.getElementById("ai-chat-container");

        if (!container) return;


        const messageElement = document.createElement("div");

        messageElement.className = `ai-message ai-message-${sender}`;


        const label = sender === "user" ? "YOU" : "CITYPULSE AI";


        let metadata = "";


        if (sender === "assistant" && responseData) {

            const dataSource =
                responseData.dataSource || "citypulse_data";

            const responseType =
                responseData.responseType || "CITYPULSE";


            metadata = `
                <div class="ai-message-source">
                    ${this.escapeHtml(
                        String(responseType).toUpperCase()
                    )}
                    &nbsp;•&nbsp;
                    ${this.escapeHtml(String(dataSource))}
                </div>
            `;

        }


        messageElement.innerHTML = `
            <div class="ai-message-label">
                ${label}
            </div>

            <div class="ai-message-content">
                ${this.formatAiAnswer(message)}
            </div>

            ${metadata}
        `;


        container.appendChild(messageElement);

        container.scrollTop = container.scrollHeight;

    },


    formatAiAnswer(message) {

        if (message === null || message === undefined) return "";

        return this.escapeHtml(String(message))
            .replace(/\r?\n/g, "<br>");

    },


    showAiMessage(message, type = "info", visible = true) {

        const element =
            document.getElementById("ai-analysis-message");

        if (!element) return;

        element.textContent = message || "";

        element.className = `analysis-message ${type}`;

        element.style.display = visible ? "block" : "none";

    },


    setupLanguageControls() {

        const selector =
            document.getElementById("language-select");

        if (!selector) return;


        const stored =
            localStorage.getItem("citypulse.language");

        const initial =
            (
                window.CityPulseTranslations &&
                window.CityPulseTranslations.supportedLanguages &&
                window.CityPulseTranslations.supportedLanguages.includes(stored)
            )
                ? stored
                : "en";


        selector.value = initial;

        this.applyTranslations(initial);

        this.applyDirection(initial);

        this.currentLanguage = initial;


        selector.addEventListener("change", () => {

            const lang = selector.value;

            localStorage.setItem(
                "citypulse.language",
                lang
            );

            this.currentLanguage = lang;

            this.applyTranslations(lang);

            this.applyDirection(lang);

        });

    },


    applyTranslations(language) {

        if (
            !window.CityPulseTranslations ||
            typeof window.CityPulseTranslations.get !== "function"
        ) {

            return;

        }


        const elements =
            document.querySelectorAll("[data-i18n]");


        elements.forEach(element => {

            const key = element.dataset.i18n;

            const translated =
                window.CityPulseTranslations.get(
                    language,
                    key
                );

            if (translated) {
                element.textContent = translated;
            }

        });


        const pageTitleMap = {

            dashboard: "header.title",
            traffic: "traffic.title",
            prediction: "prediction.title",
            routes: "routes.title",
            emergency: "emergency.title",
            alerts: "alerts.title",
            analytics: "analytics.title",
            reports: "reports.title",
            assistant: "assistant.title",
            global: "global.title"

        };


        const currentSection =
            this.currentSection || "dashboard";

        const titleKey =
            pageTitleMap[currentSection];


        const pageTitle =
            document.getElementById("page-title");

        if (pageTitle && titleKey) {

            const translated =
                window.CityPulseTranslations.get(
                    language,
                    titleKey
                );

            if (translated) {
                pageTitle.textContent = translated;
            }

        }

    },


    applyDirection(language) {

        const html =
            document.documentElement;

        if (!html) return;

        if (language === "ar") {

            html.setAttribute("dir", "rtl");
            html.setAttribute("lang", "ar");

        } else {

            html.setAttribute("dir", "ltr");
            html.setAttribute("lang", language || "en");

        }

    },


    setupGlobalWorldMapControls() {

        const countrySelect =
            document.getElementById("global-country-select");

        const searchInput =
            document.getElementById("global-country-search");

        const regionFilter =
            document.getElementById("global-region-filter");

        const resetButton =
            document.getElementById("global-reset-view-button");

        const compareButton =
            document.getElementById("global-compare-button");


        if (countrySelect) {

            countrySelect.addEventListener("change", () => {

                const code = countrySelect.value;

                if (code) {
                    this.handleGlobalCountrySelection(code);
                } else {
                    this.resetGlobalCountrySelection();
                }

            });

        }


        if (searchInput) {

            searchInput.addEventListener("input", () => {

                this.applyGlobalFilters();

            });

        }


        if (regionFilter) {

            regionFilter.addEventListener("change", () => {

                this.applyGlobalFilters();

            });

        }


        if (resetButton) {

            resetButton.addEventListener(
                "click",
                () => this.resetGlobalMapView()
            );

        }


        if (compareButton) {

            compareButton.addEventListener(
                "click",
                () => this.handleGlobalComparison()
            );

        }

    },


    async loadGlobalWorldMapPage() {

        try {

            this.showGlobalMessage(
                "Loading global coverage...",
                "info"
            );


            if (this.countries.length === 0) {
                await this.loadGlobalCountries();
            }


            this.filteredCountries = this.countries.slice();


            this.populateGlobalCountrySelect();

            this.populateGlobalCompareSelects();

            this.initializeGlobalMap();

            this.setElementText(
                "global-countries-count",
                this.countries.length
            );

            this.setElementText(
                "global-coverage-status",
                "READY"
            );

            this.showGlobalMessage(
                `${this.countries.length} countries loaded successfully.`,
                "success"
            );

        } catch (error) {

            console.error("Global world map loading failed:", error);

            this.setElementText(
                "global-coverage-status",
                "ERROR"
            );

            this.showGlobalMessage(
                "Unable to load Global World Map data.",
                "error"
            );

        }

    },


    async loadGlobalCountries() {

        const data =
            await this.apiRequest("/api/global/countries");

        this.countries = Array.isArray(data) ? data : [];

        return this.countries;

    },


    countryCodeToFlagImage(countryCode) {

        if (!countryCode) return "";

        const code =
            String(countryCode).trim().toLowerCase();

        if (code.length !== 2) return "";

        return `<img src="https://flagcdn.com/w40/${code}.png"
                     srcset="https://flagcdn.com/w80/${code}.png 2x"
                     alt="${code.toUpperCase()}"
                     width="32"
                     height="24"
                     style="display: inline-block;
                            vertical-align: middle;
                            margin-right: 8px;
                            border-radius: 3px;
                            box-shadow: 0 0 0 1px rgba(148, 163, 184, 0.4);
                            background: #e2e8f0;"
                     loading="lazy">`;

    },


    countryCodeToFlagEmoji(countryCode) {

        if (!countryCode) return "";

        const code =
            String(countryCode).trim().toUpperCase();

        if (code.length !== 2) return "";

        const codePoints = code
            .split("")
            .map(char => 127397 + char.charCodeAt(0));

        try {

            return String.fromCodePoint(...codePoints);

        } catch (error) {

            return "";

        }

    },


    populateGlobalCountrySelect() {

        const select =
            document.getElementById("global-country-select");

        if (!select) return;


        const current = select.value;

        select.innerHTML = `
            <option value="">
                Select a country
            </option>
        `;


        this.filteredCountries.forEach(country => {

            if (!country || !country.code) return;

            const option = document.createElement("option");

            option.value = String(country.code);

            const flag =
                this.countryCodeToFlagEmoji(country.code);

            option.textContent =
                `${flag} ${country.name || country.code}`.trim();

            select.appendChild(option);

        });


        if (
            current &&
            this.filteredCountries.some(
                country =>
                    String(country.code) === String(current)
            )
        ) {

            select.value = current;

        } else {

            select.value = "";

        }

    },


    populateGlobalCompareSelects() {

        const selectA =
            document.getElementById("global-compare-a");

        const selectB =
            document.getElementById("global-compare-b");


        if (selectA) {

            selectA.innerHTML = `
                <option value="">
                    Select Country A
                </option>
            `;

            this.countries.forEach(country => {

                if (!country || !country.code) return;

                const option = document.createElement("option");

                option.value = String(country.code);

                const flag =
                    this.countryCodeToFlagEmoji(country.code);

                option.textContent =
                    `${flag} ${country.name || country.code}`.trim();

                selectA.appendChild(option);

            });

        }


        if (selectB) {

            selectB.innerHTML = `
                <option value="">
                    Select Country B
                </option>
            `;

            this.countries.forEach(country => {

                if (!country || !country.code) return;

                const option = document.createElement("option");

                option.value = String(country.code);

                const flag =
                    this.countryCodeToFlagEmoji(country.code);

                option.textContent =
                    `${flag} ${country.name || country.code}`.trim();

                selectB.appendChild(option);

            });

        }

    },


    applyGlobalFilters() {

        const searchInput =
            document.getElementById("global-country-search");

        const regionFilter =
            document.getElementById("global-region-filter");


        const query =
            searchInput
                ? String(searchInput.value).trim().toLowerCase()
                : "";

        const region =
            regionFilter
                ? String(regionFilter.value).trim().toLowerCase()
                : "";


        this.filteredCountries =
            this.countries.filter(country => {

                const name =
                    String(country.name || "").toLowerCase();

                const code =
                    String(country.code || "").toLowerCase();

                const countryRegion =
                    String(country.region || "").toLowerCase();


                const matchesSearch =
                    !query ||
                    name.includes(query) ||
                    code.includes(query) ||
                    countryRegion.includes(query);


                const matchesRegion =
                    !region ||
                    countryRegion === region;


                return matchesSearch && matchesRegion;

            });


        this.populateGlobalCountrySelect();


        const statusElement =
            document.getElementById("global-country-search-status");


        if (statusElement) {

            const hasFilter = query || region;

            if (!hasFilter) {

                statusElement.textContent = "";

            } else {

                const parts = [];

                if (region) {

                    const readableRegion =
                        regionFilter.options[regionFilter.selectedIndex].text;

                    parts.push(`Region: ${readableRegion}`);

                }

                if (query) {

                    parts.push(`Search: "${searchInput.value.trim()}"`);

                }


                statusElement.textContent =
                    `${parts.join(" • ")} • Showing ${this.filteredCountries.length} of ${this.countries.length} countries`;

            }

        }


        if (this.filteredCountries.length === 0) {

            this.showGlobalMessage(
                "No countries match the current filters.",
                "info"
            );

        } else {

            this.showGlobalMessage(
                `${this.filteredCountries.length} country match(es) found.`,
                "success"
            );

        }

    },


    initializeGlobalMap() {

        const mapElement =
            document.getElementById("global-map");

        if (!mapElement) return;

        if (typeof L === "undefined") {
            console.warn("Leaflet is not available.");
            return;
        }

        if (this.globalMap) {

            this.globalMap.invalidateSize();

            return;

        }


        this.globalMap =
            L.map(
                mapElement,
                {
                    worldCopyJump: true,
                    minZoom: 2,
                    maxZoom: 12
                }
            ).setView([20, 0], 2);


        L.tileLayer(
            "https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}",
            {
                maxZoom: 19,
                attribution:
                    "Tiles &copy; Esri &mdash; Source: Esri, DeLorme, NAVTEQ, TomTom, Intermap, iPC, USGS, FAO, NPS, NRCAN, GeoBase, Kadaster NL, Ordnance Survey, Esri Japan, METI, Esri China (Hong Kong), and the GIS User Community"
            }
        ).addTo(this.globalMap);

    },


    async handleGlobalCountrySelection(countryCode) {

        const code =
            String(countryCode).trim().toUpperCase();

        if (!code) return;


        const country =
            this.countries.find(
                item => String(item.code).toUpperCase() === code
            );


        if (!country) {

            this.showGlobalMessage(
                "Selected country is not available.",
                "error"
            );

            return;

        }


        this.globalSelectedCountryCode = code;


        this.setElementText(
            "global-selected-country",
            country.name || code
        );

        this.setElementText(
            "global-selected-code",
            code
        );


        this.renderGlobalCountryProfile(country);

        this.loadCountryTrafficSummary(code);

        this.startCountryClock(country);


        const latitude =
            country.latitude !== null &&
            country.latitude !== undefined
                ? Number(country.latitude)
                : null;

        const longitude =
            country.longitude !== null &&
            country.longitude !== undefined
                ? Number(country.longitude)
                : null;


        if (
            latitude === null ||
            longitude === null ||
            !Number.isFinite(latitude) ||
            !Number.isFinite(longitude)
        ) {

            this.showGlobalMessage(
                `${country.name} coordinates are not available in CityPulse.`,
                "error"
            );

            return;

        }


        if (!this.globalMap) return;


        if (this.globalCountryMarker) {
            this.globalMap.removeLayer(this.globalCountryMarker);
            this.globalCountryMarker = null;
        }

        if (this.globalCountryCircle) {
            this.globalMap.removeLayer(this.globalCountryCircle);
            this.globalCountryCircle = null;
        }


        this.clearGlobalCityMarkers();


        this.globalCountryCircle =
            L.circle(
                [latitude, longitude],
                {
                    radius: 350000,
                    color: "#38bdf8",
                    weight: 2,
                    fillColor: "#0ea5e9",
                    fillOpacity: 0.25
                }
            ).addTo(this.globalMap);


        const capital = country.capital || "N/A";
        const region = country.region || "N/A";
        const flagImage =
            this.countryCodeToFlagImage(country.code);


        this.globalCountryMarker =
            L.marker([latitude, longitude])
                .bindPopup(
                    `<div style="display: flex; align-items: center; gap: 8px;">
                        ${flagImage}
                        <strong>${this.escapeHtml(country.name)}</strong>
                     </div>
                     <br>Capital: ${this.escapeHtml(capital)}
                     <br>Region: ${this.escapeHtml(region)}
                     <br>Code: ${this.escapeHtml(code)}
                     <br>Coordinates: ${latitude.toFixed(4)}, ${longitude.toFixed(4)}`
                )
                .addTo(this.globalMap);


        this.globalCountryMarker.openPopup();


        const zoomLevel =
            this.calculateZoomForCountry(country);


        this.globalMap.flyTo(
            [latitude, longitude],
            zoomLevel,
            { duration: 1.2 }
        );


        this.loadGlobalCityMarkers(code);


        this.showGlobalMessage(
            `${country.name} focused successfully.`,
            "success"
        );

    },


    async loadCountryTrafficSummary(countryCode) {

        const panel =
            document.getElementById("global-country-traffic-panel");

        if (!panel) return;


        try {

            const summary =
                await this.apiRequest(
                    `/api/global/countries/${countryCode}/traffic-summary`
                );


            this.renderCountryTrafficSummary(summary);

            panel.style.display = "block";

        } catch (error) {

            console.error(
                "Country traffic summary loading failed:",
                error
            );

            panel.style.display = "none";

        }

    },


    renderCountryTrafficSummary(summary) {

        if (!summary) return;


        const flagImage =
            this.countryCodeToFlagImage(summary.code);


        const titleElement =
            document.getElementById("global-traffic-title");

        if (titleElement) {

            titleElement.innerHTML =
                `<span style="display: inline-flex; align-items: center; gap: 8px;">${flagImage}<span>${this.escapeHtml(summary.countryName || "Traffic Summary")}</span></span>`;

        }


        const riskBadge =
            document.getElementById("global-traffic-risk-badge");

        if (riskBadge) {

            riskBadge.textContent =
                summary.congestionRiskLevel || "—";

        }


        this.setElementText(
            "global-traffic-cities",
            this.formatNumber(summary.totalCities)
        );


        this.setElementText(
            "global-traffic-risk",
            this.formatNumber(summary.congestionRisk, 2)
        );


        this.setElementText(
            "global-traffic-risk-level",
            summary.congestionRiskLevel || "N/A"
        );


        this.setElementText(
            "global-traffic-region",
            summary.region || "N/A"
        );


        this.setElementText(
            "global-traffic-peak-hours",
            summary.estimatedPeakHours || "N/A"
        );


        this.setElementText(
            "global-traffic-source",
            summary.dataSource || "N/A"
        );


        this.setElementText(
            "global-traffic-status",
            summary.dataStatus || "N/A"
        );


        this.setElementText(
            "global-traffic-peak-direction",
            summary.estimatedPeakDirection || "N/A"
        );


        this.setElementText(
            "global-traffic-api-status",
            summary.realTrafficApiStatus || "N/A"
        );

    },


    startCountryClock(country) {

        this.stopCountryClock();


        if (!country || !country.timezone) {

            this.setElementText("global-clock-country", "—");
            this.setElementText("global-clock-time", "—");
            this.setElementText("global-clock-day", "—");
            this.setElementText("global-clock-date", "—");
            this.setElementText("global-clock-zone", "—");
            this.setElementText(
                "global-clock-status",
                "Timezone not available for this country."
            );

            return;

        }


        const tz =
            String(country.timezone).trim();


        const flagImage =
            this.countryCodeToFlagImage(country.code);


        const clockCountryElement =
            document.getElementById("global-clock-country");

        if (clockCountryElement) {

            clockCountryElement.innerHTML =
                `<span style="display: inline-flex; align-items: center; gap: 8px;">${flagImage}<span>${this.escapeHtml(country.name || country.code)}</span></span>`;

        }


        this.setElementText(
            "global-clock-zone",
            `Time Zone: ${tz}`
        );


        this.setElementText(
            "global-clock-status",
            "Live local time • updates every second"
        );


        this.updateCountryClock(tz);


        this.globalClockTimer =
            setInterval(
                () => this.updateCountryClock(tz),
                1000
            );

    },


    stopCountryClock() {

        if (this.globalClockTimer) {

            clearInterval(this.globalClockTimer);

            this.globalClockTimer = null;

        }

    },


    updateCountryClock(tz) {

        if (!tz) return;


        try {

            const now = new Date();


            const timeFormatter =
                new Intl.DateTimeFormat(
                    "en-GB",
                    {
                        timeZone: tz,
                        hour: "2-digit",
                        minute: "2-digit",
                        second: "2-digit",
                        hour12: true
                    }
                );


            const dayFormatter =
                new Intl.DateTimeFormat(
                    "en-GB",
                    {
                        timeZone: tz,
                        weekday: "long"
                    }
                );


            const dateFormatter =
                new Intl.DateTimeFormat(
                    "en-GB",
                    {
                        timeZone: tz,
                        day: "2-digit",
                        month: "long",
                        year: "numeric"
                    }
                );


            this.setElementText(
                "global-clock-time",
                timeFormatter.format(now)
            );


            this.setElementText(
                "global-clock-day",
                dayFormatter.format(now)
            );


            this.setElementText(
                "global-clock-date",
                dateFormatter.format(now)
            );

        } catch (error) {

            console.warn("World clock update failed:", error);

            this.setElementText(
                "global-clock-status",
                "Unable to render time for this timezone."
            );

        }

    },


    async loadGlobalCityMarkers(countryCode) {

        if (!this.globalMap) return;

        try {

            const cities =
                await this.apiRequest(
                    `/api/global/countries/${countryCode}/cities`
                );

            if (!Array.isArray(cities) || cities.length === 0) {

                this.showGlobalMessage(
                    `No city data is available for ${countryCode} in CityPulse.`,
                    "info"
                );

                return;

            }


            this.clearGlobalCityMarkers();


            cities.forEach(city => {

                const isCapital = city.capital === true;

                const cityMarker =
                    L.circleMarker(
                        [
                            Number(city.latitude),
                            Number(city.longitude)
                        ],
                        {
                            radius: isCapital ? 9 : 6,
                            color: isCapital
                                ? "#facc15"
                                : "#38bdf8",
                            fillColor: isCapital
                                ? "#fbbf24"
                                : "#0ea5e9",
                            weight: 2,
                            opacity: 1,
                            fillOpacity: 0.9
                        }
                    );


                const populationLabel =
                    city.population
                        ? Number(city.population).toLocaleString("en-IN")
                        : "N/A";


                cityMarker.bindPopup(
                    `<strong>${isCapital ? "⭐ " : ""}${this.escapeHtml(city.name)}</strong>
                     <br>${isCapital ? "Capital City" : "Major City"}
                     <br>Population: ${populationLabel}`
                );


                cityMarker.addTo(this.globalMap);

                this.globalCityMarkers.push(cityMarker);

            });


            this.showGlobalMessage(
                `${cities.length} cities loaded for ${countryCode}.`,
                "success"
            );

        } catch (error) {

            console.error(
                "City markers loading failed:",
                error
            );

        }

    },


    clearGlobalCityMarkers() {

        if (!this.globalMap) {

            this.globalCityMarkers = [];

            return;

        }

        this.globalCityMarkers.forEach(
            marker => this.globalMap.removeLayer(marker)
        );

        this.globalCityMarkers = [];

    },


    renderGlobalCountryProfile(country) {

        const panel =
            document.getElementById("global-country-profile-panel");

        if (!panel) return;


        const population = country.population
            ? Number(country.population).toLocaleString("en-IN")
            : "N/A";

        const flagImage =
            this.countryCodeToFlagImage(country.code);


        const titleElement =
            document.getElementById("global-profile-title");

        if (titleElement) {

            titleElement.innerHTML =
                `<span style="display: inline-flex; align-items: center; gap: 8px;">${flagImage}<span>${this.escapeHtml(country.name || "Country Profile")}</span></span>`;

        }


        const nameElement =
            document.getElementById("global-profile-name");

        if (nameElement) {

            nameElement.innerHTML =
                `<span style="display: inline-flex; align-items: center; gap: 8px;">${flagImage}<span>${this.escapeHtml(country.name || "—")}</span></span>`;

        }


        this.setElementText(
            "global-profile-region-badge",
            country.region || "—"
        );

        this.setElementText(
            "global-profile-code",
            country.code || "—"
        );

        this.setElementText(
            "global-profile-capital",
            country.capital || "N/A"
        );

        this.setElementText(
            "global-profile-region",
            country.region || "N/A"
        );

        this.setElementText(
            "global-profile-population",
            population
        );

        this.setElementText(
            "global-profile-currency",
            country.currency || "N/A"
        );

        this.setElementText(
            "global-profile-timezone",
            country.timezone || "N/A"
        );


        const latitude =
            country.latitude !== null &&
            country.latitude !== undefined
                ? Number(country.latitude).toFixed(4)
                : "N/A";

        const longitude =
            country.longitude !== null &&
            country.longitude !== undefined
                ? Number(country.longitude).toFixed(4)
                : "N/A";


        this.setElementText(
            "global-profile-coordinates",
            `Latitude: ${latitude} • Longitude: ${longitude}`
        );


        panel.style.display = "block";

    },


    hideGlobalCountryProfile() {

        const panel =
            document.getElementById("global-country-profile-panel");

        if (panel) panel.style.display = "none";

    },


    hideGlobalTrafficPanel() {

        const panel =
            document.getElementById("global-country-traffic-panel");

        if (panel) panel.style.display = "none";

    },


    calculateZoomForCountry(country) {

        const region =
            String(country.region || "").toLowerCase();


        if (region.includes("asia")) return 4;
        if (region.includes("europe")) return 5;
        if (region.includes("americas")) return 4;
        if (region.includes("africa")) return 4;
        if (region.includes("oceania")) return 4;
        if (region.includes("middle")) return 5;

        return 4;

    },


    handleGlobalComparison() {

        const selectA =
            document.getElementById("global-compare-a");

        const selectB =
            document.getElementById("global-compare-b");


        const codeA =
            selectA ? String(selectA.value).trim().toUpperCase() : "";

        const codeB =
            selectB ? String(selectB.value).trim().toUpperCase() : "";


        if (!codeA || !codeB) {

            this.showGlobalCompareStatus(
                "Please select both Country A and Country B.",
                "error"
            );

            return;

        }

        if (codeA === codeB) {

            this.showGlobalCompareStatus(
                "Please select two different countries.",
                "error"
            );

            return;

        }


        const countryA =
            this.countries.find(
                item => String(item.code).toUpperCase() === codeA
            );

        const countryB =
            this.countries.find(
                item => String(item.code).toUpperCase() === codeB
            );


        if (!countryA || !countryB) {

            this.showGlobalCompareStatus(
                "One of the selected countries is not available.",
                "error"
            );

            return;

        }


        this.renderGlobalComparison(countryA, countryB);

        this.showGlobalCompareStatus(
            `Comparison generated for ${countryA.name} and ${countryB.name}.`,
            "success"
        );

    },


    renderGlobalComparison(countryA, countryB) {

        const container =
            document.getElementById("global-compare-results");

        if (!container) return;


        const formatPop = value =>
            value
                ? Number(value).toLocaleString("en-IN")
                : "N/A";


        const populationA = countryA.population
            ? Number(countryA.population)
            : null;

        const populationB = countryB.population
            ? Number(countryB.population)
            : null;


        let largerPopulation = null;

        if (
            populationA !== null &&
            populationB !== null
        ) {

            if (populationA > populationB) largerPopulation = "A";
            else if (populationB > populationA) largerPopulation = "B";

        }


        const flagImageA =
            this.countryCodeToFlagImage(countryA.code);

        const flagImageB =
            this.countryCodeToFlagImage(countryB.code);


        const row = (label, valueA, valueB, highlight = null) => {

            const styleA =
                highlight === "A"
                    ? `style="color: #22c55e; font-weight: 700;"`
                    : "";

            const styleB =
                highlight === "B"
                    ? `style="color: #22c55e; font-weight: 700;"`
                    : "";

            return `
                <div class="route-result-grid"
                     style="grid-template-columns: 1fr 1fr 1fr; gap: 12px; margin-top: 12px;">

                    <div>
                        <span>${label}</span>
                    </div>

                    <div>
                        <strong ${styleA}>
                            ${valueA}${highlight === "A" ? " ✅" : ""}
                        </strong>
                    </div>

                    <div>
                        <strong ${styleB}>
                            ${valueB}${highlight === "B" ? " ✅" : ""}
                        </strong>
                    </div>

                </div>
            `;

        };


        container.innerHTML = `

            <div class="route-result-grid"
                 style="grid-template-columns: 1fr 1fr 1fr; gap: 12px; margin-bottom: 8px;">

                <div>
                    <span class="section-label">FIELD</span>
                </div>

                <div>
                    <span class="section-label" style="display: inline-flex; align-items: center; gap: 8px;">${flagImageA}<span>${this.escapeHtml(countryA.name)}</span></span>
                </div>

                <div>
                    <span class="section-label" style="display: inline-flex; align-items: center; gap: 8px;">${flagImageB}<span>${this.escapeHtml(countryB.name)}</span></span>
                </div>

            </div>

            ${row("Name", `<span style="display: inline-flex; align-items: center; gap: 6px;">${flagImageA}<span>${this.escapeHtml(countryA.name)}</span></span>`, `<span style="display: inline-flex; align-items: center; gap: 6px;">${flagImageB}<span>${this.escapeHtml(countryB.name)}</span></span>`)}

            ${row("ISO Code", this.escapeHtml(countryA.code), this.escapeHtml(countryB.code))}

            ${row("Capital", this.escapeHtml(countryA.capital || "N/A"), this.escapeHtml(countryB.capital || "N/A"))}

            ${row("Region", this.escapeHtml(countryA.region || "N/A"), this.escapeHtml(countryB.region || "N/A"))}

            ${row("Population", formatPop(countryA.population), formatPop(countryB.population), largerPopulation)}

            ${row("Currency", this.escapeHtml(countryA.currency || "N/A"), this.escapeHtml(countryB.currency || "N/A"))}

            ${row("Time Zone", this.escapeHtml(countryA.timezone || "N/A"), this.escapeHtml(countryB.timezone || "N/A"))}

        `;

    },


    showGlobalCompareStatus(message, type = "info") {

        const element =
            document.getElementById("global-compare-status");

        if (!element) return;

        element.textContent = message;

        element.className = `analysis-message ${type}`;

        element.style.display = "block";

    },


    resetGlobalCountrySelection() {

        this.globalSelectedCountryCode = null;

        this.setElementText("global-selected-country", "—");

        this.setElementText("global-selected-code", "—");


        this.hideGlobalCountryProfile();

        this.hideGlobalTrafficPanel();

        this.clearGlobalCityMarkers();

        this.stopCountryClock();


        this.setElementText("global-clock-country", "—");
        this.setElementText("global-clock-time", "—");
        this.setElementText("global-clock-day", "—");
        this.setElementText("global-clock-date", "—");
        this.setElementText("global-clock-zone", "—");
        this.setElementText("global-clock-status", "");


        if (this.globalMap) {

            if (this.globalCountryMarker) {
                this.globalMap.removeLayer(this.globalCountryMarker);
                this.globalCountryMarker = null;
            }

            if (this.globalCountryCircle) {
                this.globalMap.removeLayer(this.globalCountryCircle);
                this.globalCountryCircle = null;
            }

        }

    },


    resetGlobalMapView() {

        const select =
            document.getElementById("global-country-select");

        if (select) select.value = "";

        const searchInput =
            document.getElementById("global-country-search");

        if (searchInput) searchInput.value = "";

        const regionFilter =
            document.getElementById("global-region-filter");

        if (regionFilter) regionFilter.value = "";

        const statusElement =
            document.getElementById("global-country-search-status");

        if (statusElement) statusElement.textContent = "";

        const selectA =
            document.getElementById("global-compare-a");

        if (selectA) selectA.value = "";

        const selectB =
            document.getElementById("global-compare-b");

        if (selectB) selectB.value = "";

        const compareResults =
            document.getElementById("global-compare-results");

        if (compareResults) compareResults.innerHTML = "";

        const compareStatus =
            document.getElementById("global-compare-status");

        if (compareStatus) {

            compareStatus.textContent = "";

            compareStatus.style.display = "none";

        }


        this.filteredCountries = this.countries.slice();

        this.populateGlobalCountrySelect();


        this.resetGlobalCountrySelection();


        if (this.globalMap) {

            this.globalMap.flyTo(
                [20, 0],
                2,
                { duration: 1.2 }
            );

        }


        this.showGlobalMessage(
            "Global map view reset.",
            "info"
        );

    },


    showGlobalMessage(message, type = "info") {

        const element =
            document.getElementById("global-analysis-message");

        if (!element) return;

        element.textContent = message;

        element.className = `analysis-message ${type}`;

        element.style.display = "block";

    },


    findRoad(roadId) {

        return (this.roads || []).find(
            road => Number(road.id) === Number(roadId)
        ) || null;

    },


    findLocation(locationId) {

        return (this.locations || []).find(
            location => Number(location.id) === Number(locationId)
        ) || null;

    },


    getTrafficLevelClass(level) {

        switch (String(level || "").toUpperCase()) {

            case "LOW":
                return "traffic-low";

            case "MODERATE":
                return "traffic-moderate";

            case "HIGH":
                return "traffic-high";

            case "SEVERE":
                return "traffic-severe";

            default:
                return "";

        }

    },


    formatNumber(value, decimals = 0) {

        if (value === null || value === undefined || value === "") {
            return "N/A";
        }

        const number = Number(value);

        if (!Number.isFinite(number)) return "N/A";

        return number.toLocaleString(
            "en-IN",
            {
                minimumFractionDigits: decimals,
                maximumFractionDigits: decimals
            }
        );

    },


    formatDateTime(value) {

        if (!value) return "N/A";

        const date = new Date(value);

        if (Number.isNaN(date.getTime())) return String(value);

        return date.toLocaleString(
            "en-IN",
            {
                dateStyle: "medium",
                timeStyle: "short"
            }
        );

    },


    escapeHtml(value) {

        if (value === null || value === undefined) return "";

        return String(value)
            .replace(/&/g, "&amp;")
            .replace(/</g, "&lt;")
            .replace(/>/g, "&gt;")
            .replace(/"/g, "&quot;")
            .replace(/'/g, "&#039;");

    },


    setElementText(id, value) {

        const element = document.getElementById(id);

        if (element) {

            element.textContent =
                value === null || value === undefined
                    ? "N/A"
                    : value;

        }

    },


    updateApiConnectionStatus(connected) {

        const statusElement =
            document.getElementById("api-status");

        if (!statusElement) return;

        statusElement.textContent =
            connected ? "CONNECTED" : "DISCONNECTED";

        statusElement.className =
            connected
                ? "status-connected"
                : "status-disconnected";

    },


    showDashboardError() {

        const container =
            document.getElementById("traffic-overview");

        if (!container) return;

        container.innerHTML = `
            <div class="empty-state">
                Unable to load CityPulse traffic data.
            </div>
        `;

    },


    renderTrafficPageError() {

        const tableBody =
            document.getElementById("traffic-table-body");

        if (tableBody) {

            tableBody.innerHTML = `
                <tr>
                    <td colspan="7">
                        Unable to load live traffic data.
                    </td>
                </tr>
            `;

        }

    }

};


/* ============================================
   START APPLICATION
============================================ */

document.addEventListener(
    "DOMContentLoaded",
    () => {

        CityPulseApp.init();

    }
);


/* ============================================
   CITYPULSE LIVE WEBSOCKET CLIENT
   Real-time updates via STOMP over WebSocket
============================================ */

(function () {

    // ============================================
    // FIX: STOMP 7 exposes the client as StompJs.Stomp,
    // not as a global Stomp. We alias it here so that
    // the rest of the client uses the classic name.
    // ============================================
    var StompClient =
        (typeof Stomp !== "undefined")
            ? Stomp
            : (typeof StompJs !== "undefined"
                && StompJs !== null
                && typeof StompJs.Stomp !== "undefined")
                ? StompJs.Stomp
                : null;

    let stompClient = null;
    let reconnectAttempts = 0;
    const MAX_RECONNECT_ATTEMPTS = 10;
    const RECONNECT_DELAY_MS = 5000;

    function setLiveIndicator(text, cssClass) {

        const indicator = document.getElementById(
            "traffic-live-indicator");

        if (!indicator) return;

        indicator.textContent = text;

        indicator.classList.remove("live", "offline", "connecting");

        if (cssClass) {
            indicator.classList.add(cssClass);
        }
    }

    function updateDashboardCounters(payload) {

        if (!payload) return;

        const recordsEl = document.getElementById("traffic-records");

        if (recordsEl) {

            const current = parseInt(recordsEl.textContent, 10);

            if (!isNaN(current)) {
                recordsEl.textContent = current + 1;
            }
        }
    }

    function handleTrafficUpdate(message) {

        let envelope;

        try {
            envelope = JSON.parse(message.body);
        } catch (e) {
            return;
        }

        if (!envelope || envelope.event !== "TRAFFIC_UPDATED") {
            return;
        }

        const payload = envelope.payload || {};

        setLiveIndicator("● LIVE — update received", "live");

        updateDashboardCounters(payload);

        const activeSection = document.querySelector(
            ".page-section.active-section");

        if (!activeSection) return;

        const sectionId = activeSection.id;

        try {

            if (sectionId === "traffic-section") {

                if (typeof CityPulseApp.loadLiveTrafficPage === "function") {
                    CityPulseApp.loadLiveTrafficPage();
                }
            }

            if (sectionId === "dashboard-section") {

                if (typeof CityPulseApp.loadDashboard === "function") {
                    CityPulseApp.loadDashboard();
                }
            }

        } catch (e) {
            // Silent — never break UI on live update
        }

        setTimeout(() => {
            setLiveIndicator("● LIVE", "live");
        }, 2500);
    }

    function connect() {

        if (typeof SockJS === "undefined"
                || StompClient === null) {

            setLiveIndicator("● OFFLINE (libraries missing)", "offline");
            return;
        }

        setLiveIndicator("● CONNECTING...", "connecting");

        let socket;

        try {
            socket = new SockJS("/ws-citypulse");
        } catch (e) {
            setLiveIndicator("● OFFLINE", "offline");
            return;
        }

        stompClient = StompClient.over(socket);

        stompClient.debug = null;

        stompClient.connect(
            {},
            function onConnected() {

                reconnectAttempts = 0;

                setLiveIndicator("● LIVE", "live");

                stompClient.subscribe(
                    "/topic/traffic",
                    handleTrafficUpdate
                );

                stompClient.subscribe(
                    "/topic/predictions",
                    function (message) {

                        setLiveIndicator(
                            "● LIVE — prediction received",
                            "live"
                        );

                        setTimeout(() => {
                            setLiveIndicator("● LIVE", "live");
                        }, 2500);
                    }
                );

                stompClient.subscribe(
                    "/topic/alerts",
                    function (message) {

                        setLiveIndicator(
                            "● LIVE — alert received",
                            "live"
                        );

                        setTimeout(() => {
                            setLiveIndicator("● LIVE", "live");
                        }, 2500);
                    }
                );

                stompClient.subscribe(
                    "/topic/reports",
                    function (message) {

                        setLiveIndicator(
                            "● LIVE — report received",
                            "live"
                        );

                        setTimeout(() => {
                            setLiveIndicator("● LIVE", "live");
                        }, 2500);
                    }
                );
            },
            function onError() {

                setLiveIndicator("● OFFLINE — retrying...", "offline");

                attemptReconnect();
            }
        );
    }

    function attemptReconnect() {

        if (reconnectAttempts >= MAX_RECONNECT_ATTEMPTS) {
            setLiveIndicator("● OFFLINE (gave up)", "offline");
            return;
        }

        reconnectAttempts++;

        setTimeout(() => {

            try {
                connect();
            } catch (e) {
                attemptReconnect();
            }

        }, RECONNECT_DELAY_MS);
    }

    function initialize() {

        if (document.readyState === "loading") {

            document.addEventListener(
                "DOMContentLoaded",
                connect
            );

        } else {

            connect();
        }
    }

    initialize();

})();