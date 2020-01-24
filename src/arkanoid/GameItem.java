package arkanoid;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public abstract class GameItem {
  private Image image;
  private double positionX;
  private double positionY;
  private double width;
  private double height;



  public Image getImage() {
    return image;
  }

  public void setImage(Image image) {
    this.image = image;
  }

  public double getPositionX() {
    return positionX;
  }

  public void setPositionX(double positionX) {
    this.positionX = positionX;
  }

  public double getPositionY() {
    return positionY;
  }

  public void setPositionY(double positionY) {
    this.positionY = positionY;
  }

  public double getWidth() {
    return width;
  }

  public void setWidth(double width) {
    this.width = width;
  }

  public double getHeight() {
    return height;
  }

  public void setHeight(double height) {
    this.height = height;
  }

  public void render(GraphicsContext gc)
  {
    gc.drawImage( image, positionX, positionY, width, height);
  }

  public Rectangle2D getBoundary()
  {
    return new Rectangle2D(positionX,positionY,width,height);
  }

  public boolean intersects(GameItem s)
  {
    return s.getBoundary().intersects( this.getBoundary() );
  }

  public void setAlignment(double positionX, double positionY, double height, double width){
    this.positionX = positionX;
    this.positionY = positionY;
    this.height = height;
    this.width = width;
  }
}
