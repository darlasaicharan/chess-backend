package com.neonmate.chess.repository;

import com.neonmate.chess.entity.Puzzle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PuzzleRepository extends JpaRepository<Puzzle, String> {

    @Query("SELECT p FROM Puzzle p WHERE p.isDailyPuzzle = true AND p.dailyDate = :date")
    Optional<Puzzle> findDailyPuzzle(LocalDateTime date);

    @Query("SELECT p FROM Puzzle p WHERE p.isDailyPuzzle = true ORDER BY p.dailyDate DESC")
    List<Puzzle> findLatestDaily();

    List<Puzzle> findByDifficulty(Puzzle.Difficulty difficulty);

    @Query("SELECT p FROM Puzzle p WHERE p.rating BETWEEN :minRating AND :maxRating ORDER BY FUNCTION('RAND')")
    List<Puzzle> findByRatingRange(int minRating, int maxRating);
}
