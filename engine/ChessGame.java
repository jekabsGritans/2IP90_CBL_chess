package engine;

import engine.board.ChessBoard;
import java.util.ArrayList;
import java.util.List;
import utils.FenParser;

/**
 * Represents a chess game.
 */
public class ChessGame {
    ChessBoard board;
    List<ChessMove> moveHistory;
    GameState state;

    /**
     * Creates a chess game from a FEN string.
     * @param fen FEN string representing the game state
     */
    public ChessGame(String fen) {
        FenParser.FenResult result = FenParser.parseFen(fen);
        state = new GameState();
        state.isWhiteMove = result.activeColor.equals("w");
        this.board = new ChessBoard(result.piecePositions);
        this.moveHistory = new ArrayList<ChessMove>();
    }

    /**
     * Creates a chess game.
     * Initializes the board to the starting position.
     */
    public ChessGame() {
        this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
    }

    /**
     * Makes a move on the board. (doesn't check validity)
     * @param move the move to make
     */
    public void makeMove(ChessMove move) {
        moveHistory.add(move);
        board.movePiece(move.from, move.to);

        // handle special moves
        if (move.castlingRookFrom != null) {
            board.movePiece(move.castlingRookFrom, move.castlingRookTo);
        } else if (move.enPassantAttack != null) {
            board.removePiece(move.enPassantAttack);
        }

        ChessRules.updateState(board, state, move);
    }


    /**
     * Represents state of a chess game besides the board.
     */
    public class GameState {
        public boolean isWhiteMove;

        public boolean isCheck;
        public boolean isCheckmate;
        public boolean isStalemate;

        public boolean canWhiteCastleKingside;
        public boolean canWhiteCastleQueenside;
        public boolean canBlackCastleKingside;
        public boolean canBlackCastleQueenside;

        public ChessBoard.BoardPosition enPassantTarget;
    }
}