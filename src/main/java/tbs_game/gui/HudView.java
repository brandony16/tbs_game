package tbs_game.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
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
    private StackPane unitInfo;
    private StackPane turnInfo;

    private StackPane battleInfo;
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
        turnInfo = createHudPanel(300, 50);

        turnText = new Text();
        turnText.setFont(Font.font(20));

        StackPane.setAlignment(turnText, Pos.CENTER_LEFT);

        turnInfo.getChildren().add(turnText);
    }

    private void initTroopInfoHUD() {
        unitInfo = createHudPanel(300, 200);

        unitInfoText = new Text();
        unitInfoText.setFont(Font.font(16));
        unitInfoText.setWrappingWidth(260);

        StackPane.setAlignment(unitInfoText, Pos.TOP_LEFT);

        unitInfo.getChildren().add(unitInfoText);

        unitInfo.setVisible(false);
    }

    private void initBattleInfoHUD() {
        battleInfo = createHudPanel(360, 200);

        VBox content = new VBox(10);
        content.setAlignment(Pos.CENTER);

        HBox statsRow = new HBox(20);
        statsRow.setAlignment(Pos.CENTER);

        attackerStats = new Text();
        attackerStats.setFont(Font.font(16));

        defenderStats = new Text();
        defenderStats.setFont(Font.font(16));

        statsRow.getChildren().addAll(attackerStats, defenderStats);
        content.getChildren().add(statsRow);

        battleInfo.getChildren().add(content);

        battleInfo.setVisible(false);
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

    public void hideCombatPreview() {
        battleInfo.setVisible(false);
    }

    public void showCombatPreview(HoverContext context) {
        if (!context.canAttack()) {
            battleInfo.setVisible(false);
            return;
        }

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

    private StackPane createHudPanel(double width, double height) {
        StackPane panel = new StackPane();

        panel.setPrefSize(width, height);
        panel.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        Rectangle bg = new Rectangle(width, height, HUD_BG);
        bg.setArcWidth(12);
        bg.setArcHeight(12);

        panel.getChildren().add(bg);
        panel.setPadding(new Insets(10));

        return panel;
    }
}
