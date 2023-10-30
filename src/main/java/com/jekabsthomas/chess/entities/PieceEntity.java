package com.jekabsthomas.chess.entities;

import com.jekabsthomas.chess.core.InputManager;
import com.jekabsthomas.chess.engine.ChessPiece;
import com.jekabsthomas.chess.scenes.ChessScene;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import javax.swing.ImageIcon;

/**
 * Piece entity, which can be dragged and moved to make moves on the board.

 * @author Thomas de Bock
 * 
 */
public class PieceEntity extends Entity implements MouseListener {
    public boolean dragging = false;
    public byte pieceColor;
    public Point lastMousePos;
    public ChessScene board;
    public ChessPiece piece;
    public ArrayList<Point> currentPossibleMoves = new ArrayList<Point>();
    public Point origPos = getPos();
    public byte pieceType;
    public boolean active = true;
    public boolean mouseIn = false;
    public static ArrayList<Image> textures = new ArrayList<Image>(23);
    
    
    /**
     * Constructs a piece entity by loading the texture and mouseListener.

     * @param pieceType the type of piece to load, depending on the bits xxxxx???
     * @param pieceColor the color of piece to load, depending on the bits xxx??xxx
     */
    public PieceEntity(byte pieceType, byte pieceColor) {
        super();
        this.pieceType = pieceType;
        this.pieceColor = pieceColor;
        setSize(new Point(100, 100));
        initTexture();
        initMouseEvents();
    }

    /**
     * Overwritten mouseListener function, centers the piece to the mouse, and starts dragging it.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        board.mouseReleased = false;
        if (!ChessPiece.isColor(pieceColor, board.turnColor) || !active || board.ended) {
            return;
        }
        if (ChessPiece.isColor(pieceColor, ChessPiece.BLACK) && board.withBot) {
            return;
        }
        currentPossibleMoves = board.getPossibleMovePositions(this);
        origPos = getPos();
        int xoffset = getPos().x - InputManager.lastMousePos.x + 55;
        int yoffset = getPos().y - InputManager.lastMousePos.y + 90;
        setPos(new Point(getPos().x - xoffset, getPos().y - yoffset));
        dragging = true;
    }
 
    @Override
    public void mouseReleased(MouseEvent e) {
        if (!dragging || !active) {
            board.mouseReleased = true;
            return;
        }
        dragging = false;
        stopDrag();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        mouseIn = true;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        mouseIn = false;
    }     
 
    @Override
    public void mouseClicked(MouseEvent e) {

    }

    /**
     * Initializes mouseListener.
     */
    public void initMouseEvents() { 
        graphic.addMouseListener(this);
    }

    /**
     * Sets type.
     * @param newType the type to which the piece will be set depending on the bits xxxxx???
     */
    public void setType(byte newType) {
        pieceType = newType;
        initTexture();
    }

    /**
     * initializes the texture depending on the currently set color and type.
     */
    public void initTexture() {
        if (textures.size() == 0) {
            for (int i = 0; i < 23; i++) {
                textures.add(null);
            }
        }

        if (textures.get(pieceType + pieceColor) == null) {
            String pieceString = "textures/" + ChessPiece.typeToString(pieceType).toUpperCase();
            if (ChessPiece.isColor(pieceColor, ChessPiece.WHITE)) {
                pieceString += "White.png";
            } else {
                pieceString += "Black.png";
            }
            String texPath = System.getProperty("user.dir");
            File texFile = new File(texPath + "/" + pieceString);
            Image texImg = new ImageIcon(texFile.getAbsolutePath()).getImage();
            updateTextureImage(texImg);
            textures.set(pieceType + pieceColor, texImg);
        } else {
            updateTextureImage(textures.get(pieceType + pieceColor));
        }

    }

    /**
     * This is called when the mouse is released and either returns
     * the piece to its original position, or snaps it to one of its possible moves.
     */
    public void stopDrag() {
        // Finds closest possible move
        Point closestPos = origPos;
        int moveIndex = -1;
        for (int i = 0; i < currentPossibleMoves.size(); i++) {
            Point curPos = currentPossibleMoves.get(i);
            double dist1 = Math.sqrt(Math.pow(getPos().x - closestPos.x, 2));
            dist1 += + Math.pow(getPos().y - closestPos.y, 2);
            double dist2 = Math.sqrt(Math.pow(getPos().x - curPos.x, 2));
            dist2 += Math.pow(getPos().y - curPos.y, 2);
            if (dist1 > dist2) {
                closestPos = curPos;
                moveIndex = i;
            }
        }
        double    xcalc = +Math.pow(getPos().y - closestPos.y, 2);
        double distance = Math.sqrt(Math.pow(getPos().x - closestPos.x, 2) + xcalc);
        //If in the same tile as the possible move, move to the possible move, 
        // otherwise go back to original position
        if (distance < board.boardSize.x / board.tileAmount && distance != 0 && moveIndex != -1) {
            setPos(closestPos);    
            board.nextTurn(moveIndex);
        } else {
            setPos(origPos);
        }
        board.removeIndicators();
        board.mouseReleased = false;
    }

    /**
     * Overwrites entity update function, 
     * handles updating the position when the piece is being dragged.
     */
    @Override
    public void update() {
        if (dragging) {
            if (!active || board.mouseReleased) {
                System.out.println("stop");
                dragging = false;
                stopDrag();
                return;
            }
            Point newPos = new Point();
            newPos.x = getPos().x + InputManager.deltaMousePos.x;
            newPos.y = getPos().y + InputManager.deltaMousePos.y;
            setPos(newPos);
        }
    }
}