package gui.scenes.game;

import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;

/**
 * TileView is child class of ImageView that contains methods for highlighting itself.
 */
class TileView extends ImageView {

  TileView(Image image) {
    super(image);
  }

  /**
   * Highlights TileView with yellow color.
   */
  void highlightAsSelected() {
    highlight(Color.YELLOW);
  }

  /**
   * Highlights TileView with red color.
   */
  void highlightAsFalseSequence() {
    highlight(Color.RED);
  }

  /**
   * Sets effect on TileView.
   *
   * @param color to be set.
   */
  private void highlight(Color color) {
    Lighting lighting = new Lighting();
    lighting.setDiffuseConstant(1.0);
    lighting.setSpecularConstant(0.0);
    lighting.setSpecularExponent(0.0);
    lighting.setSurfaceScale(0.0);
    lighting.setLight(new Light.Distant(45, 45, color));
    this.setEffect(lighting);
  }

  /**
   * Removes effect from TileView.
   */
  void unhighlight() {
    this.setEffect(null);
  }
}
