import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.Timer;
import java.util.TimerTask;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Platformer extends JFrame {
	public static final String BasePath = ".\\assets\\";
	private static final long serialVersionUID = 5736902251450559962L;

	private Player p = null;
	private Level l = null;

	private Level lBonus = null;
	private boolean isFullScreen = false;
	BufferStrategy bufferStrategy;

	Boolean bonusLevel = false;

	Timer gameStateUpdateTrigger;

	// Score
	LevelTimer lT;
	Scoreboard sB = new Scoreboard();

	public Platformer() {
		//exit program when window is closed
		this.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
					System.exit(0);
			}
		});

		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("./"));
		fc.setDialogTitle("Please select level input image (.bmp)");
		FileFilter filter = new FileNameExtensionFilter("Level image", "bmp");
		fc.setFileFilter(filter);
		int result = fc.showOpenDialog(this);
		File selectedFile = new File("");
		addKeyListener(new AL(this));
		createBufferStrategy(2);
		bufferStrategy = this.getBufferStrategy();


		if (result == JFileChooser.APPROVE_OPTION) {
			selectedFile = fc.getSelectedFile();
			System.out.println("Selected file: " + selectedFile.getAbsolutePath());
		} else {
			dispose();
			System.exit(0);
		}

		try {
			l = new Level(this, selectedFile.getAbsolutePath(), BasePath + "background3new.png");
			lBonus = new Level(this, "testLevel.bmp",BasePath + "background3new.png");
			p = new Player(l);
			l.player = p;

			// Score
			lT = new LevelTimer();

			this.setBounds(0, 0, 1000, 12 * 70);
			this.setVisible(true);
			gameStateUpdateTrigger = new Timer();
			gameStateUpdateTrigger.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					updateGameStateAndRepaint();
				}

			}, 0, 10);
			playSound(BasePath + "Sound\\soundtrack.wav");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void restart() throws IOException {
		p.pos.x = 0;
		p.pos.y = 0;
		l.offsetX = 0;
		p.numberOfLifes = 3;
		l.initLevel();
		p.points = 0;
	}

	private void updateGameStateAndRepaint() {
		l.update();
		p.update();
		checkCollision();

		// Score
		lT.update();
		sB.update(p.points, lT.levelTime);

		repaint();
	}

	private void checkCollision() {
		Level level;
		if (!bonusLevel) {
			level = l;
		} else {
			level = lBonus;
		}
		float playerPosX = p.pos.x;

		p.collidesDown = false;
		p.collidesLeft = false;
		p.collidesRight = false;
		p.collidesTop = false;
		p.collides = false;

		// Collision
		for (int i = 0; i < level.tiles.size(); i++) {

			Tile tile = level.tiles.get(i);

			Vec2 overlapSize = tile.bb.OverlapSize(p.boundingBox);

			float epsilon = 8.f;// experiment with it. to low can cause unwanted stucking when walking over the
									// ground, to high can cause glitching inside/through walls


			if (overlapSize.x >= 0 && overlapSize.y >= 0 && Math.abs(overlapSize.x + overlapSize.y) >= epsilon) {

				if(tile.hasRigidCollision) {
					if (Math.abs(overlapSize.x) > Math.abs(overlapSize.y)) {// Y overlap correction

						if (p.boundingBox.min.y + p.boundingBox.max.y > tile.bb.min.y + tile.bb.max.y) { // player comes from underneith
							p.pos.y += overlapSize.y;
							p.collidesTop = true;
						} else { // player comes from above
							p.pos.y -= overlapSize.y;
							p.collidesDown = true;
						}
					} else { // X overlap correction
						if (p.boundingBox.min.x + p.boundingBox.max.x > tile.bb.min.x + tile.bb.max.x) { // player comes from right{
							p.pos.x += overlapSize.x;
							p.collidesLeft = true;
						} else { // player comes from left
							p.pos.x -= overlapSize.x;
							p.collidesRight = true;
						}
					}
				}
				p.collides = true;
				tile.onCollision(p);
				p.updateBoundingBox();
				if (p.numberOfLifes == 0)
					try {
						gameOver();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		}
	}

	private void gameOver() throws IOException {
		restart();
	}

	@Override
	public void paint(Graphics g) {
		Graphics2D g2 = null;

		try {
			g2 = (Graphics2D) bufferStrategy.getDrawGraphics();
			draw(g2);

		} finally {
			g2.dispose();
		}
		bufferStrategy.show();
	}

	private void draw(Graphics2D g2d) {
		BufferedImage levelImage;
		Level level;
		if (!bonusLevel) {
			level= l;
			levelImage = (BufferedImage) l.getResultingImage();
		} else {
			level = lBonus;
			levelImage = (BufferedImage) lBonus.getResultingImage();
		}
		if (level.offsetX > levelImage.getWidth() - 1000)
			level.offsetX = levelImage.getWidth() - 1000;
		BufferedImage bi = levelImage.getSubimage((int) level.offsetX, 0, 1000, levelImage.getHeight());
		g2d.drawImage(level.backgroundImage, 0, 0, this);
		g2d.drawImage(bi, 0, 0, this);

		for (int i = 0; i< level.tiles.size(); i++) {
			level.tiles.get(i).draw(g2d,level.offsetX,0);
		}
		g2d.drawImage(getPlayer().getPlayerImage(), (int) (getPlayer().pos.x-level.offsetX), (int) getPlayer().pos.y, this);

		if (getPlayer().numberOfLifes > 0) {
			g2d.drawImage(getPlayer().tilesLife.get(3 - getPlayer().numberOfLifes), 1000 - 70, 50, this);
		}
		g2d.drawString(new String(p.points + ""), 500, 50);
		/*for(Enemies enemies : l.enemies){
			enemies.drawStatic(g2d,0,0);
		}

		 */

		// Score
		for (int i = 0; i < lT.time.length(); i++) {
			g2d.drawImage(lT.numbers.get(lT.imgIndizes[i]), (int) (lT.pos.x + i * lT.numberSize.x), (int) lT.pos.y, this);
		}
	}

	public Player getPlayer() {
		return this.p;
	}

	public Level getLevel() {
		return this.l;
	}

	public void setFullScreenMode(boolean b) {
		this.isFullScreen = b;
	}

	public boolean getFullScreenMode() {
		return this.isFullScreen;
	}

	public class AL extends KeyAdapter {
		Platformer p;

		public AL(Platformer p) {
			super();
			this.p = p;
		}

		@Override
		public void keyPressed(KeyEvent event) {
			int keyCode = event.getKeyCode();
			Player player = p.getPlayer();

			if (keyCode == KeyEvent.VK_ESCAPE) {
				// Score

				dispose();

				gameStateUpdateTrigger.cancel();
			}

			if (keyCode == KeyEvent.VK_UP) {
			}

			if (keyCode == KeyEvent.VK_DOWN) {
			}

			if (keyCode == KeyEvent.VK_LEFT) {
				player.walkingLeft = true;
			}

			if (keyCode == KeyEvent.VK_RIGHT) {
				player.walkingRight = true;
			}

			if (keyCode == KeyEvent.VK_SPACE) {
				player.jump = true;
			}

			if (keyCode == KeyEvent.VK_O) {
				sB.toggleTable();
			}

			if (keyCode == KeyEvent.VK_R) {
				try {
					restart();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		@Override
		public void keyReleased(KeyEvent event) {
			int keyCode = event.getKeyCode();
			Player player = p.getPlayer();

			if (keyCode == KeyEvent.VK_UP) {
			}

			if (keyCode == KeyEvent.VK_DOWN) {
			}

			if (keyCode == KeyEvent.VK_LEFT) {
				player.walkingLeft = false;
			}

			if (keyCode == KeyEvent.VK_RIGHT) {
				player.walkingRight = false;
			}

			if (keyCode == KeyEvent.VK_SPACE) {
				player.jump = false;
			}
		}
	}

	public void playSound(String path){
		File lol = new File(path);

		try{
			Clip clip = AudioSystem.getClip();
			clip.open(AudioSystem.getAudioInputStream(lol));
			clip.start();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	public void loadNewLevel() {
		p.updatePosBonus();
		bonusLevel = !bonusLevel;
	}
}
