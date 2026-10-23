package bomb;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EnemyTest {
    private static final int T = GameMap.TILE_SIZE;

    // ---------- Grunt ----------

    @Test
    @DisplayName("Grunt trong hành lang chỉ có một hướng đi được")
    void gruntPicksOnlyAvailableDirection() {
        GameMap map = TestMaps.walled(3, 13);     // hành lang ngang: hàng 1
        Grunt grunt = new Grunt(T, T);
        map.addEnemy(grunt);

        assertEquals(Direction.RIGHT, grunt.targetSearch(null));
    }

    @Test
    @DisplayName("Grunt đi theo tốc độ: 1 giây đi 60 pixel")
    void gruntMovesAtItsSpeed() {
        GameMap map = TestMaps.walled(3, 13);
        Grunt grunt = new Grunt(T, T);
        map.addEnemy(grunt);

        grunt.update(1.0);

        assertEquals(T + 60, grunt.getX(), 0.001);
        assertEquals(T, grunt.getY(), 0.001);
    }

    @Test
    @DisplayName("Kẻ địch chạm Player thì Player bị trừ máu")
    void enemyTouchHurtsPlayer() {
        GameMap map = TestMaps.walled(5, 5);
        Grunt grunt = new Grunt(T, T);
        map.addEnemy(grunt);
        Player player = TestMaps.newPlayer(1, 1);
        map.setPlayer(player);

        grunt.update(0.0);

        assertEquals(2, player.getHealth());
    }

    @Test
    @DisplayName("Kẻ địch bị loại khi trúng lửa")
    void enemyDiesFromFlame() {
        Grunt grunt = new Grunt(T, T);
        grunt.onFlameHit();
        assertFalse(grunt.isAlive());
    }

    // ---------- Juggernaut (BFS) ----------

    private static Juggernaut juggernautVsPlayer(GameMap map, int jr, int jc, int pr, int pc) {
        Juggernaut jug = new Juggernaut(jc * T, jr * T);
        map.addEnemy(jug);
        map.setPlayer(TestMaps.newPlayer(pr, pc));
        return jug;
    }

    @Test
    @DisplayName("Juggernaut đi về phía Player ở bên phải")
    void juggernautChasesRight() {
        GameMap map = TestMaps.walled(5, 7);
        Juggernaut jug = juggernautVsPlayer(map, 1, 1, 1, 5);

        assertEquals(Direction.RIGHT, jug.targetSearch(map.getPlayer()));
    }

    @Test
    @DisplayName("Juggernaut đi về phía Player ở bên dưới")
    void juggernautChasesDown() {
        GameMap map = TestMaps.walled(5, 7);
        Juggernaut jug = juggernautVsPlayer(map, 1, 1, 3, 1);

        assertEquals(Direction.DOWN, jug.targetSearch(map.getPlayer()));
    }

    @Test
    @DisplayName("Juggernaut vòng qua tường chắn đường")
    void juggernautGoesAroundWall() {
        GameMap map = TestMaps.walled(5, 7);
        map.setTile(1, 2, new Wall(2 * T, T));
        Juggernaut jug = juggernautVsPlayer(map, 1, 1, 1, 3);

        // Đường ngắn nhất: xuống, sang phải 2 ô, rồi lên
        assertEquals(Direction.DOWN, jug.targetSearch(map.getPlayer()));
    }

    @Test
    @DisplayName("Juggernaut cùng ô với Player thì đứng yên")
    void juggernautStaysWhenOnPlayer() {
        GameMap map = TestMaps.walled(5, 7);
        Juggernaut jug = juggernautVsPlayer(map, 2, 2, 2, 2);

        assertNull(jug.targetSearch(map.getPlayer()));
    }
}
