package main;

import bomb.GameStorage;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

public class ScoreboardMenu extends VBox {
    public ScoreboardMenu(Runnable onBack) {
        setAlignment(Pos.CENTER);
        setSpacing(12);
        setStyle("-fx-background-color: #1b5e20; -fx-padding: 24px;");
        Label title = new Label("BẢNG XẾP HẠNG");
        title.setFont(Font.font(36));
        title.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
        getChildren().add(title);
        try {
            var scores = GameStorage.loadScores();
            if (scores.isEmpty()) {
                getChildren().add(scoreLine("Chưa có điểm nào được ghi nhận"));
            } else {
                int rank = 1;
                for (GameStorage.ScoreEntry entry : scores) {
                    getChildren().add(scoreLine(String.format("%2d. %-18s %6d   %s", rank++, entry.name(), entry.score(), entry.date())));
                }
            }
        } catch (Exception e) {
            getChildren().add(scoreLine("Không đọc được bảng điểm"));
        }
        getChildren().add(MainMenu.menuButton("Quay lại", onBack));
    }

    private Label scoreLine(String text) {
        Label line = new Label(text);
        line.setStyle("-fx-font-family: monospace; -fx-font-size: 18px; -fx-text-fill: white;");
        return line;
    }
}
