package bomb;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FlameTest {
    private static final int T = GameMap.TILE_SIZE;

    @Test
    @DisplayName("Lửa chạm Player thì Player mất máu")
    void flameHurtsPlayer() {
        GameMap map = TestMaps.walled(5, 5);
        Player player = TestMaps.newPlayer(1, 1);
        map.setPlayer(player);
        Flame flame = new Flame(T, T, map);

        flame.update(0.1);

        assertEquals(2, player.getHealth());
    }

    @Test
    @DisplayName("Lửa không ảnh hưởng Player đứng ô khác")
    void flameMissesPlayerInOtherCell() {
        GameMap map = TestMaps.walled(5, 5);
        Player player = TestMaps.newPlayer(3, 3);
        map.setPlayer(player);
        Flame flame = new Flame(T, T, map);

        flame.update(0.1);

        assertEquals(3, player.getHealth());
    }

    @Test
    @DisplayName("Lửa chạm kẻ địch thì kẻ địch bị loại")
    void flameKillsEnemy() {
        GameMap map = TestMaps.walled(5, 5);
        Grunt grunt = new Grunt(T, T);
        map.addEnemy(grunt);
        Flame flame = new Flame(T, T, map);

        flame.update(0.1);

        assertFalse(grunt.isAlive());
    }

    @Test
    @DisplayName("Lửa hết hạn sau 0,5 giây")
    void flameExpires() {
        GameMap map = TestMaps.walled(5, 5);
        Flame flame = new Flame(T, T, map);

        flame.update(0.3);
        assertFalse(flame.isExpired());
        flame.update(0.3);
        assertTrue(flame.isExpired());
    }
}
