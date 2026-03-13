package com.example.EventManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.EventManagementSystem.model.Event;

public interface EventRepository extends JpaRepository<Event, Long> {
}
