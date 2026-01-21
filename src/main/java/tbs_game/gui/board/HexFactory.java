package tbs_game.gui.board;

import javafx.scene.shape.Polygon;
import tbs_game.gui.coord_systems.WorldPos;

public class HexFactory {

  public static Polygon createHex(WorldPos hexCenter) {
    Polygon hex = new Polygon();
    for (int i = 0; i < 6; i++) {
      double angle = Math.toRadians(60 * i - 30); // pointy-top
      double x = hexCenter.x() + BoardView.TILE_RADIUS * Math.cos(angle);
      double y = hexCenter.y() + BoardView.TILE_RADIUS * Math.sin(angle);
      hex.getPoints().addAll(x, y);
    }
    return hex;
  }

  public static WorldPos[] hexCorners(WorldPos hexCenter) {
    WorldPos[] corners = new WorldPos[6];
    for (int i = 0; i < 6; i++) {
      double angleRad = Math.toRadians(60 * i - 30);
      double x = hexCenter.x() + BoardView.TILE_RADIUS * Math.cos(angleRad);
      double y = hexCenter.y() - BoardView.TILE_RADIUS * Math.sin(angleRad); // +y in screen coords is down
      corners[i] = new WorldPos(x, y);
    }
    return corners;
  }

}
