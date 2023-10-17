import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class Entity extends JPanel {
    BufferedImage texture;
    Point pos = new Point(0, 0);
    int zLayer = 0;
    JLabel label;

    public Entity() {

    }

    public void render() {

    }

    public void update() {
        
    }

    public void loadTexture(File texFile) {
        //texture = new ImageIO.read(texFile);
        //label = new JLabel(new ImageIcon(texture));
        label = new JLabel("Labl");
        label.setOpaque(true);
        label.setBackground(Color.PINK);
    }
}
