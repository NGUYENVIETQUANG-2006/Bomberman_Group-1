package bomberman;

/**
 *
 * Bảng ký tự:
 *   '#' Wall          '*' Brick         'x' Portal
 *   'h' HealthItem    'f' FlameItem     's' SpeedItem    'c' CooldownItem
 *   '1' Grunt         '2' Juggernaut
 *   ' ' ô trống (cỏ)  -> trả về null
 *   'p' vị trí xuất phát của Player -> do LevelLoader xử lý, không tạo ở đây
 */
public class EntityFactory {
    public Entity create(char symbol, int r, int c) {
        double x = c * GameMap.TILE_SIZE;
        double y = r * GameMap.TILE_SIZE;

        return switch (symbol) {
            case '#' -> new Wall(x, y);
            case '*' -> new Brick(x, y);
            case 'x' -> new Portal(x, y);

            case 'h' -> new HealthItem(x, y);
            case 'f' -> new FlameItem(x, y);
            case 's' -> new SpeedItem(x, y);
            case 'c' -> new CooldownItem(x, y);

            case '1' -> new Grunt(x, y);
            case '2' -> new Juggernaut(x, y);

            case ' ' -> null;
            default -> throw new IllegalArgumentException(
                    "Ký tự map không hợp lệ '" + symbol + "' tại hàng " + r + ", cột " + c);
        };
    }
}
