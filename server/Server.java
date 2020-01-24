import java.io.*;
import java.net.*;
import java.util.*;

/**
 * Server
 */
public class Server {
  public static final String END_MESSAGE = "!disconnect";

  private int port;
  private Set<String> users = new HashSet<>();
  private Set<UserThread> userThreads = new HashSet<>();
  private Map<String, Integer> leaderboard = new HashMap<String, Integer>();

  public Server(int port) {
    this.port = port;
  }

  public void start() {
    try (ServerSocket server = new ServerSocket(port)) {
      System.out.println("Listening on: " + server);

      while (true) {
        Socket socket = server.accept();

        UserThread newUser = new UserThread(socket, this);
        userThreads.add(newUser);
        newUser.start();
      }
    } catch (IOException e) {
      System.err.println("[ERROR] An error occurred while starting the server: " + e.getMessage());
      e.printStackTrace();
    }

  }

  public static void main(String[] args) {

    if (args.length < 1) {
      System.out.println("Syntax: java Server <port-number>");
      System.exit(0);
    }

    int port = Integer.parseInt(args[0]);

    Server server = new Server(port);
    server.start();
  }

  /**
   * Broadcasts a message to the other users
   *
   * @param message
   * @param excludedUser
   */
  void broadcast(String message, UserThread excludedUser) {
    for (UserThread user : userThreads) {
      if (user != excludedUser) {
        user.sendMessage(message);
      }
    }
  }

  /**
   * Checks if any other users are connected to the server
   */
  boolean hasUsers() {
    return !this.users.isEmpty();
  }

  Set<String> getUsers() {
    return this.users;
  }

  /**
   * Stores username of a new connected user
   *
   * @param username
   */
  void addUser(String username) {
    users.add(username);
    if (leaderboard.get(username) == null) {
      leaderboard.put(username, 0);
    }
    System.out.println(leaderboard.get(username));
  }

  /**
   * Removes username and userThread when client is disconnected
   *
   * @param username
   * @param user
   */
  void removeUser(String username, UserThread user) {
    boolean removed = users.remove(username);

    if (removed) {
      userThreads.remove(user);
      System.out.println("The user " + username + " quitted");
    }
  }

  String getUserScore(String username) {
    return String.valueOf(leaderboard.get(username));
  }

  void updateScore(String username, int score) {
    leaderboard.put(username, score);
  }

  public Map<String, Integer> getLeaderboard() {
    return leaderboard;
  }
}