package com.neonmate.chess.config;

import com.neonmate.chess.entity.Achievement;
import com.neonmate.chess.entity.Puzzle;
import com.neonmate.chess.entity.RatingHistory;
import com.neonmate.chess.entity.Tournament;
import com.neonmate.chess.entity.TournamentPlayer;
import com.neonmate.chess.entity.User;
import com.neonmate.chess.entity.UserAchievement;
import com.neonmate.chess.repository.AchievementRepository;
import com.neonmate.chess.repository.PuzzleRepository;
import com.neonmate.chess.repository.RatingHistoryRepository;
import com.neonmate.chess.repository.TournamentPlayerRepository;
import com.neonmate.chess.repository.TournamentRepository;
import com.neonmate.chess.repository.UserAchievementRepository;
import com.neonmate.chess.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PuzzleRepository puzzleRepository;
    private final TournamentRepository tournamentRepository;
    private final TournamentPlayerRepository tournamentPlayerRepository;
    private final AchievementRepository achievementRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final RatingHistoryRepository ratingHistoryRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            log.info("Database already initialized, checking rating history and achievements...");
            seedRatingHistoryIfEmpty();
            seedUserAchievementsIfEmpty();
            return;
        }

        log.info("Seeding initial NEONMATE CHESS database...");

        // 1. Seed Users
        User admin = userRepository.save(User.builder()
                .username("admin")
                .email("admin@neonmate.chess")
                .password(passwordEncoder.encode("admin123"))
                .rating(2400)
                .role(User.Role.ADMIN)
                .title("FM")
                .country("US")
                .bio("System Administrator & Chess Master")
                .gamesPlayed(150)
                .wins(110)
                .losses(25)
                .draws(15)
                .isOnline(true)
                .build());

        User neonKing = userRepository.save(User.builder()
                .username("NeonKing")
                .email("neonking@chess.com")
                .password(passwordEncoder.encode("password123"))
                .rating(2847)
                .role(User.Role.USER)
                .title("GM")
                .country("IN")
                .bio("Chess is the art of cyber analysis.")
                .gamesPlayed(1247)
                .wins(834)
                .losses(287)
                .draws(126)
                .isOnline(true)
                .build());

        User cyberQueen = userRepository.save(User.builder()
                .username("CyberQueen")
                .email("cyberqueen@chess.com")
                .password(passwordEncoder.encode("password123"))
                .rating(2654)
                .role(User.Role.USER)
                .title("IM")
                .country("US")
                .bio("Tactics flow through the matrix.")
                .gamesPlayed(892)
                .wins(591)
                .losses(198)
                .draws(103)
                .isOnline(false)
                .build());

        User voidBishop = userRepository.save(User.builder()
                .username("VoidBishop")
                .email("voidbishop@chess.com")
                .password(passwordEncoder.encode("password123"))
                .rating(2401)
                .role(User.Role.USER)
                .country("RU")
                .gamesPlayed(2103)
                .wins(1204)
                .losses(712)
                .draws(187)
                .isOnline(true)
                .build());

        User quantumRook = userRepository.save(User.builder()
                .username("QuantumRook")
                .email("quantumrook@chess.com")
                .password(passwordEncoder.encode("password123"))
                .rating(1987)
                .role(User.Role.USER)
                .country("DE")
                .gamesPlayed(567)
                .wins(298)
                .losses(201)
                .draws(68)
                .isOnline(false)
                .build());

        // 2. Seed Puzzles
        puzzleRepository.saveAll(List.of(
                Puzzle.builder()
                        .fen("r1bqkb1r/pppp1ppp/2n5/4p3/2B1n3/5N2/PPPP1PPP/RNBQK2R w KQkq - 0 5")
                        .solution("Bxf7+,Kxf7,Nxe5+")
                        .rating(1450)
                        .title("The Fried Liver Echo")
                        .description("White exploits an unprotected king to win initiative.")
                        .difficulty(Puzzle.Difficulty.EASY)
                        .themes("fork,sacrifice,tactics")
                        .isDailyPuzzle(true)
                        .dailyDate(LocalDateTime.now())
                        .build(),

                Puzzle.builder()
                        .fen("6k1/5ppp/8/8/8/8/5PPP/4R1K1 w - - 0 1")
                        .solution("Re8#")
                        .rating(800)
                        .title("Back-Rank Strike")
                        .description("Classic corridor checkmate on the back rank.")
                        .difficulty(Puzzle.Difficulty.EASY)
                        .themes("backRank,mate,mateIn1")
                        .build(),

                Puzzle.builder()
                        .fen("r1b2rk1/pp3ppp/2n5/8/2B5/5Q2/P4PPP/4R1K1 w - - 0 1")
                        .solution("Qxf7+,Rxf7,Re8#")
                        .rating(1850)
                        .title("Deflection & Back Rank")
                        .description("Queen sacrifice forcing rook away from the back rank.")
                        .difficulty(Puzzle.Difficulty.MEDIUM)
                        .themes("sacrifice,deflection,mateIn2")
                        .build(),

                Puzzle.builder()
                        .fen("r4rk1/1pp1qppp/p1np4/8/2BPP1b1/2N2N2/PPP3PP/R2Q1RK1 w - - 0 12")
                        .solution("Nd5,Qd8,c3")
                        .rating(2200)
                        .title("Outpost Domination")
                        .description("Establish an insurmountable knight outpost in center.")
                        .difficulty(Puzzle.Difficulty.HARD)
                        .themes("positional,outpost,strategy")
                        .build()
        ));

        // 3. Seed Tournaments
        Tournament t1 = tournamentRepository.save(Tournament.builder()
                .name("Neon Cyber Blitz Championship")
                .type(Tournament.TournamentType.SWISS)
                .timeControl("3+2")
                .status(Tournament.TournamentStatus.UPCOMING)
                .maxPlayers(64)
                .prize("$5,000 + Cyber Trophy")
                .rounds(7)
                .currentRound(0)
                .startDate(LocalDateTime.now().plusDays(2))
                .creator(admin)
                .build());

        tournamentPlayerRepository.save(TournamentPlayer.builder().tournament(t1).user(admin).seed(1).score(0.0).build());
        tournamentPlayerRepository.save(TournamentPlayer.builder().tournament(t1).user(neonKing).seed(2).score(0.0).build());
        tournamentPlayerRepository.save(TournamentPlayer.builder().tournament(t1).user(cyberQueen).seed(3).score(0.0).build());

        Tournament t2 = tournamentRepository.save(Tournament.builder()
                .name("Matrix Rapid Open")
                .type(Tournament.TournamentType.KNOCKOUT)
                .timeControl("10+0")
                .status(Tournament.TournamentStatus.ONGOING)
                .maxPlayers(32)
                .prize("$2,500")
                .rounds(5)
                .currentRound(2)
                .startDate(LocalDateTime.now().minusHours(3))
                .creator(neonKing)
                .build());

        tournamentPlayerRepository.save(TournamentPlayer.builder().tournament(t2).user(voidBishop).seed(1).score(2.0).build());
        tournamentPlayerRepository.save(TournamentPlayer.builder().tournament(t2).user(quantumRook).seed(2).score(1.5).build());

        // 4. Seed Achievements
        achievementRepository.saveAll(List.of(
                Achievement.builder()
                        .code("FIRST_WIN")
                        .title("First Blood")
                        .description("Win your first rated chess match.")
                        .icon("⚔️")
                        .rarity(Achievement.Rarity.COMMON)
                        .maxProgress(1)
                        .build(),
                Achievement.builder()
                        .code("TACTICIAN_10")
                        .title("Tactical Vision")
                        .description("Successfully solve 10 chess puzzles.")
                        .icon("🧩")
                        .rarity(Achievement.Rarity.RARE)
                        .maxProgress(10)
                        .build(),
                Achievement.builder()
                        .code("AI_DESTROYER")
                        .title("Cyber Overlord")
                        .description("Defeat Level 5 Maximum AI.")
                        .icon("🤖")
                        .rarity(Achievement.Rarity.EPIC)
                        .maxProgress(1)
                        .build(),
                Achievement.builder()
                        .code("RATING_2000")
                        .title("Neon Grandmaster")
                        .description("Surpass a 2000 rating threshold.")
                        .icon("👑")
                        .rarity(Achievement.Rarity.LEGENDARY)
                        .maxProgress(2000)
                        .build()
        ));

        seedRatingHistoryIfEmpty();
        seedUserAchievementsIfEmpty();
        log.info("NEONMATE CHESS database initialized successfully!");
    }

    private void seedUserAchievementsIfEmpty() {
        if (userAchievementRepository.count() > 0) {
            return;
        }

        log.info("Seeding initial user achievements...");
        List<Achievement> achievements = achievementRepository.findAll();
        List<User> users = userRepository.findAll();

        for (User user : users) {
            for (Achievement ach : achievements) {
                boolean unlocked = false;
                int progress = 0;

                switch (ach.getCode()) {
                    case "FIRST_WIN":
                        unlocked = user.getWins() > 0;
                        progress = unlocked ? 1 : 0;
                        break;
                    case "TACTICIAN_10":
                        if ("admin".equals(user.getUsername()) || "NeonKing".equals(user.getUsername())) {
                            unlocked = true;
                            progress = 10;
                        } else {
                            progress = Math.min(ach.getMaxProgress(), 6);
                        }
                        break;
                    case "AI_DESTROYER":
                        if ("NeonKing".equals(user.getUsername())) {
                            unlocked = true;
                            progress = 1;
                        }
                        break;
                    case "RATING_2000":
                        unlocked = user.getRating() >= 2000;
                        progress = Math.min(ach.getMaxProgress(), user.getRating());
                        break;
                    default:
                        break;
                }

                UserAchievement ua = UserAchievement.builder()
                        .user(user)
                        .achievement(ach)
                        .progress(progress)
                        .unlockedAt(unlocked ? LocalDateTime.now().minusDays(5) : null)
                        .build();
                userAchievementRepository.save(ua);
            }
        }
        log.info("User achievements seeded successfully.");
    }

    private void seedRatingHistoryIfEmpty() {
        if (ratingHistoryRepository.count() > 0) {
            return;
        }

        log.info("Seeding initial rating history for users...");
        List<User> users = userRepository.findAll();
        for (User u : users) {
            int currentRating = u.getRating();
            int base = Math.max(400, currentRating - 120);

            // Seed 8 historical milestone points over the last 30 days
            for (int i = 0; i < 8; i++) {
                int progress = (int) ((currentRating - base) * ((double) i / 7.0));
                int pointRating = base + progress;
                LocalDateTime date = LocalDateTime.now().minusDays(28 - (i * 4));

                RatingHistory rh = RatingHistory.builder()
                        .user(u)
                        .rating(pointRating)
                        .ratingChange(i == 0 ? 0 : (currentRating - base) / 7)
                        .recordedAt(date)
                        .build();
                ratingHistoryRepository.save(rh);
            }
        }
        log.info("Rating history seeded for {} users.", users.size());
    }
}
