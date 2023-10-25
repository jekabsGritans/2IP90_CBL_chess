import java.awt.Point;
import java.awt.event.MouseListener;
import java.io.File;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class EndingEntity extends Entity implements MouseListener {
    ChessScene scene;
    public EndingEntity() {
        super();
        initMouseEvents();
    }

    public void mouseReleased(MouseEvent e) {
        scene.game.endChessGame();
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