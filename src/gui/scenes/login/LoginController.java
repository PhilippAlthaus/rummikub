package gui.scenes.login;

import gui.AbstractController;
import gui.utils.AnimationManager;
import gui.utils.SoundManager;
import java.util.Collections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import model.Player;
import model.RummikubPlayer;

/**
 * Controller for login scene.
 */
public abstract class LoginController extends AbstractController implements Initializable {

  @FXML
  protected TextField nameTextField;

  @FXML
  protected TextField ageTextField;

  /**
   * Return {@code true} if the passed String {@code playerName} is valid.
   *
   * @param playerName is the name of the player
   * @return weather the passed String {@code playerName} is valid
   */
  protected static boolean isNameValid(String playerName) {
    // check if the entered name doesn't contain special characters
    String nameRegex = "^[a-zA-Z0-9]+";
    return playerName.matches(nameRegex);
  }

  /**
   * Return {@code true} if the passed String {@code playerAge} is valid.
   *
   * @param playerAge is the age of the player
   * @return weather the passed String {@code playerAge} is valid
   */
  protected static boolean isAgeValid(String playerAge) {
    // check if the entered age is an Integer and positive
    int age;
    try {
      age = Integer.parseInt(playerAge);
    } catch (NumberFormatException e) {
      return false;
    }
    return age > 0;
  }

  /**
   * Returns {@code true} if the given IP address is a correctly written (not necessarily valid) IP
   * address.
   *
   * @param ipAddress is the IP address to be checked
   */
  public static boolean isIpValid(String ipAddress) {

    try {
      if (ipAddress == null || ipAddress.isEmpty()) {
        return false;
      }
      String[] octets = ipAddress.split("\\.");
      if (octets.length != 4) {
        return false;
      }
      for (String octet : octets) {
        int i = Integer.parseInt(octet);
        if ((i < 0) || (i > 255)) {
          return false;
        }
      }
      if (ipAddress.endsWith(".")) {
        return false;
      }
      return true;
    } catch (NumberFormatException nfe) {
      return false;
    }
  }

  /**
   * Sets the constant checking of the text boxes for correctness.
   *
   * @param textField is the {@code TextField} instance to be checked
   */
  protected static void setUpValidation(TextField textField) {
    textField.textProperty().addListener((observable, oldValue, newValue) -> validate(textField));
    validate(textField);
  }

  /**
   * Check the passed {@code TextField} instance, depending on the type of the field, to see if it
   * is correct.
   *
   * @param textField is the {@code TextField} instance to be checked
   */
  protected static void validate(TextField textField) {
    ObservableList<String> styleClass = textField.getStyleClass();
    String text = textField.getText();

    // Validation depends on the type of the text field
    boolean isValid = false;
    switch (textField.getId()) {
      case "nameTextField":
        isValid = isNameValid(text);
        break;
      case "ageTextField":
        isValid = isAgeValid(text);
        break;
      case "ipTextField":
        isValid = isIpValid(text);
        break;
      case "ipLocalTextField":
        isValid = isIpValid(text);
        break;
      default:
        // Unreachable, all possible text fields are checked.
        throw new AssertionError();
    }

    if (textField.getText().trim().isEmpty() || !isValid) {
      // If the input is not valid the text field gets a red border
      if (!styleClass.contains("error")) {
        styleClass.removeAll(Collections.singleton("noerror"));
        styleClass.add("error");
      }
      // If the input is valid the text field is displayed normal
    } else {
      styleClass.removeAll(Collections.singleton("error"));
      styleClass.add("noerror");
    }
  }

  /**
   * Returns {@code Player} instance based on the name and age text field.
   *
   * @param name is the players name
   * @param ageAsText is the players age
   * @return {@code Player} instance which is new created
   */
  protected static Player createPlayerFromInput(String name, String ageAsText) {
    int age = Integer.parseInt(ageAsText);
    return RummikubPlayer.of(name, age);
  }

  /**
   * Executed by pressing the back button. Loads the menu scene.
   */
  @FXML
  private void handleBackButtonClick() {
    SoundManager.playSoundEffect(SoundManager.BUTTON_PATH);
    AnimationManager.applyFadeAnimationOn(super.pane, (eventHandler -> {
      loadMenuScene();
    }));
  }

}
