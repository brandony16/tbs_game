package tbs_game.game;

import java.util.ArrayList;

import tbs_game.hexes.AxialPos;

public class ActionPath {

    public final AxialPos from;
    public final AxialPos to;
    public final ArrayList<AxialPos> path;
    public final int cost;

    public ActionPath(AxialPos from, AxialPos to, ArrayList<AxialPos> path, int cost) {
        this.from = from;
        this.to = to;
        this.path = path;
        this.cost = cost;
    }
}
