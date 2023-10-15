package pieces;

/**
 * Represents a king piece in chess.
 */
public class King extends ChessPiece {
    boolean hasMoved;

    public String name() {
        return this.isWhite() ? "K" : "k";
    }

    /**
     * Creates a king piece.
     * @param isWhite true if the piece is white, false if the piece is black
     */
    public King(boolean isWhite) {
        super(isWhite);
    }

}
