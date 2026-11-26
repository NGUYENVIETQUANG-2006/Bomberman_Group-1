package UI;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class MainMenu extends StackPane {
    public MainMenu(Consumer<String> onPlay, Runnable onContinue, Runnable onScores, Runnable onSettings, Runnable onQuit) {
        Image backgroundImage = new Image(MainMenu.class.getResource("/picture/Front/game background, Kai Ouyang.jpg").toExternalForm());
        setBackground(new Background(new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(1, 1, true, true, false, true))));

        ImageView bomberman = new ImageView(new Image(MainMenu.class.getResource("/picture/Front/398427898283216518.png").toExternalForm()));
        bomberman.setPreserveRatio(true);
        bomberman.setFitWidth(220);
        StackPane.setAlignment(bomberman, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(bomberman, new Insets(0, 28, 18, 0));

        VBox menu = new VBox();
        menu.setAlignment(Pos.CENTER);
        menu.setSpacing(18);
        menu.setStyle("-fx-background-color: rgba(5, 48, 83, 0.18);");

        Label title = new Label("BOMBERMAN");
        title.setStyle("-fx-font-size: 64px; -fx-font-weight: bold; -fx-text-fill: white; -fx-effect: dropshadow(gaussian, #006b9c, 14, 0.8, 0, 3);");

        Label namePrompt = new Label("Nhập tên người chơi");
        namePrompt.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        TextField playerName = new TextField("Người chơi");
        playerName.setPromptText("Tên người chơi");
        playerName.setMaxWidth(240);
        playerName.setStyle("-fx-font-size: 18px;");

        List<Button> mainButtons = new ArrayList<>();
        Button playButton = menuButton("Chơi mới", () -> {});
        mainButtons.add(playButton);
        if (core.GameStorage.hasSave()) mainButtons.add(menuButton("Tiếp tục", onContinue));
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

        menu.getChildren().add(title);
        menu.getChildren().addAll(mainButtons);
        menu.getChildren().addAll(namePrompt, playerName, startButton, backButton);
        getChildren().addAll(menu, bomberman);
    }

    private void showNameEntry(List<Button> mainButtons, Label prompt, TextField playerName, Button startButton, Button backButton) {
        mainButtons.forEach(button -> setVisibleAndManaged(button, false));
        setVisibleAndManaged(prompt, true);
        setVisibleAndManaged(playerName, true);
        setVisibleAndManaged(startButton, true);
        setVisibleAndManaged(backButton, true);
        playerName.requestFocus();
        playerName.selectAll();
    }

    private void showMainOptions(List<Button> mainButtons, Label prompt, TextField playerName, Button startButton, Button backButton) {
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
        button.setPrefHeight(48);
        button.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #073c5b; -fx-background-color: linear-gradient(to bottom, #e5fbff, #53c9ed 48%, #168dc4); -fx-background-radius: 12; -fx-border-color: #d8fbff; -fx-border-width: 2; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0, 49, 79, 0.7), 10, 0.25, 0, 4); -fx-cursor: hand;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #073c5b; -fx-background-color: linear-gradient(to bottom, white, #8deaff 48%, #28b6e7); -fx-background-radius: 12; -fx-border-color: white; -fx-border-width: 2; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, #55ddff, 16, 0.4, 0, 3); -fx-cursor: hand;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #073c5b; -fx-background-color: linear-gradient(to bottom, #e5fbff, #53c9ed 48%, #168dc4); -fx-background-radius: 12; -fx-border-color: #d8fbff; -fx-border-width: 2; -fx-border-radius: 12; -fx-effect: dropshadow(gaussian, rgba(0, 49, 79, 0.7), 10, 0.25, 0, 4); -fx-cursor: hand;"));
        button.setOnAction(e -> action.run());
        return button;
    }
}
