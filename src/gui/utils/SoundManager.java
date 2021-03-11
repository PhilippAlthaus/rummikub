package gui.utils;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public final class SoundManager {

  public static final String ELECTRO_LOOP_PATH = "/resources/sounds/electroloop.mp3";
  public static final String FIREWORKS_PATH = "/resources/sounds/firework.wav";
  public static final String PLACE_PATH = "/resources/sounds/place.wav";
  public static final String ROUND_PATH = "/resources/sounds/round.wav";
  public static final String SORT_PATH = "/resources/sounds/sort.wav";
  public static final String TAKE_PATH = "/resources/sounds/take.wav";
  public static final String WAVE_PATH = "/resources/sounds/wave.wav";
  public static final String WIN_PATH = "/resources/sounds/win.mp3";
  // Royalty Free Music from Bensound
  public static final String JAZZ_PATH = "/resources/sounds/bensound-thejazzpiano.mp3";
  // Royalty Free Music from Bensound
  public static final String SUMMER_PATH = "/resources/sounds/bensound-summer.mp3";
  public static final String BUTTON_PATH = "/resources/sounds/button.wav";
  public static final String LOSE_PATH = "/resources/sounds/lose.mp3";
  public static final String ERROR_PATH = "/resources/sounds/error.mp3";
  public static final String ALERT_PATH = "/resources/sounds/alert.mp3";
  private static MediaPlayer musicPlayer = null;
  private static MediaPlayer soundEffectPlayer = null;
  private static MediaPlayer fireworksMediaPlayer = null;
  private static DoubleProperty soundEffectVolume = new SimpleDoubleProperty(0.2);
  private static DoubleProperty backgroundMusicVolume = new SimpleDoubleProperty(0.1);

  // ensure non-instantiability
  private SoundManager() {
    throw new AssertionError();
  }

  private static MediaPlayer initMediaPlayer(String pathOf, int times, DoubleProperty volume) {
    String path = SoundManager.class.getResource(pathOf).toString();
    Media effect = new Media(path);
    MediaPlayer mediaPlayer = new MediaPlayer(effect);
    mediaPlayer.setStartTime(Duration.seconds(0));
    mediaPlayer.setStopTime(Duration.seconds(effect.getDuration().toSeconds()));
    mediaPlayer.setCycleCount(times);
    mediaPlayer.volumeProperty().bind(volume);

    return mediaPlayer;
  }

  /**
   * Method to Play a background song infinite times.
   */
  public static void playBackgroundMusic() {
    if (musicPlayer != null) {
      musicPlayer.play();
      return;
    }
    musicPlayer = initMediaPlayer(SUMMER_PATH, MediaPlayer.INDEFINITE, backgroundMusicVolume());
    musicPlayer.play();
  }

  /**
   * Method to stop the background song.
   */
  public static void stopBackgroundMusic() {
    musicPlayer.stop();
  }

  /**
   * Method to play a sound effect.
   *
   * @param pathOf the path to the effect file
   */
  public static void playSoundEffect(String pathOf) {
    playSoundEffect(pathOf, 1);
  }

  /**
   * Method to play a sound effect multiple times.
   *
   * @param pathOf the path to the effect file
   * @param times number of times the effect should be played
   */
  public static void playSoundEffect(String pathOf, int times) {
    soundEffectPlayer = initMediaPlayer(pathOf, times, soundEffectVolume);
    soundEffectPlayer.play();
  }

  /**
   * Plays fireworks sound.
   */
  public static void playFireworksSound() {
    fireworksMediaPlayer =
        initMediaPlayer(FIREWORKS_PATH, MediaPlayer.INDEFINITE, soundEffectVolume());
    fireworksMediaPlayer.play();
  }

  /**
   * Stops playing fireworks sound.
   */
  public static void stopFireworksSound() {
    fireworksMediaPlayer.stop();
    fireworksMediaPlayer = null;
  }

  /**
   * Returns background music volume property.
   *
   * @return volume property.
   */
  public static DoubleProperty backgroundMusicVolume() {
    return backgroundMusicVolume;
  }

  /**
   * Returns sound effect volume property.
   *
   * @return volume property.
   */
  public static DoubleProperty soundEffectVolume() {
    return soundEffectVolume;
  }
}
