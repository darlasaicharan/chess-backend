package com.neonmate.chess.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "puzzle_attempts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PuzzleAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "puzzle_id", nullable = false)
    private Puzzle puzzle;

    private boolean solved;

    @Column(length = 500)
    private String movesPlayed;  // Comma-separated moves the user tried

    private int ratingChangePuzzle;  // How much the puzzle rating changed
    private int ratingChangeUser;    // How much the user's puzzle rating changed (future)

    @CreationTimestamp
    private LocalDateTime attemptedAt;
}
