package testing;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import model.Color;
import model.RummikubTile;
import model.Tile;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RummikubTileTest {

  @Test
  @DisplayName("Test tile creation.")
  void testCreateTile() {
    Tile t = RummikubTile.createTile(Color.BLACK, 1);
    assertNotNull(t);
  }

  @Test
  @DisplayName("Test joker creation.")
  void testCreateJoker() {
    Tile t = RummikubTile.createJoker();
    assertNotNull(t);
  }

  @Test
  @DisplayName(value = "Tests if the method getColor() if run on a tile, returns the right color.")
  void testGetColorOnTile() {
    Tile tile = RummikubTile.createTile(Color.BLACK, 5);
    assertTrue(tile.getColor() == Color.BLACK);
  }

  @Test
  @DisplayName(value = "Getting color for joker.")
  void testGetColorOnJoker() {
    Tile joker = RummikubTile.createJoker();
    assertThrows(IllegalArgumentException.class, () -> joker.getColor());
  }

  @Test
  @DisplayName(value = "Tests if the method getValue() if run on a tile, returns the right value.")
  void testGetValueOnTile() {
    Tile tile = RummikubTile.createTile(Color.BLACK, 5);
    assert (tile.getValue() == 5);
  }

  @Test
  @DisplayName(value = "Tests if the method isJoker() if run on a tile, returns false.")
  void testIsJokerOnTile() {
    Tile tile = RummikubTile.createTile(Color.BLACK, 5);
    assertFalse(tile.isJoker());
  }

  @Test
  @DisplayName(value = "Tests if the method isJoker() if run on a joker, return true.")
  void testIsJokerOnJoker() {
    Tile joker = RummikubTile.createJoker();
    assertTrue(joker.isJoker());
  }

}
