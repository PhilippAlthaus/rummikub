package gui;

import gui.utils.SoundManager;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.stage.StageStyle;

/**
 * Custom Alert with css.
 */
public class RummikubAlert extends Alert {
  
  /**
   * Constructor for customized {@code RummikubAlert} instance.
   * 
   * @param alertType is the type of the alert
   * @param contentText is the content text of the alert
   * @param buttons is the button type of the alert
   */
  public RummikubAlert(AlertType alertType, String contentText,
      ButtonType... buttons) {
    super(alertType, contentText, buttons);
    this.initStyle(StageStyle.UNDECORATED);
    DialogPane dialogPane = this.getDialogPane();
    dialogPane.getStylesheets().add(
        getClass().getResource("/resources/Dialog.css").toExternalForm());
    SoundManager.playSoundEffect(SoundManager.ALERT_PATH);
  }
}
