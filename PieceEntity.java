import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class PieceEntity extends Entity {
    boolean dragging = false;

    public PieceEntity() {
        
    }

    @Override
    public void update() {
        if(dragging) {
            //drag
        }
    }

    public void initMouseEvents() { 
        MouseListener ml = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("Clicked");
                dragPiece();
            }
        };
    }

    public void dragPiece() {
        dragging = true;
    }
}
