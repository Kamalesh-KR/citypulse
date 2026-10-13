package com.citypulse.repository;

import com.citypulse.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findByCityIgnoreCase(String city);

    List<Location> findByLocationTypeIgnoreCase(String locationType);
}
