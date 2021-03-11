package networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.LinkedList;

import model.Player;
import model.Rummikub;
import networking.Message.Command;

/**
 * The server that starts a connection to other clients/players. It responds depending on the
 * received message.
 */
class Server implements Runnable {

  /**
   * Timeout for listening for incoming messages (in ms). It is equal to 50 minutes.
   */
  private static final int TIMEOUT_IN_MS = 3000000;

  /**
   * Delay before final close of socket.
   */
  private static final int DELAY_IN_MS = 100;

  /**
   * The server's communication socket .
   */
  private ServerSocket serverSocket;

  /**
   * The current game.
   */
  private Rummikub currentGame;

  private boolean quit;

  /**
   * List of clients.
   */
  private LinkedList<ClientInfo> clients;

  static Command command = Command.TERMINATE;

  Server(int portAdress) throws IOException {
    clients = new LinkedList<ClientInfo>();
    quit = false;
    serverSocket = new ServerSocket(portAdress);
    serverSocket.setSoTimeout(TIMEOUT_IN_MS);
    serverSocket.setReuseAddress(true);
  }

  /**
   * Let's the server socket listen as long as quit is false, otherwise shuts the server down.
   */
  @Override
  public void run() {
    while (!quit) {
      try {
        Socket socket = serverSocket.accept();
        readMessage(socket);
        socket.close();
      } catch (Exception e) {
        break;
      }
    }
    // Server termination.
    try {
      Thread.sleep(DELAY_IN_MS);
      serverSocket.close();
    } catch (InterruptedException | IOException e) {
      // do nothing
    }
    // for testing purposes.
    Client.log(" server terminates ...");
  }

  /**
   * Method to stop the server.
   */
  void stop() {
    quit = true;
  }

  /**
   * Method to send a Massage to one client.
   * 
   * @param clientInfo client information of client receiving the massage
   * @param message the massage to the client.
   */
  private void send(ClientInfo clientInfo, Message message) {
    try {
      Socket socket = connect(clientInfo);
      ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
      output.writeObject(message);
      socket.close();
    } catch (IOException e) {
      // do nothing
    }
  }

  /**
   * Sends a message to all clients.
   * 
   * @param gameWithClients the game in which the clients are participating
   * @param massageToAll the message which should be send
   */
  private void sendAll(Rummikub gameWithClients, Command massageToAll) {
    for (ClientInfo client : clients) {
      // for testing purposes.
      Client.log("server sends message to player " + client.getPlayer().getName() + "("
          + client.getPlayer().getAge() + ")");

      Message message = new Message(client.getPlayer(), gameWithClients, massageToAll);
      send(client, message);
    }
  }

  /**
   * Reads and handles massages received on the socket. Depending on the massages received sends out
   * a massage to it's clients to update the game.
   * 
   * @param socket the socket listening
   */
  private void readMessage(Socket socket) {
    try {
      ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
      Message message = (Message) input.readObject();

      // for testing purposes.
      Client.log(" (server) message received: " + message);

      Object response = execute(message);
      ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
      output.writeObject(response);
    } catch (ClassNotFoundException e) {
      // do nothing
    } catch (IOException e) {
      // do nothing
    }
  }


  /**
   * Method Handles the received message and starts the required action depending on the command and
   * creates an object to be used as response.
   * 
   * @param message the received message
   * @return the object which is used as response
   * @throws IOException if stream cannot be written to or is closed
   */
  private Object execute(Message message) throws IOException {
    // for testing purposes.
    Client.log("Server empfaengt: " + message.getCommand());

    switch (message.getCommand()) {
      case JOINGAME:
        return joinGame(message);

      case UPDATEGAME:
        return updateGame(message);

      case STARTGAME:
        return startGame(message);

      case BACKUP:
        return currentGame;

      case LEAVEGAME:
        leaveGame(message);
        break;

      case TERMINATE:
        terminate(message);
        break;

      default:
        // unreachable
        throw new AssertionError();
    }
    return Boolean.TRUE;
  }

  /**
   * Establishes a connection to the client.
   * 
   * @throws IOException if stream cannot be written to or is closed
   * @throws UnknownHostException if a machine with the specified IP does not exist, or a domain
   *         name cannot be found
   */
  private Socket connect(ClientInfo clientInfo) throws IOException, UnknownHostException {
    Socket reading = null;
    InetAddress adress = InetAddress.getByName(clientInfo.getIpAddress());

    // for testing purposes.
    Client.log(" connecting to client IP " + adress.toString() + ", using port "
        + clientInfo.getPort() + "...");

    reading = new Socket(adress, clientInfo.getPort());

    // for testing purposes.
    Client.log("...done");

    return reading;
  }

  /**
   * Adds a player to the current game and a new client to the client list. Also sends the modified
   * game to all clients afterwards.
   * 
   * @param message the message which gets sent.
   * @return returns true, if adding the player to the game was successful, false if not.
   */
  private Boolean joinGame(Message message) {
    // first player creates game
    if (message.getGame() != null) {
      if (currentGame != null) {
        return Boolean.FALSE;
      }
      currentGame = message.getGame();
    }
    // for testing purposes.
    Client.log(
        " new player " + message.getPlayer().getName() + "(" + message.getPlayer().getAge() + ")");

    // TODO: duplicate player check here if not implemented in addPlayer()
    Boolean upToDate = currentGame.addPlayer(message.getPlayer());
    if (upToDate) {
      ClientInfo clientInfo = new ClientInfo(message.getGame() != null, message.getIpAddress(),
          message.getPort(), message.getPlayer());
      clients.add(clientInfo);

      sendAll(currentGame, Command.UPDATEGAME);
    }
    return upToDate;
  }

  /**
   * Sends the current game to all clients if the game is valid. This method will be called if a
   * player has changed the game.
   * 
   * @param message the message which gets sent.
   * @return true, if the received game from client is valid, false if not.
   */
  private Boolean updateGame(Message message) {
    Boolean isValid = message.getGame().isValid();
    if (isValid) {
      currentGame = message.getGame();
      boolean isWinner = currentGame.isWon();
      if (isWinner) {
        currentGame.setWinner(currentGame.getCurrentPlayer());
      } else {
        currentGame.endCurrentRound();
      }
      sendAll(currentGame, Command.UPDATEGAME);
    }
    return isValid;
  }

  /**
   * Sends the game to all clients if the game has not been started. This method will be called if
   * the host starts a new game.
   * 
   * @param message the message which gets sent.
   * @return true if the game has not been started, false otherwise.
   */
  private Boolean startGame(Message message) {
    Boolean notStarted = message.getGame().getRound() == 1;
    if (notStarted) {
      currentGame = message.getGame();
      sendAll(currentGame, Command.UPDATEGAME);
    }
    return notStarted;
  }

  /**
   * Removes the player in the current game and sends modified game to all clients.
   * 
   * @param message the message which gets sent.
   */
  private void leaveGame(Message message) {
    for (Player p : currentGame.getPlayers()) {
      if (message.getPlayer().equals(p)) {
        currentGame.removePlayer(p);
        removeFromClientList(p);
      }
    }
    sendAll(currentGame, Command.UPDATEGAME);
  }

  /**
   * Lets the clients know that the game terminates and then terminates the server.
   * 
   * @param message the message which gets sent.
   */
  private void terminate(Message message) {
    sendAll(null, Command.TERMINATE);
    stop();
  }

  /**
   * Removes the ClientInfo object from the ClientInfo list.
   * 
   * @param player the player who wants to leave.
   */
  private void removeFromClientList(Player player) {
    for (ClientInfo c : clients) {
      if (player.equals(c.getPlayer())) {
        clients.remove(c);
        break;
      }
    }
  }

}
