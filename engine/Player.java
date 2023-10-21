package engine;

import java.util.List;
import engine.ChessBoard.Move;
import engine.ChessGame.GameState;

/**
 * Represents a player in a chess game.
 */
public abstract class Player {
    private final boolean isWhite; 

    /**
     * Creates a player.
     * @param isWhite whether the player is white
     */
    public Player(boolean isWhite) {
        this.isWhite = isWhite;
    }

    /**
     * Gets whether the player is white.
     * @return true if the player is white
     */
    public boolean isWhite() {
        return isWhite;
    }

    /**
     * Gets the player's move. Called by the game engine when it is the player's turn.
     * @param gameState current game state
     * @param validMoves a list of valid moves to choose from
     * @return the chosen move
     */
    public abstract Move chooseMove(GameState game, List<Move> validMoves);
}
