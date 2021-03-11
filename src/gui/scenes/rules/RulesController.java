package gui.scenes.rules;

import gui.AbstractController;
import gui.utils.AnimationManager;
import gui.utils.SoundManager;

import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Screen;

/**
 * Controller for rules scene.
 *
 */
public class RulesController extends AbstractController implements Initializable {

  @FXML
  private Button backButton;

  @FXML
  private Button nextButton;

  @FXML
  private ImageView imageLeft;

  @FXML
  private ImageView imageRight;

  /**
   * Contains all rules images.
   */
  private LinkedList<Image> imagesList;

  /**
   * {@code Iterator} instance to iterate through the {@code imagesList} list.
   */
  private Iterator<Image> iterator;

  /**
   * Constructor of the Controller, fills the image list with the rules.
   */
  public RulesController() {
    // to set the images in the right place for screens of various resolutions
    Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
    double width = primaryScreenBounds.getWidth() / 2 - 50;
    double height = primaryScreenBounds.getHeight() - 100;

    Image side1 =
        new Image("/resources/images/Rummikub-English-Manual-1.png", width, height, true, true);
    Image side2 =
        new Image("/resources/images/Rummikub-English-Manual-2.png", width, height, true, true);
    Image side3 =
        new Image("/resources/images/Rummikub-English-Manual-3.png", width, height, true, true);
    Image side4 =
        new Image("/resources/images/Rummikub-English-Manual-4.png", width, height, true, true);

    this.imagesList = new LinkedList<>();
    Collections.addAll(this.imagesList, side1, side2, side3, side4);
    this.iterator = this.imagesList.iterator();

  }

  @Override
  public void initialize(URL arg0, ResourceBundle arg1) {
    imageLeft.setImage(iterator.next());
    imageRight.setImage(iterator.next());
  }

  /**
   * Event handler for clicking the back button. Clicking the button results in switching to the
   * menu screen.
   *
   */
  @FXML
  private void handleBackButton(ActionEvent event) {
    SoundManager.playSoundEffect(SoundManager.BUTTON_PATH);
    AnimationManager.applyFadeAnimationOn(super.pane, (eventHandler -> {
      loadMenuScene();
    }));
  }

  /**
   * Event handler for clicking the next/previous button. Clicking the button results in switching
   * to the next or previous rules page.
   *
   */
  @FXML
  void handleNextButton(ActionEvent event) {
    SoundManager.playSoundEffect(SoundManager.BUTTON_PATH);
    if (!iterator.hasNext()) {

      iterator = imagesList.listIterator(0);
      nextButton.setText("Next");

    } else {
      nextButton.setText("Previous");
    }

    imageLeft.setImage(iterator.next());
    imageRight.setImage(iterator.next());
  }

}
