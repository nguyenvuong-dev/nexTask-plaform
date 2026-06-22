package com.nextask.auth_service.repository;

import com.nextask.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    boolean existsByName(String name);
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u WHERE u.name = :login OR u.email = :login")
    Optional<User> findByNameOrEmail(String login);
}
