package bomb;

/**
 *
 * Bảng ký tự:
 *   '#' Wall          '*' Brick         'x' Portal
 *   '1' Grunt         '2' Juggernaut
 *   'h' Brick giấu Health   'c' Brick giấu Cooldown
 *   'f' Brick giấu Flame    's' Brick giấu Speed
 *   ' ' ô trống (cỏ)  -> trả về null
 *   'p' vị trí xuất phát của Player
 */
public class EntityFactory {
    public Entity create(char symbol, int r, int c) {
        double x = c * GameMap.TILE_SIZE;
        double y = r * GameMap.TILE_SIZE;

        return switch (symbol) {
            case '#' -> new Wall(x, y);
            case '*' -> new Brick(x, y);
            case 'x' -> new Portal(x, y);

            case 'h' -> new Brick(x, y, new HealthItem(x, y));
            case 'c' -> new Brick(x, y, new CooldownItem(x, y));
            case 'f' -> new Brick(x, y, new FlameItem(x, y));
            case 's' -> new Brick(x, y, new SpeedItem(x, y));

            case '1' -> new Grunt(x, y);
            case '2' -> new Juggernaut(x, y);

            case ' ' -> null;
            default -> throw new IllegalArgumentException(
                    "Ký tự map không hợp lệ '" + symbol + "' tại hàng " + r + ", cột " + c);
        };
    }
}
