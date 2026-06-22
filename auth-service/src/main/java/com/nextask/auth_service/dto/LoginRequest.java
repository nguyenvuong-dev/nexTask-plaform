package com.nextask.auth_service.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.NotFound;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class LoginRequest {

    @NotBlank(message = "Username or email is required")
    String username;
    @NotBlank(message = "Password is required")
    String password;
}
