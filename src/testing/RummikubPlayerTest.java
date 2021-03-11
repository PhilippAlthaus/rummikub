package testing;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import model.Player;
import model.RummikubPlayer;
import model.RummikubTile;
import model.Tile;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RummikubPlayerTest {

  private Player testPlayer;

  @BeforeEach
  void setUp() {
    testPlayer = RummikubPlayer.of("Juergen", 17);
  }

  @DisplayName("Player creation.")
  @Test
  void testOf() {
    assertAll(
        () -> assertNotNull(testPlayer), () -> assertNotNull(testPlayer.getRack()),
        () -> assertTrue(testPlayer.getRack().isEmpty()),
        () -> assertNotNull(testPlayer.getName()),
        () -> assertTrue(testPlayer.getAge() > 0));
  }

  @DisplayName("Player creation for negative age.")
  @Test
  void testOfForNegativeAge() {
    assertThrows(IllegalArgumentException.class, () -> RummikubPlayer.of("Juergen", -1));
  }

  @DisplayName("Player creation for null name.")
  @Test
  void testOfForNullName() {
    assertThrows(IllegalArgumentException.class, () -> RummikubPlayer.of(null, 20));
  }

  @DisplayName("Pulling tiles from pool.")
  @Test
  void testPullTileFromPool() {
    Tile joker = RummikubTile.createJoker();
    testPlayer.pullTileFromPool(joker);
    assertAll(
        () -> assertEquals(1, testPlayer.getRack().size()),
        () -> assertTrue(testPlayer.getRack().contains(joker)));
  }

  @DisplayName("Getting tiles from rack.")
  @Test
  void testGetTileFromRack() {
    Tile joker = RummikubTile.createJoker();
    testPlayer.pullTileFromPool(joker);
    assertAll(
        () -> assertEquals(joker, testPlayer.getTileFromRack(0)),
        () -> assertThrows(IndexOutOfBoundsException.class, () -> testPlayer.getTileFromRack(0)));
  }

  @DisplayName("Player reset.")
  @Test
  void testReset() {
    testPlayer.reset();
    assertTrue(testPlayer.getRack().isEmpty());
  }

}
