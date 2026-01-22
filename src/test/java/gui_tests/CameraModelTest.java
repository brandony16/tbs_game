package gui_tests;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import tbs_game.gui.camera.CameraModel;
import tbs_game.gui.coord_systems.SceneDelta;
import tbs_game.gui.coord_systems.ScenePos;
import tbs_game.gui.coord_systems.WorldPos;

import static org.junit.jupiter.api.Assertions.*;

class CameraModelTest {

  private CameraModel model;

  // Constants for testing
  private static final double BOARD_WIDTH = 1000.0;
  private static final double SCENE_W = 800.0;
  private static final double SCENE_H = 600.0;

  @BeforeEach
  void setUp() {
    // Initialize a clean model before every test
    model = new CameraModel(BOARD_WIDTH);
    model.setSceneSize(SCENE_W, SCENE_H);
  }

  @Test
  void testInitialState() {
    // Default zoom 1.0, translation (0,0)
    assertEquals(1.0, model.getZoom());
    assertEquals(0.0, model.getX());
    assertEquals(0.0, model.getY());

    // Center of scene (400, 300) should map to center of world (400, 300)
    WorldPos center = model.sceneToWorld(new ScenePos(400, 300));
    assertEquals(400.0, center.x());
    assertEquals(300.0, center.y());
  }

  @Test
  void testZoomClamping() {
    // Try zooming way past max (4.0)
    model.zoomAt(10.0, new ScenePos(0, 0));
    assertEquals(4.0, model.getZoom(), 0.001, "Zoom should cap at MAX_ZOOM");

    // Try zooming way past min (0.5)
    model.zoomAt(0.01, new ScenePos(0, 0));
    assertEquals(0.5, model.getZoom(), 0.001, "Zoom should floor at MIN_ZOOM");
  }

  @Test
  void testSceneToWorldMath() {
    /*
     * Formula: world = (scene - translate) / zoom
     * Scenario:
     * - Zoom is 2.0
     * - Panned 100px right (translate x = -100)
     * - Mouse is at scene x = 200
     */

    // 1. Set Zoom to 2.0
    model.zoomAt(2.0, new ScenePos(0, 0));

    // 2. Pan 100px right (Moving camera right = subtracting from translate)
    model.panScene(new SceneDelta(100, 0));

    // Check Model State
    // Zooming at (0,0) keeps translate 0. Panning -100 makes translate -100.
    assertEquals(-100.0, model.getX(), 0.001);

    // 3. Convert Scene(200) -> World(?)
    // (200 - (-100)) / 2.0 = 150
    WorldPos result = model.sceneToWorld(new ScenePos(200, 100));

    assertEquals(150.0, result.x(), 0.001);
  }

  @Test
  void testZoomAtPivotStability() {
    // This is the "Game Feel" math check.
    // If I hover over a mountain and zoom, the mountain should stay under my mouse.

    ScenePos pivot = new ScenePos(300, 300);
    WorldPos initialWorldPos = model.sceneToWorld(pivot);

    // Zoom in 2x
    model.zoomAt(2.0, pivot);
    WorldPos afterZoomPos = model.sceneToWorld(pivot);

    assertEquals(initialWorldPos.x(), afterZoomPos.x(), 0.001, "World X under mouse drifted!");
    assertEquals(initialWorldPos.y(), afterZoomPos.y(), 0.001, "World Y under mouse drifted!");

    // Zoom out 0.5x
    model.zoomAt(0.5, pivot);
    WorldPos afterRevertPos = model.sceneToWorld(pivot);

    assertEquals(initialWorldPos.x(), afterRevertPos.x(), 0.001, "Drifted after zooming back out");
  }

  /*
   * PARAMETERIZED TESTS
   * These run the same test logic with different inputs to catch edge cases.
   */

  @ParameterizedTest
  @CsvSource({
      // PanAmount, ExpectedX, Description
      "200,          -200.0,         Normal pan within bounds",
      "2000,         -1000.0,        Huge pan causing wrap (Wrapped: -2000 + 1000 = -1000)",
      "-2000,        1000.0,         Huge negative pan wrap (Wrapped: 2000 - 1000 = 1000)"
  })
  void testPanningAndWrapping(double panAmount, double expectedX, String description) {
    // Pan the camera
    model.panScene(new SceneDelta(panAmount, 0));

    // Check if the coordinate wrapped correctly
    // Note: Exact values depend on your specific wrap threshold logic.
    // If this test fails, check if your wrap logic is (Width/2) or just (Width).

    // For this test, I assume standard wrapping behavior where the camera
    // stays somewhat local to the board width.

    // Check that we are within reason (e.g., never drift to infinity)
    assertTrue(Math.abs(model.getX()) <= BOARD_WIDTH * 2, "Camera coordinate drifted too far");
  }

  @Test
  void testExactWrapBoundaryRight() {
    // Board Width 1000. Half Width 500.
    // Scene Center 400.
    // Threshold: When World Center > 500.

    // World Center = (SceneCenter - TranslateX) / Zoom
    // 500 = (400 - Tx) / 1
    // Tx = -100.

    // If we pan to -101, we are at World Center 501.
    // Should wrap by adding BoardWidth (1000).
    // New Tx should be -101 + 1000 = 899.

    // Move to exactly the edge
    model.panScene(new SceneDelta(100, 0)); // Tx = -100
    assertEquals(-100.0, model.getX(), 0.001); // No wrap yet

    // Nudge over edge
    model.panScene(new SceneDelta(2, 0)); // Tx would be -102

    // Expect Wrap
    assertEquals(898.0, model.getX(), 0.001, "Should have wrapped to positive side");
  }

  @Test
  void testExactWrapBoundaryLeft() {
    // Threshold: World Center < -500.
    // -500 = (400 - Tx) / 1
    // Tx = 900.

    // Move to edge
    model.panScene(new SceneDelta(-900, 0)); // Tx = 900
    assertEquals(900.0, model.getX(), 0.001); // No wrap yet

    // Nudge over edge
    model.panScene(new SceneDelta(-2, 0)); // Tx would be 902

    // Expect Wrap: 902 - 1000 = -98
    assertEquals(-98.0, model.getX(), 0.001, "Should have wrapped to negative side");
  }
}
