package com.astro.theRealAstrologers.Entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Data
public class User {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private String fullName;

        @Column(unique = true, nullable = false)
        private String email;

        @Column(nullable = false)
        private String password;

        private LocalDateTime createdAt;


        @PrePersist
        protected void onCreate() {
            this.createdAt = LocalDateTime.now();
        }
}
