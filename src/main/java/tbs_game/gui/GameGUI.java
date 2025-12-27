package tbs_game.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javafx.animation.TranslateTransition;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;
import tbs_game.HexPos;
import tbs_game.board.Board;
import tbs_game.game.Game;
import tbs_game.player.Player;
import tbs_game.units.Unit;

public class GameGUI {

    private static final int TILE_RADIUS = 40;
    private static final double SQRT3 = Math.sqrt(3);
    private static final Color HUD_BG = Color.rgb(200, 160, 105);

    private final Game game;

    private final StackPane root;
    private final Pane boardLayer;
    private final StackPane hudLayer;

    // Board layer
    private final Group boardGroup;
    private final Group highlightGroup;
    private final Group unitGroup;
    private final Map<HexPos, Group> unitElements;

    // HUD layer
    private Text turnText;
    private Text unitInfoText;
    private Group unitInfo;
    private Group turnInfo;

    private HexPos selectedPos;
    private Set<HexPos> reachableHexes = Set.of();

    public GameGUI(Game game) {
        this.game = game;
        this.root = new StackPane();

        this.boardLayer = new Pane();
        this.hudLayer = new StackPane();
        root.getChildren().addAll(boardLayer, hudLayer);

        this.boardGroup = new Group();
        this.highlightGroup = new Group();
        this.unitGroup = new Group();
        this.unitElements = new HashMap<>();
        boardLayer.getChildren().addAll(boardGroup, highlightGroup, unitGroup);

        initHUD();

        root.setOnMouseClicked(e -> handleClick(e.getX(), e.getY()));

        redraw();

        Bounds b = boardGroup.getBoundsInParent();

        boardGroup.layoutXProperty().bind(root.widthProperty().subtract(b.getWidth()).divide(2).subtract(b.getMinX()));
        boardGroup.layoutYProperty().bind(root.heightProperty().subtract(b.getHeight()).divide(2).subtract(b.getMinY()));
        highlightGroup.layoutXProperty().bind(boardGroup.layoutXProperty());
        highlightGroup.layoutYProperty().bind(boardGroup.layoutYProperty());
        unitGroup.layoutXProperty().bind(boardGroup.layoutXProperty());
        unitGroup.layoutYProperty().bind(boardGroup.layoutYProperty());
    }

    public StackPane getRoot() {
        return root;
    }

    private void handleClick(double mouseX, double mouseY) {
        Point2D boardCoords = boardGroup.sceneToLocal(mouseX, mouseY);

        HexPos clicked = pixelToHex(boardCoords.getX(), boardCoords.getY());
        if (!game.getBoard().isOnBoard(clicked)) {
            clearSelection();
            return;
        }

        Unit unit = game.getUnitAt(clicked);
        boolean isFriendlyUnit = unit != null && unit.getOwner().equals(game.getCurrentPlayer());
        if (this.selectedPos == null) {
            if (isFriendlyUnit) {
                selectPos(clicked);
            }
        } else {
            if (reachableHexes.contains(clicked)) {
                animateMove(selectedPos, clicked);
            } else if (isFriendlyUnit) {
                selectPos(clicked); // switch selection
            } else {
                clearSelection();
            }
        }
    }

    private void selectPos(HexPos pos) {
        this.selectedPos = pos;
        this.reachableHexes = game.getReachableHexes(pos);
        redraw();
    }

    private void clearSelection() {
        this.selectedPos = null;
        this.reachableHexes = Set.of();
        redraw();
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

    private void drawBoard() {
        boardGroup.getChildren().clear();
        highlightGroup.getChildren().clear();

        Board board = game.getBoard();

        for (HexPos pos : board.getPositions()) {
            double cx = hexToPixelX(pos);
            double cy = hexToPixelY(pos);

            Polygon hex = createHex(cx, cy);
            hex.setFill(Color.GREEN);
            hex.setStroke(Color.BLACK);
            boardGroup.getChildren().add(hex);

            if (selectedPos != null) {
                if (selectedPos.equals(pos)) { // Gold outline for selected square
                    Polygon outline = createHex(cx, cy);
                    outline.setFill(Color.TRANSPARENT);
                    outline.setStroke(Color.GOLD);
                    outline.setStrokeWidth(4.0);
                    outline.setStrokeType(StrokeType.INSIDE);
                    outline.setMouseTransparent(true);
                    highlightGroup.getChildren().add(outline);
                } else if (reachableHexes.contains(pos)) {
                    Polygon highlight = createHex(cx, cy);
                    highlight.setFill(Color.rgb(0, 0, 0, 0.3));

                    Unit selectedUnit = game.getUnitAt(selectedPos);
                    Unit unitInRange = game.getUnitAt(pos);
                    if (unitInRange != null && !selectedUnit.getOwner().equals(unitInRange.getOwner())) {
                        highlight.setStroke(Color.RED);
                        highlight.setStrokeWidth(2.0);
                        highlight.setStrokeType(StrokeType.INSIDE);

                    }
                    highlightGroup.getChildren().add(highlight);
                }
            }
        }
    }

    private void drawUnits() {
        unitGroup.getChildren().clear();
        unitElements.clear();

        for (HexPos pos : game.getUnitPositions()) {
            Group unitElement = drawUnitElement(pos);

            unitElements.put(pos, unitElement);
            unitGroup.getChildren().add(unitElement);
        }
    }

    private Group drawUnitElement(HexPos pos) {
        double cx = hexToPixelX(pos);
        double cy = hexToPixelY(pos);

        Group group = new Group();
        Unit unit = game.getUnitAt(pos);

        // Unit body
        Circle body = new Circle(cx, cy, TILE_RADIUS * 0.35);
        body.setFill(colorFor(unit));
        body.setStroke(Color.BLACK);

        // Health bar sizes
        double barWidth = TILE_RADIUS * 0.6;
        double barHeight = 6;

        double barX = cx - barWidth / 2;
        double barY = cy + TILE_RADIUS * 0.45;

        // HP Background
        Rectangle bg = new Rectangle(barX, barY, barWidth, barHeight);
        bg.setFill(Color.DARKRED);

        // HP Bar
        double hpRatio = (double) unit.getHealth() / unit.getType().maxHp;
        Rectangle fg = new Rectangle(barX, barY, barWidth * hpRatio, barHeight);
        fg.setFill(Color.LIMEGREEN);

        group.getChildren().addAll(body, bg, fg);
        return group;
    }

    private void animateMove(HexPos from, HexPos to) {
        Group unitElement = unitElements.get(from);
        if (unitElement == null) {
            return;
        }

        if (game.moveUnit(from, to) == false) {
            return;
        }

        double startX = hexToPixelX(from);
        double startY = hexToPixelY(from);
        double endX = hexToPixelX(to);
        double endY = hexToPixelY(to);

        TranslateTransition tt = new TranslateTransition(Duration.millis(200), unitElement);
        tt.setFromX(0);
        tt.setFromY(0);
        tt.setToX(endX - startX);
        tt.setToY(endY - startY);

        tt.setOnFinished(e -> {
            // Reset translate and snap to final position
            unitElement.setTranslateX(0);
            unitElement.setTranslateY(0);
            unitElement.setLayoutX(endX);
            unitElement.setLayoutY(endY);

            unitElements.remove(from);
            unitElements.put(to, unitElement);

            // Update game state after animation
            clearSelection();
            redraw();
        });

        tt.play();
    }

    private void updateHUD() {
        // Update player turn
        Player current = game.getCurrentPlayer();
        turnText.setText("Player Turn: " + current.name());
        turnText.setFill(current == Player.USER ? Color.BLACK : Color.DARKRED);

        // Update selected unit info
        if (selectedPos != null) {
            Unit unit = game.getUnitAt(selectedPos);
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

    private void redraw() {
        drawBoard();
        drawUnits();
        updateHUD();
    }

    private Color colorFor(Unit unit) {
        return switch (unit.getOwner()) {
            case USER ->
                Color.BLUE;
            case AI ->
                Color.RED;
        };
    }

    private Polygon createHex(double cx, double cy) {
        Polygon hex = new Polygon();
        for (int i = 0; i < 6; i++) {
            double angle = Math.toRadians(60 * i - 30); // pointy-top
            double x = cx + TILE_RADIUS * Math.cos(angle);
            double y = cy + TILE_RADIUS * Math.sin(angle);
            hex.getPoints().addAll(x, y);
        }
        return hex;
    }

    // ----- PIXEL MATH AND CONVERSIONS ----
    private double hexToPixelX(HexPos p) {
        return TILE_RADIUS * (SQRT3 * p.q() + SQRT3 / 2 * p.r());
    }

    private double hexToPixelY(HexPos p) {
        return TILE_RADIUS * (3.0 / 2 * p.r());
    }

    private HexPos pixelToHex(double x, double y) {
        double q = (Math.sqrt(3) / 3 * x - 1.0 / 3 * y) / TILE_RADIUS;
        double r = (2.0 / 3 * y) / TILE_RADIUS;
        return hexRound(q, r);
    }

    private HexPos hexRound(double q, double r) {
        double s = -q - r;

        // Round each axis to nearest int
        int rq = (int) Math.round(q);
        int rr = (int) Math.round(r);
        int rs = (int) Math.round(s);

        // Find diff between rounded value and actual value
        double dq = Math.abs(rq - q);
        double dr = Math.abs(rr - r);
        double ds = Math.abs(rs - s);

        if (dq > dr && dq > ds) {
            rq = -rr - rs;
        } else if (dr > ds) {
            rr = -rq - rs;
        }

        return new HexPos(rq, rr);
    }
}
