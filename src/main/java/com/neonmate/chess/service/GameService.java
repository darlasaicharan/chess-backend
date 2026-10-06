package com.neonmate.chess.service;

import com.neonmate.chess.dto.request.CreateGameRequest;
import com.neonmate.chess.dto.request.MoveRequest;
import com.neonmate.chess.dto.response.GameResponse;
import com.neonmate.chess.entity.Game;
import com.neonmate.chess.entity.GameMove;
import com.neonmate.chess.entity.User;
import com.neonmate.chess.exception.ResourceNotFoundException;
import com.neonmate.chess.repository.GameMoveRepository;
import com.neonmate.chess.repository.GameRepository;
import com.neonmate.chess.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameService {

    private final GameRepository gameRepository;
    private final GameMoveRepository gameMoveRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    @Transactional
    public GameResponse createAIGame(String username, CreateGameRequest request) {
        User player = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        // Create an AI "user" placeholder — in production you'd have a system user
        User aiUser = userRepository.findByUsername("NEONMATE_AI")
                .orElseGet(() -> userRepository.save(User.builder()
                        .username("NEONMATE_AI")
                        .email("ai@neonmate.chess")
                        .password("$2a$10$nopassword")
                        .rating(getAIRating(request.getAiLevel()))
                        .role(User.Role.USER)
                        .build()));

        // Assign colors based on player request, or random if not specified
        boolean playerIsWhite;
        if ("b".equalsIgnoreCase(request.getPlayerColor())) {
            playerIsWhite = false;
        } else if ("w".equalsIgnoreCase(request.getPlayerColor())) {
            playerIsWhite = true;
        } else {
            playerIsWhite = Math.random() > 0.5;
        }

        String[] tc = request.getTimeControl().split("\\+");
        int initialTime = Integer.parseInt(tc[0]) * 60;
        int increment = tc.length > 1 ? Integer.parseInt(tc[1]) : 0;

        Game game = Game.builder()
                .white(playerIsWhite ? player : aiUser)
                .black(playerIsWhite ? aiUser : player)
                .type(Game.GameType.AI)
                .timeControl(request.getTimeControl())
                .initialTimeSec(initialTime)
                .incrementSec(increment)
                .rated(false)  // AI games are unrated
                .status(Game.GameStatus.ACTIVE)
                .whiteRatingBefore(playerIsWhite ? player.getRating() : aiUser.getRating())
                .blackRatingBefore(playerIsWhite ? aiUser.getRating() : player.getRating())
                .startedAt(LocalDateTime.now())
                .build();

        game = gameRepository.save(game);
        return GameResponse.from(game);
    }

    public GameResponse getGame(String gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game", "id", gameId));
        return GameResponse.from(game);
    }

    public List<GameResponse> getGames(String username, int page, int size) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        Page<Game> games = gameRepository.findByUser(user, PageRequest.of(page, size));
        return games.stream().map(GameResponse::from).toList();
    }

    @Transactional
    public GameResponse makeMove(String gameId, String username, MoveRequest request) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game", "id", gameId));

        if (game.getStatus() != Game.GameStatus.ACTIVE) {
            throw new IllegalStateException("Game is not active");
        }

        // Determine move color based on existing move count
        long moveCount = gameMoveRepository.countByGameId(gameId);
        String color = (moveCount % 2 == 0) ? "w" : "b";
        int moveNumber = (int) (moveCount / 2) + 1;

        // Validate it's this user's turn
        User currentPlayer = color.equals("w") ? game.getWhite() : game.getBlack();
        if (!currentPlayer.getUsername().equals(username)) {
            throw new IllegalStateException("It's not your turn");
        }

        GameMove move = GameMove.builder()
                .game(game)
                .moveNumber(moveNumber)
                .color(color)
                .san(request.getFrom() + request.getTo())  // Simplified; real SAN would come from engine
                .fromSquare(request.getFrom())
                .toSquare(request.getTo())
                .promotion(request.getPromotion())
                .build();

        gameMoveRepository.save(move);
        game.setMoveCount((int) moveCount + 1);
        gameRepository.save(game);

        return GameResponse.from(game);
    }

    @Transactional
    public GameResponse resign(String gameId, String username) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game", "id", gameId));

        if (game.getStatus() != Game.GameStatus.ACTIVE) {
            throw new IllegalStateException("Game is not active");
        }

        boolean isWhite = game.getWhite().getUsername().equals(username);
        game.setResult(isWhite ? Game.GameResult.BLACK_WIN : Game.GameResult.WHITE_WIN);
        game.setStatus(Game.GameStatus.COMPLETED);
        game.setEndedAt(LocalDateTime.now());

        // Update ratings if rated
        if (game.isRated()) {
            User winner = isWhite ? game.getBlack() : game.getWhite();
            User loser = isWhite ? game.getWhite() : game.getBlack();
            int[] changes = userService.updateRatings(winner, loser, false);

            if (isWhite) {
                game.setWhiteRatingChange(changes[1]);
                game.setBlackRatingChange(changes[0]);
            } else {
                game.setWhiteRatingChange(changes[0]);
                game.setBlackRatingChange(changes[1]);
            }
        }

        // Update game count
        updatePlayerStats(game);

        game = gameRepository.save(game);
        return GameResponse.from(game);
    }

    @Transactional
    public GameResponse offerDraw(String gameId, String username) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game", "id", gameId));

        // For simplicity, auto-accept draw (in real app, this would notify opponent via WebSocket)
        game.setResult(Game.GameResult.DRAW);
        game.setStatus(Game.GameStatus.COMPLETED);
        game.setEndedAt(LocalDateTime.now());

        if (game.isRated()) {
            int[] changes = userService.updateRatings(game.getWhite(), game.getBlack(), true);
            game.setWhiteRatingChange(changes[0]);
            game.setBlackRatingChange(changes[1]);
        }

        updatePlayerStats(game);

        game = gameRepository.save(game);
        return GameResponse.from(game);
    }

    public String exportPGN(String gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game", "id", gameId));

        List<GameMove> moves = gameMoveRepository.findByGameIdOrderByMoveNumberAscColorAsc(gameId);

        StringBuilder pgn = new StringBuilder();
        pgn.append("[Event \"NEONMATE Chess\"]\n");
        pgn.append("[White \"").append(game.getWhite().getUsername()).append("\"]\n");
        pgn.append("[Black \"").append(game.getBlack().getUsername()).append("\"]\n");
        pgn.append("[Result \"").append(game.getResultNotation()).append("\"]\n");
        pgn.append("[TimeControl \"").append(game.getTimeControl()).append("\"]\n");
        if (game.getOpening() != null) pgn.append("[Opening \"").append(game.getOpening()).append("\"]\n");
        if (game.getEco() != null) pgn.append("[ECO \"").append(game.getEco()).append("\"]\n");
        pgn.append("\n");

        for (GameMove move : moves) {
            if (move.getColor().equals("w")) {
                pgn.append(move.getMoveNumber()).append(". ");
            }
            pgn.append(move.getSan()).append(" ");
        }

        pgn.append(game.getResultNotation());
        return pgn.toString();
    }

    public String exportFEN(String gameId) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game", "id", gameId));
        return game.getFinalFen() != null
                ? game.getFinalFen()
                : "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
    }

    @Transactional
    public GameResponse createGame(String username, CreateGameRequest request) {
        if ("AI".equalsIgnoreCase(request.getType())) {
            return createAIGame(username, request);
        }

        User player = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));

        User opponent = null;
        if (request.getOpponentId() != null && !request.getOpponentId().isBlank()) {
            opponent = userRepository.findById(request.getOpponentId())
                    .orElse(null);
        }

        // Check if there is an open game waiting for an opponent with the same time control
        if (opponent == null) {
            List<Game> waitingGames = gameRepository.findWaitingGames(request.getTimeControl());
            for (Game g : waitingGames) {
                if (!g.getWhite().getId().equals(player.getId())) {
                    g.setBlack(player);
                    g.setStatus(Game.GameStatus.ACTIVE);
                    g.setBlackRatingBefore(player.getRating());
                    g.setStartedAt(LocalDateTime.now());
                    return GameResponse.from(gameRepository.save(g));
                }
            }
        }

        String[] tc = (request.getTimeControl() != null ? request.getTimeControl() : "5+0").split("\\+");
        int initialTime = Integer.parseInt(tc[0]) * 60;
        int increment = tc.length > 1 ? Integer.parseInt(tc[1]) : 0;

        Game game = Game.builder()
                .white(player)
                .black(opponent)
                .type(request.getOpponentId() != null ? Game.GameType.FRIEND : Game.GameType.ONLINE)
                .timeControl(request.getTimeControl() != null ? request.getTimeControl() : "5+0")
                .initialTimeSec(initialTime)
                .incrementSec(increment)
                .rated(request.isRated())
                .status(opponent != null ? Game.GameStatus.ACTIVE : Game.GameStatus.WAITING)
                .whiteRatingBefore(player.getRating())
                .blackRatingBefore(opponent != null ? opponent.getRating() : 0)
                .startedAt(opponent != null ? LocalDateTime.now() : null)
                .build();

        return GameResponse.from(gameRepository.save(game));
    }

    private void updatePlayerStats(Game game) {
        User white = game.getWhite();
        User black = game.getBlack();

        white.setGamesPlayed(white.getGamesPlayed() + 1);
        black.setGamesPlayed(black.getGamesPlayed() + 1);

        int whiteRatingChange = game.getWhiteRatingChange();
        int blackRatingChange = game.getBlackRatingChange();

        switch (game.getResult()) {
            case WHITE_WIN -> {
                white.setWins(white.getWins() + 1);
                black.setLosses(black.getLosses() + 1);
            }
            case BLACK_WIN -> {
                black.setWins(black.getWins() + 1);
                white.setLosses(white.getLosses() + 1);
            }
            case DRAW -> {
                white.setDraws(white.getDraws() + 1);
                black.setDraws(black.getDraws() + 1);
            }
            default -> {}
        }

        userRepository.save(white);
        userRepository.save(black);

        // Save real rating snapshots for both players so charts show real data
        // Only save for non-AI users (skip NEONMATE_AI system account)
        if (!"NEONMATE_AI".equals(white.getUsername())) {
            userService.saveRatingSnapshot(white, game, whiteRatingChange);
        }
        if (!"NEONMATE_AI".equals(black.getUsername())) {
            userService.saveRatingSnapshot(black, game, blackRatingChange);
        }
    }

    /**
     * Called by the frontend after a client-side AI game ends.
     * Saves the completed game record to the database with the result,
     * PGN moves, final FEN, and updates player stats + rating history.
     */
    @Transactional
    public GameResponse finalizeAIGame(String gameId, String username, String result, String pgn, String finalFen, int moveCount) {
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResourceNotFoundException("Game", "id", gameId));

        if (game.getStatus() == Game.GameStatus.COMPLETED) {
            return GameResponse.from(game);
        }

        // Set result
        game.setResult(switch (result) {
            case "white" -> Game.GameResult.WHITE_WIN;
            case "black" -> Game.GameResult.BLACK_WIN;
            default -> Game.GameResult.DRAW;
        });
        game.setStatus(Game.GameStatus.COMPLETED);
        game.setEndedAt(LocalDateTime.now());
        game.setPgn(pgn);
        game.setFinalFen(finalFen);
        game.setMoveCount(moveCount);

        // AI games are unrated — no Elo change, but still track stats
        updatePlayerStats(game);
        game = gameRepository.save(game);
        return GameResponse.from(game);
    }

    private int getAIRating(int level) {
        return switch (level) {
            case 1 -> 800;
            case 2 -> 1200;
            case 3 -> 1600;
            case 4 -> 2000;
            case 5 -> 2800;
            default -> 1600;
        };
    }
}
