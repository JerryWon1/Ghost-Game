import processing.core.PApplet;

import java.util.List;
import java.util.Optional;
import javax.sound.sampled.*;
import java.io.File;

public final class VirtualWorld extends PApplet {
    public static final int TILE_WIDTH = 32;
    public static final int TILE_HEIGHT = 32;
    public static final int VIEW_WIDTH = 512;
    public static final int VIEW_HEIGHT = 288;
    public static final int VIEW_SCALE = 2;
    public static final int VIEW_COLS = VIEW_WIDTH / TILE_WIDTH;
    public static final int VIEW_ROWS = VIEW_HEIGHT / TILE_HEIGHT;
    public static final String IMAGE_LIST_FILE_NAME = "imagelist";
    public static final int DEFAULT_IMAGE_COLOR = 0x808080;
    public static final String FAST_FLAG = "-fast";
    public static final String FASTER_FLAG = "-faster";
    public static final String FASTEST_FLAG = "-fastest";
    public static final String WORLD_STRING_FLAG = "-string";
    public static final double FAST_SCALE = 0.5;
    public static final double FASTER_SCALE = 0.25;
    public static final double FASTEST_SCALE = 0.0625;
    private static String[] ARGS;
    public String worldString = "world";
    public boolean worldStringIsFilePath = true;
    public long startTimeMillis = 0;
    public double timeScale = 1.0;

    public ImageLibrary imageLibrary;
    public World world;
    public WorldView view;
    public EventScheduler scheduler;

    /** Entrypoint that runs the Processing applet. */
    public static void main(String[] args) {
        VirtualWorld.ARGS = args;
        PApplet.main(VirtualWorld.class);
    }

    /** Performs an entire VirtualWorld simulation for testing. */
    public static List<String> headlessMain(String[] args, double lifetime){
        VirtualWorld.ARGS = args;

        VirtualWorld virtualWorld = new VirtualWorld();
        virtualWorld.setup();
        virtualWorld.update(lifetime);

        return virtualWorld.world.log();
    }

    /** Settings for pixelated graphics */
    public void settings() {
        noSmooth();
        size(VIEW_WIDTH * VIEW_SCALE, VIEW_HEIGHT * VIEW_SCALE);
    }

    /** Processing entry point for "sketch" setup. */
    public void setup() {
        parseCommandLine(ARGS);

        playMusic();

        loadImageLibrary(IMAGE_LIST_FILE_NAME);
        loadWorld(worldString, imageLibrary);

        view = new WorldView(VIEW_ROWS, VIEW_COLS, this, VIEW_SCALE, world, TILE_WIDTH, TILE_HEIGHT);
        scheduler = new EventScheduler();
        startTimeMillis = System.currentTimeMillis();

        scheduleInitialTasks(world, scheduler, imageLibrary);
    }

    public void playMusic() {
        try {
            File musicFile = new File("music/background_music.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(musicFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void playSoundEffect() {
        try {
            // Load sound effect file
            File soundEffectFile = new File("music/trumpet.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundEffectFile);
            Clip soundEffectClip = AudioSystem.getClip();
            soundEffectClip.open(audioStream);
            soundEffectClip.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** Handles command line arguments. */
    public void parseCommandLine(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case FAST_FLAG -> timeScale = Math.min(FAST_SCALE, timeScale);
                case FASTER_FLAG -> timeScale = Math.min(FASTER_SCALE, timeScale);
                case FASTEST_FLAG -> timeScale = Math.min(FASTEST_SCALE, timeScale);
                case WORLD_STRING_FLAG -> worldStringIsFilePath = false;
                default -> worldString = arg;
            }
        }
    }

    /** Loads the image library. */
    public void loadImageLibrary(String filename) {
        imageLibrary = new ImageLibrary(ImageLibrary.createImageColored(TILE_WIDTH, TILE_HEIGHT, DEFAULT_IMAGE_COLOR));
        imageLibrary.loadFromFile(filename, this);
    }

    /** Loads the world. */
    public void loadWorld(String loadString, ImageLibrary imageLibrary) {
        if (worldStringIsFilePath) {
            world = WorldParser.createFromFile(loadString, imageLibrary);
        } else {
            world = WorldParser.createFromString(loadString, imageLibrary);
        }
    }

    /** Called to begin each entity's behavior and/or animations when the program starts. */
    public void scheduleInitialTasks(World world, EventScheduler scheduler, ImageLibrary imageLibrary) {
        for (Entity entity : world.getEntities()) {
            if (entity instanceof Behaves e) {
                e.scheduleTasks(scheduler, world, imageLibrary);
            }
        }
    }

    /** Called multiple times automatically per second. */
    public void draw() {
        double appTime = (System.currentTimeMillis() - startTimeMillis) * 0.001;
        double frameTime = appTime / timeScale - scheduler.getCurrentTime();
        update(frameTime);
        view.drawViewport();
    }

    /** Performs update logic. */
    public void update(double frameTime){
        scheduler.updateOnTime(frameTime);
    }

    /** Mouse press input handling. */
    public void mousePressed() {
        Point pressed = mouseToPoint();
        Optional<Entity> entityOptional = world.getOccupant(pressed);
        if (entityOptional.isPresent()) {
            Entity entity = entityOptional.get();
            switch (entity) {
                case House house -> {
                    Barracks barracks = new Barracks(Barracks.HAUNTED_HOUSE_KEY + "_" + house.getId(), house.getPosition(), imageLibrary.get(Barracks.HAUNTED_HOUSE_KEY), 0.250, 0.1);
                    world.removeEntityAt(house.getPosition());
                    world.addEntity(barracks);
                    barracks.scheduleTasks(scheduler, world, imageLibrary);
                }
                case Barracks barracks -> {
                    Point spawn = new Point(barracks.getPosition().x, barracks.getPosition().y + 1);
                    if (!world.isOccupied(spawn)) {
                        Soldier soldier = new Soldier(Soldier.SOLDIER_KEY + "_" + barracks.getId(), spawn, imageLibrary.get(Soldier.SOLDIER_KEY), 0.500, 1.000);
                        world.addEntity(soldier);
                        soldier.scheduleTasks(scheduler, world, imageLibrary);
                        playSoundEffect();
                    }
                }
                case Fairy fairy -> {
                    Point spawn = new Point(fairy.getPosition().x, fairy.getPosition().y + 1);
                    if (!world.isOccupied(spawn)) {
                        Dude dude = new Dude(Dude.DUDE_KEY + "_" + fairy.getId(), spawn, imageLibrary.get(Dude.DUDE_KEY), 0.1875, 0.7500 , 0, Dude.DUDE_PARSE_PROPERTY_RESOURCE_LIMIT_INDEX);
                        world.addEntity(dude);
                        dude.scheduleTasks(scheduler, world, imageLibrary);
                    }
                }
                case Dude dude -> {
                    Ghost ghost = new Ghost(Ghost.GHOST_KEY + "_" + dude.getId(), dude.getPosition(), imageLibrary.get(Ghost.GHOST_KEY), 0.250, 0.750);
                    world.removeEntityAt(dude.getPosition());
                    world.addEntity(ghost);
                    ghost.scheduleTasks(scheduler, world, imageLibrary);
                }
                case Mushroom mushroom -> {
                    Ghost ghost = new Ghost(Ghost.GHOST_KEY + "_" + mushroom.getId(), mushroom.getPosition(), imageLibrary.get(Ghost.GHOST_KEY), 0.250, 0.750);
                    world.removeEntityAt(mushroom.getPosition());
                    world.addEntity(ghost);
                    ghost.scheduleTasks(scheduler, world, imageLibrary);
                }
                case Tree tree -> {
                    Dude dude = new Dude(Dude.DUDE_KEY + "_" + tree.getId(), tree.getPosition(), imageLibrary.get(Dude.DUDE_KEY), 0.500, 1.000, 0, 2);
                    world.removeEntityAt(tree.getPosition());
                    world.addEntity(dude);
                    dude.scheduleTasks(scheduler, world, imageLibrary);
                }
                case Water water -> {
                    Bridge bridge = new Bridge(Bridge.BRIDGE_KEY + "_" + water.getId(), water.getPosition(), imageLibrary.get(Bridge.BRIDGE_KEY));
                    world.removeEntityAt(water.getPosition());
                    world.addEntity(bridge);
                }
                case Lilypad lilypad -> {
                    lilypad.setClicked(1);
                    lilypad.scheduleTasks(scheduler, world, imageLibrary);
                }
                default -> System.out.println("Unhandled entity: " + entity);
            }
        } else if(world.getBackgroundCell(mouseToPoint()).getId().equals("lilypad")) {
            System.out.println("lilypad");
            Lilypad lilypad = new Lilypad(Lilypad.LILYPAD_KEY, mouseToPoint(), imageLibrary.get(Lilypad.LILYPAD_KEY), 0.250, 0.75);
            world.addEntity(lilypad);
            lilypad.scheduleTasks(scheduler, world, imageLibrary);
        } else {
            System.out.println(pressed);
        }
    }

    /** Converts mouse position to world position. */
    private Point mouseToPoint() {
        return view.getViewport().viewportToWorld(mouseX / TILE_WIDTH / VIEW_SCALE, mouseY / TILE_HEIGHT / VIEW_SCALE);
    }

    /** Keyboard input handling. */
    public void keyPressed() {
        if (key == CODED) {
            int dx = 0;
            int dy = 0;

            switch (keyCode) {
                case UP -> dy -= 1;
                case DOWN -> dy += 1;
                case LEFT -> dx -= 1;
                case RIGHT -> dx += 1;
            }

            view.shiftView(dx, dy);
        }
    }

}
