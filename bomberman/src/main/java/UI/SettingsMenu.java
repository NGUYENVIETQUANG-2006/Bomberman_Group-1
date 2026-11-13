package UI;

import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

import core.DisplayResolution;
import core.input.KeysBinding;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
 
public class SettingsMenu extends VBox {
    private final KeysBinding keys;
    private final DisplayResolution initialResolution;
    private final ComboBox<DisplayResolution> resolutionSelector = new ComboBox<>();
    private final Map<KeysBinding.Action, Button> keyButtons = new EnumMap<>(KeysBinding.Action.class);
    private final Label message = new Label();
 
    /** Hành động đang chờ nhấn phím mới, null nếu không chờ. */
    private KeysBinding.Action capturing;
 
    public SettingsMenu(KeysBinding keys, Path saveFile, DisplayResolution resolution,
                        Consumer<DisplayResolution> onResolutionChanged, Runnable onBack) {
        this.keys = keys;
        this.initialResolution = resolution;
        setAlignment(Pos.CENTER);
        setSpacing(14);
        setStyle("-fx-background-color: #1b5e20;");
 
        Label title = new Label("CÀI ĐẶT");
        title.setStyle("-fx-font-size: 44px; -fx-font-weight: bold; -fx-text-fill: white;");
        getChildren().add(title);

        VBox displaySection = new VBox(14);
        displaySection.setAlignment(Pos.TOP_CENTER);
        displaySection.setPrefWidth(240);
        displaySection.setStyle("-fx-background-color: rgba(0,0,0,0.18); -fx-background-radius: 12; -fx-padding: 18;");
        Label displayTitle = new Label("MÀN HÌNH");
        displayTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");
        Label resolutionName = new Label("Độ phân giải");
        resolutionName.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        resolutionSelector.getItems().setAll(DisplayResolution.values());
        resolutionSelector.setValue(resolution);
        resolutionSelector.setPrefWidth(200);
        resolutionSelector.setStyle("-fx-font-size: 18px;");
        displaySection.getChildren().addAll(displayTitle, resolutionName, resolutionSelector);

        VBox controlsSection = new VBox(8);
        controlsSection.setAlignment(Pos.TOP_CENTER);
        controlsSection.setPrefWidth(400);
        controlsSection.setStyle("-fx-background-color: rgba(0,0,0,0.18); -fx-background-radius: 12; -fx-padding: 18;");
        Label controlsTitle = new Label("ĐIỀU KHIỂN");
        controlsTitle.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: white;");
        controlsSection.getChildren().add(controlsTitle);
 
        for (KeysBinding.Action action : KeysBinding.Action.values()) {
            Label name = new Label(label(action));
            name.setPrefWidth(90);
            name.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
 
            Button keyButton = MainMenu.menuButton("", () -> startCapture(action));
            keyButton.setPrefWidth(150);
            keyButtons.put(action, keyButton);
 
            HBox row = new HBox(12, name, keyButton);
            row.setAlignment(Pos.CENTER);
            controlsSection.getChildren().add(row);
        }

        HBox sections = new HBox(20, displaySection, controlsSection);
        sections.setAlignment(Pos.CENTER);
        getChildren().add(sections);
 
        message.setStyle("-fx-font-size: 16px; -fx-text-fill: #ffeb3b;");
        message.setMinHeight(24);
 
        Button reset = MainMenu.menuButton("Mặc định", () -> {
            cancelCapture();
            keys.resetToDefaults();
            refresh();
            message.setText("Đã khôi phục phím mặc định");
        });
        Button back = MainMenu.menuButton("Lưu và quay lại", () -> saveAndBack(saveFile, onResolutionChanged, onBack));
        HBox actions = new HBox(16, reset, back);
        actions.setAlignment(Pos.CENTER);
        getChildren().addAll(message, actions);

        addEventFilter(KeyEvent.KEY_PRESSED, this::onKeyPressed);
 
        refresh();
    }
 
    private void startCapture(KeysBinding.Action action) {
        cancelCapture();
        capturing = action;
        Button button = keyButtons.get(action);
        button.setText("Nhấn phím...");
        button.requestFocus();
        message.setText("Nhấn phím mới cho \"" + label(action) + "\" (Esc để huỷ)");
    }
 
    private void cancelCapture() {
        if (capturing != null) {
            capturing = null;
            refresh();
        }
    }
 
    private void onKeyPressed(KeyEvent e) {
        if (capturing == null) {
            return;
        }
        e.consume();
        KeyCode code = e.getCode();
 
        if (code == KeyCode.ESCAPE) {
            cancelCapture();
            message.setText("");
            return;
        }
 
        KeysBinding.Action other = keys.actionFor(code);
        boolean accepted = keys.set(capturing, code);
        KeysBinding.Action changed = capturing;
        capturing = null;
        refresh();
 
        if (!accepted) {
            message.setText("Phím này dành riêng cho game, hãy chọn phím khác");
        } else if (other != null && other != changed) {
            message.setText("Phím đó đang dùng cho \"" + label(other) + "\": hai hành động đã đổi chỗ");
        } else {
            message.setText("");
        }
    }
 
    private void refresh() {
        for (KeysBinding.Action action : KeysBinding.Action.values()) {
            keyButtons.get(action).setText(keys.getKeyName(action));
        }
    }
 
    private void saveAndBack(Path saveFile, Consumer<DisplayResolution> onResolutionChanged, Runnable onBack) {
        cancelCapture();
        try {
            keys.save(saveFile);
            DisplayResolution selected = resolutionSelector.getValue();
            if (selected == null) selected = initialResolution;
            selected.save(saveFile.resolveSibling("display.properties"));
            onResolutionChanged.accept(selected);
            onBack.run();
        } catch (IOException ex) {
            message.setText("Không lưu được cài đặt: " + ex.getMessage());
        }
    }
 
    private static String label(KeysBinding.Action action) {
        return switch (action) {
            case UP -> "Lên";
            case DOWN -> "Xuống";
            case LEFT -> "Trái";
            case RIGHT -> "Phải";
            case BOMB -> "Đặt bom";
        };
    }
}
