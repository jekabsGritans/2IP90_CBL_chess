import java.awt.Color;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.ImageIcon;
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
    byte pieceType;
    boolean active = true;
    boolean mouseIn = false;
    public static ArrayList<Image> textures = new ArrayList<Image>(23);
    
    

    public PieceEntity(byte pieceType, byte pieceColor) {
        super();
        this.pieceType = pieceType;
        this.pieceColor = pieceColor;
        setSize(new Point(100, 100));
        initTexture();
        initMouseEvents();
    }

    public void mousePressed(MouseEvent e) {
        if(!ChessPiece.isColor(pieceColor, board.turnColor) || !active) {
            return;
        }
        if(ChessPiece.isColor(pieceColor, ChessPiece.Black) && board.withBot) {
            return;
        }
        currentPossibleMoves = board.getPossibleMovePositions(this);
        origPos = getPos();
        int xOffset = getPos().x-InputManager.lastMousePos.x+55;
        int yOffset = getPos().y-InputManager.lastMousePos.y+90;
        setPos(new Point(getPos().x - xOffset, getPos().y - yOffset));
        dragging = true;
     }
 
    public void mouseReleased(MouseEvent e) {
        if(!dragging || !active) {
            return;
        }
        stopDrag();
        dragging = false;
    }

    public void mouseEntered(MouseEvent e) {
        mouseIn = true;
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
        if(textures.size() == 0) {
            for(int i = 0; i < 23; i++) {
                textures.add(null);
            }
        }

        if(textures.get(pieceType+pieceColor) == null) {
            String pieceString = "textures/" + ChessPiece.typeToString(pieceType);
            if(ChessPiece.isColor(pieceColor, ChessPiece.White)) {
                pieceString += "White.png";
            } else {
                pieceString += "Black.png";
            }
            String texPath = System.getProperty("user.dir");
            File texFile = new File(texPath + '/' + pieceString);
            Image texImg = new ImageIcon(texFile.getAbsolutePath()).getImage();
            updateTextureImage(texImg);
            textures.set(pieceType+pieceColor, texImg);
        } else {
            updateTextureImage(textures.get(pieceType+pieceColor));
        }

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
