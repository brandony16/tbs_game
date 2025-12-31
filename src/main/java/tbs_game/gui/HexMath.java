package tbs_game.gui;

import tbs_game.HexPos;

public class HexMath {

    private static final double SQRT3 = Math.sqrt(3);
    private int TILE_RADIUS = 40;

    public HexMath(int tileRadius) {
        this.TILE_RADIUS = tileRadius;
    }

    public double hexToPixelX(HexPos p) {
        return TILE_RADIUS * SQRT3 * (p.q() + 0.5 * (p.r() & 1));
    }

    public double hexToPixelY(HexPos p) {
        return TILE_RADIUS * (3.0 / 2 * p.r());
    }

    public HexPos pixelToHex(double x, double y) {
        // Get row
        double r = y / (TILE_RADIUS * 1.5);

        // SQRT3 * TILE_RADIUS is the width of the tile
        // x / width gives which column, then subtract 0.5 if row is odd to account for offset
        double q = (x / (SQRT3 * TILE_RADIUS)) - 0.5 * (r % 2);
        return hexRound(q, r);
    }

    public HexPos hexRound(double q, double r) {
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

        return new HexPos(rq, rr);
    }
}
