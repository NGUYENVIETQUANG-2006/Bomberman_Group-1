package bomb;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class LevelLoaderTest {
    private final LevelLoader loader = new LevelLoader(new EntityFactory());

    @Test
    @DisplayName("Đọc đúng kích thước và các ô tĩnh")
    void loadsDimensionsAndTiles() throws IOException {
        GameMap map = loader.load("/bomberman/test_map.txt");

        assertEquals(5, map.getRows());
        assertEquals(7, map.getColumns());
        assertInstanceOf(Wall.class, map.getTile(0, 0));
        assertInstanceOf(Brick.class, map.getTile(1, 4));
        assertInstanceOf(HealthItem.class, map.getTile(3, 1));
        assertInstanceOf(Portal.class, map.getTile(3, 2));
    }

    @Test
    @DisplayName("Ghi nhận vị trí xuất phát của Player, ô đó không có đối tượng tĩnh")
    void recordsPlayerSpawn() throws IOException {
        GameMap map = loader.load("/bomberman/test_map.txt");

        assertEquals(1, map.getPlayerSpawnRow());
        assertEquals(1, map.getPlayerSpawnCol());
        assertNull(map.getTile(1, 1));
    }

    @Test
    @DisplayName("Kẻ địch vào danh sách enemies, không vào lưới")
    void enemiesGoToEnemyList() throws IOException {
        GameMap map = loader.load("/bomberman/test_map.txt");

        assertEquals(2, map.getEnemies().size());
        assertTrue(map.getEnemies().stream().anyMatch(e -> e instanceof Grunt));
        assertTrue(map.getEnemies().stream().anyMatch(e -> e instanceof Juggernaut));
        assertNull(map.getTile(1, 5));
    }

    @Test
    @DisplayName("Dòng bị thiếu ký tự được coi là ô trống")
    void shortLinesArePaddedWithEmptyCells() throws IOException {
        GameMap map = loader.load("/bomberman/padded_map.txt");

        assertInstanceOf(Wall.class, map.getTile(1, 0));
        assertNull(map.getTile(1, 4));
    }

    @Test
    @DisplayName("File không tồn tại ném IOException")
    void missingFileThrows() {
        assertThrows(IOException.class, () -> loader.load("/bomberman/khong_co.txt"));
    }

    @Test
    @DisplayName("Dòng đầu sai định dạng ném IllegalArgumentException")
    void badHeaderThrows() {
        assertThrows(IllegalArgumentException.class, () -> loader.load("/bomberman/bad_header.txt"));
    }

    @Test
    @DisplayName("Thiếu hàng so với khai báo ném IllegalArgumentException")
    void missingRowsThrows() {
        assertThrows(IllegalArgumentException.class, () -> loader.load("/bomberman/missing_rows.txt"));
    }
}
