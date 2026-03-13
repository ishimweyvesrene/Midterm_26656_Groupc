package com.example.EventManagementSystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.EventManagementSystem.model.User;
import java.util.List;

// Spring Data JPA Repository for User entity
// Requirement 3: Pageable is inherited from JpaRepository for Sorting and Pagination
// Requirement 7: existsByEmail() custom query method for existence checking
// Requirement 8: findUsersByProvinceCodeOrName() for custom JPQL query
public interface UserRepository extends JpaRepository<User, Long> {

    // Requirement 7: Implementation of existBy() method
    // Checks existence of a user by email
    boolean existsByEmail(String email);

    // Requirement 8: Retrieve all users from a given province using province code
    // OR province name
    // Using JPQL with @Query and @Param for named parameters
    @Query("SELECT u FROM User u WHERE u.location.code = :code OR u.location.name = :name")
    List<User> findUsersByProvinceCodeOrName(@Param("code") String code, @Param("name") String name);

    // Requirement 3: Sorting and Pagination functionality is provided by default
    // through JpaRepository
    // Page<User> findAll(Pageable pageable); is inherited
}
