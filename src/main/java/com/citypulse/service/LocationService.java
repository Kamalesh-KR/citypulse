package com.citypulse.service;

import com.citypulse.model.Location;
import com.citypulse.repository.LocationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LocationService {

    private final LocationRepository locationRepository;

    public LocationService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    public Optional<Location> getLocationById(Long id) {
        return locationRepository.findById(id);
    }

    public List<Location> getLocationsByCity(String city) {
        return locationRepository.findByCityIgnoreCase(city);
    }

    public List<Location> getLocationsByType(String locationType) {
        return locationRepository.findByLocationTypeIgnoreCase(locationType);
    }

    public Location createLocation(Location location) {
        return locationRepository.save(location);
    }

    public Location updateLocation(Long id, Location updatedLocation) {
        return locationRepository.findById(id)
                .map(existingLocation -> {
                    existingLocation.setName(updatedLocation.getName());
                    existingLocation.setAddress(updatedLocation.getAddress());
                    existingLocation.setCity(updatedLocation.getCity());
                    existingLocation.setState(updatedLocation.getState());
                    existingLocation.setLatitude(updatedLocation.getLatitude());
                    existingLocation.setLongitude(updatedLocation.getLongitude());
                    existingLocation.setLocationType(updatedLocation.getLocationType());
                    existingLocation.setActive(updatedLocation.getActive());

                    return locationRepository.save(existingLocation);
                })
                .orElseThrow(() ->
                        new RuntimeException("Location not found with id: " + id));
    }

    public void deleteLocation(Long id) {
        if (!locationRepository.existsById(id)) {
            throw new RuntimeException("Location not found with id: " + id);
        }

        locationRepository.deleteById(id);
    }
}