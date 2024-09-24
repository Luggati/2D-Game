import java.awt.*;

public class Enemies extends Tile{

    public Enemies(int imgIndex, float x, float y,boolean hascollision) {
        super(imgIndex, x, y,hascollision);
    }
    public void move(){

    }

    @Override
    public void drawStatic(Graphics2D g2d, float offsetX, float offsetY) {
    }
}
