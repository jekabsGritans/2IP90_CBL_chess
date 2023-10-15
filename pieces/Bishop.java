package pieces;

/**
 * Represents a bishop chess piece.
 */
public class Bishop extends ChessPiece {

    public String name() {
        return this.isWhite() ? "B" : "b";
    }

    /**
     * Creates a bishop chess piece.
     * @param isWhite true if the piece is white, false if the piece is black
     */
    public Bishop(boolean isWhite) {
        super(isWhite);
    }
}
