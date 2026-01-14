package tbs_game.gui;

import tbs_game.gui.board.BoardView;
import tbs_game.hexes.AxialPos;

public class HexMath {

    private static final double SQRT3 = Math.sqrt(3);
    public static final double HEX_WIDTH = SQRT3 * BoardView.TILE_RADIUS;

    public static double axialToPixelX(AxialPos p) {
        return BoardView.TILE_RADIUS * (SQRT3 * p.q() + SQRT3 / 2 * p.r());
    }

    public static double axialToPixelY(AxialPos p) {
        return BoardView.TILE_RADIUS * (3.0 / 2 * p.r());
    }

    public static AxialPos pixelToAxial(double x, double y) {
        double q = (SQRT3 / 3 * x - 1.0 / 3 * y) / BoardView.TILE_RADIUS;
        double r = (2.0 / 3 * y) / BoardView.TILE_RADIUS;
        return hexRound(q, r);
    }

    public static AxialPos hexRound(double q, double r) {
        double s = -q - r;

        // Round each axis to nearest int
        int rq = (int) Math.round(q);
        int rr = (int) Math.round(r);
        int rs = (int) Math.round(s);

        // Find diff between rounded value and actual value
        double dq = Math.abs(rq - q);
        double dr = Math.abs(rr - r);
        double ds = Math.abs(rs - s);

        if (dq > dr && dq > ds) {
            rq = -rr - rs;
        } else if (dr > ds) {
            rr = -rq - rs;
        }

        return new AxialPos(rq, rr);
    }
}
