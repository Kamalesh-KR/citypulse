package com.citypulse.dto;

import java.util.List;

/**
 * ============================================================
 * CITYPULSE — PEAK HOUR HEATMAP RESPONSE
 * ============================================================
 *
 * Payload for GET /api/traffic/analytics/heatmap
 *
 * Structure:
 *   {
 *     "hours": [0,1,2,...,23],
 *     "roads": [
 *       {
 *         "roadId": 1,
 *         "roadName": "MG Road",
 *         "cells": [
 *           { "hour": 0, "avgCongestion": 12.4, "sampleCount": 5 },
 *           ...
 *         ],
 *         "peakHour": 18,
 *         "peakCongestion": 87.5
 *       },
 *       ...
 *     ],
 *     "maxCongestion": 92.1,
 *     "totalRoads": 12,
 *     "generatedAt": "2026-10-28T14:30:00"
 *   }
 */
public class HourlyHeatmapResponse {

    private List<Integer> hours;
    private List<RoadHeatmap> roads;
    private Double maxCongestion;
    private Integer totalRoads;
    private String generatedAt;

    public HourlyHeatmapResponse() {
    }

    public HourlyHeatmapResponse(
            List<Integer> hours,
            List<RoadHeatmap> roads,
            Double maxCongestion,
            Integer totalRoads,
            String generatedAt) {

        this.hours = hours;
        this.roads = roads;
        this.maxCongestion = maxCongestion;
        this.totalRoads = totalRoads;
        this.generatedAt = generatedAt;
    }

    public List<Integer> getHours() {
        return hours;
    }

    public void setHours(List<Integer> hours) {
        this.hours = hours;
    }

    public List<RoadHeatmap> getRoads() {
        return roads;
    }

    public void setRoads(List<RoadHeatmap> roads) {
        this.roads = roads;
    }

    public Double getMaxCongestion() {
        return maxCongestion;
    }

    public void setMaxCongestion(Double maxCongestion) {
        this.maxCongestion = maxCongestion;
    }

    public Integer getTotalRoads() {
        return totalRoads;
    }

    public void setTotalRoads(Integer totalRoads) {
        this.totalRoads = totalRoads;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }

    public static class RoadHeatmap {

        private Long roadId;
        private String roadName;
        private List<HeatmapCell> cells;
        private Integer peakHour;
        private Double peakCongestion;

        public RoadHeatmap() {
        }

        public RoadHeatmap(
                Long roadId,
                String roadName,
                List<HeatmapCell> cells,
                Integer peakHour,
                Double peakCongestion) {

            this.roadId = roadId;
            this.roadName = roadName;
            this.cells = cells;
            this.peakHour = peakHour;
            this.peakCongestion = peakCongestion;
        }

        public Long getRoadId() {
            return roadId;
        }

        public void setRoadId(Long roadId) {
            this.roadId = roadId;
        }

        public String getRoadName() {
            return roadName;
        }

        public void setRoadName(String roadName) {
            this.roadName = roadName;
        }

        public List<HeatmapCell> getCells() {
            return cells;
        }

        public void setCells(List<HeatmapCell> cells) {
            this.cells = cells;
        }

        public Integer getPeakHour() {
            return peakHour;
        }

        public void setPeakHour(Integer peakHour) {
            this.peakHour = peakHour;
        }

        public Double getPeakCongestion() {
            return peakCongestion;
        }

        public void setPeakCongestion(Double peakCongestion) {
            this.peakCongestion = peakCongestion;
        }
    }

    public static class HeatmapCell {

        private Integer hour;
        private Double avgCongestion;
        private Long sampleCount;

        public HeatmapCell() {
        }

        public HeatmapCell(Integer hour, Double avgCongestion, Long sampleCount) {
            this.hour = hour;
            this.avgCongestion = avgCongestion;
            this.sampleCount = sampleCount;
        }

        public Integer getHour() {
            return hour;
        }

        public void setHour(Integer hour) {
            this.hour = hour;
        }

        public Double getAvgCongestion() {
            return avgCongestion;
        }

        public void setAvgCongestion(Double avgCongestion) {
            this.avgCongestion = avgCongestion;
        }

        public Long getSampleCount() {
            return sampleCount;
        }

        public void setSampleCount(Long sampleCount) {
            this.sampleCount = sampleCount;
        }
    }
}