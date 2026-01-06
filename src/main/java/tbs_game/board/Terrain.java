package tbs_game.board;

import javafx.scene.paint.Color;

public enum Terrain {
    PLAINS(true, 1, "Plains", Color.GREEN, "plains.png"),
    FOREST(true, 2, "Forest", Color.DARKGREEN, "tree.png"),
    MOUNTAIN(false, Integer.MAX_VALUE, "Mountain", Color.DARKGRAY, "mountain.png"),
    WATER(false, Integer.MAX_VALUE, "Water", Color.BLUE, "water.png");

    public final boolean passable;
    public final int moveCost;
    public final String name;
    public final Color color;
    public final String spritePath;

    Terrain(boolean passable, int moveCost, String name, Color color, String spritePath) {
        this.passable = passable;
        this.moveCost = moveCost;
        this.name = name;
        this.color = color;
        this.spritePath = "/terrain/" + spritePath;
    }
}
