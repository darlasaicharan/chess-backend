package com.neonmate.chess.controller;

import com.neonmate.chess.dto.request.AnalysisRequest;
import com.neonmate.chess.dto.response.AIAnalysisResponse;
import com.neonmate.chess.service.AiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiController {

    private final AiService aiService;

    @PostMapping("/analyze")
    public ResponseEntity<AIAnalysisResponse> analyze(@RequestBody AnalysisRequest request) {
        return ResponseEntity.ok(aiService.analyzePosition(request.getFen(), request.getDepth()));
    }

    @PostMapping("/best-move")
    public ResponseEntity<Map<String, Object>> getBestMove(@RequestBody AnalysisRequest request) {
        return ResponseEntity.ok(aiService.getBestMove(request.getFen(), request.getDepth()));
    }

    @PostMapping("/analyze-game/{gameId}")
    public ResponseEntity<Map<String, Object>> analyzeGame(@PathVariable String gameId) {
        return ResponseEntity.ok(aiService.analyzeGame(gameId));
    }
}
