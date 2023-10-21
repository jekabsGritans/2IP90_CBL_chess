import java.awt.Point;
import java.awt.event.MouseListener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class StartButton extends Entity implements MouseListener {
    MenuScene menu;
    public StartButton() {
        super();
        setSize(new Point(300, 100));
        initMouseEvents();
    }

    public void mouseReleased(MouseEvent e) {
        //        menu.game.loadScene(menu.game.SCENES.GAME);
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }
 
    public void mouseClicked(MouseEvent e) {

    }

    public void initMouseEvents() { 
        graphic.addMouseListener(this);
    }
}
