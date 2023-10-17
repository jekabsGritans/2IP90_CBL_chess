package engine.board;

/**
 * Represents a chess piece. Type and color are immutable, position is mutable.
 * Only stores data, as moves are handled elsewhere.
 * This is because valid moves depend on rules and the board state, not the piece state.
 */
public class ChessPiece {
    /**
     * Enum for the type of chess piece.
     */
    public enum PieceType {
        PAWN,
        KNIGHT,
        BISHOP,
        ROOK,
        QUEEN,
        KING
    }

    /**
     * Enum for the color of the chess piece.
     */
    public enum PieceColor {
        WHITE,
        BLACK
    }
    
    public final PieceType type;
    public final PieceColor color;

    // position is also stored here to not have to traverse the board to find the piece
    private ChessBoard.Position position;

    // for translating FEN characters to chess pieces and vice versa
    // Note: FEN_CHAR order must match Type enum order,
    // but this is simple and fine since chess pieces are fixed
    private static final char[] FEN_CHARS = {'p', 'n', 'b', 'r', 'q', 'k'};
    private static final PieceType[] TYPES = PieceType.values();

    /**
     * Creates a chess piece from a FEN character.
     * @param fenChar FEN character representing the piece
     */
    public ChessPiece(Character fenChar) {
        boolean isUpper = Character.isUpperCase(fenChar);
        fenChar = Character.toLowerCase(fenChar);

        int charIndex = new String(FEN_CHARS).indexOf(fenChar);

        if (charIndex == -1) {
            throw new IllegalArgumentException("Invalid FEN character: " + fenChar);    
        }

        this.type = TYPES[charIndex];
        this.color = isUpper ? PieceColor.WHITE : PieceColor.BLACK;
    }

    /**
     * Gets the FEN character for the chess piece.
     * @return the FEN character for the chess piece
     */
    public Character getFenCharacter() {
        int typeIndex = type.ordinal();
        char fenChar = FEN_CHARS[typeIndex];
        return color == PieceColor.WHITE ? Character.toUpperCase(fenChar) : fenChar;
    }

    /**
     * Creates a chess piece.
     * @param type the type of chess piece
     * @param color the color of the chess piece
     */
    public ChessPiece(PieceType type, PieceColor color) {
        this.type = type;
        this.color = color;
    }

    /**
     * Gets the position of the chess piece.
     * @return the position of the chess piece
     */
    public ChessBoard.Position getPosition() {
        return position;
    }

    /**
     * Sets the position of the chess piece.
     * @param position the position of the chess piece
     */
    void setPosition(ChessBoard.Position position) {
        // this method is package-private because the position should only be changed by the board
        // in order to keep the board and piece states consistent
        this.position = position;
    }
}