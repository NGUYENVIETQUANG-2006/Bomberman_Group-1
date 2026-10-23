package bomb;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/** Kiểm tra di chuyển và va chạm (cài đặt ở Character, thử qua Player). */
class CharacterTest {
    private static final int T = GameMap.TILE_SIZE;

    @Test
    @DisplayName("Đi vào ô trống thì di chuyển được")
    void movesIntoFreeSpace() {
        GameMap map = TestMaps.walled(5, 5);
        Player player = TestMaps.newPlayer(1, 1);

        player.move(10, 0, map);

        assertEquals(T + 10, player.getX(), 0.001);
        assertEquals(T, player.getY(), 0.001);
    }

    @Test
    @DisplayName("Không đi xuyên tường")
    void cannotWalkThroughWall() {
        GameMap map = TestMaps.walled(5, 5);
        Player player = TestMaps.newPlayer(1, 1);

        player.move(-10, 0, map);

        assertEquals(T, player.getX(), 0.001);
    }

    @Test
    @DisplayName("Không đi xuyên gạch")
    void cannotWalkThroughBrick() {
        GameMap map = TestMaps.walled(5, 5);
        map.setTile(1, 2, new Brick(2 * T, T));
        Player player = TestMaps.newPlayer(1, 1);

        player.move(20, 0, map);

        assertTrue(player.getX() < 2 * T);
        assertFalse(player.collisionDetect(player.getX(), player.getY(), map));
    }

    @Test
    @DisplayName("Chạm tường theo một trục vẫn trượt theo trục còn lại")
    void slidesAlongWall() {
        GameMap map = TestMaps.walled(5, 5);
        Player player = TestMaps.newPlayer(1, 1);

        player.move(-10, 10, map);           // trục x bị tường chặn, trục y đi được

        assertEquals(T, player.getX(), 0.001);
        assertEquals(T + 10, player.getY(), 0.001);
    }

    @Test
    @DisplayName("Không đi xuyên bom ở ô bên cạnh")
    void cannotWalkThroughBomb() {
        GameMap map = TestMaps.walled(5, 5);
        map.addBomb(new Bomb(2 * T, T, 1));
        Player player = TestMaps.newPlayer(1, 1);

        player.move(10, 0, map);

        assertEquals(T, player.getX(), 0.001);
    }

    @Test
    @DisplayName("Bom vừa đặt ngay dưới chân thì đi ra khỏi được")
    void canLeaveJustPlacedBomb() {
        GameMap map = TestMaps.walled(5, 5);
        map.addBomb(new Bomb(T, T, 1));
        Player player = TestMaps.newPlayer(1, 1);

        player.move(10, 0, map);

        assertEquals(T + 10, player.getX(), 0.001);
    }
}
