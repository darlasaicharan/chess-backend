package com.neonmate.chess.dto.request;

import lombok.Data;

@Data
public class AnalysisRequest {
    private String fen;
    private int depth = 18;
}
