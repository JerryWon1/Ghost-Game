import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Ghost extends Animates {
    public static final String GHOST_KEY = "ghost";
    public static final int GHOST_PARSE_PROPERTY_ANIMATION_PERIOD_INDEX = 0;
    public static final int GHOST_PARSE_PROPERTY_BEHAVIOR_PERIOD_INDEX = 1;
    public static final int GHOST_PARSE_PROPERTY_COUNT = 2;

    public Ghost(String id, Point position, List<PImage> images, double animationPeriod, double behaviorPeriod) {
        super(id, position, images, animationPeriod, behaviorPeriod);
    }

    @Override
    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        Optional<Entity> ghostTarget = world.findNearest(getPosition(), new ArrayList<>(List.of(Dude.class)));

        if (ghostTarget.isPresent()) {
            Point tgtPos = ghostTarget.get().getPosition();
            String bg = world.getBackgroundCell(getPosition()).getId();
            if (!bg.equals("ghost_grass") && !bg.equals("lilypad") && !bg.equals("bridge_vertical") && !bg.equals("bridge_horizontal")) {
                world.setBackgroundCell(getPosition(), new Background("ghost_grass", imageLibrary.get("ghost_grass"), 0));
            }

            if (moveToGhost(world, ghostTarget.get(), imageLibrary, scheduler)) {
                Tombstone tombstone = new Tombstone(Tombstone.TOMBSTONE_KEY + "_" + ghostTarget.get().getId(), tgtPos, imageLibrary.get(Tombstone.TOMBSTONE_KEY), 0.800, 5.000, "ghost");
                world.addEntity(tombstone);
                tombstone.scheduleTasks(scheduler, world, imageLibrary);
            }
        }

        scheduleBehavior(scheduler, world, imageLibrary);
    }

    public boolean moveToGhost(World world, Entity target,  ImageLibrary imageLibrary, EventScheduler scheduler) {
        if (getPosition().adjacentTo(target.getPosition())) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = nextPositionGhost(world, target.getPosition(), imageLibrary);
            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public Point nextPositionGhost(World world, Point destination,  ImageLibrary imageLibrary) {
        PathingStrategy pathingStrategy = new AStarPathingStrategy();
        Predicate<Point> canPassThrough = p -> world.inBounds(p) && !(world.isOccupied(p) && world.getOccupant(p).get().getClass() != Water.class);
        BiPredicate<Point, Point> withinReach = Point::adjacentTo;
        List<Point> path = pathingStrategy.computePath(getPosition(), destination, canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);
        if (path.isEmpty()) {
            return getPosition();
        }

        Point nextPos = path.getFirst();
        if (world.isOccupied(nextPos) && world.getOccupant(nextPos).get().getClass() == Water.class) {
            world.setBackgroundCell(nextPos, new Background("lilypad", imageLibrary.get("lilypad"), 0));
        }
        return nextPos;
    }
}
