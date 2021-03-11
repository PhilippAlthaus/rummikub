package networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

import model.Player;
import model.Rummikub;
import model.RummikubGame;
import networking.Message.Command;

/**
 * This class is the interface used by the Application for the client-server communication. A client
 * object creates a thread for the server listening for messages from clients. And a thread for a
 * client listener waiting for messages from the server.
 */
public class Client {

  /**
   * Hard coded port where the server is listening for incoming messages.
   */
  private static final int SERVERPORT = 48410;

  /**
   * The time the main thread has to wait after a client is created.
   */
  private static final int WAITING_TIME_IN_MS = 100;

  /**
   * IP-Address of this machine also called localhost.
   */
  private static final String LOCALHOST_IP_ADDRESS = "127.0.0.1";

  /**
   * Lazy instantiated singleton instance.
   */
  private static Client singletonClient = null;

  /**
   * Indicates if the client is the host of the game.
   */
  private boolean isHost;

  /**
   * The client's socket.
   */
  private Socket clientSocket;

  /**
   * The current game.
   */

  private ObjectProperty<Rummikub> currentGame;

  /**
   * The player connected via this client.
   */
  private Player player;

  /**
   * The IP-address of the server this client is connected to.
   */
  private final String ipAddress;

  /**
   * Server object created by the host.
   */
  private Server server;

  /**
   * Server thread created by host.
   */
  private Thread serverThread;

  /**
   * ClientListener object created by the client. Receives messages from server for this client.
   */
  private ClientListener clientListener;

  /**
   * Thread started by the client for listening.
   */
  private Thread clientListenerThread;

  /**
   * OutputStream used for sending messages to the server.
   */
  private ObjectOutputStream output;

  /**
   * Constructor for {@code Client} instance.
   * 
   * @param game non-{@code null} indicates this client is the host
   * @param player is the player associated with the client
   * @param ipAddress is the address of the host
   */
  private Client(Rummikub game, Player player, String ipAddress) {
    this.server = null;
    currentGame = new SimpleObjectProperty<>(game);
    this.player = player;
    this.ipAddress = ipAddress;
  }

  /**
   * Creates or modifies the {@code Client} singleton instance.
   * 
   * @param player is the player associated with the client.
   * @param ipAddress The host IP-Address.
   * @param clientIp The IP-Address of the client.
   * @return the newly created {@code Client} instance.
   * @throws IllegalStateException if the game is full or is started already.
   * @throws UnknownHostException If there is an error at clientListener creation.
   * @throws IOException If there is a connection error.
   */
  public static Client createSingletonClient(Player player, String ipAddress, String clientIp)
      throws UnknownHostException, IOException {
    singletonClient = new Client(null, player, ipAddress);
    singletonClient.isHost = false;
    if (!singletonClient.createClientListener(clientIp)) {
      throw new UnknownHostException("Could not create clientListener.");
    }
    singletonClient.joinGame();
    return singletonClient;
  }

  /**
   * Creates or modifies the {@code Client} singleton instance which also hosts the game on his
   * local machine.
   *
   * @param game the game which is needed to create the host.
   * @param player is the player associated with the client.
   * @return the newly created {@code Client} instance.
   * @throws IllegalStateException if the game is full or is started already.
   * @throws UnknownHostException if there is an error at clientListener creation.
   * @throws IOException if there is a connection error.
   */
  public static Client createSingletonHost(Rummikub game, Player player)
      throws UnknownHostException, IOException {
    singletonClient = new Client(game, player, LOCALHOST_IP_ADDRESS);
    singletonClient.isHost = true;
    singletonClient.createServer();
    if (!singletonClient.createClientListener(InetAddress.getLocalHost().getHostAddress())) {
      throw new UnknownHostException("Could not create clientListener.");
    }
    singletonClient.joinGame();
    return singletonClient;
  }

  /**
   * Returns the {@code Client} singleton instance of this class.
   */
  public static Client getInstance() {
    return singletonClient;
  }

  /**
   * Create a {@code Server} instance to host the game.
   * 
   * @throws IOException if server creation failed.
   */
  private void createServer() throws IOException {
    try {
      this.server = new Server(SERVERPORT);
      this.serverThread = new Thread(this.server);
      this.serverThread.setDaemon(true);
      this.serverThread.start();
      Thread.sleep(WAITING_TIME_IN_MS);
    } catch (InterruptedException e) {
      // do nothing
    }
  }

  /**
   * Create a {@code clientListener} instance.
   * 
   * @param clientIp
   * 
   * @return true if successful, false if not.
   */
  private boolean createClientListener(String clientIp) {
    try {
      this.clientListener = new ClientListener(this, clientIp);
      this.clientListenerThread = new Thread(this.clientListener);
      this.clientListenerThread.setDaemon(true);
      this.clientListenerThread.start();
      Thread.sleep(WAITING_TIME_IN_MS);
    } catch (InterruptedException e) {
      // do nothing
    } catch (SocketException e) {
      return false;
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  /**
   * For leaving the game. The player will be removed from the game by the server calling
   * Rummikub.removePlayer(); Because of this, it is impossible that two players leave the game at
   * the same time.
   * 
   * @return true if successful, false otherwise.
   * @throws IOException if there is a connection error.
   */
  public boolean leaveGame() {
    Message message = new Message(player, null, Command.LEAVEGAME);
    try {
      send(message);
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  /**
   * Has to be called to send a changed game to all clients.
   * 
   * @return false if currentGame is invalid and could not be updated
   */
  public boolean updateGame() {
    Message message = new Message(player, getCurrentGame(), Command.UPDATEGAME);
    Boolean ret;
    try {
      ret = (Boolean) send(message);
    } catch (IOException e) {
      return false;
    }
    return ret;
  }

  /**
   * Has to be called to send the game to all clients if the game has not been started. This method
   * will be called if the host starts a new game.
   *
   * @return True if the game has not been started, false otherwise.
   */
  public boolean startGame() {
    Message message = new Message(player, getCurrentGame(), Command.STARTGAME);
    Boolean ret;
    try {
      ret = (Boolean) send(message);
      return ret;
    } catch (IOException e) {
      return false;
    }
  }

  /**
   * Has to be called when a player needs a backup game, because he wants to reset his board.
   * 
   * @return backup game from received from server.
   */
  public Rummikub getBackup() {
    Message message = new Message(player, null, Command.BACKUP);
    try {
      setCurrentGame((RummikubGame) send(message));
    } catch (IOException e) {
      return null;
    }
    return getCurrentGame();
  }

  /**
   * Has to be called when the host has restarted the game. Client will change his current game
   * which will be sent to all clients.
   * 
   * @param game the new game that will be sent
   */
  public void restartGame(Rummikub game) {
    currentGame.setValue(game);
    startGame();
  }

  /**
   * Terminates the whole Application, server sends a message to all clients will be stopped
   * afterwards.
   * 
   * @return returns true if successful, false if not
   */
  public boolean terminateGame() {
    Message message = new Message(player, null, Command.TERMINATE);
    try {
      send(message);
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  /**
   * Establishes connection to the server.
   * 
   * @throws IOException if the connection could not be established
   * @throws UnknownHostException if the given host address was not valid
   */
  private void connect() throws IOException, UnknownHostException {
    InetAddress adr = InetAddress.getByName(ipAddress);
    // for testing purposes
    log(" connecting to server IP " + adr.toString() + ", using port " + SERVERPORT + "...");
    this.clientSocket = new Socket(adr, SERVERPORT);
    // for testing purposes
    log("...done");
    this.output = new ObjectOutputStream(clientSocket.getOutputStream());
  }

  /**
   * Disconnecting from server.
   * 
   * @return true if successful, false otherwise.
   */
  private boolean disconnect() {
    try {
      clientSocket.close();
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  /**
   * Sends a Message object to the server.
   * 
   * @param message for sending to the server.
   * @return response from server, type depends on request
   * @throws UnknownHostException if there is an error in connect().
   * @throws IOException if there is a connection error.
   */
  private Object send(Message message) throws UnknownHostException, IOException {
    Object ret = null;
    try {
      connect();
      output.writeObject(message);
      ObjectInputStream input = new ObjectInputStream(clientSocket.getInputStream());
      ret = input.readObject();
      disconnect();
    } catch (ClassNotFoundException e) {
      return null;
    }
    return ret;
  }

  /**
   * For joining the game. The player will be added to the current game by the server calling
   * RummikubGame.addPlayer(); Because of this, it is impossible that two players join the game at
   * the same time.
   * 
   * @throws UnknownHostException if there is an error in connect().
   * @throws IOException if there is a connection error.
   * @throws IllegalStateException if the game is full or is already started.
   */
  private void joinGame() throws UnknownHostException, IOException {

    Message message = new Message(player, getCurrentGame(), Command.JOINGAME,
        clientListener.getClientIp(), clientListener.getPort());
    log("IP Adresse in Message von Client nach Server: " + clientListener.getClientIp()
        + ", ClientlistenerPort: " + clientListener.getPort());
    Boolean ret = (Boolean) send(message);
    if (!ret) {
      throw new IllegalStateException("Game already started or is full.");
    }
  }

  /**
   * Returns value that is stored in property currentGame.
   *
   * @return instance of Rummikub.
   */
  public Rummikub getCurrentGame() {
    return currentGame.getValue();
  }

  /**
   * Setter for the currentGame, used only by the clientListener.
   *
   * @param game new currentGame
   */
  void setCurrentGame(Rummikub game) {
    currentGame.setValue(game);
    updatePlayer();
  }

  /**
   * Returns Property currentGame. This allows to add listener in order to be notified if new game
   * is set.
   *
   * @return Property currentGame.
   */
  public ObjectProperty<Rummikub> currentGameProperty() {
    return currentGame;
  }

  public Boolean isHost() {
    return this.isHost;
  }

  /**
   * Checks if it's my turn.
   *
   * @return {@code true} if this player equals currentGamePlayer.
   */
  public boolean isMyTurn() {
    return player.equals(getCurrentGame().getCurrentPlayer());
  }

  /**
   * Updates this player with new value. Should be called when new game is set.
   */
  private void updatePlayer() {
    for (Player newPlayer : getCurrentGame().getPlayers()) {
      if (player.equals(newPlayer)) {
        player = newPlayer;
      }
    }
  }

  /**
   * Returns an instance of player that is stored in client.
   *
   * @return player.
   */
  public Player getPlayer() {
    return player;
  }

  /**
   * Returns BooleanProperty that contains true value if game was terminated by host.
   *
   * @return BooleanProperty.
   */
  public BooleanProperty terminateProperty() {
    return clientListener.quitProperty();
  }

  /**
   * For testing purposes. Console test output for networking test.
   *
   * @param consoleOutput Text that is printed to the console.
   */
  static void log(String consoleOutput) {
    // System.out.println(consoleOutput);
  }

}
