package bomberman;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(720, 528);
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        stage.setTitle("Bomberman");
        stage.setScene(new Scene(new Group(canvas)));
        stage.show();
    }

    public static void main(String[] args) { launch(args); }
}