package com.nextask.auth_service.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginResponse {
    String accessToken;
    String refreshToken;
    String tokenType;
    UserResponse user;
}
