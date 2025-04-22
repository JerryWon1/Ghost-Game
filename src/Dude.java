import processing.core.PImage;

import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class Dude extends Animates {
    public static final String DUDE_KEY = "dude";
    public static final int DUDE_PARSE_PROPERTY_ANIMATION_PERIOD_INDEX = 0;
    public static final int DUDE_PARSE_PROPERTY_BEHAVIOR_PERIOD_INDEX = 1;
    public static final int DUDE_PARSE_PROPERTY_RESOURCE_LIMIT_INDEX = 2;
    public static final int DUDE_PARSE_PROPERTY_COUNT = 3;

    private int resourceCount;
    private final int resourceLimit;

    public Dude(String id, Point position, List<PImage> images, double animationPeriod, double behaviorPeriod, int resourceCount, int resourceLimit) {
        super(id, position, images, animationPeriod, behaviorPeriod);
        this.resourceCount = resourceCount;
        this.resourceLimit = resourceLimit;
    }

    @Override
    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        Optional<Entity> dudeTarget = findDudeTarget(world);
        if (dudeTarget.isEmpty() || !moveToDude(world, dudeTarget.get(), scheduler) || !transformDude(world, scheduler, imageLibrary)) {
            scheduleBehavior(scheduler, world, imageLibrary);
        }
    }

    public Optional<Entity> findDudeTarget(World world) {
        List<Class<?>> potentialTargets;

        if (resourceCount == resourceLimit) {
            potentialTargets = List.of(House.class);
        } else {
            potentialTargets = List.of(Tree.class, Sapling.class);
        }

        return world.findNearest(getPosition(), potentialTargets);
    }

    public boolean moveToDude(World world, Entity target, EventScheduler scheduler) {
        if (getPosition().adjacentTo(target.getPosition())) {
            if (target instanceof Tree || target instanceof Sapling) {
                ((Alive) target).setHealth(((Alive) target).getHealth() - 1);
            }
            return true;
        } else {
            Point nextPos = nextPositionDude(world, target.getPosition());

            if (!getPosition().equals(nextPos)) {
                world.moveEntity(scheduler, this, nextPos);
            }

            return false;
        }
    }

    public Point nextPositionDude(World world, Point destination) {
        PathingStrategy pathingStrategy = new AStarPathingStrategy();
        Predicate<Point> canPassThrough = p -> world.inBounds(p) && !(world.isOccupied(p) && world.getOccupant(p).get().getClass() != Stump.class);
        BiPredicate<Point, Point> withinReach = Point::adjacentTo;
        List<Point> path = pathingStrategy.computePath(getPosition(), destination, canPassThrough, withinReach, PathingStrategy.CARDINAL_NEIGHBORS);
        if (path.isEmpty()) {
            return getPosition();
        }
        return path.getFirst();
    }

    public boolean transformDude(World world, EventScheduler scheduler, ImageLibrary imageLibrary) {
        if (resourceCount < resourceLimit) {
            resourceCount++;
            if (resourceCount == resourceLimit) {
                Dude dude = new Dude(getId(), getPosition(), imageLibrary.get(DUDE_KEY + "_carry"), getAnimationPeriod(), getBehaviorPeriod(), resourceCount, resourceLimit);

                world.removeEntity(scheduler, this);

                world.addEntity(dude);
                dude.scheduleTasks(scheduler, world, imageLibrary);

                return true;
            }
        } else {
            Dude dude = new Dude(getId(), getPosition(), imageLibrary.get(DUDE_KEY), getAnimationPeriod(), getBehaviorPeriod(), 0, resourceLimit);

            world.removeEntity(scheduler, this);

            world.addEntity(dude);
            dude.scheduleTasks(scheduler, world, imageLibrary);

            return true;
        }

        return false;
    }
}
