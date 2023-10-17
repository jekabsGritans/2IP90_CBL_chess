package engine;

import engine.ChessMove;
import engine.board.ChessBoard;
import engine.board.ChessPiece;
import java.util.ArrayList;
import java.util.List;


/**
 * All chess rules are implemented here.
 */
public class ChessRules {
    /**
     * Gets all valid moves that a piece can make.
     * @param board the chess board
     * @param state the game state
     * @param piece the piece to move
     * @return a list of valid moves from the position
     */
    public static List<ChessMove> getValidMoves(
        ChessBoard board, ChessGame.GameState state, ChessPiece piece) {
        List<ChessMove> moves = new ArrayList<ChessMove>(0);
        //TODO
        return moves;
    }

    /**
     * Updates the game state after a move.
     * @param board the board after the move
     * @param state the game state before the move
     * @param move the latest move
     */
    public static void updateState(ChessBoard board, ChessGame.GameState state, ChessMove move) {
        updateEnPassantTarget(state, move);
        updateCastlingRights(board, state, move);

        if (isCheckmate(board, state)) {
            state.isCheckmate = true; 
        } else if (isStalemate(board, state)) {
            state.isStalemate = true;
        } else {
            state.isCheck = isCheck(board, state);
        }
    }

    /**
     * Check if the board is in check.
     */
    public static boolean isCheck(ChessBoard board, ChessGame.GameState state) {
        //TODO
        return false;
    }

    /**
     * Check if the board is in checkmate.
     */
    public static boolean isCheckmate(ChessBoard board, ChessGame.GameState state) {
        //TODO
        return false;
    }

    /**
     * Check if the board is in stalemate.
     */
    public static boolean isStalemate(ChessBoard board, ChessGame.GameState state) {
        //TODO
        return false;
    }

    /**
     * Add en passant target following double pawn move.
     */
    private static void updateEnPassantTarget(ChessGame.GameState state, ChessMove move) {
        boolean isPawn = move.movedPiece.type == ChessPiece.PieceType.PAWN;
        if (isPawn && Math.abs(move.from.y - move.to.y) == 2) {
            state.enPassantTarget = move.to;
        } else {
            state.enPassantTarget = null;
        }
    }

    /**
     * Update castling rights following king or rook move. 
     */
    private static void updateCastlingRights(
        ChessBoard board, ChessGame.GameState state, ChessMove move) {
        if (move.movedPiece.type == ChessPiece.PieceType.KING) {
            if (move.movedPiece.color == ChessPiece.PieceColor.WHITE) {
                state.canWhiteCastleKingside = false;
                state.canWhiteCastleQueenside = false;
            } else {
                state.canBlackCastleKingside = false;
                state.canBlackCastleQueenside = false;
            }
        } else if (move.movedPiece.type == ChessPiece.PieceType.ROOK) {
            if (move.movedPiece.color == ChessPiece.PieceColor.WHITE) {
                if (move.from.equals("a1")) {
                    state.canWhiteCastleQueenside = false;
                } else if (move.from.equals("h1")) {
                    state.canWhiteCastleKingside = false;
                }
            } else {
                if (move.from.equals("a8")) {
                    state.canBlackCastleQueenside = false;
                } else if (move.from.equals("h8")) {
                    state.canBlackCastleKingside = false;
                }
            }
        }
    }
}