package com.neonmate.chess.controller;

import com.neonmate.chess.dto.request.CreateTournamentRequest;
import com.neonmate.chess.dto.response.TournamentResponse;
import com.neonmate.chess.service.TournamentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tournaments")
@RequiredArgsConstructor
public class TournamentController {

    private final TournamentService tournamentService;

    @GetMapping
    public ResponseEntity<List<TournamentResponse>> getAllTournaments() {
        return ResponseEntity.ok(tournamentService.getAllTournaments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentResponse> getTournament(@PathVariable String id) {
        return ResponseEntity.ok(tournamentService.getTournament(id));
    }

    @PostMapping
    public ResponseEntity<TournamentResponse> createTournament(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody CreateTournamentRequest request) {
        return ResponseEntity.ok(tournamentService.createTournament(userDetails.getUsername(), request));
    }

    @PostMapping("/{id}/join")
    public ResponseEntity<TournamentResponse> joinTournament(
            @PathVariable String id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(tournamentService.joinTournament(id, userDetails.getUsername()));
    }
}
