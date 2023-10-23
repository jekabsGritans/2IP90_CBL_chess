package engine;

import java.util.List;
import utils.FenParser;
import engine.ChessBoard.ChessMove;

/**
 * Represents a chess game. Controls the game flow.
 */
public class ChessGame {
    private Player whitePlayer;
    private Player blackPlayer;
    private GameResult gameResult;
    private GameState gameState; // board is stored here
    private RuleEngine ruleEngine;

    /**
     * Creates a chess game from a FEN string.
     * @param whitePlayer the white player
     * @param blackPlayer the black player
     * @param fen FEN string representation of the game
     */
    public ChessGame(Player whitePlayer, Player blackPlayer, String fen) {
        this.whitePlayer = whitePlayer;
        this.blackPlayer = blackPlayer;
        this.gameResult = GameResult.ACTIVE;
        this.ruleEngine = new RuleEngine();

        // load game state from FEN string
        FenParser.FenResult result = FenParser.parseFen(fen);
        ChessBoard board = new ChessBoard(result.piecePositions);
        boolean isWhiteMove = result.activeColor.equals("w");
        CastlingRights castlingRights = new CastlingRights(result.castlingAvailability);

        ChessMove lastMove = result.enPassantTarget.equals("-")
            ? null : ruleEngine.inferEnPassantMove(board, result.enPassantTarget, isWhiteMove);

        gameState = new GameState(board, isWhiteMove, castlingRights, lastMove, null);
    }

    /**
     * Creates a chess game.
     * Initializes the board to the starting position.
     * @param whitePlayer the white player
     * @param blackPlayer the black player
     */
    public ChessGame(Player whitePlayer, Player blackPlayer) {
        this(whitePlayer, blackPlayer, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    /**
     * Plays a turn of the game and changes the current player.
     */
    private void playTurn() {
        List<ChessMove> legalMoves = ruleEngine.getLegalMoves(gameState);

        // no valid moves means checkmate or stalemate
        if (legalMoves.size() == 0) {

            // if king is in check, it is checkmate
            if (ruleEngine.isKingInCheck(gameState)) {
                gameResult = gameState.isWhiteMove() ? GameResult.BLACK_WINS : GameResult.WHITE_WINS;
            } else {
                gameResult = GameResult.STALEMATE;
            }
            return;
        }

        // trust that player chooses a valid move
        // ensure this in player implementations
        Player player = gameState.isWhiteMove() ? whitePlayer : blackPlayer;
        ChessMove move = player.chooseMove(gameState, legalMoves);

        gameState = ruleEngine.makeMove(gameState, move);
    }

    /**
     * Plays the game until it is over.
     * @return the result of the game
     */
    public GameResult play() {
        while (gameResult == GameResult.ACTIVE) {
            playTurn();
        }

        return gameResult;
    }

    /**
     * Gets the chess board.
     * @return the chess board
     */
    public ChessBoard getBoard() {
        return gameState.board();
    }
    
    /**
     * Different possible game results.
     */
    public enum GameResult {
        ACTIVE, // game is still ongoing
        WHITE_WINS,
        BLACK_WINS,
        STALEMATE,
    }

    /*
     * Represents castling rights.
     */
    public record CastlingRights(
        boolean whiteKingSide,
        boolean whiteQueenSide,
        boolean blackKingSide,
        boolean blackQueenSide
    ) {
        /**
         * Creates castling rights from a FEN string.
         * @param fen the FEN string castling rights component
         */
        public CastlingRights(String fen) {
            this(
                fen.contains("K"),
                fen.contains("Q"),
                fen.contains("k"),
                fen.contains("q")
            );
        }
    }

    /**
     * Represents a snapshot of the game.
     */
    public record GameState(
        ChessBoard board,
        boolean isWhiteMove,
        CastlingRights castlingRights,
        ChessMove lastMove,
        GameState previousState
    ) {}
}