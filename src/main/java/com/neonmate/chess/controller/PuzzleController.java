package com.neonmate.chess.controller;

import com.neonmate.chess.dto.response.PuzzleResponse;
import com.neonmate.chess.service.PuzzleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/puzzles")
@RequiredArgsConstructor
public class PuzzleController {

    private final PuzzleService puzzleService;

    @GetMapping("/daily")
    public ResponseEntity<PuzzleResponse> getDailyPuzzle() {
        return ResponseEntity.ok(puzzleService.getDailyPuzzle());
    }

    @GetMapping
    public ResponseEntity<List<PuzzleResponse>> getPuzzles(
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Integer minRating,
            @RequestParam(required = false) Integer maxRating) {
        return ResponseEntity.ok(puzzleService.getPuzzles(difficulty, minRating, maxRating));
    }

    @PostMapping("/{id}/attempt")
    public ResponseEntity<Map<String, Object>> submitAttempt(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, List<String>> request) {
        List<String> moves = request.get("moves");
        String username = userDetails != null ? userDetails.getUsername() : "guest";
        return ResponseEntity.ok(puzzleService.submitAttempt(id, username, moves));
    }
}
