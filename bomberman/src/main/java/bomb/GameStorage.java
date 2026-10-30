package bomb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

public final class GameStorage {
    private static final Path DIRECTORY = Path.of(System.getProperty("user.home"), ".bomberman");
    private static final Path SAVE_FILE = DIRECTORY.resolve("save.properties");
    private static final Path SCORE_FILE = DIRECTORY.resolve("scoreboard.csv");

    private GameStorage() {}

    public record Save(int level, double x, double y, int health, int score,
                       double speed, int blastRange, double bombCooldown) {}
    public record ScoreEntry(String name, int score, LocalDate date) {}

    public static boolean hasSave() { return Files.isRegularFile(SAVE_FILE); }

    public static void save(Save save) throws IOException {
        Files.createDirectories(DIRECTORY);
        Properties data = new Properties();
        data.setProperty("level", Integer.toString(save.level()));
        data.setProperty("x", Double.toString(save.x()));
        data.setProperty("y", Double.toString(save.y()));
        data.setProperty("health", Integer.toString(save.health()));
        data.setProperty("score", Integer.toString(save.score()));
        data.setProperty("speed", Double.toString(save.speed()));
        data.setProperty("blastRange", Integer.toString(save.blastRange()));
        data.setProperty("bombCooldown", Double.toString(save.bombCooldown()));
        try (OutputStream out = Files.newOutputStream(SAVE_FILE)) {
            data.store(out, "Bomberman save");
        }
    }

    public static Save load() throws IOException {
        Properties data = new Properties();
        try (InputStream in = Files.newInputStream(SAVE_FILE)) { data.load(in); }
        return new Save(Integer.parseInt(data.getProperty("level")),
                Double.parseDouble(data.getProperty("x")), Double.parseDouble(data.getProperty("y")),
                Integer.parseInt(data.getProperty("health")), Integer.parseInt(data.getProperty("score")),
                Double.parseDouble(data.getProperty("speed")), Integer.parseInt(data.getProperty("blastRange")),
                Double.parseDouble(data.getProperty("bombCooldown")));
    }

    public static void recordScore(String name, int score) throws IOException {
        List<ScoreEntry> entries = new ArrayList<>(loadScores());
        entries.add(new ScoreEntry(cleanName(name), score, LocalDate.now()));
        entries.sort(Comparator.comparingInt(ScoreEntry::score).reversed());
        Files.createDirectories(DIRECTORY);
        List<String> lines = entries.stream().limit(10).map(e -> escapeCsv(e.name()) + "," + e.score() + "," + e.date()).toList();
        Files.write(SCORE_FILE, lines);
    }

    public static List<ScoreEntry> loadScores() throws IOException {
        if (!Files.exists(SCORE_FILE)) return List.of();
        List<ScoreEntry> entries = new ArrayList<>();
        for (String line : Files.readAllLines(SCORE_FILE)) {
            String[] columns = line.split(",", -1);
            if (columns.length != 3) continue;
            try {
                entries.add(new ScoreEntry(unescapeCsv(columns[0]), Integer.parseInt(columns[1]), LocalDate.parse(columns[2])));
            } catch (RuntimeException ignored) {}
        }
        entries.sort(Comparator.comparingInt(ScoreEntry::score).reversed());
        return entries.stream().limit(10).toList();
    }

    private static String cleanName(String name) {
        if (name == null || name.isBlank()) return "Người chơi";
        return name.replace(',', ' ').replace('\n', ' ').replace('\r', ' ').trim();
    }
    private static String escapeCsv(String value) { return value.replace("%", "%25").replace(",", "%2C"); }
    private static String unescapeCsv(String value) { return value.replace("%2C", ",").replace("%25", "%"); }
}
