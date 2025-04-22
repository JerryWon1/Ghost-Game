public class Animation extends Task {
    /** Number of animation repeats. A zero indicates indefinite repeats. */
    private final int repeatCount;

    public Animation(Entity entity, int repeatCount) {
        super(entity);
        this.repeatCount = repeatCount;
    }

    /** Performs 'Animation' specific logic. */
    public void execute(EventScheduler scheduler) {
        getEntity().updateImage();

        if (repeatCount != 1) {
            scheduler.scheduleEvent(getEntity(), new Animation(this.getEntity(), Math.max(this.repeatCount - 1, 0)), getEntity().getAnimationPeriod());
        }
    }
}
