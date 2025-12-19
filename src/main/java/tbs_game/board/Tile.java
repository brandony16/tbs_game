package tbs_game.board;

public class Tile {
  private final Terrain terrain;

  Tile(Terrain terrain) {
    this.terrain = terrain;
  }

  public Terrain getTerrain() {
    return this.terrain;
  }
}
