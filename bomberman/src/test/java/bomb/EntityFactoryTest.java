package bomb;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EntityFactoryTest {
    private final EntityFactory factory = new EntityFactory();

    @Test
    @DisplayName("Mỗi ký tự tạo đúng loại đối tượng")
    void createsCorrectTypes() {
        assertInstanceOf(Wall.class, factory.create('#', 0, 0));
        assertInstanceOf(Brick.class, factory.create('*', 0, 0));
        assertInstanceOf(Portal.class, factory.create('x', 0, 0));
        assertInstanceOf(HealthItem.class, factory.create('h', 0, 0));
        assertInstanceOf(FlameItem.class, factory.create('f', 0, 0));
        assertInstanceOf(SpeedItem.class, factory.create('s', 0, 0));
        assertInstanceOf(CooldownItem.class, factory.create('c', 0, 0));
        assertInstanceOf(Grunt.class, factory.create('1', 0, 0));
        assertInstanceOf(Juggernaut.class, factory.create('2', 0, 0));
    }

    @Test
    @DisplayName("Ô trống trả về null")
    void spaceReturnsNull() {
        assertNull(factory.create(' ', 1, 1));
    }

    @Test
    @DisplayName("Ký tự lạ ném IllegalArgumentException")
    void unknownSymbolThrows() {
        assertThrows(IllegalArgumentException.class, () -> factory.create('?', 0, 0));
    }

    @Test
    @DisplayName("Toạ độ pixel = cột/hàng nhân TILE_SIZE")
    void positionIsGridTimesTileSize() {
        Entity e = factory.create('#', 2, 3);   // hàng 2, cột 3
        assertEquals(3 * GameMap.TILE_SIZE, e.getX(), 0.001);
        assertEquals(2 * GameMap.TILE_SIZE, e.getY(), 0.001);
    }
}
