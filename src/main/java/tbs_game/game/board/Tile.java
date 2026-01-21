package tbs_game.game.board;

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

    public int cost() {
        return this.terrain.moveCost;
    }

    public boolean isPassable() {
        return this.terrain.passable;
    }
}
