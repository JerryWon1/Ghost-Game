import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Soldier extends Animates {
    public static final String SOLDIER_KEY = "soldier";

    public Soldier(String id, Point position, List<PImage> images, double animationPeriod, double behaviorPeriod) {
        super(id, position, images, animationPeriod, behaviorPeriod);
    }

    @Override
    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        Optional<Entity> soldierTarget = world.findNearest(getPosition(), new ArrayList<>(List.of(Ghost.class)));

        if (soldierTarget.isPresent()) {
            Point tgtPos = soldierTarget.get().getPosition();
            String bg = world.getBackgroundCell(getPosition()).getId();
            if (!bg.equals("ghost_flowers") && !bg.equals("lilypad") && !bg.equals("bridge_vertical") && !bg.equals("bridge_horizontal")) {
                world.setBackgroundCell(getPosition(), new Background("ghost_flowers", imageLibrary.get("ghost_flowers"), 0));
            }

            if (moveToSoldier(world, soldierTarget.get(), imageLibrary, scheduler)) {
                Tombstone tombstone = new Tombstone(Tombstone.TOMBSTONE_KEY + "_" + soldierTarget.get().getId(), tgtPos, imageLibrary.get(Tombstone.TOMBSTONE_KEY), 0.800, 5.000, "soldier");
                world.addEntity(tombstone);
                tombstone.scheduleTasks(scheduler, world, imageLibrary);
            }
        }

        scheduleBehavior(scheduler, world, imageLibrary);
    }

    public boolean moveToSoldier(World world, Entity target, ImageLibrary imageLibrary, EventScheduler scheduler) {
        if (getPosition().adjacentTo(target.getPosition())) {
            world.removeEntity(scheduler, target);
            return true;
        } else {
            Point nextPos = nextPositionSoldier(world, target.getPosition(), imageLibrary);
            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }
            return false;
        }
    }

    public Point nextPositionSoldier(World world, Point destination, ImageLibrary imageLibrary) {
        PathingStrategy pathingStrategy = new AStarPathingStrategy();
        Predicate<Point> canPassThrough = p -> world.inBounds(p) && !(world.isOccupied(p) && world.getOccupant(p).get().getClass() != Water.class);
        BiPredicate<Point, Point> withinReach = Point::adjacentTo;
        List<Point> path = pathingStrategy.computePath(getPosition(), destination, canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);
        if (path.isEmpty()) {
            return getPosition();
        }
        Point nextPos = path.getFirst();
        if (world.isOccupied(nextPos) && world.getOccupant(nextPos).get().getClass() == Water.class) {
            Background background;
            if (nextPos.x == this.getPosition().x) {
                background = new Background("bridge_vertical", imageLibrary.get("soldier_bridge_vertical"), 0);

            } else {
                background = new Background("bridge_horizontal", imageLibrary.get("soldier_bridge_horizontal"), 0);
            }
            world.setBackgroundCell(nextPos, background);
        }
        return nextPos;
    }
}
