package pieces;

/**
 * Represents a pawn chess piece.
 */
public class Pawn extends ChessPiece {
    /**
     * Creates a pawn chess piece.
     * @param isWhite true if the piece is white, false if the piece is black
     * @param hasMoved true if the piece has moved, false otherwise
     */
    public Pawn(boolean isWhite, boolean hasMoved) {
        super(isWhite, hasMoved);
    }
}
