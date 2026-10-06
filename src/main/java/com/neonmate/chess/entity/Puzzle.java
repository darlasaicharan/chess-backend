package com.neonmate.chess.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "puzzles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Puzzle {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String fen;

    @Column(nullable = false, length = 500)
    private String solution;  // Comma-separated SAN moves, e.g. "Ng5,Nd4,Nxf7"

    @Builder.Default
    private int rating = 1500;

    private String themes;  // Comma-separated, e.g. "fork,tactic"

    private String title;

    @Column(length = 500)
    private String description;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Difficulty difficulty = Difficulty.MEDIUM;

    @Builder.Default
    private int attempts = 0;

    @Builder.Default
    private int solves = 0;

    @Builder.Default
    private boolean isDailyPuzzle = false;

    private LocalDateTime dailyDate;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum Difficulty {
        EASY, MEDIUM, HARD
    }
}
