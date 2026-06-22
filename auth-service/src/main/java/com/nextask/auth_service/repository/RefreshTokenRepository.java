package com.nextask.auth_service.repository;

import com.nextask.auth_service.model.RefreshToken;
import com.nextask.auth_service.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByUser(User user);
}
