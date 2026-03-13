package com.example.EventManagementSystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.EventManagementSystem.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
