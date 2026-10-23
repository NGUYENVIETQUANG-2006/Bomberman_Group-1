package bomb;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PlayerTest {
    private static final int T = GameMap.TILE_SIZE;

    /** Player đặt vào bản đồ trống, sẵn sàng update(). */
    private static Player playerOnMap(int row, int col, GameMap map) {
        Player player = TestMaps.newPlayer(row, col);
        map.setPlayer(player);
        return player;
    }

    @Test
    @DisplayName("Bị trúng đòn: mất 1 máu và bất tử tạm thời")
    void killReducesHealthAndGrantsInvincibility() {
        Player player = TestMaps.newPlayer(1, 1);

        player.kill();

        assertEquals(2, player.getHealth());
        assertTrue(player.isAlive());
        assertTrue(player.isInvincible());
    }

    @Test
    @DisplayName("Đang bất tử thì không bị trừ máu thêm")
    void invincibleIgnoresFurtherHits() {
        Player player = TestMaps.newPlayer(1, 1);

        player.kill();
        player.kill();
        player.kill();

        assertEquals(2, player.getHealth());
    }

    @Test
    @DisplayName("Hết máu thì chết")
    void diesWhenHealthRunsOut() {
        GameMap map = TestMaps.walled(5, 5);
        Player player = playerOnMap(1, 1, map);

        for (int i = 0; i < 3; i++) {
            player.kill();
            player.update(2.0);              // hết thời gian bất tử
        }

        assertFalse(player.isAlive());
    }

    @Test
    @DisplayName("Máu, tầm lửa, tốc độ đều có giới hạn trên")
    void statsAreCapped() {
        Player player = TestMaps.newPlayer(1, 1);

        for (int i = 0; i < 20; i++) {
            player.addHealth();
            player.increaseRange();
            player.increaseSpeed();
        }

        assertEquals(5, player.getHealth());
        assertEquals(6, player.getBlastRange());
        assertEquals(player.getMaxSpeed(), player.speed, 0.001);
    }

    @Test
    @DisplayName("Đặt bom: căn đúng vào ô của Player")
    void placeBombAlignsToCell() {
        GameMap map = TestMaps.walled(7, 7);
        Player player = playerOnMap(3, 2, map);

        Bomb bomb = player.placeBomb(map);

        assertNotNull(bomb);
        assertEquals(2 * T, bomb.getX(), 0.001);
        assertEquals(3 * T, bomb.getY(), 0.001);
    }

    @Test
    @DisplayName("Chưa hết cooldown thì chưa đặt được bom tiếp")
    void cooldownBlocksSecondBomb() {
        GameMap map = TestMaps.walled(7, 7);
        Player player = playerOnMap(3, 3, map);

        assertNotNull(player.placeBomb(map));
        assertNull(player.placeBomb(map));

        player.update(0.5);
        assertFalse(player.canPlaceBomb());
        player.update(0.6);
        assertTrue(player.canPlaceBomb());
    }

    @Test
    @DisplayName("Không đặt hai bom cùng một ô")
    void cannotStackBombsInOneCell() {
        GameMap map = TestMaps.walled(7, 7);
        Player player = playerOnMap(3, 3, map);

        map.addBomb(player.placeBomb(map));
        player.update(2.0);                  // hết cooldown

        assertNull(player.placeBomb(map));
    }

    @Test
    @DisplayName("CooldownItem rút ngắn thời gian hồi")
    void reduceCooldownShortensWait() {
        GameMap map = TestMaps.walled(7, 7);
        Player player = playerOnMap(3, 3, map);
        for (int i = 0; i < 10; i++) {
            player.reduceCooldown();
        }

        assertNotNull(player.placeBomb(map));
        player.update(0.31);                 // cooldown thấp nhất là 0,3 giây

        assertTrue(player.canPlaceBomb());
    }

    @Test
    @DisplayName("Player đã chết không đặt được bom")
    void deadPlayerCannotPlaceBomb() {
        GameMap map = TestMaps.walled(7, 7);
        Player player = playerOnMap(3, 3, map);
        for (int i = 0; i < 3; i++) {
            player.kill();
            player.update(2.0);
        }

        assertNull(player.placeBomb(map));
    }
}
