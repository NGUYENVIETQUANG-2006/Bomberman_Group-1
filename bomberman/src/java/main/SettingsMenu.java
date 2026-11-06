package main;

import java.io.IOException;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
 
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import bomb.KeysBinding;
 
public class SettingsMenu extends VBox {
    private final KeysBinding keys;
    private final Map<KeysBinding.Action, Button> keyButtons = new EnumMap<>(KeysBinding.Action.class);
    private final Label message = new Label();
 
    /** Hành động đang chờ nhấn phím mới, null nếu không chờ. */
    private KeysBinding.Action capturing;
 
    public SettingsMenu(KeysBinding keys, Path saveFile, Runnable onBack) {
        this.keys = keys;
        setAlignment(Pos.CENTER);
        setSpacing(14);
        setStyle("-fx-background-color: #1b5e20;");
 
        Label title = new Label("CÀI ĐẶT PHÍM");
        title.setStyle("-fx-font-size: 44px; -fx-font-weight: bold; -fx-text-fill: white;");
        getChildren().add(title);
 
        for (KeysBinding.Action action : KeysBinding.Action.values()) {
            Label name = new Label(label(action));
            name.setPrefWidth(120);
            name.setStyle("-fx-font-size: 20px; -fx-text-fill: white;");
 
            Button keyButton = MainMenu.menuButton("", () -> startCapture(action));
            keyButton.setPrefWidth(180);
            keyButtons.put(action, keyButton);
 
            HBox row = new HBox(20, name, keyButton);
            row.setAlignment(Pos.CENTER);
            getChildren().add(row);
        }
 
        message.setStyle("-fx-font-size: 16px; -fx-text-fill: #ffeb3b;");
        message.setMinHeight(24);
 
        Button reset = MainMenu.menuButton("Mặc định", () -> {
            cancelCapture();
            keys.resetToDefaults();
            refresh();
            message.setText("Đã khôi phục phím mặc định");
        });
        Button back = MainMenu.menuButton("Lưu và quay lại", () -> saveAndBack(saveFile, onBack));
        getChildren().addAll(message, reset, back);

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
 
    private void saveAndBack(Path saveFile, Runnable onBack) {
        cancelCapture();
        try {
            keys.save(saveFile);
            onBack.run();
        } catch (IOException ex) {
            message.setText("Không lưu được file phím: " + ex.getMessage());
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
