package arkanoid;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;

public class Controller {

  @FXML
  private ToggleButton pauseButton;

  @FXML
  private TextField scoreField;

  @FXML
  private AnchorPane gamePart;

  @FXML
  private TextField pauseBox;

  public TextField getPauseBox() {
    return pauseBox;
  }

  public ToggleButton getPauseButton() {
    return pauseButton;
  }

  public TextField getScoreField() {
    return scoreField;
  }

  public AnchorPane getGamePart() {
    return gamePart;
  }

  @FXML
  void handlePause(ActionEvent event) {

    if (Game.running) {
      Game.running = false;
      System.out.println("PAUSING...");
      pauseBox.setVisible(true);
    } else {
      Game.running = true;
      System.out.println("RESUMING...");
      pauseBox.setVisible(false);
    }
  }

}
