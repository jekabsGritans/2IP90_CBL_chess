package engine;

import java.util.List;
import engine.ChessBoard.ChessMove;
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
    public abstract ChessMove chooseMove(GameState game, List<ChessMove> validMoves);

    /**
     * Gets the player's chosen promotion piece. Called by the game engine when a pawn can be promoted.
     * Chose queen if not overridden.
     * @return the chosen promotion piece
     */
    public byte choosePromotionPiece(GameState game) {
        return ChessPiece.Queen;
    }
}
