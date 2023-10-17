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

    public ChessScene() {
        super();
        frame.getContentPane().setBackground(Color.GRAY);
        initChessBoard();
    }

    public void initWhitePieces() {
        entities.add(0, null);
    }

    public void initChessBoard() {
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
