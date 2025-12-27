package tbs_game.gui;

import tbs_game.HexPos;

public class HexMath {

    private static final double SQRT3 = Math.sqrt(3);
    private int TILE_RADIUS = 40;

    public HexMath(int tileRadius) {
        this.TILE_RADIUS = tileRadius;
    }

    public double hexToPixelX(HexPos p) {
        return TILE_RADIUS * (SQRT3 * p.q() + SQRT3 / 2 * p.r());
    }

    public double hexToPixelY(HexPos p) {
        return TILE_RADIUS * (3.0 / 2 * p.r());
    }

    public HexPos pixelToHex(double x, double y) {
        double q = (Math.sqrt(3) / 3 * x - 1.0 / 3 * y) / TILE_RADIUS;
        double r = (2.0 / 3 * y) / TILE_RADIUS;
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
