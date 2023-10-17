import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Entity {
    private Point pos = new Point(150, 200);
    private Point size = new Point(100, 100);
    private Color color = Color.PINK;
    BufferedImage texture;
    int zLayer = 0;
    JPanel graphic;

    public Entity() {
        graphic = new JPanel();
        updateTransform();
    }

    public void render() {

    }

    public void update() {
        
    }

    public void setColor(Color newColor) {
        color = newColor;
        updateTransform();
    }

    public void setPos(Point newPos) {
        pos = newPos;
        updateTransform();
    }

    public Point getPos() {
        return pos;
    }

    public void setSize(Point newSize) {
        size = newSize;
        updateTransform();
    }

    public Point getSize() {
        return size;
    }

    private void updateTransform() {
        graphic.setBounds(pos.x, pos.y, size.x, size.y);
        graphic.setBackground(color);
    }

    public void loadTexture(File texFile) {
        //texture = new ImageIO.read(texFile);
        //label = new JLabel(new ImageIcon(texture));
    }
}
