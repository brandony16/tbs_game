package tbs_game.board;

import javafx.scene.paint.Color;

public enum Terrain {
    PLAINS(true, 1, "Plains", Color.GREEN),
    FOREST(true, 2, "Forest", Color.DARKGREEN),
    MOUNTAIN(false, Integer.MAX_VALUE, "Mountain", Color.DARKGRAY),
    WATER(false, Integer.MAX_VALUE, "Water", Color.BLUE);

    public final boolean passable;
    public final int moveCost;
    public final String name;
    public final Color color;

    Terrain(boolean passable, int moveCost, String name, Color color) {
        this.passable = passable;
        this.moveCost = moveCost;
        this.name = name;
        this.color = color;
    }
}
