package tbs_game.gui.board.renderers;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import tbs_game.gui.AssetManager;
import tbs_game.gui.board.BoardView;
import tbs_game.gui.coord_systems.WorldPos;
import tbs_game.units.Unit;

public class UnitRenderer {

  public static Node renderUnit(Unit unit, WorldPos hexCenter) {
    Image img = AssetManager.getImage(unit.getType().spritePath);
    ImageView iv = new ImageView(img);

    iv.setFitHeight(BoardView.TILE_RADIUS * 1.25);
    iv.setPreserveRatio(true);

    double scale = (BoardView.TILE_RADIUS * 1.25) / img.getHeight();
    double width = img.getWidth() * scale;

    iv.setX(hexCenter.x() - width / 2);
    iv.setY(hexCenter.y() - iv.getFitHeight() / 2);

    return iv;
  }

  public static Node renderHealthBar(Unit unit, WorldPos hexCenter) {
    double barWidth = BoardView.TILE_RADIUS * 0.6;
    double barHeight = 6;
    double barX = hexCenter.x() - barWidth / 2;
    double barY = hexCenter.y() + BoardView.TILE_RADIUS * 0.45;

    Rectangle bg = new Rectangle(barX, barY, barWidth, barHeight);
    bg.setFill(Color.DARKRED);

    double hpRatio = (double) unit.getHealth() / unit.getType().maxHp;
    Rectangle fg = new Rectangle(barX, barY, barWidth * hpRatio, barHeight);
    fg.setFill(Color.LIMEGREEN);

    Group healthBar = new Group();
    healthBar.getChildren().addAll(bg, fg);
    return healthBar;
  }
}
