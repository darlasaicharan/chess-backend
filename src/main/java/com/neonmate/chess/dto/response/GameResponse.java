package com.neonmate.chess.dto.response;

import com.neonmate.chess.entity.Game;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameResponse {
    private String id;
    private UserResponse white;
    private UserResponse black;
    private String result;
    private String timeControl;
    private String opening;
    private String eco;
    private String date;
    private int moves;
    private Integer whiteRatingChange;
    private Integer blackRatingChange;
    private AccuracyDto accuracy;
    private String status;
    private String type;
    private boolean rated;
    private String pgn;
    private String fen;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AccuracyDto {
        private double white;
        private double black;
    }

    public static GameResponse from(Game game) {
        GameResponse.GameResponseBuilder builder = GameResponse.builder()
                .id(game.getId())
                .white(UserResponse.from(game.getWhite()))
                .black(UserResponse.from(game.getBlack()))
                .result(game.getResultNotation())
                .timeControl(game.getTimeControl())
                .opening(game.getOpening())
                .eco(game.getEco())
                .date(game.getCreatedAt() != null ? game.getCreatedAt().toString() : null)
                .moves(game.getMoveCount())
                .whiteRatingChange(game.getWhiteRatingChange())
                .blackRatingChange(game.getBlackRatingChange())
                .status(game.getStatus().name())
                .type(game.getType().name())
                .rated(game.isRated())
                .pgn(game.getPgn())
                .fen(game.getFinalFen());

        if (game.getWhiteAccuracy() != null && game.getBlackAccuracy() != null) {
            builder.accuracy(AccuracyDto.builder()
                    .white(game.getWhiteAccuracy())
                    .black(game.getBlackAccuracy())
                    .build());
        }

        return builder.build();
    }
}
