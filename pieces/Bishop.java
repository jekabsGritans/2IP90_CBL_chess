package pieces;

/**
 * Represents a bishop chess piece.
 */
public class Bishop extends ChessPiece{

    /**
     * Creates a bishop chess piece.
     * @param isWhite true if the piece is white, false if the piece is black
     * @param hasMoved true if the piece has moved, false otherwise
     */
    public Bishop(boolean isWhite, boolean hasMoved) {
        super(isWhite, hasMoved);
    }
}
