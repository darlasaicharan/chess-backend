package com.neonmate.chess.dto.response;

import com.neonmate.chess.entity.Puzzle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PuzzleResponse {
    private String id;
    private String fen;
    private List<String> solution;
    private int rating;
    private List<String> themes;
    private String title;
    private String description;
    private String difficulty;

    public static PuzzleResponse from(Puzzle puzzle) {
        return PuzzleResponse.builder()
                .id(puzzle.getId())
                .fen(puzzle.getFen())
                .solution(Arrays.asList(puzzle.getSolution().split(",")))
                .rating(puzzle.getRating())
                .themes(puzzle.getThemes() != null ? Arrays.asList(puzzle.getThemes().split(",")) : List.of())
                .title(puzzle.getTitle())
                .description(puzzle.getDescription())
                .difficulty(puzzle.getDifficulty().name().toLowerCase())
                .build();
    }
}
