public class Tile_Exit extends Tile{

    Platformer p;
    boolean active = true;
    public Tile_Exit(int imgIndex, float x, float y, boolean hasRigidCollision, Platformer p) {
        super(imgIndex, x, y, hasRigidCollision);
        this.p = p;
    }

    @Override
    public void onCollision(Player p2) {
        if(active){
            active = false;
            p.sB.addScore();
            p.sB.setVisible(false);
            p.sB = new Scoreboard();
            p.sB.setVisible(true);

            p.dispose();

            p.gameStateUpdateTrigger.cancel();
        }
    }
}
