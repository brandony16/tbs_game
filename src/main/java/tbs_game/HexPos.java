package tbs_game;

public record HexPos(int q, int r) {

    public HexPos add(HexPos other) {
        return new HexPos(q + other.q, r + other.r);
    }

    public int distanceTo(HexPos other) {
        // Compute dist among all axes
        int dq = Math.abs(q - other.q);
        int dr = Math.abs(r - other.r);
        int ds = Math.abs((q + r) - (other.q + other.r));

        // Take max dist along an axis
        return Math.max(dq, Math.max(dr, ds));
    }
}
