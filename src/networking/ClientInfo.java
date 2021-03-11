package networking;

import model.Player;

/**
 * Contains the information of a client needed in the server.
 */
class ClientInfo {

  /**
   * Indicates whether this client is also the host i.e., it holds the server role.
   */
  private boolean isHost;

  /**
   * The IPv4-Address of the client.
   */
  private String ipAddress;

  /**
   * The port used by the client.
   */
  private int port;

  /**
   * The player connected via the client.
   */
  private Player player;

  /**
   * Initializes a new {@code ClientInfo} with the specified parameters.
   */
  ClientInfo(boolean isHost, String ipAddress, int port, Player player) {
    this.isHost = isHost;
    this.ipAddress = ipAddress;
    this.port = port;
    this.player = player;
  }

  /**
   * Returns {@code true} if and only if the client is the host.
   */
  boolean isHost() {
    return isHost;
  }

  /**
   * Returns the IP-Address of a ClientListener.
   */
  String getIpAddress() {
    return ipAddress;
  }

  /**
   * Returns the port of a CLientListener.
   */
  int getPort() {
    return port;
  }

  /**
   * Returns the player.
   */
  Player getPlayer() {
    return player;
  }

}
