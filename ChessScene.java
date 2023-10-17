import java.awt.Color;

import engine.board.ChessPiece.PieceType;

public class ChessScene extends Scene {

    public ChessScene() {
        super();
        frame.getContentPane().setBackground(Color.RED);
    }

    @Override
    public void init() {
        initChessBoard();
    }

    public void initWhitePieces() {
        entities.add(0, null);
    }

    public void initChessBoard() {
        entities.add(new PieceEntity(PieceType.PAWN));
    }
}
