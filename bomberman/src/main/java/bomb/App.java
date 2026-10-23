package bomb;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(Game.WIDTH, Game.HEIGHT);
        Scene scene = new Scene(new Group(canvas));

        InputHandler input = new InputHandler();
        input.attach(scene);

        Game game = new Game(canvas, input);

        stage.setTitle("Bomberman");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        game.start();
    }

    public static void main(String[] args) {
        launch(args);
    }
}