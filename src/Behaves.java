import processing.core.PImage;

import java.util.List;

public class Behaves extends Entity {
    /** Positive (non-zero) time delay between the entity's behaviors. */
    private final double behaviorPeriod;

    public Behaves(String id, Point position, List<PImage> images, double behaviorPeriod) {
        super(id, position, images);
        this.behaviorPeriod = behaviorPeriod;
    }

    /** Schedules a single behavior update for the entity. */
    public void scheduleBehavior(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduler.scheduleEvent(this, new Behavior(this, world, imageLibrary), behaviorPeriod);
    }

    public void scheduleTasks(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduleBehavior(scheduler, world, imageLibrary);
    }

    @Override
    public double getBehaviorPeriod() {
        return behaviorPeriod;
    }

}
