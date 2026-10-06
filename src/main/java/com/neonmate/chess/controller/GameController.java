package com.neonmate.chess.controller;

import com.neonmate.chess.dto.request.CreateGameRequest;
import com.neonmate.chess.dto.request.MoveRequest;
import com.neonmate.chess.dto.response.GameResponse;
import com.neonmate.chess.service.GameService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @GetMapping
    public ResponseEntity<List<GameResponse>> getGames(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        String targetUser = (username != null && !username.isBlank())
                ? username
                : (userDetails != null ? userDetails.getUsername() : null);

        if (targetUser == null) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok(gameService.getGames(targetUser, page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GameResponse> getGame(@PathVariable String id) {
        return ResponseEntity.ok(gameService.getGame(id));
    }

    @PostMapping
    public ResponseEntity<GameResponse> createGame(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateGameRequest request) {
        return ResponseEntity.ok(gameService.createGame(userDetails.getUsername(), request));
    }

    @PostMapping("/ai")
    public ResponseEntity<GameResponse> createAIGame(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateGameRequest request) {
        return ResponseEntity.ok(gameService.createAIGame(userDetails.getUsername(), request));
    }

    @PostMapping("/{id}/move")
    public ResponseEntity<GameResponse> makeMove(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody MoveRequest request) {
        return ResponseEntity.ok(gameService.makeMove(id, userDetails.getUsername(), request));
    }

    @PostMapping("/{id}/resign")
    public ResponseEntity<GameResponse> resign(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(gameService.resign(id, userDetails.getUsername()));
    }

    @PostMapping("/{id}/draw-offer")
    public ResponseEntity<GameResponse> offerDraw(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(gameService.offerDraw(id, userDetails.getUsername()));
    }

    @GetMapping("/{id}/pgn")
    public ResponseEntity<Map<String, String>> exportPGN(@PathVariable String id) {
        return ResponseEntity.ok(Map.of("pgn", gameService.exportPGN(id)));
    }

    @GetMapping("/{id}/fen")
    public ResponseEntity<Map<String, String>> exportFEN(@PathVariable String id) {
        return ResponseEntity.ok(Map.of("fen", gameService.exportFEN(id)));
    }

    /**
     * Called by the frontend when a client-side AI game ends.
     * Persists the result, PGN, final FEN, and move count to the database.
     * Also updates player stats (wins/losses/draws) and saves a rating history snapshot.
     */
    @PostMapping("/{id}/finalize-ai")
    public ResponseEntity<GameResponse> finalizeAIGame(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> payload) {
        String result = (String) payload.getOrDefault("result", "draw");
        String pgn = (String) payload.getOrDefault("pgn", "");
        String finalFen = (String) payload.getOrDefault("finalFen", "");
        int moveCount = ((Number) payload.getOrDefault("moveCount", 0)).intValue();
        return ResponseEntity.ok(
                gameService.finalizeAIGame(id, userDetails.getUsername(), result, pgn, finalFen, moveCount)
        );
    }
}
