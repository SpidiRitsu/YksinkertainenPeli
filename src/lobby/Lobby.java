package lobby;

import arkanoid.Controller;
import arkanoid.Game;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

public class Lobby {
  private Stage stage;

  private String username;
  private String hostname;
  private int port;

  public Socket socket;
  public PrintWriter writer;
  private ReadTask reader;

  @FXML
  public ListView<String> messageList;

  public PrintWriter getWriter() {
    return writer;
  }

  @FXML
  private TreeView<String> userList;
  private TreeItem<String> rootItem;

  @FXML
  private TextField message;

  public void sendMessage(String message) {
    writer.println(message);

    printMessage("[" + username + "] " + message);
  }

  public void setStage(Stage stage) {
    this.stage = stage;
    this.stage.setOnHiding(event -> {
      writer.println("!disconnect");
    });
  }

  public void connectTo(String username, String hostname, int port) {
    this.username = username;
    this.hostname = hostname;
    this.port = port;

    message.setOnKeyPressed(keyEvent -> {
      if (keyEvent.getCode() == KeyCode.ENTER) {
        String text = message.getText();
        if (!text.equals("")) {
          sendMessage(message.getText());
          message.setText("");
          if (text.equals("!disconnect")) {
            try {
              socket.close();
            } catch (IOException e) {
              e.printStackTrace();
            }
            stage.close();
            reader.cancel();
          }
        }
      }
    });

    try {
      socket = new Socket(hostname, port);
      writer = new PrintWriter(socket.getOutputStream(), true);
      writer.println(this.username);
      messageList.getItems().add("[CLIENT] Connected to " + hostname + ":" + port + " as " + username);
      rootItem = new TreeItem<String>("Server");
      rootItem.setExpanded(true);
      userList.setRoot(rootItem);
      addUserToTree(username);

      this.reader = new ReadTask(this);
      new Thread(reader).start();
    } catch (IOException e) {
      e.printStackTrace();
//      Platform.exit();
      stage.close();
      reader.cancel();
    }


  }

  private String getTime() {
    SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
    Date date = new Date();
    String time = formatter.format(date);

    return time;
  }

  public void addUserToTree(String username) {
    TreeItem<String> user = new TreeItem<String>(username);
    rootItem.getChildren().add(user);
    rootItem.getChildren().sort(Comparator.comparing(TreeItem::toString));
  }

  public void removeUserFromTree(String username) {
    TreeItem<String> user = getTreeViewItem(rootItem, username);
    rootItem.getChildren().remove(user);
    rootItem.getChildren().sort(Comparator.comparing(TreeItem::toString));
  }

  public static TreeItem getTreeViewItem(TreeItem<String> item , String value)
  {
    if (item != null && item.getValue().equals(value))
      return  item;

    for (TreeItem<String> child : item.getChildren()){
      TreeItem<String> s=getTreeViewItem(child, value);
      if(s!=null)
        return s;

    }
    return null;
  }

  public void printMessage(String message) {
    if (message.length() > 0) {
      messageList.getItems().add(getTime() + " " + message);
      if (message.contains("!arkanoid")) {
        if (rootItem.getChildren().size() < 2) {
          messageList.getItems().add("There must be at least 2 players to start the game!");
        } else {
          startArkanoid();
        }
      }
    }
  }

  public void startArkanoid() {
    if (!Game.isActive) {
      try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../resources/arkanoid.fxml"));
        Parent root = loader.load();
        Controller controller = loader.getController();
        Stage stage = new Stage();
        stage.setTitle("[" + this.username + "] Arkanoid");
        Game game = new Game();
        game.runTheGame();
        game.setLobby(this);
        stage.setOnHiding(event -> {
          game.stopAnimatronix();
          sendMessage("Disconnected from the Arkanoid");
        });
        stage.setScene(new Scene(game.getRoot()));
        stage.show();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } else {
      sendMessage("A game is already running for me!");
    }
  }
}
