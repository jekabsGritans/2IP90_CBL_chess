package pieces;

/**
 * Represents a pawn chess piece.
 */
public class Pawn extends ChessPiece {

    public String name() {
        return this.isWhite() ? "P" : "p";
    }

    /**
     * Creates a pawn chess piece.
     * @param isWhite true if the piece is white, false if the piece is black
     */
    public Pawn(boolean isWhite) {
        super(isWhite);
    }
}
