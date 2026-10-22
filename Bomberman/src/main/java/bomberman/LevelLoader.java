package bomberman;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * Đọc file map từ classpath (thư mục resources) và dựng GameMap.
 *
 * Định dạng file:
 *   Dòng 1: "<số hàng> <số cột>"
 *   Các dòng tiếp theo: mỗi dòng là một hàng, mỗi ký tự là một ô (xem EntityFactory).
 */
public class LevelLoader {
    private final EntityFactory factory;

    public LevelLoader(EntityFactory factory) {
        this.factory = factory;
    }

    /**
     * @param resourcePath đường dẫn tính từ gốc resources, bắt đầu bằng "/",
     *                     ví dụ "/levels/level1.txt"
     */
    public GameMap load(String resourcePath) throws IOException {
        List<String> lines = readLines(resourcePath);
        if (lines.isEmpty()) {
            throw new IllegalArgumentException("File map rỗng: " + resourcePath);
        }

        String[] header = lines.get(0).trim().split("\\s+");
        if (header.length < 2) {
            throw new IllegalArgumentException(
                    "Dòng đầu phải có dạng '<số hàng> <số cột>': " + resourcePath);
        }
        int rows = Integer.parseInt(header[0]);
        int cols = Integer.parseInt(header[1]);

        if (lines.size() - 1 < rows) {
            throw new IllegalArgumentException(
                    "File map thiếu hàng: cần " + rows + ", chỉ có " + (lines.size() - 1));
        }

        GameMap map = new GameMap(rows, cols);

        for (int r = 0; r < rows; r++) {
            String line = lines.get(r + 1);
            for (int c = 0; c < cols; c++) {
                char symbol = c < line.length() ? line.charAt(c) : ' ';

                if (symbol == 'p') {
                    map.setPlayerSpawn(r, c);
                    continue;
                }

                Entity e = factory.create(symbol, r, c);
                if (e instanceof Enemy enemy) {
                    map.addEnemy(enemy);
                } else if (e != null) {
                    map.setTile(r, c, e);
                }
            }
        }
        return map;
    }

    /** Đọc toàn bộ dòng của một file trong classpath. */
    private List<String> readLines(String resourcePath) throws IOException {
        try (InputStream in = LevelLoader.class.getResourceAsStream(resourcePath)) {
            if (in == null) {
                throw new IOException("Không tìm thấy file map trong resources: " + resourcePath);
            }
            try (BufferedReader reader =
                         new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                return reader.lines().toList();
            }
        }
    }
}