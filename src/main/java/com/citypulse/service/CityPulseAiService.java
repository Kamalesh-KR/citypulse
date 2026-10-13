package com.citypulse.service;

import com.citypulse.dto.CityPulseAiResponse;
import com.citypulse.model.Alert;
import com.citypulse.model.CitizenReport;
import com.citypulse.model.Location;
import com.citypulse.model.Road;
import com.citypulse.model.TrafficPrediction;
import com.citypulse.model.TrafficRecord;
import com.citypulse.model.WeatherRecord;
import com.citypulse.repository.AlertRepository;
import com.citypulse.repository.CitizenReportRepository;
import com.citypulse.repository.LocationRepository;
import com.citypulse.repository.RoadRepository;
import com.citypulse.repository.TrafficPredictionRepository;
import com.citypulse.repository.TrafficRecordRepository;
import com.citypulse.repository.WeatherRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

@Service
public class CityPulseAiService {

    private final TrafficRecordRepository trafficRecordRepository;
    private final TrafficPredictionRepository trafficPredictionRepository;
    private final AlertRepository alertRepository;
    private final WeatherRecordRepository weatherRecordRepository;
    private final CitizenReportRepository citizenReportRepository;
    private final RoadRepository roadRepository;
    private final LocationRepository locationRepository;

    public CityPulseAiService(
            TrafficRecordRepository trafficRecordRepository,
            TrafficPredictionRepository trafficPredictionRepository,
            AlertRepository alertRepository,
            WeatherRecordRepository weatherRecordRepository,
            CitizenReportRepository citizenReportRepository,
            RoadRepository roadRepository,
            LocationRepository locationRepository) {

        this.trafficRecordRepository = trafficRecordRepository;
        this.trafficPredictionRepository = trafficPredictionRepository;
        this.alertRepository = alertRepository;
        this.weatherRecordRepository = weatherRecordRepository;
        this.citizenReportRepository = citizenReportRepository;
        this.roadRepository = roadRepository;
        this.locationRepository = locationRepository;
    }

    @Transactional(readOnly = true)
    public CityPulseAiResponse processQuestion(String question) {

        String cleanedQuestion = question == null
                ? ""
                : question.trim();

        String normalizedQuestion = cleanedQuestion.toLowerCase(Locale.ROOT);

        if (normalizedQuestion.isBlank()) {
            return response(
                    cleanedQuestion,
                    "Please enter a question about CityPulse traffic, predictions, alerts, weather, roads, locations, or citizen reports.",
                    "citypulse",
                    "HELP");
        }

        if (isGreeting(normalizedQuestion)) {
            return response(
                    cleanedQuestion,
                    "Hello! I am CityPulse AI. I can provide information from the CityPulse system about current traffic, congestion predictions, alerts, weather, roads, locations, and citizen reports.",
                    "citypulse",
                    "GREETING");
        }

        if (isHelpQuestion(normalizedQuestion)) {
            return response(
                    cleanedQuestion,
                    buildHelpAnswer(),
                    "citypulse",
                    "HELP");
        }

        if (containsAny(
                normalizedQuestion,
                "citizen report",
                "citizen reports",
                "reported problem",
                "reported issues",
                "road report",
                "reports")) {

            return answerCitizenReports(cleanedQuestion);
        }

        if (containsAny(
                normalizedQuestion,
                "alert",
                "alerts",
                "warning",
                "warnings")) {

            return answerAlerts(cleanedQuestion);
        }

        if (containsAny(
                normalizedQuestion,
                "weather",
                "rain",
                "rainfall",
                "visibility",
                "wind",
                "temperature")) {

            return answerWeather(cleanedQuestion);
        }

        if (containsAny(
                normalizedQuestion,
                "predict",
                "prediction",
                "predictions",
                "forecast",
                "future traffic",
                "next 15",
                "next 30",
                "next 45",
                "next 60")) {

            return answerPredictions(cleanedQuestion);
        }

        if (containsAny(
                normalizedQuestion,
                "traffic",
                "congestion",
                "congested",
                "vehicle",
                "vehicles",
                "speed",
                "density")) {

            return answerTraffic(cleanedQuestion);
        }

        if (containsAny(
                normalizedQuestion,
                "road",
                "roads",
                "location",
                "locations",
                "junction",
                "junctions")) {

            return answerRoadsAndLocations(cleanedQuestion);
        }

        return response(
                cleanedQuestion,
                buildUnknownQuestionAnswer(),
                "citypulse",
                "UNKNOWN");
    }

    private CityPulseAiResponse answerTraffic(String question) {

        List<TrafficRecord> records =
                trafficRecordRepository.findAllByOrderByRecordedAtDesc();

        if (records.isEmpty()) {
            return response(
                    question,
                    "There are currently no traffic records available in CityPulse.",
                    "traffic_records",
                    "TRAFFIC");
        }

        List<TrafficRecord> latestRecords =
                getLatestTrafficRecords(records);

        StringBuilder answer = new StringBuilder();

        answer.append("Current CityPulse traffic status:\n\n");

        for (TrafficRecord record : latestRecords) {

            String roadName = getRoadName(record);

            String trafficLevel =
                    safeValue(
                            record.getTrafficLevel(),
                            "UNKNOWN");

            String congestionScore =
                    formatNumber(record.getCongestionScore());

            String averageSpeed =
                    formatNumber(record.getAverageSpeed());

            answer.append("- ")
                    .append(roadName)
                    .append(": ")
                    .append(trafficLevel);

            if (congestionScore != null) {
                answer.append(" (congestion score ")
                        .append(congestionScore)
                        .append(")");
            }

            if (averageSpeed != null) {
                answer.append(", ")
                        .append(averageSpeed)
                        .append(" km/h");
            }

            answer.append("\n");
        }

        return response(
                question,
                answer.toString().trim(),
                "traffic_records",
                "TRAFFIC");
    }

    private CityPulseAiResponse answerPredictions(String question) {

        List<TrafficPrediction> predictions =
                trafficPredictionRepository.findAll();

        if (predictions.isEmpty()) {
            return response(
                    question,
                    "There are currently no traffic predictions available in CityPulse.",
                    "traffic_predictions",
                    "PREDICTION");
        }

        List<TrafficPrediction> sortedPredictions =
                predictions.stream()
                        .sorted(
                                Comparator.comparing(
                                        TrafficPrediction::getPredictionTime,
                                        Comparator.nullsLast(
                                                Comparator.naturalOrder())))
                        .toList();

        StringBuilder answer = new StringBuilder();

        answer.append("CityPulse traffic predictions:\n\n");

        int count = 0;

        for (TrafficPrediction prediction : sortedPredictions) {

            if (count >= 8) {
                break;
            }

            String roadName =
                    getPredictionRoadName(prediction);

            String trafficLevel =
                    safeValue(
                            prediction.getPredictedTrafficLevel(),
                            "UNKNOWN");

            String horizon =
                    prediction.getPredictionHorizonMinutes() == null
                            ? null
                            : String.valueOf(
                                    prediction.getPredictionHorizonMinutes());

            String congestionScore =
                    formatNumber(
                            prediction.getPredictedCongestionScore());

            answer.append("- ")
                    .append(roadName)
                    .append(": ");

            if (horizon != null) {
                answer.append("+")
                        .append(horizon)
                        .append(" min -> ");
            }

            answer.append(trafficLevel);

            if (congestionScore != null) {
                answer.append(" (congestion score ")
                        .append(congestionScore)
                        .append(")");
            }

            answer.append("\n");

            count++;
        }

        return response(
                question,
                answer.toString().trim(),
                "traffic_predictions",
                "PREDICTION");
    }

    private CityPulseAiResponse answerAlerts(String question) {

        List<Alert> alerts =
                alertRepository.findAll();

        if (alerts.isEmpty()) {
            return response(
                    question,
                    "There are currently no alerts recorded in CityPulse.",
                    "alerts",
                    "ALERT");
        }

        List<Alert> activeAlerts =
                alerts.stream()
                        .filter(alert ->
                                Boolean.TRUE.equals(
                                        alert.getActive()))
                        .toList();

        List<Alert> recordsToShow =
                activeAlerts.isEmpty()
                        ? alerts
                        : activeAlerts;

        StringBuilder answer = new StringBuilder();

        if (!activeAlerts.isEmpty()) {
            answer.append(
                    "Current active CityPulse alerts:\n\n");
        } else {
            answer.append(
                    "CityPulse alert records:\n\n");
        }

        int count = 0;

        for (Alert alert : recordsToShow) {

            if (count >= 8) {
                break;
            }

            String title =
                    safeValue(
                            alert.getTitle(),
                            "Traffic alert");

            String severity =
                    safeValue(
                            alert.getSeverity(),
                            "UNKNOWN");

            String message =
                    safeValue(
                            alert.getMessage(),
                            "");

            answer.append("- ")
                    .append(title)
                    .append(" [")
                    .append(severity)
                    .append("]");

            if (!message.isBlank()) {
                answer.append(": ")
                        .append(message);
            }

            answer.append("\n");

            count++;
        }

        return response(
                question,
                answer.toString().trim(),
                "alerts",
                "ALERT");
    }

    private CityPulseAiResponse answerWeather(String question) {

        List<WeatherRecord> weatherRecords =
                weatherRecordRepository.findAll();

        if (weatherRecords.isEmpty()) {
            return response(
                    question,
                    "There are currently no weather records available in CityPulse.",
                    "weather_records",
                    "WEATHER");
        }

        List<WeatherRecord> latestWeather =
                getLatestWeatherRecords(weatherRecords);

        StringBuilder answer = new StringBuilder();

        answer.append(
                "Current CityPulse weather information:\n\n");

        int count = 0;

        for (WeatherRecord weather : latestWeather) {

            if (count >= 8) {
                break;
            }

            String locationName =
                    getWeatherLocationName(weather);

            answer.append("- ")
                    .append(locationName);

            if (weather.getWeatherCondition() != null) {
                answer.append(": ")
                        .append(weather.getWeatherCondition());
            }

            if (weather.getTemperature() != null) {
                answer.append(", ")
                        .append(
                                formatNumber(
                                        weather.getTemperature()))
                        .append("°C");
            }

            if (weather.getRainfall() != null) {
                answer.append(", rainfall ")
                        .append(
                                formatNumber(
                                        weather.getRainfall()));
            }

            if (weather.getVisibility() != null) {
                answer.append(", visibility ")
                        .append(
                                formatNumber(
                                        weather.getVisibility()));
            }

            if (weather.getWindSpeed() != null) {
                answer.append(", wind ")
                        .append(
                                formatNumber(
                                        weather.getWindSpeed()));
            }

            answer.append("\n");

            count++;
        }

        return response(
                question,
                answer.toString().trim(),
                "weather_records",
                "WEATHER");
    }

    private CityPulseAiResponse answerCitizenReports(
            String question) {

        List<CitizenReport> reports =
                citizenReportRepository.findAll();

        if (reports.isEmpty()) {
            return response(
                    question,
                    "There are currently no citizen traffic reports in CityPulse.",
                    "citizen_reports",
                    "CITIZEN_REPORT");
        }

        long pendingCount =
                reports.stream()
                        .filter(report ->
                                report.getStatus() != null
                                        && "PENDING".equalsIgnoreCase(
                                        report.getStatus()))
                        .count();

        long resolvedCount =
                reports.stream()
                        .filter(report ->
                                report.getStatus() != null
                                        && "RESOLVED".equalsIgnoreCase(
                                        report.getStatus()))
                        .count();

        StringBuilder answer = new StringBuilder();

        answer.append(
                        "CityPulse citizen reports:\n\n")
                .append("- Total reports: ")
                .append(reports.size())
                .append("\n")
                .append("- Pending reports: ")
                .append(pendingCount)
                .append("\n")
                .append("- Resolved reports: ")
                .append(resolvedCount)
                .append("\n\n");

        int count = 0;

        for (CitizenReport report : reports) {

            if (count >= 5) {
                break;
            }

            answer.append("- ")
                    .append(
                            safeValue(
                                    report.getReportType(),
                                    "Traffic report"));

            if (report.getStatus() != null) {
                answer.append(" [")
                        .append(report.getStatus())
                        .append("]");
            }

            if (report.getDescription() != null
                    && !report.getDescription().isBlank()) {

                answer.append(": ")
                        .append(report.getDescription());
            }

            answer.append("\n");

            count++;
        }

        return response(
                question,
                answer.toString().trim(),
                "citizen_reports",
                "CITIZEN_REPORT");
    }

    private CityPulseAiResponse answerRoadsAndLocations(
            String question) {

        List<Road> roads =
                roadRepository.findAll();

        List<Location> locations =
                locationRepository.findAll();

        StringBuilder answer = new StringBuilder();

        answer.append(
                "CityPulse road and location information:\n\n");

        answer.append("- Roads available: ")
                .append(roads.size())
                .append("\n");

        answer.append("- Locations available: ")
                .append(locations.size())
                .append("\n\n");

        if (!roads.isEmpty()) {

            answer.append("Roads:\n");

            for (Road road : roads) {

                answer.append("- ")
                        .append(
                                safeValue(
                                        road.getName(),
                                        "Unnamed road"));

                if (road.getRoadCode() != null) {
                    answer.append(" (")
                            .append(road.getRoadCode())
                            .append(")");
                }

                answer.append("\n");
            }
        }

        if (!locations.isEmpty()) {

            answer.append("\nLocations:\n");

            int count = 0;

            for (Location location : locations) {

                if (count >= 10) {
                    break;
                }

                answer.append("- ")
                        .append(
                                safeValue(
                                        location.getName(),
                                        "Unnamed location"))
                        .append("\n");

                count++;
            }
        }

        return response(
                question,
                answer.toString().trim(),
                "roads_locations",
                "ROAD_LOCATION");
    }

    private List<TrafficRecord> getLatestTrafficRecords(
            List<TrafficRecord> records) {

        List<TrafficRecord> sortedRecords =
                records.stream()
                        .sorted(
                                Comparator.comparing(
                                        TrafficRecord::getRecordedAt,
                                        Comparator.nullsLast(
                                                Comparator.reverseOrder())))
                        .toList();

        List<TrafficRecord> latestRecords =
                new ArrayList<>();

        Set<Long> processedRoadIds =
                new HashSet<>();

        Set<Long> processedLocationIds =
                new HashSet<>();

        for (TrafficRecord record : sortedRecords) {

            Long roadId =
                    record.getRoad() == null
                            ? null
                            : record.getRoad().getId();

            Long locationId =
                    record.getLocation() == null
                            ? null
                            : record.getLocation().getId();

            if (roadId != null) {

                if (processedRoadIds.add(roadId)) {
                    latestRecords.add(record);
                }

            } else if (locationId != null) {

                if (processedLocationIds.add(locationId)) {
                    latestRecords.add(record);
                }

            } else {

                latestRecords.add(record);
            }

            if (latestRecords.size() >= 10) {
                break;
            }
        }

        return latestRecords;
    }

    private List<WeatherRecord> getLatestWeatherRecords(
            List<WeatherRecord> records) {

        return records.stream()
                .sorted(
                        Comparator.comparing(
                                WeatherRecord::getRecordedAt,
                                Comparator.nullsLast(
                                        Comparator.reverseOrder())))
                .toList();
    }

    private String getRoadName(
            TrafficRecord record) {

        if (record.getRoad() != null
                && record.getRoad().getName() != null) {

            return record.getRoad().getName();
        }

        if (record.getLocation() != null
                && record.getLocation().getName() != null) {

            return record.getLocation().getName();
        }

        return "Unknown location";
    }

    private String getPredictionRoadName(
            TrafficPrediction prediction) {

        if (prediction.getRoad() != null
                && prediction.getRoad().getName() != null) {

            return prediction.getRoad().getName();
        }

        if (prediction.getLocation() != null
                && prediction.getLocation().getName() != null) {

            return prediction.getLocation().getName();
        }

        return "Unknown location";
    }

    private String getWeatherLocationName(
            WeatherRecord weather) {

        if (weather.getLocation() != null
                && weather.getLocation().getName() != null) {

            return weather.getLocation().getName();
        }

        return "Unknown location";
    }

    private String formatNumber(
            BigDecimal value) {

        if (value == null) {
            return null;
        }

        return value
                .setScale(
                        2,
                        RoundingMode.HALF_UP)
                .stripTrailingZeros()
                .toPlainString();
    }

    private String safeValue(
            String value,
            String fallback) {

        if (value == null || value.isBlank()) {
            return fallback;
        }

        return value;
    }

    private boolean isGreeting(
            String question) {

        return containsAny(
                question,
                "hello",
                "hi",
                "hey",
                "good morning",
                "good afternoon",
                "good evening");
    }

    private boolean isHelpQuestion(
            String question) {

        return containsAny(
                question,
                "help",
                "what can you do",
                "what do you know",
                "how can you help");
    }

    private boolean containsAny(
            String text,
            String... values) {

        for (String value : values) {

            if (text.contains(value)) {
                return true;
            }
        }

        return false;
    }

    private String buildHelpAnswer() {

        return """
                CityPulse AI can answer questions using CityPulse system data.

                You can ask about:
                - Current traffic and congestion
                - Traffic predictions for the next 15, 30, 45, or 60 minutes
                - Active traffic alerts
                - Weather conditions
                - Citizen traffic reports
                - Roads and locations

                Example questions:
                - What is the current traffic situation?
                - What are the traffic predictions?
                - Are there any active alerts?
                - What is the weather situation?
                - How many citizen reports are there?
                - What roads are available?
                """.trim();
    }

    private String buildUnknownQuestionAnswer() {

        return """
                I can help with CityPulse traffic information.

                Try asking about:
                - Current traffic
                - Congestion
                - Traffic predictions
                - Alerts
                - Weather
                - Citizen reports
                - Roads or locations
                """.trim();
    }

    private CityPulseAiResponse response(
            String question,
            String answer,
            String dataSource,
            String responseType) {

        return new CityPulseAiResponse(
                question,
                answer,
                dataSource,
                responseType);
    }
}