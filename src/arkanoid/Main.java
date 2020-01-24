package arkanoid;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
//        Parent root = FXMLLoader.load(getClass().getResource("../resources/arkanoid.fxml"));
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("../resources/arkanoid.fxml"));
        primaryStage.setTitle("Arkanoid");
        arkanoid.Game game = new Game();
        game.runTheGame();
//        primaryStage.resizableProperty().set(false);
//        primaryStage.setScene(new Scene(root, 600, 400));
        Scene scene = new Scene(game.getRoot());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
