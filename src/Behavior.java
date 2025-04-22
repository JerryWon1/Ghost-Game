public class Behavior extends Task {
    /** World in which the task occurs. */
    private final World world;

    /** Image data that may be used by the task. */
    private final ImageLibrary imageLibrary;

    public Behavior(Entity entity, World world, ImageLibrary imageLibrary) {
        super(entity);
        this.world = world;
        this.imageLibrary = imageLibrary;
    }

    /** Performs 'Behavior' specific logic. */
    public void execute(EventScheduler scheduler) {
        getEntity().executeBehavior(world, imageLibrary, scheduler);
    }
}
