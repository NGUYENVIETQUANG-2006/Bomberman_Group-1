package bomb;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class InputHandler {
    private final Set<KeyCode> held = new HashSet<>();
    private final Set<KeyCode> pressedThisFrame = new HashSet<>();

    public void attach(Scene scene) {
        scene.setOnKeyPressed(e -> {
            if (held.add(e.getCode())) {
                pressedThisFrame.add(e.getCode());
            }
        });
        scene.setOnKeyReleased(e -> held.remove(e.getCode()));
    }

    public boolean isHeld(KeyCode k) {
        return held.contains(k);
    }

    public boolean wasPressed(KeyCode k) {
        return pressedThisFrame.contains(k);
    }

    public void endFrame() {
        pressedThisFrame.clear();
    }
}