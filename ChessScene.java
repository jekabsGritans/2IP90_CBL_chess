import java.awt.Color;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JLabel;

import engine.ChessGame;
import engine.ChessPiece;
import java.util.*;

public class ChessScene extends Scene {
    Point boardPos = new Point(50, 50);
    Point boardSize = new Point(800, 800);
    int tileAmount = 8;
    double pieceSizeModifier = 0.8;
    ChessGame chessGame;
    

    public ChessScene() {
        super();
        frame.getContentPane().setBackground(Color.GRAY);
        initGame();
        initPieces();
        initBoard();
    }

    public ArrayList<Point> getPossibleMovePositions(PieceEntity piece) {
        //getMoves(piece)
        ArrayList<Point> possiblePositions = new ArrayList<Point>();
        //possiblePositions = getMoves(piece);
        possiblePositions.add(new Point(piece.getPos().x, piece.getPos().y-(boardSize.y/tileAmount)));
        possiblePositions.add(new Point(piece.getPos().x, piece.getPos().y+(boardSize.y/tileAmount)));
        possiblePositions.add(new Point(piece.getPos().x+(boardSize.x/tileAmount), piece.getPos().y));
        possiblePositions.add(new Point(piece.getPos().x-(boardSize.x/tileAmount), piece.getPos().y));
        possiblePositions.add(new Point(piece.getPos().x-(boardSize.x/tileAmount), piece.getPos().y-(boardSize.y/tileAmount)));
        possiblePositions.add(new Point(piece.getPos().x+(boardSize.x/tileAmount), piece.getPos().y+(boardSize.y/tileAmount)));
        possiblePositions.add(new Point(piece.getPos().x-(boardSize.x/tileAmount), piece.getPos().y+(boardSize.y/tileAmount)));
        possiblePositions.add(new Point(piece.getPos().x+(boardSize.x/tileAmount), piece.getPos().y-(boardSize.y/tileAmount)));

        return possiblePositions;
    }

    public void initGame() {
        chessGame = new ChessGame(null, null);
    }
 
    public void initPieces() {
        double tileSize = boardSize.x / tileAmount;
        Point pieceSize = new Point((int)(tileSize*pieceSizeModifier), (int)(tileSize*pieceSizeModifier));
        ArrayList<Point> startPositions = new ArrayList<Point>();
        byte[] startTypes = new byte[] {ChessPiece.Rook, ChessPiece.Knight, ChessPiece.Bishop, ChessPiece.Queen, ChessPiece.King, ChessPiece.Bishop, ChessPiece.Knight, ChessPiece.Rook};
        // Initialize white positions
        for(int y = tileAmount-1; y >= tileAmount-2; y--) {
            for(int x = 0 ; x < tileAmount; x++) {
                Point piecePos = new Point(boardPos.x + (boardSize.x/tileAmount)*x, boardPos.y + (boardSize.y/tileAmount)*y);
                piecePos.x += (tileSize-tileSize*pieceSizeModifier)*0.5;
                piecePos.y += (tileSize-tileSize*pieceSizeModifier)*0.5;
                startPositions.add(piecePos);
            }
        }

        // Initialize white pieces
        for(int i = 0; i < tileAmount*2; i++) {
            byte newType = i < tileAmount ? startTypes[i] : ChessPiece.Pawn;
            PieceEntity newPiece = new PieceEntity(newType, ChessPiece.White);
            newPiece.setSize(pieceSize);
            newPiece.setPos(startPositions.get(i));
            addEntity(newPiece);
        }
        // Initialize black positions
        startPositions = new ArrayList<Point>();
        for(int y = 0; y < 2; y++) {
            for(int x = 0; x < tileAmount; x++) {
                Point piecePos = new Point(boardPos.x + (boardSize.x/tileAmount)*x, boardPos.y + (boardSize.y/tileAmount)*y);
                piecePos.x += (tileSize-tileSize*pieceSizeModifier)*0.5;
                piecePos.y += (tileSize-tileSize*pieceSizeModifier)*0.5;
                startPositions.add(piecePos);
            }
        }
        // Initialize black pieces
        for(int i = 0; i < tileAmount*2; i++) {
            byte newType = i < tileAmount ? startTypes[i] : ChessPiece.Pawn;
            PieceEntity newPiece = new PieceEntity(newType, ChessPiece.Black);
            newPiece.setSize(pieceSize);
            newPiece.setPos(startPositions.get(i));
            addEntity(newPiece);
        }
    }

    public void initBoard() {
        // Initialize FloorTiles
        for(int x = 0; x < tileAmount; x++) {
            for(int y = 0; y < tileAmount; y++) {
                byte tileColor = (y+(x+1*tileAmount)) % 2 == 0 ? ChessPiece.Black : ChessPiece.White;
                FloorTileEntity tile = new FloorTileEntity(tileColor);
                tile.setPos(new Point(boardPos.x + (boardSize.x/tileAmount)*x, boardPos.y + (boardSize.y/tileAmount)*y));
                addEntity(tile);
            }
        }
    }

    public void addEntity(PieceEntity entity) {
        super.addEntity(entity);
        entity.board = this;
    }
}
