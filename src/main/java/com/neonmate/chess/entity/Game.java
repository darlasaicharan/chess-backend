package com.neonmate.chess.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "games")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "white_id", nullable = false)
    private User white;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "black_id", nullable = false)
    private User black;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private GameResult result = GameResult.IN_PROGRESS;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private GameType type = GameType.ONLINE;

    @Column(nullable = false)
    private String timeControl;  // e.g. "5+0", "10+5"

    private int initialTimeSec;
    private int incrementSec;

    private String opening;
    private String eco;

    private int moveCount;

    @Column(columnDefinition = "TEXT")
    private String pgn;

    private String finalFen;

    private int whiteRatingBefore;
    private int blackRatingBefore;
    private int whiteRatingChange;
    private int blackRatingChange;

    private Double whiteAccuracy;
    private Double blackAccuracy;

    @Builder.Default
    private boolean rated = true;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private GameStatus status = GameStatus.WAITING;

    @OneToMany(mappedBy = "game", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<GameMove> moves = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    private LocalDateTime startedAt;
    private LocalDateTime endedAt;

    // Tournament reference (nullable)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;

    private Integer tournamentRound;

    public enum GameResult {
        WHITE_WIN,      // 1-0
        BLACK_WIN,      // 0-1
        DRAW,           // 1/2-1/2
        IN_PROGRESS     // *
    }

    public enum GameType {
        ONLINE, AI, FRIEND
    }

    public enum GameStatus {
        WAITING, ACTIVE, COMPLETED, ABORTED
    }

    public String getResultNotation() {
        return switch (result) {
            case WHITE_WIN -> "1-0";
            case BLACK_WIN -> "0-1";
            case DRAW -> "1/2-1/2";
            case IN_PROGRESS -> "*";
        };
    }
}
