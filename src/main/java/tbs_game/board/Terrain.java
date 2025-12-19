package tbs_game.board;

public enum Terrain {
  PLAINS(true, 1, "Plains"),
  FOREST(true, 2, "Forest"),
  MOUNTAIN(false, Integer.MAX_VALUE, "Mountain"),
  WATER(false, Integer.MAX_VALUE, "Water");

  public final boolean passable;
  public final int moveCost;
  public final String name;

  Terrain(boolean passable, int moveCost, String name) {
    this.passable = passable;
    this.moveCost = moveCost;
    this.name = name;
  }
}
