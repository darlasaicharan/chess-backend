package com.neonmate.chess.dto.request;

import lombok.Data;

@Data
public class CreateTournamentRequest {
    private String name;
    private String type = "SWISS";           // KNOCKOUT, ROUND_ROBIN, SWISS
    private String timeControl = "5+0";
    private int maxPlayers = 64;
    private String prize;
    private int rounds = 5;
    private String startDate;                // ISO-8601
}
