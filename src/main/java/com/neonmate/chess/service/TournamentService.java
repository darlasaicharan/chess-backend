package com.neonmate.chess.service;

import com.neonmate.chess.dto.request.CreateTournamentRequest;
import com.neonmate.chess.dto.response.TournamentResponse;
import com.neonmate.chess.entity.Tournament;
import com.neonmate.chess.entity.TournamentPlayer;
import com.neonmate.chess.entity.User;
import com.neonmate.chess.exception.ResourceNotFoundException;
import com.neonmate.chess.repository.TournamentPlayerRepository;
import com.neonmate.chess.repository.TournamentRepository;
import com.neonmate.chess.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final TournamentPlayerRepository tournamentPlayerRepository;
    private final UserRepository userRepository;

    public List<TournamentResponse> getAllTournaments() {
        return tournamentRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(TournamentResponse::from)
                .collect(Collectors.toList());
    }

    public TournamentResponse getTournament(String id) {
        Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", "id", id));
        return TournamentResponse.from(tournament);
    }

    @Transactional
    public TournamentResponse createTournament(String username, CreateTournamentRequest request) {
        User creator = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Tournament.TournamentType type = Tournament.TournamentType.SWISS;
        if (request.getType() != null) {
            try {
                type = Tournament.TournamentType.valueOf(request.getType().toUpperCase().replace("-", "_"));
            } catch (IllegalArgumentException ignored) {}
        }

        LocalDateTime startDate = request.getStartDate() != null && !request.getStartDate().isBlank()
                ? LocalDateTime.parse(request.getStartDate().replace("Z", ""))
                : LocalDateTime.now().plusDays(1);

        Tournament tournament = Tournament.builder()
                .name(request.getName())
                .type(type)
                .timeControl(request.getTimeControl())
                .status(Tournament.TournamentStatus.UPCOMING)
                .maxPlayers(request.getMaxPlayers() > 0 ? request.getMaxPlayers() : 64)
                .prize(request.getPrize())
                .rounds(request.getRounds() > 0 ? request.getRounds() : 5)
                .currentRound(0)
                .startDate(startDate)
                .creator(creator)
                .build();

        tournament = tournamentRepository.save(tournament);

        // Creator automatically joins
        TournamentPlayer player = TournamentPlayer.builder()
                .tournament(tournament)
                .user(creator)
                .seed(1)
                .score(0.0)
                .build();
        tournamentPlayerRepository.save(player);
        tournament.getPlayers().add(player);

        return TournamentResponse.from(tournament);
    }

    @Transactional
    public TournamentResponse joinTournament(String tournamentId, String username) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new ResourceNotFoundException("Tournament", "id", tournamentId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        if (tournament.getStatus() != Tournament.TournamentStatus.UPCOMING) {
            throw new IllegalStateException("Tournament has already started or finished");
        }

        if (tournamentPlayerRepository.existsByTournamentIdAndUserId(tournamentId, user.getId())) {
            return TournamentResponse.from(tournament);
        }

        long currentCount = tournamentPlayerRepository.countByTournamentId(tournamentId);
        if (currentCount >= tournament.getMaxPlayers()) {
            throw new IllegalStateException("Tournament is already full");
        }

        TournamentPlayer player = TournamentPlayer.builder()
                .tournament(tournament)
                .user(user)
                .seed((int) currentCount + 1)
                .score(0.0)
                .build();

        tournamentPlayerRepository.save(player);
        tournament.getPlayers().add(player);

        return TournamentResponse.from(tournament);
    }
}
