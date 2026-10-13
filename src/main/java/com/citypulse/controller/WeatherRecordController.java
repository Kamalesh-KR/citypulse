package com.citypulse.controller;

import com.citypulse.dto.WeatherRecordRequest;
import com.citypulse.model.Location;
import com.citypulse.model.WeatherRecord;
import com.citypulse.repository.LocationRepository;
import com.citypulse.service.WeatherRecordService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/weather")
public class WeatherRecordController {

private final WeatherRecordService weatherRecordService;
private final LocationRepository locationRepository;

public WeatherRecordController(
        WeatherRecordService weatherRecordService,
        LocationRepository locationRepository) {

    this.weatherRecordService = weatherRecordService;
    this.locationRepository = locationRepository;
}

@GetMapping
public ResponseEntity<List<WeatherRecord>> getAllWeatherRecords() {
    return ResponseEntity.ok(
            weatherRecordService.getAllWeatherRecords());
}

@GetMapping("/{id}")
public ResponseEntity<WeatherRecord> getWeatherRecordById(
        @PathVariable Long id) {

    return weatherRecordService.getWeatherRecordById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
}

@GetMapping("/location/{locationId}")
public ResponseEntity<List<WeatherRecord>> getWeatherRecordsByLocation(
        @PathVariable Long locationId) {

    return ResponseEntity.ok(
            weatherRecordService
                    .getWeatherRecordsByLocation(locationId));
}

@GetMapping("/condition/{weatherCondition}")
public ResponseEntity<List<WeatherRecord>> getWeatherRecordsByCondition(
        @PathVariable String weatherCondition) {

    return ResponseEntity.ok(
            weatherRecordService
                    .getWeatherRecordsByCondition(weatherCondition));
}

@GetMapping("/between")
public ResponseEntity<List<WeatherRecord>> getWeatherRecordsBetween(
        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime start,

        @RequestParam
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
        LocalDateTime end) {

    return ResponseEntity.ok(
            weatherRecordService
                    .getWeatherRecordsBetween(start, end));
}

@PostMapping
public ResponseEntity<WeatherRecord> createWeatherRecord(
        @Valid @RequestBody WeatherRecordRequest request) {

    Location location = locationRepository
            .findById(request.getLocationId())
            .orElseThrow(() ->
                    new RuntimeException(
                            "Location not found with id: "
                                    + request.getLocationId()));

    WeatherRecord weatherRecord = new WeatherRecord();

    weatherRecord.setLocation(location);

    if (request.getTemperature() != null) {
        weatherRecord.setTemperature(
                BigDecimal.valueOf(request.getTemperature()));
    }

    if (request.getHumidity() != null) {
        weatherRecord.setHumidity(
                BigDecimal.valueOf(request.getHumidity()));
    }

    if (request.getRainfall() != null) {
        weatherRecord.setRainfall(
                BigDecimal.valueOf(request.getRainfall()));
    }

    if (request.getWindSpeed() != null) {
        weatherRecord.setWindSpeed(
                BigDecimal.valueOf(request.getWindSpeed()));
    }

    weatherRecord.setWeatherCondition(
            request.getWeatherCondition());

    if (request.getVisibility() != null) {
        weatherRecord.setVisibility(
                BigDecimal.valueOf(request.getVisibility()));
    }

    weatherRecord.setRecordedAt(
            request.getRecordedAt());

    weatherRecord.setDataSource(
            request.getDataSource());

    return ResponseEntity.ok(
            weatherRecordService.createWeatherRecord(weatherRecord));
}

@PutMapping("/{id}")
public ResponseEntity<WeatherRecord> updateWeatherRecord(
        @PathVariable Long id,
        @RequestBody WeatherRecord weatherRecord) {

    return ResponseEntity.ok(
            weatherRecordService.updateWeatherRecord(
                    id,
                    weatherRecord));
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> deleteWeatherRecord(
        @PathVariable Long id) {

    weatherRecordService.deleteWeatherRecord(id);
    return ResponseEntity.noContent().build();
}

}
