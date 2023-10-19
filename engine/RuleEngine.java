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