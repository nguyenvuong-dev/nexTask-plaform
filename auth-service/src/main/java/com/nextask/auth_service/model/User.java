package com.nextask.auth_service.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @Column(nullable = false, unique = true, length = 20)
    String name;

    @Column(nullable = false)
    String password;

    @Column(nullable = false,unique = true,length = 100)
    String email;

    @Column(nullable = false,unique = true,length = 100)
    String fullName;

    @Column(length = 100)
    String phone;

    @Column(nullable = false)
    @Builder.Default
    Boolean enabled = false;

    @Column(nullable = false)
    @Builder.Default
    Boolean disabled = false;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    LocalDateTime createdAt;

    @LastModifiedBy
    @Column(nullable = false)
    LocalDateTime updatedAt;

    @Column(updatable = false)
    LocalDateTime emailVerifiedAt;
}
