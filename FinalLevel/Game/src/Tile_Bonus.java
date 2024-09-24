public class Tile_Bonus extends Tile{

    Platformer p;
    boolean active = true;
    public Tile_Bonus(int imgIndex, float x, float y, boolean hasRigidCollision, Platformer p) {
        super(imgIndex, x, y, hasRigidCollision);
        this.p = p;
    }

    @Override
    public void onCollision(Player p2) {
        if(active){
            active = false;
            p.loadNewLevel();
    }
    }
}
