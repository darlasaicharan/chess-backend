package com.neonmate.chess.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "game_moves")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GameMove {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    private int moveNumber;       // 1-based full move number
    private String color;         // "w" or "b"

    @Column(nullable = false, length = 10)
    private String san;           // Standard Algebraic Notation, e.g. "Nf3"

    @Column(length = 10)
    private String fromSquare;    // e.g. "g1"

    @Column(length = 10)
    private String toSquare;      // e.g. "f3"

    private String promotion;     // e.g. "q" if pawn promoted

    @Column(length = 100)
    private String fen;           // FEN after this move

    private Long timeSpentMs;     // Time the player took for this move

    private Double evaluation;    // Engine eval (centipawns), null if not analyzed
    private String bestMove;      // Engine's best move at this position, null if not analyzed

    @Column(length = 20)
    private String classification; // "best", "excellent", "good", "inaccuracy", "mistake", "blunder"
}
