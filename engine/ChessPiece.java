package engine;

/**
 * Provides static methods for chess piece byte representation.
 */
public class ChessPiece {
    // Piece type and color are stored in different bits of the byte.
    // Bitwise operations are used to extract the type and color.
    // Idea credit: https://github.com/SebLague/Chess-Coding-Adventure/tree/Chess-V1-Unity

    // 3 bits for type
    public static final byte Empty = 0; // 00 000
    public static final byte Pawn = 1; // 00 001
    public static final byte Knight = 2; // 00 010
    public static final byte Bishop = 3; // 00 011
    public static final byte Rook = 4; // 00 100
    public static final byte Queen = 5; // 00 101
    public static final byte King = 6; // 00 110

    // 2 bits for color
    public static final byte White = 8; // 01 000
    public static final byte Black = 16; // 10 000

    /**
     * Checks if a piece is a certain type.
     * e.g. ChessPiece.isType(piece, ChessPiece.Pawn)
     * @param piece the piece to check
     * @param type the type to check for
     * @return true if the piece is of the given type, false otherwise
     */
    public static boolean isType(byte piece, byte type) {
        // 7 is 00 111, so this operation zeroes out color bits and only compares type bits 
        return (piece & 7) == type;
    }

    /**
     * Checks if a piece is a certain color.
     * e.g. ChessPiece.isColor(piece, ChessPiece.White)
     * @param piece the piece to check
     * @param color the color to check for
     */
    public static boolean isColor(byte piece, byte color) {
        // 24 is 11 000, so this operation zeroes out type bits and only compares color bits
        return (piece & 24) == color;
    }

    /**
     * Checks if a piece is empty.
     * @param piece the piece to check
     * @return true if the piece is empty, false otherwise
     */
    public static boolean isEmpty(byte piece) {
        return isType(piece, Empty);
    }

    /**
     * Checks if a piece is white.
     * @param piece the piece to check
     * @return true if the piece is white, false otherwise
     */
    public static boolean isWhite(byte piece) {
        return isColor(piece, White);
    }
     
    // for translating FEN characters to piece bytes and vice versa
    // Note: FEN_CHAR indices must match type values
    // but this is ok since chess pieces are fixed
    private static final char[] FEN_CHARS = {'1', 'p', 'n', 'b', 'r', 'q', 'k'};

    /**
     * Gets the FEN character for the chess piece.
     * @param piece the chess piece
     * @return the FEN character for the chess piece
     */
    public static Character getFenCharacter(byte piece) {
        byte colorLessPiece = (byte) (piece & 7);
        char fenChar = FEN_CHARS[colorLessPiece];

        return isColor(piece, White) ? Character.toUpperCase(fenChar) : fenChar;
    }

    /**
     * Gets the chess piece byte for the FEN character.
     * @param fenChar the FEN character
     * @return the chess piece byte for the FEN character
     */
    public static byte getPieceFromFenCharacter(char fenChar) {
        String fenChars = new String(FEN_CHARS);
        char lowerFenChar = Character.toLowerCase(fenChar);
        byte piece = (byte) fenChars.indexOf(lowerFenChar);
        boolean isWhite = Character.isUpperCase(fenChar);

        return isWhite ? (byte) (piece | White) : piece; // black by default
    }

}