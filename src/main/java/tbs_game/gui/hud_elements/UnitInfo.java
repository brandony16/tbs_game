package tbs_game.gui.hud_elements;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import tbs_game.gui.HudView;
import tbs_game.units.Unit;
import tbs_game.units.UnitType;

public class UnitInfo {

    private StackPane unitInfo;
    private Text unitHealth;
    private Text unitName;
    private Text unitMove;
    private Text unitAttack;

    public UnitInfo() {
        initTroopInfoHUD();
    }

    public StackPane getRoot() {
        return this.unitInfo;
    }

    public void updateInfo(Unit unit) {
        if (unit == null) {
            return;
        }

        UnitType type = unit.getType();

        unitName.setText(type.name());
        unitHealth.setText("HP: " + unit.getHealth() + "/" + type.maxHp);
        unitMove.setText("Movement: " + type.moveRange);
        unitAttack.setText("Strength: " + type.attackDamage);
    }

    public void resetInfo() {
        unitName.setText("");
        unitHealth.setText("");
        unitMove.setText("");
        unitAttack.setText("");
    }

    public void setVisibility(boolean isVisible) {
        this.unitInfo.setVisible(isVisible);
    }

    private void initTroopInfoHUD() {
        unitInfo = PanelFactory.createHudPanel(300, 200);

        VBox content = new VBox(6);
        content.setPadding(new Insets(10));
        content.setAlignment(Pos.TOP_LEFT);

        unitName = new Text();
        unitName.setFont(Font.font(HudView.FONT_FAMILTY, FontWeight.BOLD, 24));
        unitName.setWrappingWidth(260);

        unitHealth = new Text();
        unitHealth.setFont(HudView.HUD_FONT);

        unitMove = new Text();
        unitMove.setFont(HudView.HUD_FONT);

        unitAttack = new Text();
        unitAttack.setFont(HudView.HUD_FONT);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        content.getChildren().addAll(
                unitName,
                unitHealth,
                unitMove,
                unitAttack
        );

        unitInfo.getChildren().add(content);
        unitInfo.setVisible(false);
    }
}
