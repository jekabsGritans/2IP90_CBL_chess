
import java.awt.Point;
import java.awt.event.MouseListener;
import java.io.File;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class EndingEntity extends Entity implements MouseListener {
    ChessScene scene;
    /**
     * Constructor that calls entity constructor and also initializes mouseListener
     */
    public EndingEntity() {
        super();
        initMouseEvents();
    }

    /**
     * Overwritten mouseListener function, loads menu scene on release
     */
    public void mouseReleased(MouseEvent e) {
        if(!scene.withBot) {
            scene.game.endPlayerChessGame();
            return;
        }
        scene.game.endBotChessGame();
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {
        
    }
 
    public void mouseClicked(MouseEvent e) {

    }
    
    public void mousePressed(MouseEvent e) {

    }

    /**
     * Initializes mouseListener
     */
    public void initMouseEvents() { 
        graphic.addMouseListener(this);
    }
}