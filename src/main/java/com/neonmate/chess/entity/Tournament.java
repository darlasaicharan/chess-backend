package com.neonmate.chess.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tournaments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TournamentType type = TournamentType.SWISS;

    @Column(nullable = false)
    private String timeControl;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TournamentStatus status = TournamentStatus.UPCOMING;

    @Builder.Default
    private int maxPlayers = 64;

    private String prize;

    @Builder.Default
    private int rounds = 5;

    @Builder.Default
    private int currentRound = 0;

    private LocalDateTime startDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id")
    private User creator;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<TournamentPlayer> players = new ArrayList<>();

    @OneToMany(mappedBy = "tournament")
    @Builder.Default
    private List<Game> games = new ArrayList<>();

    @CreationTimestamp
    private LocalDateTime createdAt;

    public enum TournamentType {
        KNOCKOUT, ROUND_ROBIN, SWISS
    }

    public enum TournamentStatus {
        UPCOMING, ONGOING, FINISHED
    }
}
