package gui.utils;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.util.Duration;

public final class AnimationManager {

  private static final int ANIMATION_DURATION = 1000;
  private static final int SHAKE_DURATION = 100;
  private static final int SHAKE_CYCLES = 5;

  // ensure non-instantiability
  private AnimationManager() {
    throw new AssertionError();
  }

  /**
   * Method to create a transition animation between views.
   * 
   * @param node the node on which the transition is applied
   * @param eventHandler the event happening after transition
   */
  public static void applyFadeAnimationOn(Node node, EventHandler<ActionEvent> eventHandler) {
    FadeTransition fadeTransition = new FadeTransition(Duration.millis(ANIMATION_DURATION), node);
    fadeTransition.setOnFinished(eventHandler);
    fadeTransition.setFromValue(1);
    fadeTransition.setToValue(0);
    fadeTransition.setAutoReverse(true);
    fadeTransition.play();
  }

  /**
   * Method to create a shake animation for a specified node element.
   * 
   * @param node the target node
   */
  public static void applyShakeAnimationOn(Node node) {
    TranslateTransition transitionLeft =
        new TranslateTransition(Duration.millis(SHAKE_DURATION), node);
    transitionLeft.setFromX(0);
    transitionLeft.setToX(-5);
    transitionLeft.setInterpolator(Interpolator.LINEAR);

    TranslateTransition transitionRight =
        new TranslateTransition(Duration.millis(SHAKE_DURATION), node);
    transitionRight.setFromX(-5);
    transitionRight.setToX(5);
    transitionRight.setDelay(Duration.millis(SHAKE_DURATION));
    transitionRight.setInterpolator(Interpolator.LINEAR);
    transitionRight.setCycleCount(SHAKE_CYCLES);

    TranslateTransition transitionCenter =
        new TranslateTransition(Duration.millis(SHAKE_DURATION), node);
    transitionCenter.setToX(0);
    transitionCenter.setDelay(Duration.millis((SHAKE_CYCLES + 1) * SHAKE_DURATION));
    transitionCenter.setInterpolator(Interpolator.LINEAR);

    transitionLeft.play();
    transitionRight.play();
    transitionCenter.play();
  }

  /**
   * Method to Create a falling node element.
   * 
   * @param node the target node
   */
  public static void matrixAnimation(Node node) {
    TranslateTransition transition =
        new TranslateTransition(Duration.millis(5000 + 3000 * Math.random()), node);
    transition.setFromY(-200);
    transition.setToY(1200);
    transition.setInterpolator(Interpolator.LINEAR);
    transition.setCycleCount(Animation.INDEFINITE);
    transition.play();
  }
}
