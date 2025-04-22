import processing.core.PImage;

import java.util.List;

public class Bridge extends Entity {
    public static final String BRIDGE_KEY = "bridge_vertical";

    public Bridge(String id, Point position, List<PImage> images) {
        super(id, position, images);
    }
}

