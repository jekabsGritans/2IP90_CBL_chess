import java.awt.Point;
import java.awt.event.MouseListener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PlayerVSPlayerButton extends Entity implements MouseListener {
    MenuScene menu;
    public PlayerVSPlayerButton() {
        super();
        setSize(new Point(300, 100));
        initMouseEvents();
    }

    public void mouseReleased(MouseEvent e) {
        menu.game.bindScene(menu.game.playerVSPlayerScene);
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }
 
    public void mouseClicked(MouseEvent e) {

    }
    
    public void mousePressed(MouseEvent e) {

    }

    public void initMouseEvents() { 
        graphic.addMouseListener(this);
    }
}
