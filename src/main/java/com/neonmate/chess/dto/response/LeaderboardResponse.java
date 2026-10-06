package com.neonmate.chess.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaderboardResponse {
    private List<LeaderboardEntry> entries;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class LeaderboardEntry {
        private int rank;
        private UserResponse user;
        private int rating;
        private int gamesPlayed;
        private double winRate;
    }
}
