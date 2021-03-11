package gui;

import gui.utils.AnimationManager;
import gui.utils.SceneLoader;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public abstract class AbstractController {

  /**
   * Contains view elements.
   */
  @FXML
  protected VBox pane;

  /**
   * Event handler for clicking the exit icon. Clicking the button results in shutting down the
   * game.
   *
   */
  @FXML
  protected void handleExitButton(ActionEvent event) {
    AnimationManager.applyFadeAnimationOn(pane, (eventHandler -> {
      Platform.exit();
    }));
  }

  /**
   * To switch to login host scene.
   */
  protected void loadLoginHostScene() {
    Scene loginScene = SceneLoader.getSceneForPath(SceneLoader.LOGIN_HOST_VIEW_PATH);
    Stage menuStage = (Stage) pane.getScene().getWindow();
    menuStage.setScene(loginScene);
  }

  /**
   * To switch to login client scene.
   */
  protected void loadLoginClientScene() {
    Scene loginScene = SceneLoader.getSceneForPath(SceneLoader.LOGIN_CLIENT_VIEW_PATH);
    Stage menuStage = (Stage) pane.getScene().getWindow();
    menuStage.setScene(loginScene);
  }

  /**
   * To switch to settings scene.
   */
  protected void loadRulesScene() {
    Scene scene = SceneLoader.getSceneForPath(SceneLoader.RULES_VIEW_PATH);
    Stage stage = (Stage) pane.getScene().getWindow();
    stage.setScene(scene);
  }

  /**
   * To switch to lobby scene.
   */
  protected void loadLobbyScene() {
    Scene lobbyScene = SceneLoader.getSceneForPath(SceneLoader.LOBBY_VIEW_PATH);
    Stage loginStage = (Stage) pane.getScene().getWindow();
    loginStage.setScene(lobbyScene);
  }

  /**
   * To switch to menu scene.
   */
  protected void loadMenuScene() {
    loadMenuScene(pane);
  }

  /**
   * To switch to menu scene.
   *
   * @param node current root.
   */
  protected void loadMenuScene(Node node) {
    Scene scene = SceneLoader.getSceneForPath(SceneLoader.MENU_VIEW_PATH);
    Stage stage = (Stage) node.getScene().getWindow();
    stage.setScene(scene);
  }

  /**
   * To switch to game scene.
   */
  protected void loadGameScene() {
    Scene scene = SceneLoader.getSceneForPath(SceneLoader.GAME_VIEW_PATH);
    scene.getRoot().requestFocus();
    Stage stage = (Stage) pane.getScene().getWindow();
    stage.setScene(scene);
  }

  /**
   * To switch to endgame scene.
   */
  protected void loadEndgameScene(Node node) {
    Scene scene = SceneLoader.getSceneForPath(SceneLoader.ENDGAME_VIEW_PATH);
    Stage stage = (Stage) node.getScene().getWindow();
    stage.setScene(scene);
  }

  /**
   * To switch to settings scene.
   */
  protected void loadSettingsScene() {
    Scene scene = SceneLoader.getSceneForPath(SceneLoader.SETTINGS_VIEW_PATH);
    Stage stage = (Stage) pane.getScene().getWindow();
    stage.setScene(scene);
  }
  
}
