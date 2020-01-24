package lobby;

import arkanoid.Game;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;

public class Connect {

  private List<String> servers = Arrays.asList(
          "127.0.0.1",
//          "herwoner.pl",
          "217.182.79.157"
  );

  private String hostname;
  private int port = 52345;

  @FXML
  private TextField nickname;

  @FXML
  private ComboBox<String> serversDropdown;

  @FXML
  private Button connect;

  public void initialize() {
    System.out.println("Starting...");
    for (String server:servers) {
      boolean status = isTcpPortAvailable(server, 52345);
      System.out.println(server + ": " + status);
      serversDropdown.getItems().add((status ? "[ONLINE]  " : "[OFFLINE] ") + server);
    }
  }

  @FXML
  void handleConnection(ActionEvent event) {
    String username = this.nickname.getText();
//    String hostname = this.hostname.getText();
//    int port = Integer.parseInt(this.port.getText());

    try {
      FXMLLoader loader = new FXMLLoader(getClass().getResource("lobby.fxml"));
      Parent root = loader.load();

      Lobby lobby = loader.getController();
      lobby.connectTo(username, hostname, port);
      Stage stage = new Stage();
      lobby.setStage(stage);
      stage.setScene(new Scene(root));
      stage.setTitle("Connected to " + hostname + ":" + port + " as " + username);
      stage.show();


    } catch (IOException e) {
      e.printStackTrace();
    }

  }

  @FXML
  void handleServersDropdown(ActionEvent event) {
    String server = serversDropdown.getValue();
    hostname = getServerHostname();
    if (server.contains("[ONLINE]")) {
      connect.setDisable(false);
    } else {
      connect.setDisable(true);
    }
  }

  String getServerHostname() {
    String server = serversDropdown.getValue();
    String hostname = server.split("\\[[A-Z]+\\]")[1].trim();
    return hostname;
  }

  public static boolean isTcpPortAvailable(String hostname, int port) {
    try (Socket serverSocket = new Socket(hostname, port)) {
      serverSocket.close();
      return true;
    } catch (Exception ex) {
      // ex.printStackTrace();
      return false;
    }
  }
}