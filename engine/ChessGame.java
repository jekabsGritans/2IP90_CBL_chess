package engine;

import java.util.HashMap;
import java.util.List;
import utils.FenParser;
import engine.ChessBoard.ChessMove;
import engine.ChessBoard.ChessPosition;

/**
 * Represents a chess game.
 */
public class ChessGame {
    private GameState state;
    private ChessBoard board;
    private boolean isWhiteMove;
    private int halfMoveClock;
    private int fullMoveNumber;

    // for threefold repetition draw
    private HashMap<String, Integer> positionCount = new HashMap<String, Integer>();

    // for debug
    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        ChessBoard board = game.getBoard();
        board.print();
        System.out.println(game.getFenString());
    }

    /**
     * Creates a chess game.
     * Initializes the board to the starting position.
     */
    public ChessGame() {
        this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    /**
     * Constructor for cloning.
     * @param game the game to clone
     */
    public ChessGame(ChessGame game) {
        this.state = game.state;
        this.board = new ChessBoard(game.board);
        this.isWhiteMove = game.isWhiteMove;
        this.halfMoveClock = game.halfMoveClock;
        this.fullMoveNumber = game.fullMoveNumber;
    }

    /**
     * Creates a chess game from a FEN string.
     * @param fen FEN string representation of the game
     */
    public ChessGame(String fenStr) {
        FenParser.FenResult fen = FenParser.parseFen(fenStr);
        state = GameState.ACTIVE;
        board = new ChessBoard(fen.piecePositions, fen.castlingAvailability, fen.enPassantTarget);
        isWhiteMove = fen.activeColor.equals("w");
        halfMoveClock = fen.halfMoveClock;
        fullMoveNumber = fen.fullMoveNumber;
        updatePositionCount();
    }

    /**
     * Gets the FEN string representation of the game.
     * @return the FEN string representation of the game
     */
    public String getFenString() {
        String piecePositions = board.getFenPiecePlacement();
        String activeColor = isWhiteMove ? "w" : "b";
        String castlingAvailability = board.getFenCastlingAvailability();
        String enPassantTarget = board.getFenEnPassantTarget();
        String halfMoveClock = String.valueOf(this.halfMoveClock);
        String fullMoveNumber = String.valueOf(this.fullMoveNumber);

        return String.join(" ", piecePositions, activeColor, castlingAvailability, enPassantTarget, halfMoveClock, fullMoveNumber);
    }

    /**
     * Gets a list of legal moves for the current player.
     * @param from the position of the piece to move
     * @return list of legal moves
     * @throws IllegalStateException if game is over
     * @throws IllegalArgumentException if from does not contain a friendly piece
     */
    public List<ChessMove> getLegalMoves(ChessPosition from) {
        if (state != GameState.ACTIVE) {
            throw new IllegalStateException("Game is over");
        }

        int from1D = from.get1D();
        byte piece = board.getPiece(from1D);
        if (ChessPiece.isEmpty(piece) ||
            ChessPiece.isWhite(piece) != isWhiteMove) {
            throw new IllegalArgumentException("No friendly piece at position " + from);
        }

        return ChessRules.getLegalMoves(board, isWhiteMove, from1D);
    }

    /**
     * Gets a list of legal moves for the current player starting from any position.
     * @return list of legal moves
     * @throws IllegalStateException if game is over
     */
    public List<ChessMove> getLegalMoves() {
        if (state != GameState.ACTIVE) {
            throw new IllegalStateException("Game is over");
        }

        return ChessRules.getLegalMoves(board, isWhiteMove);
    }

    /**
     * Makes a move and returns the new game state.
     * (Does not check if move is legal)
     * @param move the move to make
     * @return the new game state
     * @throws IllegalStateException if game is over
     */
       /**
     * Makes a move and returns the new game state.
     * (Does not check if move is legal)
     * @param move the move to make
     * @return the new game state
     * @throws IllegalStateException if game is over
     */
    public GameState makeMove(ChessMove move) {
        System.out.println(move);

        if (state != GameState.ACTIVE) {
            throw new IllegalStateException("Game is over");
        }

        byte movedPiece = board.getPiece(move.from1D);
        byte capturedPiece = board.getPiece(move.to1D);
        board.makeMove(move);

        // increment clocks, check for draw
        if (ChessPiece.isPiece(capturedPiece) || ChessPiece.isType(movedPiece, ChessPiece.Pawn)) {
            halfMoveClock = 0;
        } else {
            halfMoveClock++;
            if (halfMoveClock >= 100) {
                state = GameState.DRAW;
                return state;
            }
        }

        // check for draw by insufficient material
        if (ChessRules.isInsufficientMaterial(board)) {
            state = GameState.DRAW;
            return state;
        }

        // check for draw by threefold repetition
        updatePositionCount();
        if (isThreefoldRepetition()) {
            state = GameState.DRAW;
            return state;
        }

        if (!isWhiteMove) {
            fullMoveNumber++;
        }

        // switch turns
        isWhiteMove = !isWhiteMove;

        // check if the enemy has no legal moves
        // technically, it's now the enemy's turn
        if (getLegalMoves().size() == 0) {
            // if the enemy king can be captured by my piece, they lose
            if (ChessRules.canCaptureKing(board, !isWhiteMove)) {
                state = isWhiteMove ? GameState.BLACK_WINS : GameState.WHITE_WINS;
            } else {
                // no legal moves and not in check, this is a stalemate
                state = GameState.STALEMATE;
            }
        }

        // return the current game state
        return state;
    }

    /**
     * Gets the chess board.
     * @return the chess board
     */
    public ChessBoard getBoard() {
        return board;
    }

    /**
     * Gets the current game state.
     * @return the current game state
     */
    public GameState getGameState() {
        return state;
    }

    /**
     * Gets the full move number.
     * @return the full move number
     */
    public int getFullMoveNumber() {
        return fullMoveNumber;
    }

    /*
     * Updates the position count for threefold repetition draw.
     */
    private void updatePositionCount() {
        String boardStateHash = board.getUniqueHash();
        positionCount.put(boardStateHash, positionCount.getOrDefault(boardStateHash, 0) + 1);
    }

    /*
     * Checks if the current position has been repeated three times.
     */
    private boolean isThreefoldRepetition() {
        String boardStateHash = board.getUniqueHash();
        return positionCount.getOrDefault(boardStateHash, 0) >= 3;
    }

    
    /**
     * Represents possible game states.
     */
    public enum GameState {
        ACTIVE, // game is still ongoing
        WHITE_WINS,
        BLACK_WINS,
        STALEMATE,
        DRAW, // draw by insufficient material, 50 move rule, or threefold repetition
    }
}