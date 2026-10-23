package bomb;

import java.util.Random;

public class ItemSpawner {
    public static final double DEFAULT_DROP_CHANCE = 0.30;

    private static final int W_FLAME = 3;
    private static final int W_SPEED = 3;
    private static final int W_COOLDOWN = 3;
    private static final int W_HEALTH = 1;
    private static final int TOTAL_WEIGHT = W_FLAME + W_SPEED + W_COOLDOWN + W_HEALTH;

    private final Random random;
    private final double dropChance;

    public ItemSpawner() {
        this(new Random(), DEFAULT_DROP_CHANCE);
    }

    public ItemSpawner(Random random, double dropChance) {
        this.random = random;
        this.dropChance = dropChance;
    }

    public Item rollDrop(double x, double y) {
        if (random.nextDouble() >= dropChance) {
            return null;
        }
        return randomItem(x, y);
    }

    public Item randomItem(double x, double y) {
        int roll = random.nextInt(TOTAL_WEIGHT);
        if (roll < W_FLAME) {
            return new FlameItem(x, y);
        }
        roll -= W_FLAME;
        if (roll < W_SPEED) {
            return new SpeedItem(x, y);
        }
        roll -= W_SPEED;
        if (roll < W_COOLDOWN) {
            return new CooldownItem(x, y);
        }
        return new HealthItem(x, y);
    }
}