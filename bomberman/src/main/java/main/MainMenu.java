package main;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
 
/**
 * Menu chính. Lớp này chỉ dựng giao diện và gọi lại các hành động được truyền vào
 * (Runnable), nên nó không cần biết "Chơi" hay "Cài đặt" thực sự làm gì: App quyết định.
 */
public class MainMenu extends VBox {
 
    public MainMenu(Runnable onPlay, Runnable onSettings, Runnable onQuit) {
        setAlignment(Pos.CENTER);
        setSpacing(18);
        setStyle("-fx-background-color: #1b5e20;");
 
        Label title = new Label("BOMBERMAN");
        title.setStyle("-fx-font-size: 64px; -fx-font-weight: bold; -fx-text-fill: white;");
 
        getChildren().addAll(
                title,
                menuButton("Chơi", onPlay),
                menuButton("Cài đặt", onSettings),
                menuButton("Thoát", onQuit));
    }
 
    /** Nút dùng chung cho các menu. */
    static Button menuButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefWidth(240);
        button.setStyle("-fx-font-size: 20px;");
        button.setOnAction(e -> action.run());
        return button;
    }
}
