package engine;

import engine.ChessGame.GameState;
import engine.ChessBoard.Move;
import java.util.ArrayList;
import java.util.List;

/**
 * All chess rules are implemented here.
 */
public class RuleEngine {
    /**
     * Gets all valid moves for a player.
     */
    public List<Move> getValidMoves(GameState state) {
        List<Move> moves = new ArrayList<Move>(0);
        boolean isWhite = state.isWhiteMove();
        ChessBoard board = state.board();

        for (int rowIdx = 0; rowIdx < 8; rowIdx++) {
            for (int colIdx = 0; colIdx < 8; colIdx++) {
                byte piece = board.getPiece(rowIdx, colIdx);
                ChessBoard.Position pos = new ChessBoard.Position(rowIdx, colIdx);

                // dummy moves, allow 1 step in any direction
                if (!ChessPiece.isEmpty(piece) && ChessPiece.isWhite(piece) == isWhite) {
                    // horizontal
                    if (board.checkInBounds(rowIdx, colIdx - 1)) moves.add(new Move(pos, new ChessBoard.Position(rowIdx, colIdx - 1)));
                    if (board.checkInBounds(rowIdx, colIdx + 1)) moves.add(new Move(pos, new ChessBoard.Position(rowIdx, colIdx + 1)));

                    // vertical
                    if (board.checkInBounds(rowIdx + 1, colIdx)) moves.add(new Move(pos, new ChessBoard.Position(rowIdx + 1, colIdx)));
                    if (board.checkInBounds(rowIdx - 1, colIdx)) moves.add(new Move(pos, new ChessBoard.Position(rowIdx - 1, colIdx)));

                    // diagonal
                    if (board.checkInBounds(rowIdx + 1, colIdx + 1)) moves.add(new Move(pos, new ChessBoard.Position(rowIdx + 1, colIdx + 1)));
                    if (board.checkInBounds(rowIdx - 1, colIdx - 1)) moves.add(new Move(pos, new ChessBoard.Position(rowIdx - 1, colIdx - 1)));
                    if (board.checkInBounds(rowIdx + 1, colIdx - 1)) moves.add(new Move(pos, new ChessBoard.Position(rowIdx + 1, colIdx - 1)));
                    if (board.checkInBounds(rowIdx - 1, colIdx + 1)) moves.add(new Move(pos, new ChessBoard.Position(rowIdx - 1, colIdx + 1)));
                }
            }
        }

        return moves;
    }

    /**
     * Checks if the board is in checkmate.
     */
    public boolean isCheckmate(GameState state) {
        //TODO
        return false;
    }
}