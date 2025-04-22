import processing.core.PImage;
import java.util.List;

public class Lilypad extends Animates {
    public static final String LILYPAD_KEY = "lilypad";
    private int clicked = 0;

    public Lilypad(String id, Point position, List<PImage> images, double animationPeriod, double behaviorPeriod) {
        super(id, position, images, animationPeriod, behaviorPeriod);
    }

    @Override
    public void executeBehavior(World world, ImageLibrary imageLibrary, EventScheduler scheduler) {
        if (clicked == 1) {
            world.removeEntityAt(getPosition());
        }
        scheduleBehavior(scheduler, world, imageLibrary);
    }

    public void setClicked(int clicked) {
        this.clicked = clicked;
    }
}
