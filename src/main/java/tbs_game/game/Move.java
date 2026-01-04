package tbs_game.game;

import java.util.ArrayList;

import tbs_game.hexes.HexPos;

public class Move {

    public final HexPos from;
    public final HexPos to;
    public final ArrayList<HexPos> path;
    public final int cost;

    public Move(HexPos from, HexPos to, ArrayList<HexPos> path, int cost) {
        this.from = from;
        this.to = to;
        this.path = path;
        this.cost = cost;
    }
}
