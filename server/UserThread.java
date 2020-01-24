import java.io.*;
import java.net.*;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
/**
 * UserThread
 */
public class UserThread extends Thread {
  Socket socket;
  Server server;
  PrintWriter writer;
  String username;

  public UserThread(Socket socket, Server server) {
    this.socket = socket;
    this.server = server;
  }

  public void run() {
    username = "DEFAULT_USERNAME";
    try {
      BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

      writer = new PrintWriter(socket.getOutputStream(), true);

      printUsers();

      username = reader.readLine();

      if (username.length() >  0) {
        server.addUser(username);

        String serverMessage = "A new user has connected: " + username + "!";
        server.broadcast(serverMessage, this);
        System.out.println(serverMessage);

        String clientMessage;

        do {
          clientMessage = reader.readLine();
          serverMessage = "[" + username + "]: " + clientMessage;
          server.broadcast(serverMessage, this);
          specialCommandHandler(clientMessage);
        } while (!clientMessage.equalsIgnoreCase(server.END_MESSAGE));

        server.removeUser(username, this);
        socket.close();

        serverMessage = username + " has quit.";
        server.broadcast(serverMessage, this);
      }
    } catch (IOException e) {
      System.err.println("[ERROR] An error occurred in the UserThread: " + e.getMessage());
      e.printStackTrace();
    } catch (NullPointerException e) {
      e.printStackTrace();
      if (username != null && !username.isEmpty()) {
        server.removeUser(username, this);

        String serverMessage = username + " has quit.";
        server.broadcast(serverMessage, this);
        System.out.println(serverMessage);
      }
    }
  }

  /**
   * Prints current online users
   */
  void printUsers() {
    if (server.hasUsers()) {
      writer.println("\nConnected users: " + server.getUsers());
    } else {
      writer.println("\nNo other users connected :c");
    }
  }

  /**
   * Sends a message to the client
   *
   * @param message
   */
  void sendMessage(String message) {
    if (username != null) {
      writer.println(message);
    }
  }

  void specialCommandHandler(String message) {
    if (username != null) {
      if (message.contains("User won")) {
        System.out.println("Awarding one point to " + username);
        server.updateScore(username, Integer.valueOf(server.getUserScore(username)) + Integer.valueOf(message.split("User won with ")[1].split(" ")[0]));
        System.out.println(server.getUserScore(username));

      } else if (message.contains("User lost")) {
        System.out.println(username + " lost! -1 DKP");
        server.updateScore(username, Integer.valueOf(server.getUserScore(username)) - 1);
        System.out.println(server.getUserScore(username));
      }

      if (message.contains("!score")) {
        String user = username;
        System.out.println("SCORE TIME! " + username + " " + user);
        String scoreMessage = "Your score is: " + server.getUserScore(user);
        // writer.println(scoreMessage);
        server.broadcast(scoreMessage, null);
      } else if (message.contains("!leaderboard")) {
        Map<String, Integer> leaderboard = server.getLeaderboard();
        Map<String, Integer> sortedMap = leaderboard.entrySet().stream()
                         .sorted(Entry.comparingByValue())
                         .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));
        for (Map.Entry<String, Integer> entry : sortedMap.entrySet()) {
          server.broadcast(entry.getKey() + ": " + entry.getValue() + " points", null);
        }
      } else if (message.contains("!games")) {
        String games = "To start a game add ! before it's name\nCurrent active games:\n\t\t-arkanoid";
        server.broadcast(games, null);
      }
    }
  }
}