import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class Enemies_ground extends Enemies{
    int x;
    int y;
    int mid;
    int counter = 0;
    boolean left;
    int countermax = 0;
    int ticks;

    public Enemies_ground(int imgIndex, float x, float y, boolean hasRigidCollision) {
        super(imgIndex, x, y, hasRigidCollision);
        this.x =(int) x;
        this.y = (int) y;
        this.mid = (int)x;
    }
    @Override
    public void move(){
        //if(counter < countermax) {
            if(ticks >100){
                x -= 2;
                left = false;
                if(ticks == 200 ){
                    ticks =0;
                }
            }else if(ticks <=100){
                x += 2;
                left = true;
            }
            bb = new BoundingBox(x,y,x+tileSize,y+tileSize);
            ticks++;
            counter++;
       /* } else {
            counter = 0;
            left = new Random().nextBoolean();
            countermax = new Random().nextInt(7);
        }


         */


    }

    @Override
    public void draw(Graphics2D g2d, float offsetX, float offsetY) {
        if (left) {
            BufferedImage b = images.get(imageIndex);
            AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-b.getWidth(null), 0);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            b = op.filter(b, null);
            g2d.drawImage(b, null, (int) (bb.min.x - offsetX), (int) (bb.min.y - offsetY));
        } else {
            g2d.drawImage(images.get(imageIndex), null, (int) (bb.min.x - offsetX), (int) (bb.min.y - offsetY));
        }
    }

    @Override
    public void onCollision(Player p) {
        p.kill();
    }
}
