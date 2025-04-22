import processing.core.PImage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Barracks extends Animates {
    public static final String HAUNTED_HOUSE_KEY = "barrack";
    public boolean spawned = false;

    public Barracks(String id, Point position, List<PImage> images, double animationPeriod, double behaviorPeriod) {
        super(id, position, images, animationPeriod, behaviorPeriod);
    }

    @Override
    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        List<Point> adjacentPositions = new ArrayList<>(List.of(
                getPosition(),
                new Point(getPosition().x - 1, getPosition().y),
                new Point(getPosition().x + 1, getPosition().y),
                new Point(getPosition().x, getPosition().y - 1),
                new Point(getPosition().x, getPosition().y + 1),
                new Point(getPosition().x + 1, getPosition().y + 1),
                new Point(getPosition().x + 1, getPosition().y - 1),
                new Point(getPosition().x - 1, getPosition().y + 1),
                new Point(getPosition().x - 1, getPosition().y - 1)
        ));
        Collections.shuffle(adjacentPositions);
        List<Point> ghostBackgroundPositions = new ArrayList<>();
        Point adjacentPosition = adjacentPositions.removeFirst();
        if (world.inBounds(adjacentPosition) && world.hasBackground(adjacentPosition) && !(world.isOccupied(adjacentPosition) && world.getOccupant(adjacentPosition).get().getClass() == Water.class)) {
            Background bg = world.getBackgroundCell(adjacentPosition);
            if (!bg.getId().equals("ghost_flowers")) {
                ghostBackgroundPositions.add(adjacentPosition);
            }
        }

        if (!ghostBackgroundPositions.isEmpty()) {
            Point backgroundPosition = ghostBackgroundPositions.getFirst();
            Background background = new Background("ghost_flowers", imageLibrary.get("ghost_flowers"), 0);
            world.setBackgroundCell(backgroundPosition, background);
            if (!spawned && world.inBounds(backgroundPosition) && !world.isOccupied(backgroundPosition)) {
                Soldier soldier = new Soldier(Soldier.SOLDIER_KEY + "_" + this.getId(), backgroundPosition, imageLibrary.get(Soldier.SOLDIER_KEY), 0.500, 1.000);
                world.addEntity(soldier);
                soldier.scheduleTasks(scheduler, world, imageLibrary);
                spawned = true;
            }
        }

        scheduleBehavior(scheduler, world, imageLibrary);
    }
}
