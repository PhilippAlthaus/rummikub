package gui.utils;

import java.util.concurrent.atomic.AtomicInteger;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.util.Duration;

public class RummikubTimer {

  private static final int FREQUENCY = 1;
  private Timeline timeline;
  private AtomicInteger seconds = new AtomicInteger(0);

  /**
   * Starts timer which calls event every second.
   *
   * @param event to be executed every second.
   */
  public void start(EventHandler<ActionEvent> event) {
    timeline = new Timeline(new KeyFrame(Duration.seconds(FREQUENCY), event));
    timeline.setCycleCount(Timeline.INDEFINITE);
    timeline.play();
  }

  /**
   * Stops timer.
   */
  public void stop() {
    timeline.stop();
  }

  /**
   * Resets timer.
   */
  public void reset() {
    seconds.set(0);
  }

  /**
   * Returns time in string format.
   *
   * @return string time value.
   */
  public String getTime() {
    int minutes = (this.seconds.getAndIncrement() % 3600) / 60;
    int seconds = this.seconds.get() % 60;
    return String.format("%02d:%02d", minutes, seconds);
  }
}
