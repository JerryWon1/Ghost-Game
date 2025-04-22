import processing.core.PImage;

import java.util.List;

public class Tombstone extends Animates {
    public static final String TOMBSTONE_KEY = "tombstone";
    private String killedBy;

    public Tombstone(String id, Point position, List<PImage> images, double animationPeriod, double behaviorPeriod, String killedBy) {
        super(id, position, images, animationPeriod, behaviorPeriod);
        this.killedBy = killedBy;
    }

    @Override
    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        if (killedBy.equals("ghost")) {
            Ghost ghost = new Ghost(Ghost.GHOST_KEY + "_" + getId(), getPosition(), imageLibrary.get(Ghost.GHOST_KEY), 0.250, 0.750);
            world.removeEntityAt(ghost.getPosition());
            world.addEntity(ghost);
            ghost.scheduleTasks(scheduler, world, imageLibrary);
        } else {
            world.removeEntityAt(getPosition());
        }
    }
}
