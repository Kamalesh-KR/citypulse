package com.citypulse.service;

import com.citypulse.model.Event;
import com.citypulse.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class EventService {

    private final EventRepository eventRepository;

    public EventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public Optional<Event> getEventById(Long id) {
        return eventRepository.findById(id);
    }

    public List<Event> getEventsByLocation(Long locationId) {
        return eventRepository.findByLocationId(locationId);
    }

    public List<Event> getEventsByType(String eventType) {
        return eventRepository.findByEventTypeIgnoreCase(eventType);
    }

    public List<Event> getEventsByImpactLevel(String impactLevel) {
        return eventRepository.findByImpactLevelIgnoreCase(impactLevel);
    }

    public List<Event> getActiveEvents() {
        return eventRepository.findByActiveTrue();
    }

    public List<Event> getEventsBetween(
            LocalDateTime start,
            LocalDateTime end) {

        return eventRepository.findByStartTimeBetween(start, end);
    }

    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event updateEvent(Long id, Event updatedEvent) {
        return eventRepository.findById(id)
                .map(existingEvent -> {
                    existingEvent.setName(updatedEvent.getName());
                    existingEvent.setDescription(updatedEvent.getDescription());
                    existingEvent.setEventType(updatedEvent.getEventType());
                    existingEvent.setLocation(updatedEvent.getLocation());
                    existingEvent.setExpectedAttendance(
                            updatedEvent.getExpectedAttendance());
                    existingEvent.setStartTime(updatedEvent.getStartTime());
                    existingEvent.setEndTime(updatedEvent.getEndTime());
                    existingEvent.setImpactLevel(updatedEvent.getImpactLevel());
                    existingEvent.setActive(updatedEvent.getActive());

                    return eventRepository.save(existingEvent);
                })
                .orElseThrow(() ->
                        new RuntimeException(
                                "Event not found with id: " + id));
    }

    public void deleteEvent(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new RuntimeException(
                    "Event not found with id: " + id);
        }

        eventRepository.deleteById(id);
    }
}