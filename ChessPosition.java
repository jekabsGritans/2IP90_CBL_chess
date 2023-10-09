import pieces.ChessPiece;

/**
 * Represents a position on a chess board.
 * Points to a piece and has an x and y coordinate.
 * If the piece is null, then the position is empty.
 */
public class ChessPosition {
    ChessPiece piece;
    int x;
    int y;

    /**
     * Creates a chess position.
     * @param x x coordinate on the board
     * @param y y coordinate on the board
     * @param piece the piece at this position or null if the position is empty
     */
    public ChessPosition(int x, int y, ChessPiece piece) {
        this.piece = piece;
        this.x = x;
        this.y = y;
    }
}
