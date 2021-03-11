package gui.scenes.menu;

import gui.AbstractController;
import gui.utils.AnimationManager;
import gui.utils.SceneLoader;
import gui.utils.SoundManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;

public class MenuController extends AbstractController implements Initializable {

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    SoundManager.playBackgroundMusic();
    SceneLoader.loadAnimationSceneOnVBox(pane);
  }

  /**
   * Event handler for clicking the create game icon.
   *
   * @param event ActionEvent.
   */
  @FXML
  private void handleCreateGameButton(ActionEvent event) {
    SoundManager.playSoundEffect(SoundManager.BUTTON_PATH);
    AnimationManager.applyFadeAnimationOn(pane, (eventHandler -> {
      loadLoginHostScene();
    }));
  }

  /**
   * Event handler for clicking the join game icon.
   *
   * @param event ActionEvent.
   */
  @FXML
  private void handleJoinGameButton(ActionEvent event) {
    SoundManager.playSoundEffect(SoundManager.BUTTON_PATH);
    AnimationManager.applyFadeAnimationOn(pane, (eventHandler -> {
      loadLoginClientScene();
    }));
  }

  /**
   * Event handler for clicking the settings icon. Clicking the button results in opening a settings
   * page.
   *
   * @param event ActionEvent.
   */
  // TODO! @param linked with actual button.
  @FXML
  private void handleRulesButton(ActionEvent event) {
    SoundManager.playSoundEffect(SoundManager.BUTTON_PATH);
    AnimationManager.applyFadeAnimationOn(pane, (eventHandler -> {
      loadRulesScene();
    }));
  }

  /**
   * Loads settings controller.
   *
   * @param event ActionEvent.
   */
  @FXML
  private void handleSettingsButton(ActionEvent event) {
    SoundManager.playSoundEffect(SoundManager.BUTTON_PATH);
    AnimationManager.applyFadeAnimationOn(pane, (eventHandler -> {
      loadSettingsScene();
    }));
  }

}
