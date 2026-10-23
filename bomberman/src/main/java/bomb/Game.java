package bomb;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

public class Game {
    public static final int COLS = 20;
    public static final int ROWS = 15;
    public static final int HUD_HEIGHT = 48;
    public static final int WIDTH = COLS * GameMap.TILE_SIZE;
    public static final int HEIGHT = ROWS * GameMap.TILE_SIZE + HUD_HEIGHT;

    private static final int MAX_LEVEL = 3;
    private static final String LEVEL_PATH = "/bomb/maps/level%d.txt";
    private static final int ENEMY_SCORE = 100;
    private static final double MAX_DT = 0.05;

    private enum State { PLAYING, GAME_OVER, VICTORY }

    private final GraphicsContext gc;
    private final InputHandler input;
    private final LevelLoader loader = new LevelLoader(new EntityFactory());
    private final AnimationTimer timer;

    private GameMap map;
    private Player player;
    private int level = 1;
    private State state;

    public Game(Canvas canvas, InputHandler input) {
        this.gc = canvas.getGraphicsContext2D();
        this.input = input;
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
    }

    public void start() {
        startNewGame();
        timer.start();
    }

    private void startNewGame() {
        level = 1;
        player = null;
        state = State.PLAYING;
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
            player = new Player(px, py, input, KeysBinding.wasd());
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
        if (state != State.PLAYING) {
            if (input.wasPressed(KeyCode.ENTER)) {
                startNewGame();
            }
            return;
        }

        player.update(dt);
        for (Enemy e : new ArrayList<>(map.getEnemies())) {
            e.update(dt);
        }
        updateBombs(dt);
        updateFlames(dt);
        updateItems(dt);
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

    private void updateItems(double dt) {
        for (int r = 0; r < map.getRows(); r++) {
            for (int c = 0; c < map.getColumns(); c++) {
                if (map.getTile(r, c) instanceof Item item) {
                    item.update(dt);
                    if (item.isExpired()) {
                        map.removeTile(r, c);
                    }
                }
            }
        }
    }

    private void removeDeadEnemies() {
        for (Enemy e : map.getEnemies()) {
            if (!e.isAlive()) {
                player.addScore(ENEMY_SCORE);
            }
        }
        map.getEnemies().removeIf(e -> !e.isAlive());
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
            return;
        }
        boolean onPortal = map.getTile(player.getRow(), player.getCol()) instanceof Portal;
        if (onPortal && map.getEnemies().isEmpty()) {
            nextLevel();
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
        if (state != State.PLAYING) {
            drawOverlay();
        }
    }

    private void draw(Entity e) {
        if (e.img != null) {
            e.render(gc);
        } else {
            drawPlaceholder(e);
        }
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
        gc.fillText("Điểm: " + player.getScore() + "   -   Nhấn ENTER để chơi lại",
                WIDTH / 2.0, HEIGHT / 2.0 + 30);
    }
}