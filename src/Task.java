/** A scheduled task to be carried out by a specific entity. */
public abstract class Task {
    /** Entity enacting the task. */
    private final Entity entity;

    /**
     * Constructs an Task object with specified characteristics.
     * In the base program, this is not called directly.
     * Instead, the encapsulated 'create' method are used to create specific kinds.
     *
     * @param entity The entity enacting the task.
     */
    public Task(Entity entity) {
        this.entity = entity;
    }

    /** Called when the task's scheduled time occurs. */
    abstract void execute(EventScheduler scheduler);

    public Entity getEntity() {
        return entity;
    }
}
