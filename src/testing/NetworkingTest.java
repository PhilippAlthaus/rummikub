package testing;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.io.InputStreamReader;
import model.Rummikub;
import model.RummikubGame;
import model.RummikubPlayer;
import networking.Client;

/*
 * Testapplication for testing the classes in the networking package based on commands to be typed
 * into the commandline Arguments in the commandline are seperated by ":". Examples: create host:
 * c:44:tom, join a game: j:localhost:32:fritz or j:192.168.1.27:55:tim, leave a game: l, get a
 * backup: b, terminate server: t
 * 
 */
public class NetworkingTest {

  enum Command {
    JOINGAME("J"), CREATEGAME("C"), STARTGAME("S"), LEAVEGAME("L"), TERMINATE("T"), RESTART(
        "R"), BACKUPGAME("B");
    String value;

    Command(String value) {
      this.value = value;
    }
  }

  // master is the client, who has the server
  static boolean isMaster = false;
  static Command command = Command.TERMINATE;
  static int paramCount = 0;
  static Rummikub game = null;
  static RummikubPlayer player = null;
  static Client client = null;

  public static void main(String[] args) {

    /**
     * for terminating.
     */
    boolean terminate = false;

    // Buffered reader for reading the commandline
    BufferedReader reader =
        new BufferedReader(new InputStreamReader(System.in, Charset.defaultCharset()));
    while (!terminate) {
      try {
        System.out.print("client" + "> ");
        String input = reader.readLine();

        // Newline => do nothing, new prompt
        if (input.trim().isEmpty()) {
          continue;
        }
        String[] tokens = input.trim().split(":");


        // check command and parameter
        command = getCommand(tokens[0]);
        paramCount = tokens.length - 1;

        switch (command) {
          case JOINGAME: {
            // token[1] - String IpAdress, tokens[2] - int age, tokens[3] - String name
            int age = isPositiveNumber(tokens[2]);
            // player = new RummikubPlayer(tokens[3], age);
            player = RummikubPlayer.of(tokens[3], age);

            client = Client.createSingletonClient(player, tokens[1],
                InetAddress.getLocalHost().getHostAddress());
            if (client == null) {
              System.out.println("Client could not join the game.");
            }
          }
            break;

          case CREATEGAME: {
            // game = new RummikubGame();
            game = RummikubGame.create();
            isMaster = true;
            // tokens[1] int age, tokens[2] String name
            int age = isPositiveNumber(tokens[1]);
            // player = new RummikubPlayer(tokens[2], age);
            player = RummikubPlayer.of(tokens[2], age);
            client = Client.createSingletonHost(game, player);
          }
            break;


          case STARTGAME: {
            game.start();
            client.startGame();
          }
            break;

          case RESTART: {
            game = RummikubGame.create();
            client.restartGame(game);
          }
            break;

          case LEAVEGAME: {
            client.leaveGame();
          }
            break;

          case TERMINATE: {
            client.terminateGame();
            terminate = true;
          }
            break;

          case BACKUPGAME: {
            game = client.getBackup();
          }
            break;

          default: {
            System.out.println("Command not used: " + command);
          }

            // wait to display server output
            Thread.sleep(5000);
        }

      } catch (IllegalArgumentException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (IllegalStateException e) {
        e.printStackTrace();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  private static Command getCommand(String input) {
    for (Command c : Command.values()) {
      // System.out.println(c.toString() + " " + c.name() + " " + c.value);
      if (input.toUpperCase().equals(c.value)) {
        return c;
      }
    }
    throw new IllegalArgumentException("The command " + input + " is not known.");
  }

  private static int isPositiveNumber(String number) {

    int ret;
    try {
      ret = Integer.parseInt(number);

      // valid number, but may not be negative
      if (ret < 0) {
        throw new IllegalArgumentException("Negative number " + number + " not allowed.");
      }
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Argument " + number + " is no number");
    }
    return ret;
  }

}


