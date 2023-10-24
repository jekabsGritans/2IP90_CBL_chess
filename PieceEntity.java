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

import engine.ChessPiece;

class PieceEntity extends Entity implements MouseListener {
    boolean dragging = false;
    byte pieceColor;
    Point lastMousePos;
    ChessScene board;
    ChessPiece piece;
    ArrayList<Point> currentPossibleMoves = new ArrayList<Point>();
    Point origPos = getPos();
    private byte pieceType;
    

    public PieceEntity(byte pieceType, byte pieceColor) {
        super();
        this.pieceType = pieceType;
        this.pieceColor = pieceColor;
        setSize(new Point(80, 80));
        initTexture();
        initMouseEvents();
    }

    public void mousePressed(MouseEvent e) {
        if(!ChessPiece.isColor(pieceColor, board.turnColor)) {
            return;
        }
        currentPossibleMoves = board.getPossibleMovePositions(this);
        origPos = getPos();
        dragging = true;
     }
 
    public void mouseReleased(MouseEvent e) {
        if(!dragging) {
            return;
        }
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

    public void setType(byte newType) {
        pieceType = newType;
        initTexture();
    }

    public void initTexture() {
        String pieceString = "textures/" + ChessPiece.typeToString(pieceType);
        if(ChessPiece.isColor(pieceColor, ChessPiece.Black)) {
            pieceString += "Black.png";
        } else {
            pieceString += "White.png";
        }
        String texPath = System.getProperty("user.dir");
        File texFile = new File(texPath + '/' + pieceString);
        loadTexture(texFile);
    }

    public void stopDrag() {
        Point closestPos = origPos;
        int moveIndex = -1;
        for(int i = 0; i < currentPossibleMoves.size(); i++) {
            Point curPos = currentPossibleMoves.get(i);
            if(Math.sqrt(Math.pow(getPos().x-closestPos.x, 2)+Math.pow(getPos().y-closestPos.y, 2)) > Math.sqrt(Math.pow(getPos().x-curPos.x, 2)+Math.pow(getPos().y-curPos.y, 2))) {
                closestPos = curPos;
                moveIndex = i;
            }
        }
        double distance = Math.sqrt(Math.pow(getPos().x-closestPos.x, 2)+Math.pow(getPos().y-closestPos.y, 2));
        //If in the same tile as the possible move, move to the possible move, otherwise go back to original position
        if(distance < board.boardSize.x/board.tileAmount && distance != 0 && moveIndex != -1) {
            board.nextTurn(moveIndex);
            setPos(closestPos);    
        } else {
            setPos(origPos);
        }
        board.removeIndicators();
    }

    @Override
    public void update() {
        if(dragging) {
            setPos(new Point(getPos().x+InputManager.deltaMousePos.x, getPos().y+InputManager.deltaMousePos.y));
        }
    }

    @Override
    public void updateTransform() {
        // Override so color not set on updateTransform for this entity, as it uses textures
        graphic.setBounds(getPos().x, getPos().y, getSize().x, getSize().y);
    }
}
