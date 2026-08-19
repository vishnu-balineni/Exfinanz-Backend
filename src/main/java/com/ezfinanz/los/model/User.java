package com.ezfinanz.los.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data // Lombok: Generates getters, setters, toString automatically
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users") // Plural so it doesn't conflict with PostgreSQL 'user' keyword
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String phone;

    @Column(nullable = false)
    private String password; // Will store encrypted hash

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role; // ADMIN or CUSTOMER

    @Builder.Default
    @Column(nullable = false)
    private boolean isEmailVerified = false;

    @Builder.Default
    @Column(nullable = false)
    private boolean isKycVerified = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
