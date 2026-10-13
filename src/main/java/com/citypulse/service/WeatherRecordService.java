package com.citypulse.service;

import com.citypulse.model.WeatherRecord;
import com.citypulse.repository.WeatherRecordRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class WeatherRecordService {

    private final WeatherRecordRepository weatherRecordRepository;

    public WeatherRecordService(WeatherRecordRepository weatherRecordRepository) {
        this.weatherRecordRepository = weatherRecordRepository;
    }

    public List<WeatherRecord> getAllWeatherRecords() {
        return weatherRecordRepository.findAll();
    }

    public Optional<WeatherRecord> getWeatherRecordById(Long id) {
        return weatherRecordRepository.findById(id);
    }

    public List<WeatherRecord> getWeatherRecordsByLocation(Long locationId) {
        return weatherRecordRepository.findByLocationId(locationId);
    }

    public List<WeatherRecord> getWeatherRecordsByCondition(
            String weatherCondition) {

        return weatherRecordRepository
                .findByWeatherConditionIgnoreCase(weatherCondition);
    }

    public List<WeatherRecord> getWeatherRecordsBetween(
            LocalDateTime start,
            LocalDateTime end) {

        return weatherRecordRepository.findByRecordedAtBetween(start, end);
    }

    public WeatherRecord createWeatherRecord(WeatherRecord weatherRecord) {
        return weatherRecordRepository.save(weatherRecord);
    }

    public WeatherRecord updateWeatherRecord(
            Long id,
            WeatherRecord updatedWeatherRecord) {

        return weatherRecordRepository.findById(id)
                .map(existingRecord -> {
                    existingRecord.setLocation(updatedWeatherRecord.getLocation());
                    existingRecord.setTemperature(
                            updatedWeatherRecord.getTemperature());
                    existingRecord.setHumidity(
                            updatedWeatherRecord.getHumidity());
                    existingRecord.setRainfall(
                            updatedWeatherRecord.getRainfall());
                    existingRecord.setWindSpeed(
                            updatedWeatherRecord.getWindSpeed());
                    existingRecord.setWeatherCondition(
                            updatedWeatherRecord.getWeatherCondition());
                    existingRecord.setVisibility(
                            updatedWeatherRecord.getVisibility());
                    existingRecord.setRecordedAt(
                            updatedWeatherRecord.getRecordedAt());
                    existingRecord.setDataSource(
                            updatedWeatherRecord.getDataSource());

                    return weatherRecordRepository.save(existingRecord);
                })
                .orElseThrow(() ->
                        new RuntimeException(
                                "Weather record not found with id: " + id));
    }

    public void deleteWeatherRecord(Long id) {
        if (!weatherRecordRepository.existsById(id)) {
            throw new RuntimeException(
                    "Weather record not found with id: " + id);
        }

        weatherRecordRepository.deleteById(id);
    }
}