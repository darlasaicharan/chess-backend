package com.neonmate.chess.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(length = 500)
    private String bio;

    private String avatar;

    @Column(length = 10)
    private String country;

    @Column(length = 10)
    private String title;  // GM, IM, FM, etc.

    @Builder.Default
    private int rating = 1200;

    @Builder.Default
    private int gamesPlayed = 0;

    @Builder.Default
    private int wins = 0;

    @Builder.Default
    private int losses = 0;

    @Builder.Default
    private int draws = 0;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Role role = Role.USER;

    @Builder.Default
    private boolean isOnline = false;

    @Builder.Default
    private boolean enabled = true;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // Settings stored as simple fields
    @Builder.Default
    private boolean soundEnabled = true;

    @Builder.Default
    private String boardTheme = "cyber";

    @Builder.Default
    private String pieceStyle = "classic";

    public enum Role {
        USER, ADMIN, MODERATOR
    }
}
