package bomb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;

public class InputHandler {
    private final Set<KeyCode> held = new LinkedHashSet<>();
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

    public List<KeyCode> getHeldKeysNewestFirst() {
        List<KeyCode> keys = new ArrayList<>(held);
        Collections.reverse(keys);
        return keys;
    }

    public boolean wasPressed(KeyCode k) {
        return pressedThisFrame.contains(k);
    }

    public void endFrame() {
        pressedThisFrame.clear();
    }
}