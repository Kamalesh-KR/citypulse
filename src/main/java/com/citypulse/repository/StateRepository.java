package com.citypulse.repository;

import com.citypulse.model.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface StateRepository extends JpaRepository<State, Long> {

    List<State> findByActiveTrueOrderByNameAsc();

    Optional<State> findByCode(String code);

    Optional<State> findByName(String name);
}