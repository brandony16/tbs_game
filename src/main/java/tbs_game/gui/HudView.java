package tbs_game.gui;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import tbs_game.HexPos;
import tbs_game.game.Game;
import tbs_game.player.Player;
import tbs_game.units.Unit;

public class HudView {

    private static final Color HUD_BG = Color.rgb(200, 160, 105);

    private final Game game;
    private final StackPane hudLayer;

    // HUD layer
    private Text turnText;
    private Text unitInfoText;
    private Group unitInfo;
    private Group turnInfo;

    private Group battleInfo;
    private Text attackerStats;
    private Text defenderStats;

    public HudView(Game game) {
        this.game = game;
        hudLayer = new StackPane();
    }

    public StackPane getHudRoot() {
        return this.hudLayer;
    }

    public void initHUD() {
        initTurnHUD();
        initTroopInfoHUD();
        initBattleInfoHUD();

        // Add HUD on top of everything
        hudLayer.getChildren().addAll(turnInfo, unitInfo, battleInfo);

        // Set alignments
        StackPane.setAlignment(turnInfo, Pos.TOP_LEFT);
        StackPane.setAlignment(unitInfo, Pos.BOTTOM_LEFT);
        StackPane.setAlignment(battleInfo, Pos.BOTTOM_CENTER);
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

    private void initBattleInfoHUD() {
        battleInfo = new Group();

        Rectangle bg = new Rectangle(300, 200, HUD_BG);
        attackerStats = new Text();
        attackerStats.setFont(Font.font(16));

        defenderStats = new Text();
        defenderStats.setFont(Font.font(16));

        double padding = 10;
        attackerStats.setX(padding);
        attackerStats.setY(bg.getHeight() / 2.0 + attackerStats.getFont().getSize() / 4.0);
        defenderStats.setX(bg.getWidth() - padding);
        defenderStats.setY(bg.getHeight() / 2.0 + defenderStats.getFont().getSize() / 4.0);

        battleInfo.getChildren().addAll(bg, attackerStats);

        battleInfo.setVisible(false);
        battleInfo.setManaged(false);
    }

    public void updateHUD(HexPos selected) {
        // Update player turn
        Player current = game.getCurrentPlayer();
        turnText.setText("Player Turn: " + current.name());
        turnText.setFill(current == Player.USER ? Color.BLACK : Color.DARKRED);

        // Update selected unit info
        if (selected != null) {
            Unit unit = game.getUnitAt(selected);
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

    public void showCombatPreview(HoverContext context) {
        if (!context.canAttack()) {
            battleInfo.setVisible(false);
            return;
        }
        
        System.out.println("showing preview");
        updateCombatPreview(context);
        battleInfo.setVisible(true);
    }

    private void updateCombatPreview(HoverContext context) {
        Unit attacker = game.getUnitAt(context.selected());
        Unit defender = game.getUnitAt(context.hovered());

        if (attacker == null || defender == null) {
            throw new Error("Combat preview must have attacker and defender units");
        }

        attackerStats.setText("This is the attacker");
        defenderStats.setText("This is the defender");
    }
}
