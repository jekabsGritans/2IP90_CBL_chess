import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import engine.board.ChessPiece.PieceColor;
import engine.board.ChessPiece.PieceType;

class PieceEntity extends Entity {
    boolean dragging = false;
    PieceType pieceType;
    PieceColor pieceColor;

    public PieceEntity(PieceType pieceType) {
        this.pieceType = pieceType;
        this.pieceColor = PieceColor.WHITE;
        initTexture();
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

    public void initTexture() {
        String texPath = System.getProperty("user.dir");
        File texFile = new File(texPath, pieceType.name() + ".png");
        loadTexture(texFile);

    }

    @Override
    public void update() {
        if(dragging) {
            //drag
        }
    }

    public void dragPiece() {
        dragging = true;
    }
}
