package pieces;

/**
 * Represents a queen chess piece.
 */
public class Queen extends ChessPiece {

    public String name() {
        return this.isWhite() ? "Q" : "q";
    }

    /**
     * Creates a queen chess piece.
     * @param isWhite true if the piece is white, false if the piece is black
     */
    public Queen(boolean isWhite) {
        super(isWhite);
    }
}
