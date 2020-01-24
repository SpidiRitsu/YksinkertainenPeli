package arkanoid;

public class Brick extends GameItem {
  private int hp = 3;

  public int getHp() {
    return hp;
  }

  public void delHp() {
    this.hp -= 1;
  }
}
