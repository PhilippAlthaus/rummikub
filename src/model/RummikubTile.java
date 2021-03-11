package model;

import java.io.Serializable;

/**
 * This class represents all tiles including jokers for the game Rummikub. Regular tiles can only
 * have values between 1 and 13.
 */
public class RummikubTile implements Serializable, Tile {

  private static final long serialVersionUID = -7432276029287581575L;

  /**
   * Represents the initial joker value. After being set the joker's value is the value it
   * represents in the sequence it is part of.
   */
  private static final int INITIAL_JOKER_VALUE = 0;

  /**
   * The lowest possible value a tile can have.
   */
  static final int LOWEST_VALUE = 1;

  /**
   * The highest possible value a tile can have.
   */
  static final int HIGHEST_VALUE = 13;

  /**
   * Flag to mark if the tile is a joker. {@code true} indicates that this tile is a joker.
   */
  private final boolean isJoker;

  /**
   * The tile's color.
   */
  private final Color color;

  /**
   * The tile's value. Must be greater than 0 and less than 14.
   */
  private int value;

  /**
   * Flag to mark if the tile is part of a correct sequence. {@code false} indicates that this tile
   * is at a wrong position on the board.
   */
  private boolean isInCorrectSequence;

  /**
   * Initializes a new {@code Tile} with the specified values.
   * 
   * @param color the color of the tile
   * @param value the value of the tile
   * @param joker indicates whether the tile is a joker or not
   */
  private RummikubTile(Color color, int value, boolean joker) {
    this.color = color;
    this.value = value;
    this.isJoker = joker;
    this.isInCorrectSequence = true;
  }

  /**
   * Returns a tile with the specified values. The value must be greater than 0 and less than 14
   * i.e., {@code value >= 1 && value <= 13}.
   * 
   * @param color the color of the tile
   * @param value the value of the tile
   * @return the newly created tile
   * @throws IllegalArgumentException if the specified value is less than 1 or greater than 13
   */
  public static RummikubTile createTile(Color color, int value) {
    if (value < LOWEST_VALUE || value > HIGHEST_VALUE) {
      throw new IllegalArgumentException();
    }
    return new RummikubTile(color, value, false);
  }

  /**
   * Returns a joker. Note that the joker has no color so {@link #getColor()} will fail with an
   * {@code IllegalArgumentException}.
   * 
   * @return the newly created joker
   */
  public static RummikubTile createJoker() {
    return new RummikubTile(null, INITIAL_JOKER_VALUE, true);
  }

  @Override
  public Color getColor() {
    // because the joker has no color
    if (this.isJoker) {
      throw new IllegalArgumentException();
    }
    return this.color;
  }

  @Override
  public int getValue() {
    return value;
  }

  @Override
  public boolean isJoker() {
    return this.isJoker;
  }

  /**
   * Sets the value of a joker to the value it is supposed to be in a sequence. For example in [Red
   * 3, Red 4, Joker] the joker's value is 5 in order to complete the run it is part of.
   * 
   * @throws IllegalArgumentException if this tile was not a joker i.e., {@code isJoker() == false}
   */
  void setJokerValue(int jokerValue) {
    if (!isJoker) {
      throw new IllegalArgumentException();
    }
    value = jokerValue;
  }

  @Override
  public boolean isInCorrectSequence() {
    return this.isInCorrectSequence;
  }

  /**
   * Marks this tile as false, this indicates it is part of an incorrect sequence.
   */
  void markAsFalse() {
    isInCorrectSequence = false;
  }

  /**
   * Removes the marking of this tile, that means after calling this method
   * {@link #isInCorrectSequence()} will return {@code true} after any invocation of this method.
   */
  void removeMarking() {
    isInCorrectSequence = true;
  }

  /**
   * Returns a copy of the tile with the specified values.
   * 
   * @param other the tile to be copied
   * @return the copy of the original tile
   */
  static RummikubTile copyOf(RummikubTile other) {
    if (!other.isJoker) {
      return createTile(other.getColor(), other.getValue());
    } else {
      return createJoker();
    }
  }

  @Override
  public String toString() {
    if (isJoker) {
      return "Joker";
    } else {
      return color + " " + value;
    }
  }

}
