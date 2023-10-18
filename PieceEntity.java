import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.text.Position;

import engine.ChessMove;
import engine.board.ChessPiece;
import engine.board.ChessPiece.PieceColor;
import engine.board.ChessPiece.PieceType;

class PieceEntity extends Entity implements MouseListener {
    boolean dragging = false;
    PieceColor pieceColor;
    Point lastMousePos;
    ChessScene board;
    ChessPiece piece;
    ArrayList<Point> currentPossibleMoves = new ArrayList<Point>();
    Point origPos = getPos();
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
        currentPossibleMoves = board.getPossibleMovePositions(this);
        origPos = getPos();
        dragging = true;
     }
 
     public void mouseReleased(MouseEvent e) {
        stopDrag();
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

    public void stopDrag() {
        Point closestPos = origPos;
        for(int i = 0; i < currentPossibleMoves.size(); i++) {
            Point curPos = currentPossibleMoves.get(i);
            if(Math.sqrt(Math.pow(getPos().x-closestPos.x, 2)+Math.pow(getPos().y-closestPos.y, 2)) > Math.sqrt(Math.pow(getPos().x-curPos.x, 2)+Math.pow(getPos().y-curPos.y, 2))) {
                closestPos = curPos;
            }
        }
        double distance = Math.sqrt(Math.pow(getPos().x-closestPos.x, 2)+Math.pow(getPos().y-closestPos.y, 2));
        //If in the same tile as the possible move, move to the possible move, otherwise go back to original position
        if(distance < board.boardSize.x/board.tileAmount && distance != 0) {
            setPos(closestPos);    
        } else {
            setPos(origPos);
        }
    }

    @Override
    public void update() {
        if(dragging) {
            setPos(new Point(getPos().x+InputManager.deltaMousePos.x, getPos().y+InputManager.deltaMousePos.y));
        }
    }
}
