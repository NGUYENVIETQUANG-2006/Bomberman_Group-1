package bomb;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class GameMapTest {
    private static final int T = GameMap.TILE_SIZE;

    @Test
    @DisplayName("Tường và gạch chặn; ô trống, cổng, vật phẩm không chặn")
    void blockedOnlyByWallAndBrick() {
        GameMap map = new GameMap(5, 5);
        map.setTile(1, 1, new Wall(T, T));
        map.setTile(1, 2, new Brick(2 * T, T));
        map.setTile(1, 3, new Portal(3 * T, T));
        map.setTile(2, 1, new HealthItem(T, 2 * T));

        assertTrue(map.isBlockedAt(1, 1));
        assertTrue(map.isBlockedAt(1, 2));
        assertFalse(map.isBlockedAt(1, 3));
        assertFalse(map.isBlockedAt(2, 1));
        assertFalse(map.isBlockedAt(3, 3));      // ô trống
    }

    @Test
    @DisplayName("Ngoài bản đồ coi như bị chặn")
    void outOfBoundsIsBlocked() {
        GameMap map = new GameMap(5, 5);
        assertTrue(map.isBlockedAt(-1, 0));
        assertTrue(map.isBlockedAt(0, -1));
        assertTrue(map.isBlockedAt(5, 0));
        assertTrue(map.isBlockedAt(0, 5));
    }

    @Test
    @DisplayName("removeTile làm ô trở thành trống")
    void removeTileClearsCell() {
        GameMap map = new GameMap(5, 5);
        map.setTile(2, 2, new Brick(2 * T, 2 * T));
        map.removeTile(2, 2);

        assertNull(map.getTile(2, 2));
        assertFalse(map.isBlockedAt(2, 2));
    }

    @Test
    @DisplayName("hasBombAt nhận ra bom theo hàng/cột")
    void hasBombAtFindsBomb() {
        GameMap map = new GameMap(5, 5);
        map.addBomb(new Bomb(2 * T, 1 * T, 1));   // hàng 1, cột 2

        assertTrue(map.hasBombAt(1, 2));
        assertFalse(map.hasBombAt(1, 3));
        assertFalse(map.hasBombAt(2, 2));
    }

    @Test
    @DisplayName("setPlayer gắn Player và gắn lại bản đồ cho Player")
    void setPlayerLinksBothWays() {
        GameMap map = new GameMap(5, 5);
        Player player = TestMaps.newPlayer(1, 1);
        map.setPlayer(player);

        assertSame(player, map.getPlayer());
        assertSame(map, player.map);
    }
}
