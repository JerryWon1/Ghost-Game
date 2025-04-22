import processing.core.PImage;

import java.util.List;

public class Animates extends Behaves {
    /** Positive (non-zero) time delay between the entity's animations. */
    private final double animationPeriod;

    public Animates(String id, Point position, List<PImage> images, double animationPeriod, double behaviorPeriod) {
        super(id, position, images, behaviorPeriod);
        this.animationPeriod = animationPeriod;
    }

    @Override
    public void updateImage() {
        setImageIndex(getImageIndex() + 1);
    }

    /** Begins all animation updates for the entity. */
    public void scheduleAnimation(EventScheduler scheduler) {
        scheduler.scheduleEvent(this, new Animation(this, 0), animationPeriod);
    }

    public void scheduleTasks(EventScheduler scheduler, World world, ImageLibrary imageLibrary) {
        scheduleAnimation(scheduler);
        scheduleBehavior(scheduler, world, imageLibrary);
    }

    @Override
    public double getAnimationPeriod() {
        return animationPeriod;
    }
}