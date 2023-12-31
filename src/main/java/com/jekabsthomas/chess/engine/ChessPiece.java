package com.jekabsthomas.chess.engine;

/**
 * Provides static methods for chess piece byte representation.
 * @author Jekabs Gritans
 */
public class ChessPiece {
    // Piece type and color are stored in different bits of the byte.
    // Bytes are ints in JVM anyways, but at least this helps to distinguish them.
    // Bitwise operations are used to extract the type and color.
    // Idea credit: https://github.com/SebLague/Chess-Coding-Adventure/tree/Chess-V1-Unity

    // 3 bits for type
    public static final byte EMPTY = 0; // 00 000
    public static final byte PAWN = 1; // 00 001
    public static final byte KNIGHT = 2; // 00 010
    public static final byte BISHOP = 3; // 00 011
    public static final byte ROOK = 4; // 00 100
    public static final byte QUEEN = 5; // 00 101
    public static final byte KING = 6; // 00 110

    // 2 bits for color
    public static final byte WHITE = 8; // 01 000
    public static final byte BLACK = 16; // 10 000

    // special value
    public static final byte INVALID = -128; // 100 00 000 (no color/piece, but also not empty)

    /**
     * Gets the type of the piece.
     * @param piece the piece
     * @return the type of the piece
     */
    public static byte getType(byte piece) {
        // 7 is 00 111, so this operation zeroes out color bits and only returns type bits
        return (byte) (piece & 7);
    }

    /**
     * Gets the color of the piece.
     * @param piece the piece
     * @return the color of the piece
     */
    public static byte getColor(byte piece) {
        // 24 is 11 000, so this operation zeroes out type bits and only returns color bits
        return (byte) (piece & 24);
    }

    /**
     * Checks if a piece is a certain type.
     * e.g. ChessPiece.isType(piece, ChessPiece.Pawn)
     * @param piece the piece to check
     * @param type the type to check for
     * @return true if the piece is of the given type, false otherwise
     */
    public static boolean isType(byte piece, byte type) {
        return getType(piece) == type;
    }

    /**
     * Sets the type of a piece.
     * @param piece the piece
     * @param type the type to set
     * @return the piece with the type set
     */
    public static byte setType(byte piece, byte type) {
        return (byte) (getColor(piece) | type);
    }

    /**
     * Gets the name (String) of the piece type.
     * @param type the piece type
     * @return the name of the piece type
     */
    public static String typeToString(byte type) {
        switch (type) {
            case PAWN:
                return "Pawn";
            case KNIGHT:
                return "Knight";
            case BISHOP:
                return "Bishop";
            case ROOK:
                return "Rook";
            case QUEEN:
                return "Queen";
            case KING:
                return "King";
            case EMPTY:
                return "Empty";
            default:
                return "Invalid";
        }
    }

    /**
     * Checks if a piece is a certain color.
     * e.g. ChessPiece.isColor(piece, ChessPiece.White)
     * @param piece the piece to check
     * @param color the color to check for
     * @return true if the piece is of the given color, false otherwise
     */
    public static boolean isColor(byte piece, byte color) {
        return getColor(piece) == color;
    }

    /**
     * Checks if a piece is empty.
     * @param piece the piece to check
     * @return true if the piece is empty, false otherwise
     */
    public static boolean isEmpty(byte piece) {
        // empty can't have a color
        return piece == EMPTY;
    }

    /**
     * Checks if a piece is invalid.
     * @param piece the piece to check
     * @return true if the piece is invalid, false otherwise
     */
    public static boolean isInvalid(byte piece) {
        // invalid can't have a color
        return piece == INVALID;
    }

    /**
     * Checks if a piece is white.
     * @param piece the piece to check
     * @return true if the piece is white, false otherwise
     */
    public static boolean isWhite(byte piece) {
        return isColor(piece, WHITE);
    }

    /**
     * Checks if a potential piece is actually a piece.a
     * @param piece the piece to check
     * @return true if the piece is a piece, false otherwise
     */
    public static boolean isPiece(byte piece) {
        return !isEmpty(piece) && !isInvalid(piece);
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
        if (!(isPiece(piece) || isEmpty(piece))) {
            throw new IllegalArgumentException("Invalid piece");
        }

        byte colorLessPiece = (byte) (piece & 7);
        char fenChar = FEN_CHARS[colorLessPiece];

        // colorless pieces (types) are printed as black (lowercase)
        return isColor(piece, WHITE) ? Character.toUpperCase(fenChar) : fenChar;
    }

    /**
     * Gets the chess piece byte for the FEN character.
     * @param fenChar the FEN character
     * @return the chess piece byte for the FEN character
     */
    public static byte getPieceFromFenCharacter(char fenChar) {
        if (fenChar == '1') {
            return EMPTY;
        }

        String fenChars = new String(FEN_CHARS);
        char lowerFenChar = Character.toLowerCase(fenChar);
        byte piece = (byte) fenChars.indexOf(lowerFenChar);
        if (piece == -1) {
            throw new IllegalArgumentException("Invalid FEN character");
        }

        boolean isWhite = Character.isUpperCase(fenChar);
        return isWhite ? (byte) (piece | WHITE) : (byte) (piece | BLACK); 
    }
}