package pieces;

/**
 * Represents a king piece in chess.
 */
public class King extends ChessPiece {
    boolean hasMoved;

    /**
     * Creates a king piece.
     * @param isWhite true if the piece is white, false if the piece is black
     * @param hasMoved true if the piece has moved, false otherwise
     */
    public King(boolean isWhite, boolean hasMoved) {
        super(isWhite, hasMoved);
    }

}
