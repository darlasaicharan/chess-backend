package com.neonmate.chess.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rating_history", indexes = {
    @Index(name = "idx_rating_history_user_id", columnList = "user_id"),
    @Index(name = "idx_rating_history_recorded_at", columnList = "recordedAt")
})
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class RatingHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private int rating;

    private int ratingChange;

    // The game that caused this rating change (nullable for initial seeding)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    private LocalDateTime recordedAt;

    @PrePersist
    public void prePersist() {
        if (recordedAt == null) {
            recordedAt = LocalDateTime.now();
        }
    }
}
