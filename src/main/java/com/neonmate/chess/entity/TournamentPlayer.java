package com.neonmate.chess.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "tournament_players",
       uniqueConstraints = @UniqueConstraint(columnNames = {"tournament_id", "user_id"}))
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TournamentPlayer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder.Default
    private double score = 0;  // Points accumulated (1 for win, 0.5 draw, 0 loss)

    @Builder.Default
    private int gamesPlayed = 0;

    private int seed;  // For seeded brackets

    @CreationTimestamp
    private LocalDateTime joinedAt;
}
