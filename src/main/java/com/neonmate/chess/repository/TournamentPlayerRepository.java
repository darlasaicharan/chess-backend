package com.neonmate.chess.repository;

import com.neonmate.chess.entity.TournamentPlayer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TournamentPlayerRepository extends JpaRepository<TournamentPlayer, Long> {

    List<TournamentPlayer> findByTournamentIdOrderByScoreDesc(String tournamentId);

    Optional<TournamentPlayer> findByTournamentIdAndUserId(String tournamentId, String userId);

    boolean existsByTournamentIdAndUserId(String tournamentId, String userId);

    long countByTournamentId(String tournamentId);
}
