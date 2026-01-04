package tbs_game.hexes;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public record HexPos(int q, int r) {

    public HexPos add(HexPos other) {
        return new HexPos(q + other.q, r + other.r);
    }

    public HexPos subtract(HexPos other) {
        return new HexPos(q - other.q, r - other.r);
    }

    public HexPos scale(int k) {
        return new HexPos(q * k, r * k);
    }

    public HexPos rotateLeft() {
        return new HexPos(q + r, -q);
    }

    public HexPos rotateRight() {
        return new HexPos(-r, q + r);
    }

    static public ArrayList<HexPos> directions = new ArrayList<HexPos>() {
        {
            add(new HexPos(1, 0));
            add(new HexPos(1, -1));
            add(new HexPos(0, -1));
            add(new HexPos(-1, 0));
            add(new HexPos(-1, 1));
            add(new HexPos(0, 1));
        }
    };

    static public HexPos direction(int direction) {
        return HexPos.directions.get(direction);
    }

    public HexPos neighbor(int direction) {
        return add(HexPos.direction(direction));
    }

    public ArrayList<HexPos> getNeighbors() {
        ArrayList<HexPos> neighbors = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            neighbors.add(neighbor(i));
        }
        return neighbors;
    }

    static public ArrayList<HexPos> diagonals = new ArrayList<HexPos>() {
        {
            add(new HexPos(2, -1));
            add(new HexPos(1, -2));
            add(new HexPos(-1, -1));
            add(new HexPos(-2, 1));
            add(new HexPos(-1, 2));
            add(new HexPos(1, 1));
        }
    };

    public HexPos diagonalNeighbor(int direction) {
        return add(HexPos.diagonals.get(direction));
    }

    public int length() {
        return (int) ((Math.abs(q) + Math.abs(r) + Math.abs(-q - r)) / 2);
    }

    public int distanceTo(HexPos other) {
        return subtract(other).length();
    }
}
