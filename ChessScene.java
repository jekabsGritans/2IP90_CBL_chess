import java.awt.Color;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JLabel;

import engine.board.ChessPiece.PieceColor;
import engine.board.ChessPiece.PieceType;

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
        // Initialize white pieces
        for(int x = 0 ; x < tileAmount; x++) {
            for(int y = 0; y < 2; y++) {
                PieceEntity piece = new PieceEntity(PieceType.BISHOP, PieceColor.WHITE);
                Point piecePos = new Point(boardPos.x + (boardSize.x/tileAmount)*x, boardPos.y + (boardSize.y/tileAmount)*y);
                piecePos.x += (tileSize-tileSize*pieceSizeModifier)*0.5;
                piecePos.y += (tileSize-tileSize*pieceSizeModifier)*0.5;

                piece.setPos(piecePos);
                piece.setSize(new Point((int)(tileSize*pieceSizeModifier), (int)(tileSize*pieceSizeModifier)));
                piece.setColor(Color.PINK);
                addEntity(piece);
            }
        }
        // Initialize Black pieces
        for(int x = 0 ; x < tileAmount; x++) {
            for(int y = tileAmount-2; y < tileAmount; y++) {
                PieceEntity piece = new PieceEntity(PieceType.BISHOP, PieceColor.WHITE);
                Point piecePos = new Point(boardPos.x + (boardSize.x/tileAmount)*x, boardPos.y + (boardSize.y/tileAmount)*y);
                piecePos.x += (tileSize-tileSize*pieceSizeModifier)*0.5;
                piecePos.y += (tileSize-tileSize*pieceSizeModifier)*0.5;

                piece.setPos(piecePos);
                piece.setSize(new Point((int)(tileSize*pieceSizeModifier), (int)(tileSize*pieceSizeModifier)));
                piece.setColor(Color.BLUE);
                addEntity(piece);
            }
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
