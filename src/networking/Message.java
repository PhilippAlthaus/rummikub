package networking;

import java.io.Serializable;
import model.Player;
import model.Rummikub;

/**
 * A message is the object that gets sent from server to the client and the other way.
 */
class Message implements Serializable {

  private static final long serialVersionUID = -4409722445222139025L;

  /**
   * Different commands to differentiate the receiving messages.
   */
  enum Command {
    JOINGAME, UPDATEGAME, STARTGAME, BACKUP, LEAVEGAME, TERMINATE;
  }

  /**
   * Used for addPlayer and removePlayer at server side.
   */
  private Player player;

  /**
   * Game that gets send.
   */
  private Rummikub game;

  /**
   * Is needed for connecting the server to the clientListener.
   */
  private String ipAddress;

  /**
   * Port where clientListener is listening for messages from server.
   */
  private int port;

  /**
   * The command that indicates the operation.
   */
  private Command command;

  /**
   * A normal message does not contain a ipAddress and a port of the clientListener.
   * 
   * @param player who sends the message
   * @param game to be sent
   * @param command that shows the reason of the message
   */
  Message(Player player, Rummikub game, Command command) {
    this(player, game, command, null, 0);
  }

  /**
   * Special message used when the client connects the first time to the Server.
   * 
   * @param player who sends the message
   * @param game game to be sent
   * @param command command that shows the reason of the message
   * @param ipAddress of the clientListener
   * @param port of the clientListener
   */
  Message(Player player, Rummikub game, Command command, String ipAddress, int port) {
    this.game = game;
    this.player = player;
    this.command = command;
    this.ipAddress = ipAddress;
    this.port = port;
  }

  /**
   * Returns the player contained in this {@code Message}.
   */
  Player getPlayer() {
    return player;
  }

  /**
   * Returns the game contained in this {@code Message}.
   */
  Rummikub getGame() {
    return game;
  }

  /**
   * Returns the command contained in this {@code Message}.
   */
  Command getCommand() {
    return command;
  }

  /**
   * Returns the IPv4-Address contained in this {@code Message}.
   */
  String getIpAddress() {
    return ipAddress;
  }

  /**
   * Returns the port contained in this {@code Message}.
   */
  int getPort() {
    return port;
  }

  @Override
  public String toString() {
    return (command + ":" + player + ":" + game);
  }

}
