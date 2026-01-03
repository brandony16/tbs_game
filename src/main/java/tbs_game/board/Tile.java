package tbs_game.board;

public class Tile {

    private Terrain terrain;

    public Tile(Terrain terrain) {
        this.terrain = terrain;
    }

    public Terrain getTerrain() {
        return this.terrain;
    }

    public void setTerrain(Terrain t) {
        this.terrain = t;
    }
}
