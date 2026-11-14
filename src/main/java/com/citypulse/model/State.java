package com.citypulse.model;

import jakarta.persistence.*;

/**
 * ============================================================
 * CITYPULSE — STATE / UNION TERRITORY
 * ============================================================
 * Indian state or union territory.
 *
 * Phase 1 of all-India coverage.
 */
@Entity
@Table(name = "states")
public class State {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "code", unique = true, length = 10)
    private String code;

    @Column(length = 50)
    private String type;  // "STATE" or "UNION_TERRITORY"

    @Column(nullable = false)
    private Boolean active = true;

    public State() {
    }

    public State(String name, String code, String type) {
        this.name = name;
        this.code = code;
        this.type = type;
        this.active = true;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}