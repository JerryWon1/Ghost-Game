import processing.core.PImage;

import java.util.List;

public class Alive extends Animates {
    /** Entity's current health level. */
    private int health;

    public Alive(String id, Point position, List<PImage> images, double animationPeriod, double behaviorPeriod, int health) {
        super(id, position, images, animationPeriod, behaviorPeriod);
        this.health = health;
    }

    public int getHealth() {
        return health;
    }

    public void setHealth(int health) {
        this.health = health;
    }
}
