package core;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Fixed window sizes that preserve the game's 800:648 logical aspect ratio. */
public enum DisplayResolution {
    LOW(800, 648),
    MEDIUM(1000, 810),
    HIGH(1200, 972);

    private final int width;
    private final int height;

    DisplayResolution(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public int width() { return width; }
    public int height() { return height; }

    @Override
    public String toString() {
        return width + " × " + height;
    }

    public static DisplayResolution load(Path file) throws IOException {
        if (!Files.isRegularFile(file)) return LOW;
        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(file)) {
            properties.load(input);
        }
        try {
            return DisplayResolution.valueOf(properties.getProperty("resolution", LOW.name()));
        } catch (IllegalArgumentException e) {
            return LOW;
        }
    }

    public void save(Path file) throws IOException {
        Path parent = file.toAbsolutePath().getParent();
        if (parent != null) Files.createDirectories(parent);
        Properties properties = new Properties();
        properties.setProperty("resolution", name());
        try (OutputStream output = Files.newOutputStream(file)) {
            properties.store(output, "Bomberman display settings");
        }
    }
}
