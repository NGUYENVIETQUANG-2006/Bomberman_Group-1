package core;

import java.io.IOException;
import java.nio.file.Path;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import UI.MainMenu;
import UI.SettingsMenu;
import core.input.InputHandler;
import core.input.KeysBinding;
import UI.ScoreboardMenu;

public class App extends Application {
    private static final Path KEYS_FILE = Path.of("bomberman", "src", "resources", "saves", "keys_p1.properties");
    private static final Path DISPLAY_FILE = Path.of("bomberman", "src", "resources", "saves", "display.properties");
    private final KeysBinding keys = new KeysBinding(KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.SPACE);
    private Stage stage;
    private DisplayResolution resolution = DisplayResolution.LOW;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        keys.load(KEYS_FILE);
        resolution = DisplayResolution.load(DISPLAY_FILE);

        stage.setTitle("Bomberman");
        stage.setResizable(false);
        showMainMenu();
        stage.show();
    }

    @Override
    public void stop() {
        AudioManager.stopAll();
    }

    private void showMainMenu() {
        AudioManager.playMenuMusic();
        show(new MainMenu(this::startNewGame, this::continueGame, this::showScoreboard, this::showSettings, Platform::exit));
    }

    private void continueGame() {
        try {
            showGame(GameStorage.load());
        } catch (IOException | RuntimeException e) {
            show(new MainMenu(this::startNewGame, this::continueGame, this::showScoreboard, this::showSettings, Platform::exit));
        }
    }

    private void showScoreboard() {
        show(new ScoreboardMenu(this::showMainMenu));
    }

    private void showSettings() {
        show(new SettingsMenu(keys, KEYS_FILE, resolution, this::applyResolution, this::showMainMenu));
    }

    private void applyResolution(DisplayResolution selected) {
        resolution = selected;
    }

    private void startNewGame(String playerName) {
        startGame(null, playerName);
    }

    private void showGame(GameStorage.Save savedGame) {
        startGame(savedGame, savedGame.playerName());
    }

    private void startGame(GameStorage.Save savedGame, String playerName) {
        AudioManager.stopMusic();
        Canvas canvas = new Canvas(Game.WIDTH, Game.HEIGHT);
        Pane gameRoot = new Pane(canvas);
        canvas.widthProperty().bind(gameRoot.widthProperty());
        canvas.heightProperty().bind(gameRoot.heightProperty());
        Scene scene = new Scene(gameRoot, resolution.width(), resolution.height());

        InputHandler input = new InputHandler();
        input.attach(scene);

        Game game = new Game(canvas, input, keys);
        game.setOnExit(this::showMainMenu);

        stage.setScene(scene);
        stage.sizeToScene();
        if (savedGame == null) game.start(playerName); else game.startSaved(savedGame);
    }

    private void show(Parent root) {
        stage.setScene(new Scene(root, resolution.width(), resolution.height()));
        stage.sizeToScene();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
