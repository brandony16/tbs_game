package tbs_game.gui.camera.coord_systems;

public record ScenePos(double x, double y) {

    public SceneDelta subtract(ScenePos other) {
        return new SceneDelta(x - other.x(), y - other.y());
    }
}
