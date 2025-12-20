package tbs_game;

public record HexPos(int q, int r) {

    public HexPos add(HexPos other) {
        return new HexPos(q + other.q, r + other.r);
    }
}
