package main;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class MainMenu extends VBox {

    public MainMenu(Consumer<String> onPlay, Runnable onContinue, Runnable onScores, Runnable onSettings, Runnable onQuit) {
        setAlignment(Pos.CENTER);
        setSpacing(18);
        setStyle("-fx-background-color: #1b5e20;");

        Label title = new Label("BOMBERMAN");
        title.setStyle("-fx-font-size: 64px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label namePrompt = new Label("Nhập tên người chơi");
        namePrompt.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        TextField playerName = new TextField("Người chơi");
        playerName.setPromptText("Tên người chơi");
        playerName.setMaxWidth(240);
        playerName.setStyle("-fx-font-size: 18px;");

        List<Button> mainButtons = new ArrayList<>();
        Button playButton = menuButton("Chơi mới", () -> {});
        mainButtons.add(playButton);
        if (bomb.GameStorage.hasSave()) mainButtons.add(menuButton("Tiếp tục", onContinue));
        mainButtons.add(menuButton("Bảng xếp hạng", onScores));
        mainButtons.add(menuButton("Cài đặt", onSettings));
        mainButtons.add(menuButton("Thoát", onQuit));

        Button startButton = menuButton("Bắt đầu", () -> onPlay.accept(playerName.getText()));
        Button backButton = menuButton("Quay lại", () -> {});
        backButton.setOnAction(e -> showMainOptions(mainButtons, namePrompt, playerName, startButton, backButton));
        playButton.setOnAction(e -> showNameEntry(mainButtons, namePrompt, playerName, startButton, backButton));

        namePrompt.setVisible(false);
        namePrompt.setManaged(false);
        playerName.setVisible(false);
        playerName.setManaged(false);
        startButton.setVisible(false);
        startButton.setManaged(false);
        backButton.setVisible(false);
        backButton.setManaged(false);

        getChildren().add(title);
        getChildren().addAll(mainButtons);
        getChildren().addAll(namePrompt, playerName, startButton, backButton);
    }

    private void showNameEntry(List<Button> mainButtons, Label prompt, TextField playerName,
                               Button startButton, Button backButton) {
        mainButtons.forEach(button -> setVisibleAndManaged(button, false));
        setVisibleAndManaged(prompt, true);
        setVisibleAndManaged(playerName, true);
        setVisibleAndManaged(startButton, true);
        setVisibleAndManaged(backButton, true);
        playerName.requestFocus();
        playerName.selectAll();
    }

    private void showMainOptions(List<Button> mainButtons, Label prompt, TextField playerName,
                                 Button startButton, Button backButton) {
        mainButtons.forEach(button -> setVisibleAndManaged(button, true));
        setVisibleAndManaged(prompt, false);
        setVisibleAndManaged(playerName, false);
        setVisibleAndManaged(startButton, false);
        setVisibleAndManaged(backButton, false);
    }

    private static void setVisibleAndManaged(javafx.scene.Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    static Button menuButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setPrefWidth(240);
        button.setStyle("-fx-font-size: 20px;");
        button.setOnAction(e -> action.run());
        return button;
    }
}
