package arkanoid;

import javafx.animation.AnimationTimer;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import lobby.Lobby;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Game {
  //constants
  private static final int SCREEN_HEIGHT = 510;
  private static final int SCREEN_WIDTH = 1024;
  private static final int PLATFORM_HEIGHT = 35;
  private static final int PLATFORM_WIDTH = 100;
  private static final int BALL_HEIGHT = 30;
  private static final int BALL_WIDTH = 30;
  private static final int BRICK_HEIGHT = 30; // 40
  private static final int BRICK_WIDTH = 48; // 72
  private static final int BRICK_COUNT = 2;
  private static final int BRICK_MARGIN = 10;

  //game state flags
  public static boolean running = false;
  public static boolean started = true;
  public static boolean isActive = false;

  //variables
  private Random random = new Random();
  private Platform platform = new Platform();
  private Ball ball = new Ball();
  private ArrayList<Brick> bricks = new ArrayList<>();
  private Brick brick = new Brick();
  private Image background = new Image(new File("src/resources/7.png").toURI().toString());
  private Image startBackground = new Image("file:../resources/8.png");
  private boolean win = false;
  private static Lobby lobby;

  public static Lobby getLobby() {
    return lobby;
  }

  private AnimationTimer animatrix;

  GraphicsContext gc;
  Parent root;
  FXMLLoader loader = new FXMLLoader();
  Canvas canvas;
  Controller controller;

  public void reset() {
    Game.started = true;
    Game.running = false;

  }

  public void runTheGame() {
    reset();
    loader.setLocation(getClass().getResource("../resources/arkanoid.fxml"));
    try {
      root = loader.load();
    } catch (IOException e) {
      e.printStackTrace();
    }

    controller = loader.getController();
    canvas = new Canvas(SCREEN_WIDTH,SCREEN_HEIGHT);
    gc = canvas.getGraphicsContext2D();
    System.out.println(canvas.getWidth());
    System.out.println(canvas.getHeight());
    controller.getGamePart().getChildren().add(0, canvas);
    System.out.println(controller.getGamePart().getChildren());

    initializePlatform();
    initializeBall();
    initializeBricks();

//    gc.drawImage(startBackground, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
    System.out.println("FUCKERINO");

    animatrix = new AnimationTimer()
    {
      public void handle(long currentNanoTime) {
        controller.getPauseBox().setVisible(!Game.running);
        if(running) {
          gc.clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
//          gc.drawImage(new Image(new File(controller.getImageView().getImage().getUrl()).toURI().toString()), 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
          platform.render(gc);

          for (Brick brick:bricks) {
            brick.render(gc);
          }

          checkWin();
          makeReflection();
          updateBallOnTheScreen();
        } else if (!Game.started) {
        // BRUTE FORCE CLOSE
        // HELL YEAH BROTHER
          controller.getPauseBox().setVisible(true);
          if (!win) {
            controller.getPauseBox().setText("Other player won!");
          }
//          ((Stage)controller.getGamePart().getScene().getWindow()).close();
          System.out.println("HARD FINITO");
          animatrix.stop();
          Game.isActive = false;
        }
      }
    };

    animatrix.start();

    canvas.setOnMouseDragged(
            new EventHandler<MouseEvent>() {
              public void handle(MouseEvent event) {
                if((event.getX() > platform.getWidth()/2) && (event.getX() < SCREEN_WIDTH - platform.getWidth()/2)) {
                  platform.setPositionX(event.getX() - 40);

                }
              }
            }
    );

    new java.util.Timer().schedule(
            new java.util.TimerTask() {
              @Override
              public void run() {
                Game.isActive = true;
              }
            },
            1500
    );

  }

  public Parent getRoot() {
    return root;
  }

  public void initializePlatform(){
    System.out.println(new File("/src/resources/8.png").toURI().toString());
    platform.setImage(new Image(new File("src/resources/8.png").toURI().toString()));
    System.out.println(platform.getImage());
    platform.setAlignment(SCREEN_WIDTH / 2 - 25, SCREEN_HEIGHT - 30, PLATFORM_HEIGHT, PLATFORM_WIDTH);
    platform.render(gc);
    System.out.println(platform.getPositionX());
  }
  public void initializeBall(){
    ball.setImage(new Image("file:src/resources/bombs/tilt-back-skull.png"));
    ball.setAlignment(SCREEN_WIDTH/2-BALL_WIDTH/2,SCREEN_HEIGHT-BALL_HEIGHT-PLATFORM_HEIGHT,BALL_HEIGHT,BALL_WIDTH);
//    ball.setSpeed(random.nextInt(4)+1);
    ball.setSpeed(8);
    ball.setAngle(random.nextDouble()*(-Math.PI));
  }

  public void initializeBrick(Brick brick, int index) {
    int rowMax = (int)Math.ceil(SCREEN_WIDTH / (BRICK_MARGIN + BRICK_WIDTH)); // CEIL or FLOOR here?
    int rows = (int)Math.ceil(BRICK_COUNT / rowMax);

    int columnIndex = index % rowMax;
    int rowIndex = (int)Math.floor(index / rowMax);
    int rowElements = BRICK_COUNT - (rowIndex * rowMax);
    rowElements = rowElements >= rowMax ? rowMax : rowElements;

    int freeSpace = (SCREEN_WIDTH - (BRICK_WIDTH * rowElements) - (BRICK_MARGIN * (rowElements - 1))) / 2;

    brick.setImage(new Image("file:src/resources/107.png"));

    brick.setAlignment(freeSpace + (BRICK_WIDTH + BRICK_MARGIN) * columnIndex, BRICK_MARGIN + (BRICK_HEIGHT + BRICK_MARGIN) * rowIndex, BRICK_HEIGHT, BRICK_WIDTH);

    brick.render(gc);
  }

  public void initializeBricks() {
    bricks = new ArrayList<>();
    for (int i = 0; i < BRICK_COUNT; i++) {
      Brick brick = new Brick();
      initializeBrick(brick, i);
      bricks.add(brick);
    }
  }

  public void makeReflection(){
    //wall reflection logic

    //top wall
    if(ball.getPositionY() < 0 && ball.getPositionX() > 0 && ball.getPositionX() < SCREEN_WIDTH){
      ball.setAngle(-ball.getAngle());
    }
    //left wall
    if(ball.getPositionX() < 0 && ball.getPositionY() > 0 && ball.getPositionY() < SCREEN_HEIGHT){
      if(ball.getAngle() > 0) ball.setAngle(Math.PI - ball.getAngle()); //going down
      if(ball.getAngle() < 0) ball.setAngle(-Math.PI - ball.getAngle()); //going up
    }
    //right wall
    if(ball.getPositionX() > SCREEN_WIDTH - BALL_WIDTH && ball.getPositionY() > 0 && ball.getPositionY() < SCREEN_HEIGHT){
      if(ball.getAngle() > 0) ball.setAngle(Math.PI - ball.getAngle()); //going down
      if(ball.getAngle() < 0) ball.setAngle(-Math.PI - ball.getAngle()); //going up
    }

    //bottom wall
    if (ball.getPositionY() > SCREEN_HEIGHT) {
//      Game.running = false;
//      Game.started = false;
      controller.getPauseBox().setVisible(true);
      controller.getPauseBox().setText("You lost!");
      lobby.sendMessage("User lost!");
      animatrix.stop();

      new java.util.Timer().schedule(
              new java.util.TimerTask() {
                @Override
                public void run() {
                  if (Game.started) {
                    initializePlatform();
                    initializeBall();
                    initializeBricks();
                  }
                  controller.getPauseBox().setText("Pause");
                  animatrix.start();
                }
              },
              5000
      );
    }

    //platform
    if(ball.intersects(platform)){
//      ball.setAngle(-ball.getAngle());
      ball.setAngle(random.nextDouble()*(-Math.PI));
    }

    //brick
    List<Brick> destroyed = new ArrayList<Brick>();
    for (Brick brick:bricks) {
      if(ball.intersects(brick)){
        ball.setAngle(-ball.getAngle());
        brick.delHp();
        int currentScore = Integer.parseInt(controller.getScoreField().getText());
        controller.getScoreField().setText(String.valueOf(currentScore + 1));
        if (brick.getHp() == 2) {
          brick.setImage(new Image("file:src/resources/108.png"));
        } else if (brick.getHp() == 1) {
          brick.setImage(new Image("file:src/resources/109.png"));
        } else if (brick.getHp() == 0) {
          destroyed.add(brick);
        }
      }
    }
    bricks.removeAll(destroyed);



  }

  public void updateBallOnTheScreen(){
    ball.update();
    ball.render(gc);
  }

  public void checkWin() {
    if (bricks.size() == 0 && win == false) {
      win = true;
      Game.started = false;
      Game.running = false;
      controller.getPauseBox().setVisible(true);
      controller.getPauseBox().setText("You won!");
      lobby.sendMessage("User won with " + controller.getScoreField().getText() + " points!");
      Game.isActive = false;
    }
  }

  public void setLobby(Lobby lobby) {
    this.lobby = lobby;
  }

  public void stopAnimatronix() {
    this.animatrix.stop();
    Game.isActive = false;
  }

}
