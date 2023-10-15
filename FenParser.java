import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides utilities for parsing FEN strings.
 */
public class FenParser {
    private static final String FEN_REGEX = "^" // beginning of string
        + "(" // begin piece placement group
            + "[rnbqkpRNBQKP1-8]+\\/"
            + "[rnbqkpRNBQKP1-8]+\\/"
            + "[rnbqkpRNBQKP1-8]+\\/"
            + "[rnbqkpRNBQKP1-8]+\\/"
            + "[rnbqkpRNBQKP1-8]+\\/"
            + "[rnbqkpRNBQKP1-8]+\\/"
            + "[rnbqkpRNBQKP1-8]+\\/"
            + "[rnbqkpRNBQKP1-8]+"
        + ")" // end piece placement group
        + "\\s([wb])" // active color
        + "\\s([KQkq]{1,4}|-)" // castling availability
        + "\\s([a-h][36]|-)" // en passant target square
        + "\\s(\\d+)" // halfmove clock
        + "\\s(\\d+)" // fullmove number
        + "$"; // end of string
    
    private static final Pattern PATTERN = Pattern.compile(FEN_REGEX);

    /**
     * Parse a FEN string.
     * @param fen FEN string
     * @return FenResult object containing components of the FEN string
     */
    public static FenResult parseFen(String fen) {
        Matcher matcher = PATTERN.matcher(fen);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Invalid FEN string");
        }

        FenResult result = new FenResult();
        result.piecePositions = matcher.group(1);
        result.activeColor = matcher.group(2);
        result.castlingAvailability = matcher.group(3);
        result.enPassantTarget = matcher.group(4);
        result.halfMoveClock = Integer.parseInt(matcher.group(5));
        result.fullMoveNumber = Integer.parseInt(matcher.group(6));

        return result;
    }

    /**
     * Stores components of a FEN string.
     */
    public static class FenResult {
        public String piecePositions;
        public String activeColor;
        public String castlingAvailability;
        public String enPassantTarget;
        public int halfMoveClock;
        public int fullMoveNumber;
    }
    
}


