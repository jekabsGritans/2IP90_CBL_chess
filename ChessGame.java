/**
 * Represents a chess game.
 */
class ChessGame {
    ChessBoard board;

    boolean isWhiteMove;
    boolean canWhiteCastleKingside;
    boolean canWhiteCastleQueenside;
    boolean canBlackCastleKingside;
    boolean canBlackCastleQueenside;

    // number of moves both players have made since last pawn advance or piece capture.
    // used for 50 move rule - if this number reaches 100, the game is a draw.
    int numHalfMoves; 

    // number of moves white has made. just to keep track
    int numFullMoves; 

    // store unique square where en passant capture is possible, null if not possible
    ChessPosition enPassantTargetSquare;

    /**
     * Initialize a chess game from a FEN string.
     * @param fen FEN string representing the game state
     */
    public ChessGame(String fen) {
        FenParser.FenResult result = FenParser.parseFen(fen);
        this.board = new ChessBoard(result.piecePositions);
        this.isWhiteMove = result.activeColor.equals("w");
        this.canWhiteCastleKingside = result.castlingAvailability.contains("K");
        this.canWhiteCastleQueenside = result.castlingAvailability.contains("Q");
        this.canBlackCastleKingside = result.castlingAvailability.contains("k");
        this.canBlackCastleQueenside = result.castlingAvailability.contains("q");
        this.numHalfMoves = result.halfMoveClock;
        this.numFullMoves = result.fullMoveNumber;
        this.enPassantTargetSquare = result.enPassantTarget.equals("-")
            ? null : this.board.getPosition(result.enPassantTarget);
    }

    /**
     * Default constructor for ChessGame.
     * Initializes the board to the starting position.
     */
    public ChessGame() {
        this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }
}