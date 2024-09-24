import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Enemies_Fly extends Enemies{
    int x;
    int y;
    int xMax;
    int counter = 0;
    boolean left;
    int countermax = 0;
    int ticks;

    int imageCounter = 0;

    public Enemies_Fly(int imgIndex, float x, float y, boolean hasRigidCollision, int xMax) {
        super(imgIndex, x, y, hasRigidCollision);
        this.x =(int) x;
        this.y = (int) y;
        this.xMax = xMax;

    }
    @Override
    public void move(){
        x += 7;
        if (x >= xMax - tileSize) {
            x = 10;
        }
        bb = new BoundingBox(x,y,x+tileSize,y+30);
    }

    @Override
    public void draw(Graphics2D g2d, float offsetX, float offsetY) {


        if (imageCounter < 8) {
            BufferedImage b = images.get(imageIndex);
            AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-b.getWidth(null), 0);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            b = op.filter(b, null);
            g2d.drawImage(b, null,(int)(bb.min.x - offsetX),(int) (bb.min.y- offsetY));
        } else {
            BufferedImage b = images.get(imageIndex + 1);
            AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-b.getWidth(null), 0);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            b = op.filter(b, null);
            g2d.drawImage(b, null, (int) (bb.min.x - offsetX), (int) (bb.min.y - offsetY));
        }
        imageCounter++;
        if (imageCounter == 16) {
            imageCounter = 0;
        }
    }

    @Override
    public void onCollision(Player p) {
        p.kill();
    }

}
