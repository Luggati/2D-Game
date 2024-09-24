import java.io.*;
import javax.swing.*;
import java.util.ArrayList;


public class Scoreboard extends JFrame {

    float time = 0;
    int coins = 0;
    boolean  toggle = true;

    ArrayList<String> lines = new ArrayList<>();

    Scoreboard() {
        createTable();
    }

    public void getScore() {
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader("scoreboard.txt"));
            String line = reader.readLine();

            while (line != null) {
                lines.add(line);

                // read next line
                line = reader.readLine();
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void update(int coins, float time) {
        this.coins = coins;
        this.time = time;
    }


    public void addScore() {
        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            String player = "Player" + lines.size();
            int tableCoins = Integer.parseInt(parts[1]);
            float tableTime = Float.parseFloat(parts[2]);
            if (tableCoins == coins) {
                if (tableTime < time) {
                    continue;
                } else {
                    lines.add(i, player + "," + coins + "," + time);
                }
                break;
            }
            if (tableCoins < coins) {
                lines.add(i, player + "," + coins + "," + time);
                break;
            }
        }

        try {
            FileWriter filewriter = new FileWriter("scoreboard.txt");
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);

                if (i < lines.size() -1) {
                    line += "\n";
                }
                filewriter.write(line);
            }
            filewriter.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void createTable(){

        getScore();

        String[] columnNames = {"Player", "Coins", "Time"};
        Object[][] data = new Object[lines.size()][];
        for (int i = 0; i < lines.size(); i++) {
            String[] parts = lines.get(i).split(",");
            data[i] = new Object[]{parts[0], parts[1], parts[2]};
        }

        JTable table = new JTable(data, columnNames);
        table.setSize(columnNames.length, data.length);
        add(table);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 400);
        setLocationRelativeTo(null);
    }

    public void toggleTable() {
        setVisible(toggle);
        toggle = !toggle;
    }
}
