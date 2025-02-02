import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO;

public class Level {
	BufferedImage levelImg, resultingLevelImg, backgroundImage, cloudImage1;
	public Player player;
	Vec2 lvlSize;
	float offsetX;
	ArrayList<Tile> tiles;
	ArrayList<Enemies> enemies;

	Platformer p;

	public Level(Platformer p, String levelMapPath, String levelBackgroundMapPath) {
		try {
			backgroundImage = ImageIO.read(new File(levelBackgroundMapPath));
			tiles = new ArrayList<Tile>();
			enemies = new ArrayList<>();
			lvlSize = new Vec2(0, 0);
			offsetX = 0.0f;
			this.p = p;

			try {
				// Level image
				levelImg = ImageIO.read(new File(levelMapPath));

				// Cloud image
				cloudImage1 = ImageIO.read(new File(".\\assets\\Items\\cloud1.png"));

				// Tile images
				Tile.images.add(ImageIO.read(new File(".\\assets\\Tiles\\liquidLavaTop_mid.png")));
				Tile.images.add(ImageIO.read(new File(".\\assets\\Tiles\\stoneMid.png")));
				Tile.images.add(ImageIO.read(new File(".\\assets\\Tiles\\signRight.png")));
				Tile.images.add(ImageIO.read(new File(".\\assets\\Tiles\\signExit.png")));
				Tile.images.add(ImageIO.read(new File(".\\assets\\Tiles\\stoneCliffRight.png")));
				Tile.images.add(ImageIO.read(new File(".\\assets\\Tiles\\stoneCliffLeft.png")));
				Tile.images.add(ImageIO.read(new File(".\\assets\\Items\\coinGold.png")));
				Tile.images.add(ImageIO.read(new File(".\\assets\\Items\\springboardDown.png")));
				Tile.images.add(ImageIO.read(new File(".\\assets\\Items\\springboardUp.png")));
				Tile.images.add(ImageIO.read(new File(".\\assets\\Items\\keyBlue.png")));
				Tile.images.add(ImageIO.read(new File(".\\assets\\Tiles\\lock_blue.png")));
				Tile.images.add(ImageIO.read(new File(".\\assets\\Enemies\\blockerMad.png")));
				Tile.images.add(ImageIO.read(new File(".\\assets\\Tiles\\boxWarning.png")));
				Tile.images.add(ImageIO.read(new File(".\\assets\\Enemies\\snailWalk1.png")));
				Tile.images.add(ImageIO.read(new File(".\\assets\\Enemies\\flyFly1.png")));
				Tile.images.add(ImageIO.read(new File(".\\assets\\Enemies\\flyFly2.png")));
				Tile.images.add(ImageIO.read(new File(".\\assets\\Items\\spikes.png")));
				Tile.images.add(ImageIO.read(new File(".\\assets\\Tiles\\stoneCenter.png")));

			} catch (IOException e) {

			}
			initLevel();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void update() {

		//update camera offset
		float diff = (player.boundingBox.max.x + player.boundingBox.min.x)*0.5f-500  - offsetX;

		int noMoveZone = 100;

		if(Math.abs(diff)>noMoveZone){
			if(diff<0)
				diff+=noMoveZone;
			else
				diff-=noMoveZone;
			offsetX += diff;
		}


		if (offsetX < 0)
			offsetX = 0;

		if (offsetX > resultingLevelImg.getWidth() - 1000)
			offsetX = resultingLevelImg.getWidth() - 1000;
		updateAllEnemies();
	}

	private void updateAllEnemies() {

		for(int i = 0; i< enemies.size();i++){
			enemies.get(i).move();
		}

	}


	public void initLevel() {
		lvlSize.x = Tile.tileSize * levelImg.getWidth(null);
		lvlSize.y = Tile.tileSize * levelImg.getHeight(null);

		resultingLevelImg = new BufferedImage((int) lvlSize.x, (int) lvlSize.y, BufferedImage.TYPE_INT_RGB);

		Graphics2D g2d = null;
		g2d = (Graphics2D) resultingLevelImg.getGraphics();

		for (int x = 0; x < resultingLevelImg.getWidth(null); x += backgroundImage.getWidth()) {

			g2d.drawImage((BufferedImage) backgroundImage, null, x, 0);

			AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
			tx.translate(-backgroundImage.getWidth(null), 0);
			AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			backgroundImage = op.filter(backgroundImage, null);
		}

		for (int x = 0; x < resultingLevelImg.getWidth(null); x += cloudImage1.getWidth() * 2) {
			Random r = new Random();
			g2d.drawImage((BufferedImage) cloudImage1, null, x + r.nextInt(250), r.nextInt(250) + 50);
		}

		tiles.clear();

		for (int y = 0; y < levelImg.getHeight(null); y++) {
			for (int x = 0; x < levelImg.getWidth(null); x++) {
				Color color = new Color(levelImg.getRGB(x, y));

				 Tile t = null;

				// Compare color of pixels in order to select the corresponding tiles

				if (color.equals(Color.BLUE))
					t = new TileDeath(0,x*Tile.tileSize,y*Tile.tileSize);
				if (color.equals(Color.BLACK))
					t = new Tile(1,x*Tile.tileSize,y*Tile.tileSize);
				if (color.equals(Color.GREEN))
					t = new Tile(2 , x*Tile.tileSize,y*Tile.tileSize,false);
				if (color.equals(Color.RED))
					t = new Tile_Exit(3,x*Tile.tileSize,y*Tile.tileSize,false,p	);
				if (color.equals(Color.YELLOW))
					t = new Tile(4,x*Tile.tileSize,y*Tile.tileSize);
				if (color.equals(Color.GRAY))
					t = new Tile(5,x*Tile.tileSize,y*Tile.tileSize);
				if (color.equals(Color.ORANGE))
					t = new TileCoin(6,x*Tile.tileSize,y*Tile.tileSize);
				if (color.equals(Color.CYAN))
					t = new TileSpringBoard(7,x*Tile.tileSize,y*Tile.tileSize);
				if(color.equals(Color.decode("#ffc0cb"))){
					t = new Enemies_ground(13,x*Tile.tileSize,y*Tile.tileSize+40,true);
					enemies.add((Enemies_ground) t);
				}if(color.equals(Color.MAGENTA)){
					t = new Enemies_ground(11,x*Tile.tileSize,y*Tile.tileSize+20,true);
					enemies.add((Enemies_ground) t);
				}
				if(color.equals(Color.decode("#a9a9a9"))){
					t = new Tile(12, x*Tile.tileSize,y*Tile.tileSize);
				}
				if(color.equals(Color.decode("#800080"))){
					t = new Enemies_Fly(14,x*Tile.tileSize,y*Tile.tileSize,true,resultingLevelImg.getWidth());
					enemies.add((Enemies_Fly) t);
				}
				if(color.equals(Color.decode("#0FFF50"))){
					t = new TileDeath(16,x*Tile.tileSize,y*Tile.tileSize);
				}
				if(color.equals(Color.decode("#c0c0c0"))){
					t = new Tile(17,x*Tile.tileSize,y*Tile.tileSize);
				}
				if(color.equals(Color.decode("#00008B"))){
					t = new Tile_Bonus(10,x*Tile.tileSize,y*Tile.tileSize,true,p);
				}

				if(t!=null) {
					tiles.add(t);
					// Get graphics context
					g2d = (Graphics2D) resultingLevelImg.getGraphics();

					// Draw tile into data of imag
					if(t.imageIndex != 11 && t.imageIndex != 13){
					t.drawStatic(g2d,0,0);
				}
				}
			}
		}
		g2d.dispose();
	}

	public Image getResultingImage() {
		return resultingLevelImg;
	}

	public int getSizeX() {
		return resultingLevelImg.getWidth();
	}

	public int getSizeY() {
		return resultingLevelImg.getHeight();
	}
}
