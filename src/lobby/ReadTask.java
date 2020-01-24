package lobby;

import arkanoid.Game;
import javafx.application.Platform;
import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import java.io.File;

public class ReadTask extends Task<Void> {
  Lobby lobby;
  BufferedReader reader;

  public ReadTask(Lobby lobby) {
    this.lobby = lobby;

    try {
      this.reader = new BufferedReader(new InputStreamReader(lobby.socket.getInputStream()));
    } catch (IOException e) {
      System.err.println("[ERROR] An error occurred while getting output the stream: " + e.getMessage());
      e.printStackTrace();
    }
  }

  @Override
  protected Void call() {
    try {
      while (!lobby.socket.isClosed()) {
        String message;
        message = reader.readLine();

        if (message == null && message.isEmpty()) {
          break;
        }

        Platform.runLater(new Runnable() {
          @Override
          public void run() {
            if (message.startsWith("Connected users: ")) {
              String[] users = message.split("Connected users: ");
              String temp = users[1].substring(1, users[1].length() - 1);
              users = temp.split(",");
              for (String user : users) {
                lobby.addUserToTree(user.trim());
              }
            } else if (message.startsWith("A new user has connected: ")) {
              String user = message.substring(0, message.length() - 1).split("A new user has connected: ")[1];
              lobby.addUserToTree(user.trim());
              lobby.printMessage(message);
            } else if (message.matches("^(\\w+ has quit.)$")) {
              String user = message.split(" has quit.")[0];
              lobby.removeUserFromTree(user.trim());
              lobby.printMessage(message);
            } else {
              System.out.println(message);
              lobby.printMessage(message);
              if (message.contains("!pause arkanoid")) {
                Game.running = false;
              } else if (message.contains("!resume arkanoid")) {
                Game.running = true;
              } else if (message.contains("User won with")) {
                Game.running = false;
                Game.started = false;
                Game.isActive = false;
              }
            }
          }
        });


      }
    } catch (IOException e) {
//      e.printStackTrace();
    }

    return null;
  }
}
