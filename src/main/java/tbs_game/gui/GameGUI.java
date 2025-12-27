package tbs_game.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import tbs_game.game.Game;
import tbs_game.player.Player;
import tbs_game.units.Unit;

public class GameGUI {

    private static final Color HUD_BG = Color.rgb(200, 160, 105);

    private final Game game;
    private final BoardView boardView;

    private final StackPane root;
    private final StackPane hudLayer;

    // HUD layer
    private Text turnText;
    private Text unitInfoText;
    private Group unitInfo;
    private Group turnInfo;

    public GameGUI(Game game) {
        this.game = game;
        this.boardView = new BoardView(game);

        this.root = new StackPane();
        this.hudLayer = new StackPane();

        root.getChildren().addAll(boardView.getWorldRoot(), hudLayer);

        root.setOnMouseClicked(e -> handleClick(e.getX(), e.getY()));

        initHUD();
        boardView.redraw();
        updateHUD();
    }

    public StackPane getRoot() {
        return root;
    }

    private void handleClick(double mouseX, double mouseY) {
        boardView.handleClick(mouseX, mouseY);
        updateHUD();
    }

    private void initHUD() {
        initTurnHUD();
        initTroopInfoHUD();

        // Add HUD on top of everything
        hudLayer.getChildren().addAll(turnInfo, unitInfo);

        // Set alignments
        StackPane.setAlignment(turnInfo, Pos.TOP_LEFT);
        StackPane.setMargin(turnInfo, new Insets(10));

        StackPane.setAlignment(unitInfo, Pos.BOTTOM_LEFT);
        StackPane.setMargin(unitInfo, new Insets(10));
    }

    private void initTurnHUD() {
        turnInfo = new Group();

        Rectangle bg = new Rectangle(300, 50, HUD_BG);
        turnText = new Text();
        turnText.setFont(Font.font(20));

        double padding = 10;
        turnText.setX(padding);
        turnText.setY(bg.getHeight() / 2.0 + turnText.getFont().getSize() / 4.0);

        turnInfo.getChildren().addAll(bg, turnText);
    }

    private void initTroopInfoHUD() {
        unitInfo = new Group();

        Rectangle bg = new Rectangle(300, 200, HUD_BG);
        unitInfoText = new Text();
        unitInfoText.setFont(Font.font(16));

        double padding = 10;
        unitInfoText.setX(padding);
        unitInfoText.setY(bg.getHeight() / 2.0 + unitInfoText.getFont().getSize() / 4.0);

        unitInfo.getChildren().addAll(bg, unitInfoText);

        unitInfo.setVisible(false);
        unitInfo.setManaged(false);
    }

    private void updateHUD() {
        // Update player turn
        Player current = game.getCurrentPlayer();
        turnText.setText("Player Turn: " + current.name());
        turnText.setFill(current == Player.USER ? Color.BLACK : Color.DARKRED);

        // Update selected unit info
        if (boardView.getSelected() != null) {
            Unit unit = game.getUnitAt(boardView.getSelected());
            if (unit != null) {
                unitInfoText.setText("Selected Unit: " + unit.getType().name()
                        + " HP: " + unit.getHealth()
                        + "/" + unit.getType().maxHp);
                unitInfo.setVisible(true);
                unitInfo.setManaged(true);
            } else {
                unitInfoText.setText("");
                unitInfo.setVisible(false);
                unitInfo.setManaged(false);
            }
        } else {
            unitInfoText.setText("");
            unitInfo.setVisible(false);
            unitInfo.setManaged(false);
        }
    }
}
