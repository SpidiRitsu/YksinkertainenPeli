package arkanoid;

public class Ball extends GameItem {
  private double velocityX;
  private double velocityY;
  private double angle;
  private int speed;

  public double getVelocityX() {
    return velocityX;
  }

  public void setVelocityX(double velocityX) {
    this.velocityX = velocityX;
  }

  public double getVelocityY() {
    return velocityY;
  }

  public void setVelocityY(double velocityY) {
    this.velocityY = velocityY;
  }

  public double getAngle() {
    return angle;
  }

  public void setAngle(double angle) {
    this.angle = angle;
  }

  public int getSpeed() {
    return speed;
  }

  public void setSpeed(int speed) {
    this.speed = speed;
  }

  public void update()
  {
    setPositionX(getPositionX() + speed * Math.cos(angle));
    setPositionY(getPositionY() + speed * Math.sin(angle));
  }

}
