package com.neonmate.chess.repository;

import com.neonmate.chess.entity.Game;
import com.neonmate.chess.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, String> {

    @Query("SELECT g FROM Game g WHERE g.white = :user OR g.black = :user ORDER BY g.createdAt DESC")
    Page<Game> findByUser(User user, Pageable pageable);

    @Query("SELECT g FROM Game g WHERE (g.white = :user OR g.black = :user) AND g.status = 'COMPLETED' ORDER BY g.createdAt DESC")
    List<Game> findCompletedByUser(User user);

    List<Game> findByStatusOrderByCreatedAtAsc(Game.GameStatus status);

    @Query("SELECT g FROM Game g WHERE g.status = 'WAITING' AND g.timeControl = :timeControl ORDER BY g.createdAt ASC")
    List<Game> findWaitingGames(String timeControl);

    @Query("SELECT g FROM Game g WHERE g.status = 'ACTIVE' AND (g.white = :user OR g.black = :user)")
    List<Game> findActiveGamesByUser(User user);

    @Query("SELECT g FROM Game g WHERE g.tournament.id = :tournamentId ORDER BY g.tournamentRound, g.createdAt")
    List<Game> findByTournamentId(String tournamentId);

    long countByWhiteAndResult(User white, Game.GameResult result);
    long countByBlackAndResult(User black, Game.GameResult result);
}
