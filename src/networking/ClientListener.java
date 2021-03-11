package networking;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import networking.Message.Command;

/**
 * ClientListener for receiving messages from server.
 */
class ClientListener implements Runnable {

  /**
   * Timeout for listening for incoming messages in ms (50 minutes).
   */
  private static final int TIMEOUT_IN_MS = 3000000;

  /**
   * Delay before final close of socket.
   */
  private static final int DELAY_IN_MS = 1000;

  /**
   * The socket for listening.
   */
  private ServerSocket clientSocket;

  /**
   * Port used by the socket for listening.
   */
  private int clientPort;

  /**
   * IP-Address of the clients computer.
   */
  private String clientIp;

  /**
   * The client which started this listener.
   */
  private Client client;

  private BooleanProperty quit;

  ClientListener(Client client, String clientIp) throws SocketException, IOException {
    quit = new SimpleBooleanProperty(false);
    this.client = client;
    clientSocket = new ServerSocket(0); // 0 -> free Port
    clientSocket.setSoTimeout(TIMEOUT_IN_MS);
    clientSocket.setReuseAddress(true);
    clientPort = clientSocket.getLocalPort();
    this.clientIp = clientIp;
  }

  @Override
  public void run() {
    while (!quit.getValue()) {
      try {
        Socket socket = clientSocket.accept();
        readMessage(socket);
        socket.close();
      } catch (Exception e) {
        break;
      }
    }

    // ClientListener termination.
    try {
      Thread.sleep(DELAY_IN_MS);
      clientSocket.close();
    } catch (InterruptedException | IOException e) {
      // do nothing
    }
    // for testing purposes.
    Client.log(" clientListener terminates ...");
  }

  /**
   * Method to stop the clientListener.
   */
  void stop() {
    quit.setValue(true);
  }

  /**
   * Reading the message received from the server.
   * 
   * @param socket The socket for reading.
   */
  private void readMessage(Socket socket) {
    try {
      ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
      Message message = (Message) input.readObject();
      // for testing purposes
      Client.log(" Client Listener empfängt: " + message.getCommand());
      // refresh the clients currentGame
      if (message.getCommand() == Command.UPDATEGAME) {
        assert (message.getGame() != null);
        client.setCurrentGame(message.getGame());
      } else {
        if (message.getCommand() == Command.TERMINATE) {
          stop();
        }
      }
    } catch (ClassNotFoundException e) {
      // do nothing
    } catch (IOException e) {
      // do nothing
    }
  }

  /**
   * Returns the port used by this {@code ClientListener}.
   */
  int getPort() {
    return clientPort;
  }

  /**
   * Returns the IP-Address of this {@code ClientListener}.
   */
  String getClientIp() {
    return clientIp;
  }

  /**
   * Returns the {@code BooleanProperty} quit of this listener. It indicates whether this
   * ClientListener is still running.
   */
  public BooleanProperty quitProperty() {
    return quit;
  }

}
