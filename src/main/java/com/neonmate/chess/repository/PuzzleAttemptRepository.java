package com.neonmate.chess.repository;

import com.neonmate.chess.entity.PuzzleAttempt;
import com.neonmate.chess.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PuzzleAttemptRepository extends JpaRepository<PuzzleAttempt, Long> {

    List<PuzzleAttempt> findByUserOrderByAttemptedAtDesc(User user);

    long countByUserAndSolvedTrue(User user);

    boolean existsByUserIdAndPuzzleId(String userId, String puzzleId);
}
