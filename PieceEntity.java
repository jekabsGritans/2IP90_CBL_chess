import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JLabel;

import engine.ChessMove;
import engine.board.ChessPiece.PieceColor;
import engine.board.ChessPiece.PieceType;

class PieceEntity extends Entity implements MouseListener {
    boolean dragging = false;
    PieceColor pieceColor;
    Point lastMousePos;
    ChessScene board;
    ArrayList<ChessMove> currentPossibleMoves = new ArrayList<ChessMove>();
    private PieceType pieceType;
    

    public PieceEntity(PieceType pieceType, PieceColor pieceColor) {
        super();
        this.pieceType = pieceType;
        this.pieceColor = pieceColor;
        setSize(new Point(80, 80));
        initTexture();
        initMouseEvents();
    }
    public void mousePressed(MouseEvent e) {
        dragging = true;
     }
 
     public void mouseReleased(MouseEvent e) {
        dragging = false;
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

    public void setType(PieceType newType) {
        pieceType = newType;
        //initTexture();
    }

    public void initTexture() {
        /* 
        String texPath = System.getProperty("user.dir");
        File texFile = new File(texPath, pieceType.name() + ".png");
        loadTexture(texFile);
        */
        if(pieceColor == PieceColor.BLACK) {
            setColor(Color.BLUE);
        } else if(pieceColor == PieceColor.WHITE) {
             setColor(Color.PINK);
        }
    }

    @Override
    public void update() {
        if(dragging) {
            setPos(new Point(getPos().x+InputManager.deltaMousePos.x, getPos().y+InputManager.deltaMousePos.y));
        }
    }
}
