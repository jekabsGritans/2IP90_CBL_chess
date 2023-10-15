package pieces;

/**
 * Abstract class representing a chess piece.
 */
public abstract class ChessPiece {
    boolean isWhite;
    boolean hasMoved;

    /**
     * Return string representation of the piece.
     * @return FEN name of the piece
     */
    public abstract String name();

    /**
     * Default constructor for a chess piece.
     * @param isWhite true if the piece is white, false if the piece is black
     */
    public ChessPiece(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public boolean isWhite() {
        return this.isWhite;
    }

    /**
     * Initialize a chess piece from a FEN character.
     * @param fenChar FEN character for the piece
     * @return the chess piece or null if the character is invalid
     */
    public static ChessPiece initializeChessPiece(Character fenChar) {
        // No need for a factory class since pieces are fixed
        switch (fenChar) {
            case 'p':
                return new Pawn(true);
            case 'P':
                return new Pawn(false);
            case 'r':
                return new Rook(true);
            case 'R':
                return new Rook(false);
            case 'n':
                return new Knight(true);
            case 'N':
                return new Knight(false);
            case 'b':
                return new Bishop(true);
            case 'B':
                return new Bishop(false);
            case 'q':
                return new Queen(true);
            case 'Q':
                return new Queen(false);
            case 'k':
                return new King(true);
            case 'K':
                return new King(false);
            default:
                return null;
        }
    }
}
