package bomb;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ItemTest {

    @Test
    @DisplayName("HealthItem hồi 1 máu")
    void healthItemRestoresHealth() {
        Player player = TestMaps.newPlayer(1, 1);
        player.kill();                       // 3 -> 2
        new HealthItem(0, 0).applyTo(player);
        assertEquals(3, player.getHealth());
    }

    @Test
    @DisplayName("FlameItem tăng tầm lửa")
    void flameItemIncreasesRange() {
        Player player = TestMaps.newPlayer(1, 1);
        int before = player.getBlastRange();
        new FlameItem(0, 0).applyTo(player);
        assertEquals(before + 1, player.getBlastRange());
    }

    @Test
    @DisplayName("SpeedItem tăng tốc độ")
    void speedItemIncreasesSpeed() {
        Player player = TestMaps.newPlayer(1, 1);
        double before = player.speed;
        new SpeedItem(0, 0).applyTo(player);
        assertTrue(player.speed > before);
    }

    @Test
    @DisplayName("Đa hình: gọi applyTo qua kiểu Item")
    void polymorphicApply() {
        Player player = TestMaps.newPlayer(1, 1);
        Item item = new FlameItem(0, 0);     // biến kiểu Item, đối tượng kiểu FlameItem
        item.applyTo(player);
        assertEquals(2, player.getBlastRange());
    }

    @Test
    @DisplayName("Vật phẩm hết hạn sau thời gian tồn tại")
    void itemExpiresOverTime() {
        Item item = new HealthItem(0, 0);
        item.update(14.9);
        assertFalse(item.isExpired());
        item.update(0.2);
        assertTrue(item.isExpired());
    }

    @Test
    @DisplayName("Lửa chạm vật phẩm thì vật phẩm bị huỷ")
    void flameDestroysItem() {
        Item item = new SpeedItem(0, 0);
        item.onFlameHit();
        assertTrue(item.isExpired());
    }
}
