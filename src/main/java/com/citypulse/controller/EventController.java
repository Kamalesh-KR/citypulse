package com.citypulse.controller;

import com.citypulse.model.Event;
import com.citypulse.service.EventService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping
    public ResponseEntity<List<Event>> getAllEvents() {
        return ResponseEntity.ok(eventService.getAllEvents());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(
            @PathVariable Long id) {

        return eventService.getEventById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/location/{locationId}")
    public ResponseEntity<List<Event>> getEventsByLocation(
            @PathVariable Long locationId) {

        return ResponseEntity.ok(
                eventService.getEventsByLocation(locationId));
    }

    @GetMapping("/type/{eventType}")
    public ResponseEntity<List<Event>> getEventsByType(
            @PathVariable String eventType) {

        return ResponseEntity.ok(
                eventService.getEventsByType(eventType));
    }

    @GetMapping("/impact/{impactLevel}")
    public ResponseEntity<List<Event>> getEventsByImpactLevel(
            @PathVariable String impactLevel) {

        return ResponseEntity.ok(
                eventService.getEventsByImpactLevel(impactLevel));
    }

    @GetMapping("/active")
    public ResponseEntity<List<Event>> getActiveEvents() {
        return ResponseEntity.ok(eventService.getActiveEvents());
    }

    @GetMapping("/between")
    public ResponseEntity<List<Event>> getEventsBetween(
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime start,

            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime end) {

        return ResponseEntity.ok(
                eventService.getEventsBetween(start, end));
    }

    @PostMapping
    public ResponseEntity<Event> createEvent(
            @RequestBody Event event) {

        return ResponseEntity.ok(eventService.createEvent(event));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Event> updateEvent(
            @PathVariable Long id,
            @RequestBody Event event) {

        return ResponseEntity.ok(
                eventService.updateEvent(id, event));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvent(
            @PathVariable Long id) {

        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }
}