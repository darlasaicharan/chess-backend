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
public class AIAnalysisResponse {
    private double evaluation;
    private int depth;
    private String bestMove;
    private List<String> pv;
    private Long nodes;
    private Long time;
    private Integer mate;
}
