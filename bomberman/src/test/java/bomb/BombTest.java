package bomb;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class BombTest {
    private static final int T = GameMap.TILE_SIZE;

    private static Bomb bombAt(int row, int col, int range) {
        return new Bomb(col * T, row * T, range);
    }

    private static Set<String> cells(List<Flame> flames) {
        return flames.stream().map(f -> f.getRow() + "," + f.getCol()).collect(Collectors.toSet());
    }

    @Test
    @DisplayName("Bom nổ sau 2 giây")
    void explodesAfterTwoSeconds() {
        Bomb bomb = bombAt(1, 1, 1);

        bomb.update(1.9);
        assertFalse(bomb.isReadyToExplode());
        bomb.update(0.2);
        assertTrue(bomb.isReadyToExplode());
    }

    @Test
    @DisplayName("Trên đất trống, lửa lan 4 hướng đủ tầm: 1 + 4 x tầm ô")
    void flamesSpreadInFourDirections() {
        GameMap map = TestMaps.walled(7, 7);
        Bomb bomb = bombAt(3, 3, 2);
        map.addBomb(bomb);

        List<Flame> flames = bomb.explode(map);

        assertEquals(9, flames.size());
        Set<String> c = cells(flames);
        assertTrue(c.containsAll(Set.of("3,3", "1,3", "2,3", "4,3", "5,3", "3,1", "3,2", "3,4", "3,5")));
    }

    @Test
    @DisplayName("Lửa bị tường chặn, không vào ô tường")
    void wallBlocksFlame() {
        GameMap map = TestMaps.walled(7, 7);
        map.setTile(3, 5, new Wall(5 * T, 3 * T));
        Bomb bomb = bombAt(3, 3, 3);
        map.addBomb(bomb);

        Set<String> c = cells(bomb.explode(map));

        assertTrue(c.contains("3,4"));
        assertFalse(c.contains("3,5"));
        assertFalse(c.contains("3,6"));
        assertEquals(8, c.size());
    }

    @Test
    @DisplayName("Lửa phá gạch và dừng lại: mỗi hướng chỉ phá một gạch")
    void onlyOneBrickPerDirection() {
        GameMap map = TestMaps.walled(7, 7);
        map.setTile(3, 4, new Brick(4 * T, 3 * T));
        map.setTile(3, 5, new Brick(5 * T, 3 * T));
        Bomb bomb = bombAt(3, 3, 4);
        map.addBomb(bomb);

        Set<String> c = cells(bomb.explode(map));

        assertTrue(c.contains("3,4"));
        assertFalse(c.contains("3,5"));
        assertNull(map.getTile(3, 4));                     // gạch đầu bị phá
        assertInstanceOf(Brick.class, map.getTile(3, 5));  // gạch sau còn nguyên
    }

    @Test
    @DisplayName("Vật phẩm bị lửa huỷ, lửa vẫn đi tiếp")
    void itemDestroyedAndFlameContinues() {
        GameMap map = TestMaps.walled(7, 7);
        map.setTile(3, 4, new FlameItem(4 * T, 3 * T));
        Bomb bomb = bombAt(3, 3, 2);
        map.addBomb(bomb);

        Set<String> c = cells(bomb.explode(map));

        assertNull(map.getTile(3, 4));
        assertTrue(c.contains("3,5"));
    }

    @Test
    @DisplayName("Nổ dây chuyền: lửa chạm bom khác thì bom đó nổ ngay")
    void chainExplosion() {
        GameMap map = TestMaps.walled(7, 7);
        Bomb a = bombAt(3, 3, 1);
        Bomb b = bombAt(3, 4, 1);
        map.addBomb(a);
        map.addBomb(b);

        Set<String> c = cells(a.explode(map));

        assertTrue(b.isExploded());
        assertTrue(c.contains("3,5"));    // chỉ lửa của bom b mới với tới ô này
    }

    @Test
    @DisplayName("Bom chỉ nổ một lần")
    void explodesOnlyOnce() {
        GameMap map = TestMaps.walled(7, 7);
        Bomb bomb = bombAt(3, 3, 1);
        map.addBomb(bomb);

        assertFalse(bomb.explode(map).isEmpty());
        assertTrue(bomb.explode(map).isEmpty());
        assertFalse(bomb.isReadyToExplode());
    }
}
