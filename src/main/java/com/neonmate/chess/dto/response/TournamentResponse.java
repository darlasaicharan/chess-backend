package com.neonmate.chess.dto.response;

import com.neonmate.chess.entity.Tournament;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TournamentResponse {
    private String id;
    private String name;
    private String type;          // "knockout", "round-robin", "swiss"
    private String timeControl;
    private String status;        // "upcoming", "ongoing", "finished"
    private List<UserResponse> players;
    private int maxPlayers;
    private String startDate;
    private String prize;
    private int rounds;
    private int currentRound;

    public static TournamentResponse from(Tournament tournament) {
        String typeStr = switch (tournament.getType()) {
            case KNOCKOUT -> "knockout";
            case ROUND_ROBIN -> "round-robin";
            case SWISS -> "swiss";
        };

        String statusStr = tournament.getStatus().name().toLowerCase();

        List<UserResponse> playerList = tournament.getPlayers() != null
                ? tournament.getPlayers().stream()
                    .map(p -> UserResponse.from(p.getUser()))
                    .collect(Collectors.toList())
                : List.of();

        return TournamentResponse.builder()
                .id(tournament.getId())
                .name(tournament.getName())
                .type(typeStr)
                .timeControl(tournament.getTimeControl())
                .status(statusStr)
                .players(playerList)
                .maxPlayers(tournament.getMaxPlayers())
                .startDate(tournament.getStartDate() != null ? tournament.getStartDate().toString() : null)
                .prize(tournament.getPrize())
                .rounds(tournament.getRounds())
                .currentRound(tournament.getCurrentRound())
                .build();
    }
}
