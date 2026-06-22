package com.nextask.auth_service.service;

import com.nextask.auth_service.broker.EventPublisher;
import com.nextask.auth_service.dto.LoginRequest;
import com.nextask.auth_service.dto.LoginResponse;
import com.nextask.auth_service.dto.RefreshTokenRequest;
import com.nextask.auth_service.dto.SignUpRequest;
import com.nextask.auth_service.dto.UserResponse;
import com.nextask.auth_service.event.UserLoggedInEvent;
import com.nextask.auth_service.event.UserRegisteredEvent;
import com.nextask.auth_service.mapper.UserMapper;
import com.nextask.auth_service.model.RefreshToken;
import com.nextask.auth_service.model.User;
import com.nextask.auth_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    TokenService tokenService;
    EmailService emailService;
    EventPublisher eventPublisher;
    JwtTokenProvider jwtTokenProvider;
    UserMapper userMapper;

    @Transactional
    public UserResponse signUp(SignUpRequest signUpRequest) {
        if (userRepository.existsByName(signUpRequest.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        User user = User.builder()
                .name(signUpRequest.getUsername())
                .email(signUpRequest.getEmail())
                .password(passwordEncoder.encode(signUpRequest.getPassword()))
                .fullName(signUpRequest.getUsername())
                .enabled(false)
                .disabled(false)
                .build();

        user = userRepository.save(user);

        log.info("User registered successfully: [{}]", user.getEmail());

        eventPublisher.publish(new UserRegisteredEvent(
                user.getId(), user.getEmail(), user.getName()));

        emailService.sendWelcomeEmail(user.getId(), user.getEmail(), user.getName());

        return userMapper.toUserResponse(user);
    }

    public UserResponse getUserById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toUserResponse(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByNameOrEmail(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        if (!user.getEnabled()) {
            throw new RuntimeException("Account not verified");
        }

        if (user.getDisabled()) {
            throw new RuntimeException("Account disabled");
        }

        RefreshToken refreshToken = tokenService.createRefreshToken(user);
        String accessToken = jwtTokenProvider.generateAccessToken(user);

        eventPublisher.publish(new UserLoggedInEvent(user.getId(), user.getEmail()));

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .tokenType("Bearer")
                .user(userMapper.toUserResponse(user))
                .build();
    }

    @Transactional
    public LoginResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = tokenService.findByToken(request.getRefreshToken())
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        tokenService.verifyExpiration(refreshToken);
        User user = refreshToken.getUser();

        String accessToken = jwtTokenProvider.generateAccessToken(user);
        RefreshToken newRefreshToken = tokenService.createRefreshToken(user);
        tokenService.deleteByUser(user);

        return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(newRefreshToken.getToken())
                .tokenType("Bearer")
                .user(userMapper.toUserResponse(user))
                .build();
    }
}
