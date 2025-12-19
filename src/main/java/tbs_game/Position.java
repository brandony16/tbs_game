package tbs_game;

public record Position(int x, int y) {

    public int toIndex(int boardWidth) {
        return y * boardWidth + x;
    }
}
