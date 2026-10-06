package com.neonmate.chess.service;

import com.neonmate.chess.dto.response.PuzzleResponse;
import com.neonmate.chess.entity.Puzzle;
import com.neonmate.chess.entity.PuzzleAttempt;
import com.neonmate.chess.entity.User;
import com.neonmate.chess.exception.ResourceNotFoundException;
import com.neonmate.chess.repository.PuzzleAttemptRepository;
import com.neonmate.chess.repository.PuzzleRepository;
import com.neonmate.chess.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PuzzleService {

    private final PuzzleRepository puzzleRepository;
    private final PuzzleAttemptRepository puzzleAttemptRepository;
    private final UserRepository userRepository;

    public PuzzleResponse getDailyPuzzle() {
        List<Puzzle> dailies = puzzleRepository.findLatestDaily();
        if (!dailies.isEmpty()) {
            return PuzzleResponse.from(dailies.get(0));
        }

        // Fallback: return any first puzzle or create a starter daily puzzle
        return puzzleRepository.findAll().stream().findFirst()
                .map(PuzzleResponse::from)
                .orElseGet(this::createStarterPuzzle);
    }

    public List<PuzzleResponse> getPuzzles(String difficulty, Integer minRating, Integer maxRating) {
        if (difficulty != null && !difficulty.isBlank()) {
            try {
                Puzzle.Difficulty diff = Puzzle.Difficulty.valueOf(difficulty.toUpperCase());
                return puzzleRepository.findByDifficulty(diff).stream()
                        .map(PuzzleResponse::from)
                        .collect(Collectors.toList());
            } catch (IllegalArgumentException ignored) {}
        }

        if (minRating != null && maxRating != null) {
            return puzzleRepository.findByRatingRange(minRating, maxRating).stream()
                    .map(PuzzleResponse::from)
                    .collect(Collectors.toList());
        }

        return puzzleRepository.findAll().stream()
                .map(PuzzleResponse::from)
                .collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> submitAttempt(String puzzleId, String username, List<String> moves) {
        Puzzle puzzle = puzzleRepository.findById(puzzleId)
                .orElseThrow(() -> new ResourceNotFoundException("Puzzle", "id", puzzleId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        List<String> solution = Arrays.asList(puzzle.getSolution().split(","));

        // Check if user's moves match the solution
        boolean solved = moves != null && moves.size() >= solution.size();
        if (solved) {
            for (int i = 0; i < solution.size(); i++) {
                if (!moves.get(i).trim().equalsIgnoreCase(solution.get(i).trim())) {
                    solved = false;
                    break;
                }
            }
        }

        puzzle.setAttempts(puzzle.getAttempts() + 1);
        if (solved) {
            puzzle.setSolves(puzzle.getSolves() + 1);
        }
        puzzleRepository.save(puzzle);

        int ratingChange = solved ? 15 : -10;

        PuzzleAttempt attempt = PuzzleAttempt.builder()
                .puzzle(puzzle)
                .user(user)
                .solved(solved)
                .movesPlayed(moves != null ? String.join(",", moves) : "")
                .ratingChangeUser(ratingChange)
                .build();
        puzzleAttemptRepository.save(attempt);

        return Map.of(
                "solved", solved,
                "message", solved ? "Puzzle solved! +15 rating" : "Incorrect move. Try again!",
                "ratingChange", ratingChange,
                "solution", solution
        );
    }

    private PuzzleResponse createStarterPuzzle() {
        Puzzle puzzle = Puzzle.builder()
                .fen("r1bqkb1r/pppp1ppp/2n5/4p3/2B1n3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0 5")
                .solution("Bxf7+,Kxf7,Nxe5+")
                .rating(1400)
                .title("Fork Trick")
                .description("Find the tactical opportunity to win material or damage opponent king position.")
                .difficulty(Puzzle.Difficulty.EASY)
                .themes("fork,tactics")
                .isDailyPuzzle(true)
                .build();
        return PuzzleResponse.from(puzzleRepository.save(puzzle));
    }
}
