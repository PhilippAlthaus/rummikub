package model;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/**
 * This class represents a player for the game Rummikub. A player can set tiles from his own rack,
 * pull a tile from the pool or sort his rack. This class is also responsible for validating a
 * player's round from the player's perspective (it does not validate the entire game).
 */
public class RummikubPlayer implements Player, Serializable {

  private static final long serialVersionUID = 5844355563697027785L;

  /**
   * Minimal sum of all tile values set by the player to successfully make his first move.
   */
  private static final int MIN_SUM_VALUE_FOR_FIRST_MOVE = 30;

  /**
   * The minus points for a joker on the players rack.
   */
  private static final int JOKER_MINUS_POINTS = 30;

  /**
   * The tiles the player currently has on his rack.
   */
  private final List<Tile> tilesOnRack;

  /**
   * The player's name, must be non-{@code null}.
   */
  private final String name;

  /**
   * The players age, must be positive.
   */
  private final int age;

  /**
   * Unique identifier for this player in order to compare players for equality even if their name
   * and age are equal.
   */
  private UUID id;

  /**
   * State of the player i.e., if the player successfully made the first move.
   */
  private boolean isFirstMoveDone;

  /**
   * Indicates whether the player pulled a tile from the pool.
   */
  private boolean hasPulledFromPool;

  /**
   * The tiles this player set to the board during one round.
   */
  private Set<Tile> setTiles;

  /**
   * Initializes a new {@code RummikubPlayer} with the specified values.
   * 
   * @param name the name of this player
   * @param age non-negative age of this player
   */
  private RummikubPlayer(String name, int age) {
    this.tilesOnRack = new LinkedList<>();
    this.name = name;
    this.age = age;
    this.id = UUID.randomUUID();
    this.isFirstMoveDone = false;
    this.setTiles = new HashSet<>();
  }

  /**
   * Returns a new {@code RummikubPlayer} with the specified values.
   * 
   * @param name the name of this player, must be non-{@code null}
   * @param age non-negative age of this player
   * 
   * @return the newly created {@code RummikubPlayer}
   * @throws IllegalArgumentException if {@code age} is negative i.e., {@code (age < 0) == true} or
   *         {@code name == null}
   */
  public static RummikubPlayer of(String name, int age) {
    if (age < 0 || name == null) {
      throw new IllegalArgumentException();
    }
    return new RummikubPlayer(name, age);
  }

  @Override
  public List<Tile> getRack() {
    return tilesOnRack;
  }

  @Override
  public Tile getTileFromRack(int index) {
    setTiles.add(tilesOnRack.get(index));
    return tilesOnRack.remove(index);
  }

  /**
   * Adds a tile to this player's rack. This method should only be used during the game's start to
   * hand out tiles to each player.
   * 
   * @param t the tile to be added
   */
  void pullTileAtStart(Tile t) {
    tilesOnRack.add(t);
  }

  @Override
  public void pullTileFromPool(Tile t) {
    tilesOnRack.add(t);
    hasPulledFromPool = true;
  }

  @Override
  public void sortRack() {
    Collections.sort(tilesOnRack, (tile1, tile2) -> {
      // joker is the highest possible value
      if (tile1.isJoker() && !tile2.isJoker()) {
        return 1;
      } else if (!tile1.isJoker() && tile2.isJoker()) {
        return -1;
      } else {
        return tile1.getValue() - tile2.getValue();
      }
    });
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public int getAge() {
    return age;
  }

  @Override
  public void reset() {
    tilesOnRack.clear();
    setTiles.clear();
    isFirstMoveDone = false;
    hasPulledFromPool = false;
  }

  @Override
  public int getMinusPoints() {
    int sum = 0;
    for (Tile tile : tilesOnRack) {
      if (tile.isJoker()) {
        sum += JOKER_MINUS_POINTS;
      } else {
        sum += tile.getValue();
      }
    }
    return -sum;
  }

  /**
   * Validates the round of this player. A player's round is valid if the player pulled a tile or
   * set at least one tile to the board. If it is his first move the values of the tiles set to the
   * board must add up to at least 30.
   * 
   * <p>Note that this method does not validate the entire game, it only checks if the player made
   * at least one move no matter if the state of the entire game is valid or invalid.
   * 
   * <p>Note also that this method clears the tiles that this player set to the board so
   * {@link #getSetTiles()} will return an empty {@code Set}.
   * 
   * @return {@code true} if the round was valid from this player's perspective
   */
  boolean validateRound() {
    int valueSum = 0;
    for (Tile t : setTiles) {
      valueSum += t.getValue();
    }
    setTiles = new HashSet<>();
    if (hasPulledFromPool) {
      hasPulledFromPool = false;
      return true;
    } else if (!isFirstMoveDone) {
      isFirstMoveDone = valueSum >= MIN_SUM_VALUE_FOR_FIRST_MOVE;
      return isFirstMoveDone;
    } else {
      return valueSum > 0;
    }
  }

  /**
   * Returns {@code true} if this player successfully made his first move, otherwise {@code false}.
   */
  boolean isFirstMoveDone() {
    return isFirstMoveDone;
  }

  /**
   * Returns the tiles this player set to the board during his round.
   */
  Set<Tile> getSetTiles() {
    return setTiles;
  }

  /**
   * Returns a copy of the player with the specified values.
   * 
   * @param other the player to be copied
   * @return the copy of the original player
   */
  public static RummikubPlayer copyOf(RummikubPlayer other) {
    RummikubPlayer copy = of(other.name, other.age);
    copy.isFirstMoveDone = other.isFirstMoveDone;
    for (Tile tile : other.tilesOnRack) {
      copy.tilesOnRack.add(RummikubTile.copyOf((RummikubTile) tile));
    }
    for (Tile tile : other.setTiles) {
      copy.setTiles.add(RummikubTile.copyOf((RummikubTile) tile));
    }
    return copy;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + age;
    result = prime * result + ((id == null) ? 0 : id.hashCode());
    result = prime * result + ((name == null) ? 0 : name.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (!(obj instanceof RummikubPlayer)) {
      return false;
    }
    RummikubPlayer other = (RummikubPlayer) obj;
    if (age != other.age) {
      return false;
    }
    if (id == null) {
      if (other.id != null) {
        return false;
      }
    } else if (!id.equals(other.id)) {
      return false;
    }
    if (name == null) {
      if (other.name != null) {
        return false;
      }
    } else if (!name.equals(other.name)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return name + " (" + age + ")" + "\n" + tilesOnRack;
  }

}
