package com.neonmate.chess.dto.request;

import lombok.Data;

@Data
public class CreateGameRequest {
    private String type = "ONLINE";        // ONLINE, AI, FRIEND
    private String timeControl = "5+0";    // e.g. "5+0", "10+5"
    private boolean rated = true;
    private String opponentId;             // For friend challenges
    private int aiLevel = 3;               // 1-5, only for AI games
    private String playerColor;            // "w" or "b", null for random
}
