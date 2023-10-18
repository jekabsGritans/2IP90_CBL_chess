import java.awt.Color;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JLabel;

import engine.board.ChessPiece.PieceColor;
import engine.board.ChessPiece.PieceType;
import java.util.*;

public class ChessScene extends Scene {
    static Point boardPos = new Point(50, 50);
    static Point boardSize = new Point(800, 800);
    static int tileAmount = 8;
    static double pieceSizeModifier = 0.5;

    public ChessScene() {
        super();
        frame.getContentPane().setBackground(Color.GRAY);
        initWhitePieces();
        initBoard();
    }
 
    public void initWhitePieces() {
        double tileSize = boardSize.x / tileAmount;
        Point pieceSize = new Point((int)(tileSize*pieceSizeModifier), (int)(tileSize*pieceSizeModifier));
        ArrayList<Point> startPositions = new ArrayList<Point>();
        PieceType[] startTypes = new PieceType[] {PieceType.ROOK, PieceType.KNIGHT, PieceType.BISHOP, PieceType.QUEEN, PieceType.KING, PieceType.BISHOP, PieceType.KNIGHT, PieceType.ROOK};
        // Initialize white positions
        for(int x = 0 ; x < tileAmount; x++) {
            for(int y = tileAmount-1; y >= tileAmount-2; y--) {
                Point piecePos = new Point(boardPos.x + (boardSize.x/tileAmount)*x, boardPos.y + (boardSize.y/tileAmount)*y);
                piecePos.x += (tileSize-tileSize*pieceSizeModifier)*0.5;
                piecePos.y += (tileSize-tileSize*pieceSizeModifier)*0.5;
                startPositions.add(piecePos);
            }
        }

        // Initialize white pieces
        for(int i = 0; i < tileAmount*2; i++) {
            PieceType newType = i < tileAmount ? startTypes[i] : PieceType.PAWN;
            PieceEntity newPiece = new PieceEntity(newType, PieceColor.WHITE);
            newPiece.setSize(pieceSize);
            newPiece.setPos(startPositions.get(i));
            addEntity(newPiece);
        }
        // Initialize black positions
        startPositions = new ArrayList<Point>();
        for(int x = 0 ; x < tileAmount; x++) {
            for(int y = 0; y < 2; y++) {
                Point piecePos = new Point(boardPos.x + (boardSize.x/tileAmount)*x, boardPos.y + (boardSize.y/tileAmount)*y);
                piecePos.x += (tileSize-tileSize*pieceSizeModifier)*0.5;
                piecePos.y += (tileSize-tileSize*pieceSizeModifier)*0.5;
                startPositions.add(piecePos);
            }
        }
        // Initialize black pieces
        for(int i = 0; i < tileAmount*2; i++) {
            PieceType newType = i < tileAmount ? startTypes[i] : PieceType.PAWN;
            PieceEntity newPiece = new PieceEntity(newType, PieceColor.BLACK);
            newPiece.setSize(pieceSize);
            newPiece.setPos(startPositions.get(i));
            addEntity(newPiece);
        }
    }

    public void initBoard() {
        // Initialize FloorTiles
        for(int x = 0; x < tileAmount; x++) {
            for(int y = 0; y < tileAmount; y++) {
                PieceColor tileColor = (y+(x+1*tileAmount)) % 2 == 0 ? PieceColor.BLACK : PieceColor.WHITE;
                FloorTileEntity tile = new FloorTileEntity(tileColor);
                tile.setPos(new Point(boardPos.x + (boardSize.x/tileAmount)*x, boardPos.y + (boardSize.y/tileAmount)*y));
                addEntity(tile);
            }
        }
    }
}
