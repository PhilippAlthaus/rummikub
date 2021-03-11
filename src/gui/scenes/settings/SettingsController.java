package gui.scenes.settings;

import gui.AbstractController;
import gui.utils.AnimationManager;
import gui.utils.SoundManager;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;

public class SettingsController extends AbstractController implements Initializable {

  @FXML
  private Slider musicSlider;
  @FXML
  private Slider effectsSlider;

  @Override
  public void initialize(URL location, ResourceBundle resources) {
    musicSlider.valueProperty().bindBidirectional(SoundManager.backgroundMusicVolume());
    effectsSlider.valueProperty().bindBidirectional(SoundManager.soundEffectVolume());
  }

  @FXML
  private void handleBackClick() {
    SoundManager.playSoundEffect(SoundManager.BUTTON_PATH);
    musicSlider.valueProperty().unbind();
    effectsSlider.valueProperty().unbind();
    AnimationManager.applyFadeAnimationOn(pane, event -> loadMenuScene());
  }
}
