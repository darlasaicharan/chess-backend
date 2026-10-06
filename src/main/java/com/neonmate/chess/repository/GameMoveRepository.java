package com.neonmate.chess.repository;

import com.neonmate.chess.entity.GameMove;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GameMoveRepository extends JpaRepository<GameMove, Long> {

    List<GameMove> findByGameIdOrderByMoveNumberAscColorAsc(String gameId);

    long countByGameId(String gameId);
}
