package com.nextask.auth_service.service;

import com.nextask.auth_service.broker.EventPublisher;
import com.nextask.auth_service.event.PasswordResetEvent;
import com.nextask.auth_service.event.PasswordResetRequestedEvent;
import com.nextask.auth_service.event.UserLoggedInEvent;
import com.nextask.auth_service.model.RefreshToken;
import com.nextask.auth_service.model.User;
import com.nextask.auth_service.repository.RefreshTokenRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenService {

    private static final long REFRESH_TOKEN_DURATION_MS = 7L * 24 * 60 * 60 * 1000;

    RefreshTokenRepository refreshTokenRepository;
    EventPublisher eventPublisher;

    public RefreshToken createRefreshToken(User user) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(UUID.randomUUID().toString())
                .user(user)
                .expiredAt(Instant.now().plusMillis(REFRESH_TOKEN_DURATION_MS))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public void deleteByUser(User user) {
        refreshTokenRepository.deleteByUser(user);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiredAt().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException("Refresh token expired");
        }
        return token;
    }

    public void publishLoginEvent(User user) {
        eventPublisher.publish(new UserLoggedInEvent(user.getId(), user.getEmail()));
    }

    public void publishPasswordResetRequested(User user, String token) {
        eventPublisher.publish(new PasswordResetRequestedEvent(
                user.getId(), user.getEmail(), token));
    }

    public void publishPasswordReset(User user) {
        eventPublisher.publish(new PasswordResetEvent(user.getId(), user.getEmail()));
    }
}
