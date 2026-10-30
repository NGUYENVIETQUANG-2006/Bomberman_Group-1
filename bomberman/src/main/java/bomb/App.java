package bomb;

import java.io.IOException;
import java.nio.file.Path;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

import main.MainMenu;
import main.SettingsMenu;

public class App extends Application {
    private static final Path KEYS_FILE =
            Path.of(System.getProperty("user.home"), ".bomberman", "keys_p1.properties");

    private final KeysBinding keys = KeysBinding.arrows();
    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException {
        this.stage = stage;
        keys.load(KEYS_FILE);

        stage.setTitle("Bomberman");
        stage.setResizable(false);
        showMainMenu();
        stage.show();
    }

    private void showMainMenu() {
        show(new MainMenu(this::showGame, this::showSettings, Platform::exit));
    }

    private void showSettings() {
        show(new SettingsMenu(keys, KEYS_FILE, this::showMainMenu));
    }

    private void showGame() {
        Canvas canvas = new Canvas(Game.WIDTH, Game.HEIGHT);
        Scene scene = new Scene(new Group(canvas));

        InputHandler input = new InputHandler();
        input.attach(scene);

        Game game = new Game(canvas, input, keys);
        game.setOnExit(this::showMainMenu);

        stage.setScene(scene);
        stage.sizeToScene();
        game.start();
    }

    private void show(Parent root) {
        stage.setScene(new Scene(root, Game.WIDTH, Game.HEIGHT));
        stage.sizeToScene();
    }

    public static void main(String[] args) {
        launch(args);
    }
}