import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LevelTimer {
    List<BufferedImage> numbers = new ArrayList<>();
    BufferedImage dot;
    String time;
    float levelTime;
    Vec2 pos;
    Vec2 numberSize = new Vec2(32, 39);
    int[] imgIndizes;


    LevelTimer () {
        try {
            for (int i = 0; i < 10; i++) {
                numbers.add(ImageIO.read(new File("assets\\HUD\\hud_" + i + ".png")));
            }
            dot = ImageIO.read(new File("assets\\HUD\\hud_coins.png"));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        levelTime = 0;
        time = "0";
        pos = new Vec2(20, 50);
        imgIndizes = new int[1];
    }

    public void update() {
        levelTime += 0.01;
        time = Integer.toString((int) levelTime);
        int timeDigits = time.length();
        imgIndizes = new int[timeDigits];
        for (int i = 0; i < timeDigits; i++) {
            imgIndizes[i] = Integer.parseInt(String.valueOf(time.charAt(i)));
        }
    }
}
