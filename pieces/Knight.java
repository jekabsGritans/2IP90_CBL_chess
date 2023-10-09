package pieces;

/**
 * Represents a knight chess piece.
 */
public class Knight extends ChessPiece {
    
    /**
     * Creates a knight chess piece.
     * @param isWhite true if the piece is white, false if the piece is black
     * @param hasMoved true if the piece has moved, false otherwise
     */
    public Knight(boolean isWhite, boolean hasMoved) {
        super(isWhite, hasMoved);
    }
}
