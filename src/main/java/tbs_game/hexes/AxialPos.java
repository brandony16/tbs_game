package tbs_game.hexes;

import java.util.ArrayList;

public class AxialPos {

    public final int q;
    public final int r;

    public AxialPos(int q, int r) {
        this.q = q;
        this.r = r;
    }

    public int q() {
        return this.q;
    }

    public int r() {
        return this.r;
    }

    public AxialPos add(AxialPos other) {
        return new AxialPos(q + other.q, r + other.r);
    }

    public AxialPos subtract(AxialPos other) {
        return new AxialPos(q - other.q, r - other.r);
    }

    public AxialPos scale(int k) {
        return new AxialPos(q * k, r * k);
    }

    public AxialPos rotateLeft() {
        return new AxialPos(q + r, -q);
    }

    public AxialPos rotateRight() {
        return new AxialPos(-r, q + r);
    }

    static public ArrayList<AxialPos> directions = new ArrayList<AxialPos>() {
        {
            add(new AxialPos(1, 0));
            add(new AxialPos(1, -1));
            add(new AxialPos(0, -1));
            add(new AxialPos(-1, 0));
            add(new AxialPos(-1, 1));
            add(new AxialPos(0, 1));
        }
    };

    static public AxialPos direction(int direction) {
        return AxialPos.directions.get(direction);
    }

    public AxialPos neighbor(int direction) {
        return add(AxialPos.direction(direction));
    }

    public ArrayList<AxialPos> getNeighbors() {
        ArrayList<AxialPos> neighbors = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            neighbors.add(neighbor(i));
        }
        return neighbors;
    }

    public static ArrayList<AxialPos> diagonals = new ArrayList<AxialPos>() {
        {
            add(new AxialPos(2, -1));
            add(new AxialPos(1, -2));
            add(new AxialPos(-1, -1));
            add(new AxialPos(-2, 1));
            add(new AxialPos(-1, 2));
            add(new AxialPos(1, 1));
        }
    };

    public AxialPos diagonalNeighbor(int direction) {
        return add(AxialPos.diagonals.get(direction));
    }

    public int length() {
        return (int) ((Math.abs(q) + Math.abs(r) + Math.abs(-q - r)) / 2);
    }

    public int distanceTo(AxialPos other) {
        return subtract(other).length();
    }

    public OffsetPos toOffset() {
        int parity = r & 1;
        int col = q + (int) ((r + OffsetPos.OFFSET * parity) / 2);
        int row = r;

        return new OffsetPos(col, row);
    }

    @Override
    public String toString() {
        return "(" + q + ", " + r + ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AxialPos)) {
            return false;
        }
        AxialPos other = (AxialPos) o;
        return q == other.q && r == other.r;
    }

    @Override
    public int hashCode() {
        return 31 * q + r;
    }
}
