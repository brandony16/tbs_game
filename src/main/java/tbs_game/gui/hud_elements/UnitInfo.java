package tbs_game.gui.hud_elements;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
        unitInfo = PanelFactory.createHudPanel(300, 200);

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
        unitHealth.setText(unit.getHealth() + "/" + type.maxHp);
        unitMove.setText(unit.getMovementPoints() + "/" + unit.getMaxMovementPoints());
        unitAttack.setText("" + type.attackDamage);
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
        VBox content = new VBox(8);
        content.setPadding(new Insets(12));
        content.setAlignment(Pos.TOP_LEFT);

        // ---- Unit name (header) ----
        unitName = new Text();
        unitName.setFont(HudView.HEADER_FONT);
        unitName.setWrappingWidth(260);

        // Divider
        Separator separator = new Separator();
        separator.setOpacity(0.6);

        // ---- Stats ----
        unitHealth = new Text();
        unitHealth.setFont(HudView.HUD_FONT);

        unitMove = new Text();
        unitMove.setFont(HudView.HUD_FONT);

        unitAttack = new Text();
        unitAttack.setFont(HudView.HUD_FONT);

        VBox stats = new VBox(4);
        stats.getChildren().addAll(
                createStatRow("Health", HudIcons.HEALTH, unitHealth),
                createStatRow("Move", HudIcons.MOVE_PTS, unitMove),
                createStatRow("Strength", HudIcons.STRENGTH, unitAttack)
        );

        content.getChildren().addAll(
                unitName,
                separator,
                stats
        );

        unitInfo.getChildren().add(content);
        unitInfo.setVisible(false);
    }

    private HBox createStatRow(String label, Image icon, Text value) {
        Node iconNode = buildStatIcon(icon);

        Text labelText = new Text(label);
        labelText.setFont(HudView.HUD_FONT);
        labelText.setOpacity(0.85);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox row = new HBox(6, iconNode, labelText, spacer, value);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    private Node buildStatIcon(Image icon) {
        StackPane panel = new StackPane();

        Circle bg = new Circle(HudView.FONT_SIZE * 4 / 5, Color.GRAY);
        bg.setStroke(Color.BLACK);
        bg.setStrokeWidth(2);

        ImageView iv = HudIcons.buildIcon(icon, HudView.FONT_SIZE);
        HudIcons.recolorIcon(iv, Color.BLACK);

        panel.getChildren().addAll(bg, iv);
        panel.setAlignment(Pos.CENTER);

        return panel;
    }
}
