package tbs_game.hexes;

public class OffsetPos {

    public final int col;
    public final int row;

    public OffsetPos(int col, int row) {
        this.col = col;
        this.row = row;
    }
    public static int OFFSET = -1;

    public static OffsetPos offsetFromAxial(AxialPos h) {
        int parity = h.r & 1;
        int col = h.q + (int) ((h.r + OFFSET * parity) / 2);
        int row = h.r;

        return new OffsetPos(col, row);
    }

    public static AxialPos offsetToAxial(OffsetPos h) {
        int parity = h.row & 1;
        int q = h.col - (int) ((h.row + OFFSET * parity) / 2);
        int r = h.row;

        return new AxialPos(q, r);
    }

    public AxialPos toAxial() {
        int parity = row & 1;
        int q = col - (int) ((row + OFFSET * parity) / 2);
        int r = row;

        return new AxialPos(q, r);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OffsetPos)) {
            return false;
        }
        OffsetPos other = (OffsetPos) o;
        return col == other.col && row == other.row;
    }

    @Override
    public int hashCode() {
        return 31 * col + row;
    }
}
