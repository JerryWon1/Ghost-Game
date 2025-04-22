import processing.core.PImage;

import java.util.List;

/** Represents an entity that exists in the virtual world. */
public abstract class Entity {
    // Constant string identifiers for the corresponding type of entity.
    // Used to identify entities in the save file and retrieve image information.

    // Constant save file column positions for properties required by all entities.
    public static final int ENTITY_PROPERTY_KEY_INDEX = 0;
    public static final int ENTITY_PROPERTY_ID_INDEX = 1;
    public static final int ENTITY_PROPERTY_POSITION_X_INDEX = 2;
    public static final int ENTITY_PROPERTY_POSITION_Y_INDEX = 3;
    public static final int ENTITY_PROPERTY_COLUMN_COUNT = 4;

    /** Entity's identifier that often includes the corresponding 'key' constant. */
    private final String id;

    /** Entity's x/y position in the world. */
    private Point position;

    /** Entity's inanimate (singular) or animation (multiple) images. */
    private final List<PImage> images;

    /** Index of the element from 'images' used to draw the entity. */
    private int imageIndex;

    /**
     * Constructs an Entity with specified characteristics.
     * In the base program, this is not called directly.
     * Instead, the encapsulated 'create' methods are used to create specific types of entities.
     *
     * @param id              The entity's identifier.
     * @param position        The entity's x/y position in the world.
     * @param images          The entity's inanimate (singular) or animation (multiple) images.
     */
    public Entity(String id, Point position, List<PImage> images) {
        this.id = id;
        this.position = position;
        this.images = images;
        this.imageIndex = 0;
    }

    /** Helper method for testing. Preserve this functionality for all kinds of entities. */
    public String log(){
        if (id.isEmpty()) {
            return null;
        } else {
            return String.format("%s %d %d %d", id, position.x, position.y, imageIndex);
        }
    }

    /** Called when an animation task occurs. */
    public void updateImage() {
        throw new UnsupportedOperationException(String.format("updateImage not supported for %s", getClass()));
    }

    /** Performs the entity's behavior logic. */
    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        throw new UnsupportedOperationException(String.format("executeBehavior not supported for %s", getClass()));
    }

    public double getAnimationPeriod() {
        throw new UnsupportedOperationException(String.format("getAnimationPeriod not supported for %s", getClass()));
    }

    public double getBehaviorPeriod() {
        throw new UnsupportedOperationException(String.format("getBehaviorPeriod not supported for %s", getClass()));
    }

    public int getImageIndex() {
        return imageIndex;
    }

    public void setImageIndex(int imageIndex) {
        this.imageIndex = imageIndex;
    }

    public List<PImage> getImages() {
        return images;
    }

    public String getId() {
        return id;
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point position) {
        this.position = position;
    }
}
