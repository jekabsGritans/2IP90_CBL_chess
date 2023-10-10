package pieces;

/**
 * Represents a rook chess piece.
 */
public class Rook extends ChessPiece {

    public String name() {
        return this.isWhite() ? "R" : "r";
    }
    /**
     * Creates a rook chess piece.
     * @param isWhite true if the piece is white, false if the piece is black
     * @param hasMoved true if the piece has moved, false otherwise
     */
    public Rook(boolean isWhite, boolean hasMoved) {
        super(isWhite, hasMoved);
    }
}
