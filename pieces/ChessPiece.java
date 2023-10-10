package pieces;

/**
 * Abstract class representing a chess piece.
 */
public abstract class ChessPiece {
    boolean isWhite;
    boolean isCaptured;
    boolean hasMoved;

    /**
     * Return string representation of the piece.
     * @return FEN name of the piece
     */
    public abstract String name();

    /**
     * Default constructor for a chess piece.
     * @param isWhite true if the piece is white, false if the piece is black
     * @param hasMoved true if the piece has moved, false otherwise
     */
    public ChessPiece(boolean isWhite, boolean hasMoved) {
        this.isWhite = isWhite;
        this.isCaptured = false;
        this.hasMoved = hasMoved;
    }

    public boolean isWhite() {
        return this.isWhite;
    }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    public boolean hasMoved() {
        return this.hasMoved;
    }
}
