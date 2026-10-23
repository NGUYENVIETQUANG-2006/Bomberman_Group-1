package bomb;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import javafx.scene.input.KeyCode;

public class KeysBinding {

    public enum Action { UP, DOWN, LEFT, RIGHT, BOMB }

    private final Map<Action, KeyCode> keys = new EnumMap<>(Action.class);
    private final Map<Action, KeyCode> defaults = new EnumMap<>(Action.class);

    public KeysBinding(KeyCode up, KeyCode down, KeyCode left, KeyCode right, KeyCode bomb) {
        defaults.put(Action.UP, up);
        defaults.put(Action.DOWN, down);
        defaults.put(Action.LEFT, left);
        defaults.put(Action.RIGHT, right);
        defaults.put(Action.BOMB, bomb);
        keys.putAll(defaults);
    }

    public static KeysBinding arrows() {
        return new KeysBinding(KeyCode.UP, KeyCode.DOWN, KeyCode.LEFT, KeyCode.RIGHT, KeyCode.DIGIT0);
    }

    public static KeysBinding wasd() {
        return new KeysBinding(KeyCode.W, KeyCode.S, KeyCode.A, KeyCode.D, KeyCode.SPACE);
    }

    public KeyCode get(Action action) {
        return keys.get(action);
    }

    public String getKeyName(Action action) {
        return keys.get(action).getName();
    }

    public Action actionFor(KeyCode key) {
        for (Map.Entry<Action, KeyCode> e : keys.entrySet()) {
            if (e.getValue() == key) {
                return e.getKey();
            }
        }
        return null;
    }

    public boolean set(Action action, KeyCode key) {
        if (key == null || isReserved(key)) {
            return false;
        }
        Action other = actionFor(key);
        if (other != null && other != action) {
            keys.put(other, keys.get(action));
        }
        keys.put(action, key);
        return true;
    }

    public static boolean isReserved(KeyCode key) {
        return key == KeyCode.ENTER || key == KeyCode.ESCAPE;
    }

    public void resetToDefaults() {
        keys.putAll(defaults);
    }

    public void save(Path file) throws IOException {
        Properties props = new Properties();
        for (Action a : Action.values()) {
            props.setProperty(a.name(), keys.get(a).name());
        }
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (OutputStream out = Files.newOutputStream(file)) {
            props.store(out, "Bomberman key bindings");
        }
    }

    public void load(Path file) throws IOException {
        if (!Files.exists(file)) {
            return;
        }
        Properties props = new Properties();
        try (InputStream in = Files.newInputStream(file)) {
            props.load(in);
        }

        Map<Action, KeyCode> loaded = new EnumMap<>(Action.class);
        for (Action a : Action.values()) {
            String name = props.getProperty(a.name());
            if (name == null) {
                return;
            }
            try {
                loaded.put(a, KeyCode.valueOf(name));
            } catch (IllegalArgumentException e) {
                return;
            }
        }
        if (new HashSet<>(loaded.values()).size() != loaded.size()) {
            return;
        }
        for (KeyCode k : loaded.values()) {
            if (isReserved(k)) {
                return;
            }
        }
        keys.putAll(loaded);
    }
}