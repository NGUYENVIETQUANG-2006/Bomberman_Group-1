package bomb;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.io.IOException;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.scene.Cursor;
import javafx.scene.input.MouseButton;

public class Game {
    public static final int COLS = 30;
    public static final int ROWS = 20;
    public static final int HUD_HEIGHT = 48;
    public static final int WIDTH = COLS * GameMap.TILE_SIZE;
    public static final int HEIGHT = ROWS * GameMap.TILE_SIZE + HUD_HEIGHT;
    private static final int MAX_LEVEL = 3;
    private static final String LEVEL_PATH = "/bomb/maps/level%d.txt";
    private static final int ENEMY_SCORE = 100;
    private static final double MAX_DT = 0.05;
    private enum State { PLAYING, PAUSED, GAME_OVER, VICTORY }
    private final GraphicsContext gc;
    private final InputHandler input;
    private final LevelLoader loader = new LevelLoader(new EntityFactory());
    private final AnimationTimer timer;
    private GameMap map;
    private Player player;
    private int level = 1;
    private State state;
    private final KeysBinding keys;
    private Runnable onExit;
    private static final String[] PAUSE_OPTIONS = {"Tiếp tục", "Lưu ván", "Chơi lại", "Về menu chính"};
    private int pauseIndex;
    private static final double PAUSE_TOP = HEIGHT / 2.0 - 55;
    private static final double PAUSE_ITEM_H = 44;
    private static final double PAUSE_ITEM_W = 320;
    private boolean scoreRecorded;

    public Game(Canvas canvas, InputHandler input, KeysBinding keys) {
        this.gc = canvas.getGraphicsContext2D();
        this.input = input;
        this.keys = keys;
        this.timer = new AnimationTimer() {
            private long last = 0;

            @Override
            public void handle(long now) {
                if (last == 0) {
                    last = now;
                    return;
                }

                double dt = Math.min((now - last) / 1e9, MAX_DT);
                last = now;

                update(dt);
                render();
                input.endFrame();
            }
        };

        canvas.setOnMouseMoved(e -> onMouseMoved(canvas, e.getX(), e.getY()));
        canvas.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                onMouseClicked(e.getX(), e.getY());
            }
        });
    }

    public void start() {
        startNewGame();
        timer.start();
    }

    public void startSaved(GameStorage.Save save) {
        level = Math.max(1, Math.min(MAX_LEVEL, save.level()));
        state = State.PLAYING;
        loadLevel();
        player.setPosition(save.x(), save.y());
        player.restoreState(save.health(), save.score(), save.speed(), save.blastRange(), save.bombCooldown());
        scoreRecorded = false;
        timer.start();
    }

    private void startNewGame() {
        level = 1;
        player = null;
        state = State.PLAYING;
        scoreRecorded = false;
        loadLevel();
    }

    private void loadLevel() {
        try {
            map = loader.load(String.format(LEVEL_PATH, level));
        } catch (IOException e) {
            throw new UncheckedIOException("Không nạp được màn " + level, e);
        }

        double px = map.getPlayerSpawnCol() * GameMap.TILE_SIZE;
        double py = map.getPlayerSpawnRow() * GameMap.TILE_SIZE;
        if (player == null) {
            player = new Player(px, py, input, keys);
        } else {
            player.setPosition(px, py);
        }
        map.setPlayer(player);
    }

    private void nextLevel() {
        level++;
        if (level > MAX_LEVEL) {
            state = State.VICTORY;
        } else {
            loadLevel();
        }
    }

    private void update(double dt) {
        if (state == State.PAUSED) {
            updatePauseMenu();
            return;
        }

        if (state != State.PLAYING) {
            if (input.wasPressed(KeyCode.ESCAPE)) {
                exitToMenu();
                return;
            }
            if (input.wasPressed(KeyCode.ENTER)) {
                startNewGame();
            }
            return;
        }

        if (input.wasPressed(KeyCode.ESCAPE)) {
            state = State.PAUSED;
            pauseIndex = 0;
            return;
        }

        player.update(dt);
        for (Enemy e : new ArrayList<>(map.getEnemies())) {
            e.update(dt);
        }

        updateBombs(dt);
        updateFlames(dt);
        removeDeadEnemies();
        pickUpItem();
        checkEndConditions();
    }

    private void updateBombs(double dt) {
        for (Bomb b : new ArrayList<>(map.getBombs())) {
            b.update(dt);
            if (b.isReadyToExplode()) {
                for (Flame f : b.explode(map)) {
                    map.addFlame(f);
                }
            }
        }
        map.getBombs().removeIf(Bomb::isExploded);
    }

    private void updateFlames(double dt) {
        for (Flame f : new ArrayList<>(map.getFlames())) {
            f.update(dt);
        }
        map.getFlames().removeIf(Flame::isExpired);
    }

    private void removeDeadEnemies() {
        for (Enemy e : map.getEnemies()) {
            if (!e.isAlive()) {
                if (e.markScoreAwarded()) {
                    player.addScore(ENEMY_SCORE);
                }
            }
        }
        map.getEnemies().removeIf(Enemy::isDeadDisplayExpired);
    }

    private void pickUpItem() {
        if (!player.isAlive()) {
            return;
        }
        int r = player.getRow();
        int c = player.getCol();
        if (map.getTile(r, c) instanceof Item item) {
            item.applyTo(player);
            map.removeTile(r, c);
        }
    }

    private void checkEndConditions() {
        if (!player.isAlive()) {
            state = State.GAME_OVER;
            recordScore();
            return;
        }
        boolean onPortal = map.getTile(player.getRow(), player.getCol()) instanceof Portal;
        if (onPortal /*&& map.getEnemies().isEmpty()*/) {
            nextLevel();
            if (state == State.VICTORY) recordScore();
        }
    }

    private void render() {
        int tile = GameMap.TILE_SIZE;

        gc.setFill(Color.web("#2e7d32"));
        gc.fillRect(0, 0, WIDTH, ROWS * tile);

        for (int r = 0; r < map.getRows(); r++) {
            for (int c = 0; c < map.getColumns(); c++) {
                Entity t = map.getTile(r, c);
                if (t != null) {
                    draw(t);
                }
            }
        }
        map.getFlames().forEach(this::draw);
        map.getBombs().forEach(this::draw);
        map.getEnemies().forEach(this::draw);
        if (player.isAlive()) {
            draw(player);
        }

        drawHud();
        if (state == State.PAUSED) {
            drawPauseMenu();
        } else if (state != State.PLAYING) {
            drawOverlay();
        }
    }

    private void draw(Entity e) {
        if (e.img != null) {
            e.render(gc);
        } else {
            drawPlaceholder(e);
        }
        if (e instanceof Brick brick && brick.getDrop() != null) {
            drawBrickDropIndicator(brick);
        }
    }

    private void drawBrickDropIndicator(Brick brick) {
        Item item = brick.getDrop();
        String label;
        Color color;
        if (item instanceof HealthItem) {
            label = "H";
            color = Color.HOTPINK;
        } else if (item instanceof CooldownItem) {
            label = "C";
            color = Color.CYAN;
        } else if (item instanceof FlameItem) {
            label = "F";
            color = Color.ORANGERED;
        } else if (item instanceof SpeedItem) {
            label = "S";
            color = Color.GOLD;
        } else {
            return;
        }

        double badgeSize = 17;
        double x = brick.getX() + GameMap.TILE_SIZE - badgeSize - 2;
        double y = brick.getY() + 2;
        gc.setFill(Color.color(0, 0, 0, 0.85));
        gc.fillRoundRect(x, y, badgeSize, badgeSize, 5, 5);
        gc.setFill(color);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        gc.setTextAlign(TextAlignment.CENTER);
        gc.fillText(label, x + badgeSize / 2, y + 13);
    }

    private void drawPlaceholder(Entity e) {
        double x = e.getX();
        double y = e.getY();
        double s = GameMap.TILE_SIZE;

        if (e instanceof Player p && p.isInvincible() && (System.nanoTime() / 100_000_000L) % 2 == 0) {
            return;
        }

        gc.setFill(placeholderColor(e));
        if (e instanceof Bomb || e instanceof GameCharacter) {
            gc.fillOval(x + 6, y + 6, s - 12, s - 12);
        } else if (e instanceof Item) {
            gc.fillRect(x + 10, y + 10, s - 20, s - 20);
        } else if (e instanceof Flame) {
            gc.fillRect(x + 4, y + 4, s - 8, s - 8);
        } else {
            gc.fillRect(x, y, s, s);
        }
    }

    private Color placeholderColor(Entity e) {
        if (e instanceof Wall) return Color.web("#424242");
        if (e instanceof Brick) return Color.web("#a1662f");
        if (e instanceof Portal) return Color.web("#1e88e5");
        if (e instanceof HealthItem) return Color.HOTPINK;
        if (e instanceof FlameItem) return Color.ORANGERED;
        if (e instanceof SpeedItem) return Color.GOLD;
        if (e instanceof CooldownItem) return Color.CYAN;
        if (e instanceof Bomb) return Color.BLACK;
        if (e instanceof Flame) return Color.ORANGE;
        if (e instanceof Player) return Color.WHITE;
        if (e instanceof Grunt) return Color.RED;
        if (e instanceof Juggernaut) return Color.PURPLE;
        return Color.MAGENTA;
    }

    private void drawHud() {
        double y = ROWS * GameMap.TILE_SIZE;
        gc.setFill(Color.web("#212121"));
        gc.fillRect(0, y, WIDTH, HUD_HEIGHT);

        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        gc.setTextAlign(TextAlignment.LEFT);
        String text = "Màn " + level + "/" + MAX_LEVEL
                + "     Máu: " + player.getHealth()
                + "     Điểm: " + player.getScore()
                + "     Tầm lửa: " + player.getBlastRange();
        gc.fillText(text, 16, y + 31);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        gc.fillText("Dấu trên gạch: H = Máu | C = Giảm hồi bom | F = Tầm lửa | S = Tốc độ",
                650, y + 30);
    }

    private void drawOverlay() {
        gc.setFill(Color.color(0, 0, 0, 0.65));
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        String title = state == State.VICTORY ? "CHIẾN THẮNG!" : "THUA RỒI!";
        gc.setFill(Color.WHITE);
        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        gc.fillText(title, WIDTH / 2.0, HEIGHT / 2.0 - 20);
        gc.setFont(Font.font("Arial", 22));
        gc.fillText("Điểm: " + player.getScore() + "   -   ENTER: chơi lại   ESC: về menu", WIDTH / 2.0, HEIGHT / 2.0 + 30);
    }

    private void drawPauseMenu() {
        gc.setFill(Color.color(0, 0, 0, 0.65));
        gc.fillRect(0, 0, WIDTH, HEIGHT);

        gc.setTextAlign(TextAlignment.CENTER);
        gc.setFill(Color.WHITE);
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 48));
        gc.fillText("TẠM DỪNG", WIDTH / 2.0, PAUSE_TOP - 40);

        gc.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        for (int i = 0; i < PAUSE_OPTIONS.length; i++) {
            double top = PAUSE_TOP + i * PAUSE_ITEM_H;
            boolean selected = i == pauseIndex;
            if (selected) {
                gc.setFill(Color.color(1, 1, 1, 0.15));
                gc.fillRoundRect(WIDTH / 2.0 - PAUSE_ITEM_W / 2, top, PAUSE_ITEM_W, PAUSE_ITEM_H - 4, 14, 14);
            }
            gc.setFill(selected ? Color.GOLD : Color.WHITE);
            gc.fillText(PAUSE_OPTIONS[i], WIDTH / 2.0, top + 30);
        }

        gc.setFill(Color.LIGHTGRAY);
        gc.setFont(Font.font("Arial", 18));
        gc.fillText("Chuột hoặc Lên/Xuống: chọn    Enter / Click: xác nhận    Esc: tiếp tục", WIDTH / 2.0, PAUSE_TOP + PAUSE_OPTIONS.length * PAUSE_ITEM_H + 40);
    }

    public void setOnExit(Runnable onExit) {
        this.onExit = onExit;
    }

    public void stop() {
        timer.stop();
    }

    private void updatePauseMenu() {
        if (input.wasPressed(KeyCode.ESCAPE)) {
            state = State.PLAYING;
            return;
        }
        int n = PAUSE_OPTIONS.length;
        if (input.wasPressed(KeyCode.UP)) {
            pauseIndex = (pauseIndex + n - 1) % n;
        }
        if (input.wasPressed(KeyCode.DOWN)) {
            pauseIndex = (pauseIndex + 1) % n;
        }
        if (input.wasPressed(KeyCode.ENTER)) {
            activatePauseOption();
        }
    }

    private void exitToMenu() {
        stop();
        if (onExit != null) {
            onExit.run();
        }
    }

    private int pauseOptionAt(double mx, double my) {
        if (Math.abs(mx - WIDTH / 2.0) > PAUSE_ITEM_W / 2) {
            return -1;
        }
        int i = (int) Math.floor((my - PAUSE_TOP) / PAUSE_ITEM_H);
        return (i >= 0 && i < PAUSE_OPTIONS.length) ? i : -1;
    }

    private void onMouseMoved(Canvas canvas, double mx, double my) {
        if (state != State.PAUSED) {
            canvas.setCursor(Cursor.DEFAULT);
            return;
        }
        int i = pauseOptionAt(mx, my);
        if (i >= 0) {
            pauseIndex = i;
        }
        canvas.setCursor(i >= 0 ? Cursor.HAND : Cursor.DEFAULT);
    }

    private void onMouseClicked(double mx, double my) {
        if (state != State.PAUSED) {
            return;
        }
        int i = pauseOptionAt(mx, my);
        if (i >= 0) {
            pauseIndex = i;
            activatePauseOption();
        }
    }

    private void activatePauseOption() {
        switch (pauseIndex) {
            case 0 -> state = State.PLAYING;
            case 1 -> saveGame();
            case 2 -> startNewGame();
            default -> exitToMenu();
        }
    }

    private void saveGame() {
        try {
            GameStorage.save(new GameStorage.Save(level, player.getX(), player.getY(), player.getHealth(),
                    player.getScore(), player.getSpeed(), player.getBlastRange(), player.getBombCooldown()));
            state = State.PLAYING;
        } catch (IOException e) {
            System.err.println("Không lưu được ván chơi: " + e.getMessage());
        }
    }

    private void recordScore() {
        if (scoreRecorded) return;
        scoreRecorded = true;
        try {
            GameStorage.recordScore("Người chơi", player.getScore());
        } catch (IOException e) {
            System.err.println("Không lưu được scoreboard: " + e.getMessage());
        }
    }
}
