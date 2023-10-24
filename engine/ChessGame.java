package engine;

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

    // for debug
    public static void main(String[] args) {
        ChessGame game = new ChessGame();
        ChessBoard board = game.getBoard();
        board.print();

        ChessPosition from = new ChessPosition("g1");


        List<ChessMove> moves = game.getLegalMoves(from);
        System.out.println(moves.size() + " legal moves from " + from);

        ChessMove move = moves.get(1);
        game.makeMove(move);
        System.out.println(move.from1D - move.to1D);

        board.print();
    }

    /**
     * Creates a chess game.
     * Initializes the board to the starting position.
     */
    public ChessGame() {
        this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
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
    public GameState makeMove(ChessMove move) {
        System.out.println(move);

        if (state != GameState.ACTIVE) {
            throw new IllegalStateException("Game is over");
        }

        board.makeMove(move);
        isWhiteMove = !isWhiteMove;

        // if enemy can't make a move, game is over
        if (getLegalMoves().size() == 0) {

            // if I can capture enemy king, I win
            if (ChessRules.canCaptureKing(board, !isWhiteMove)) {
                state = isWhiteMove ? GameState.WHITE_WINS : GameState.BLACK_WINS;
            } else {
                state = GameState.STALEMATE;
            }
        }

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
     * Represents possible game states.
     */
    public enum GameState {
        ACTIVE, // game is still ongoing
        WHITE_WINS,
        BLACK_WINS,
        STALEMATE,
    }
}