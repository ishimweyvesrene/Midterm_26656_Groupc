package com.example.EventManagementSystem.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.EventManagementSystem.model.User;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {

    // 7. Implementation of existBy() method.
    boolean existsByEmail(String email);

    // 8. Retrieve all users from a given province using province code OR province name.
    // Using JPQL with @Query
    @Query("SELECT u FROM User u WHERE u.location.code = :code OR u.location.name = :name")
    List<User> findUsersByProvinceCodeOrName(@Param("code") String code, @Param("name") String name);

    // 3. Sorting and Pagination functionality is provided by default through JpaRepository
    // Page<User> findAll(Pageable pageable);
}
