package tbs_game.hexes;

import java.util.ArrayList;

public class FractionalHex {

    public FractionalHex(double q, double r) {
        this.q = q;
        this.r = r;
    }
    public final double q;
    public final double r;

    public HexPos hexRound() {
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

    public FractionalHex hexLerp(FractionalHex b, double t) {
        return new FractionalHex(q * (1.0 - t) + b.q * t, r * (1.0 - t) + b.r * t);
    }

    static public ArrayList<HexPos> hexLinedraw(HexPos a, HexPos b) {
        int N = a.distanceTo(b);
        FractionalHex a_nudge = new FractionalHex(a.q() + 1e-06, a.r() + 1e-06);
        FractionalHex b_nudge = new FractionalHex(b.q() + 1e-06, b.r() + 1e-06);
        
        ArrayList<HexPos> results = new ArrayList<HexPos>() {
            {
            }
        };
        double step = 1.0 / Math.max(N, 1);
        for (int i = 0; i <= N; i++) {
            results.add(a_nudge.hexLerp(b_nudge, step * i).hexRound());
        }
        return results;
    }
}
