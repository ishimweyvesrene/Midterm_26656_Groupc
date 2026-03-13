package com.example.EventManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.EventManagementSystem.model.Location;

public interface LocationRepository extends JpaRepository<Location, Long> {
}
