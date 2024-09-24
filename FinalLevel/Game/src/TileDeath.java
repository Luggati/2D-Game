public class TileDeath extends Tile {

    public TileDeath(int imgIndex, float x, float y){
        super(imgIndex,x,y);
    }

    public void onCollision(Player p){
        p.kill();
    }
}
