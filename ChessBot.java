import engine.ChessBoard;
import engine.ChessGame;
import engine.ChessPiece;
import engine.ChessBoard.ChessMove;
import engine.ChessGame.GameState;

import java.util.Collections;
import java.util.Map;
import java.util.List;

public class ChessBot {

    private static int SEARCH_DEPTH = 2;

    // debug - bot controls both sides, so should almost always win
    public static void main(String[] args) {
        ChessGame game = new ChessGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR b KQkq - 0 1");
  
        while (game.getGameState() == GameState.ACTIVE) {
            ChessMove move = generateMove(game);
            game.makeMove(move);
            game.getBoard().print();
        }
        System.out.println(game.getGameState());
    }

    public static ChessMove generateMove(ChessGame game) {
        List<ChessMove> moves = game.getLegalMoves();

        // so that equal scoring moves are chosen randomly
        Collections.shuffle(moves);

        ChessMove bestMove = moves.get(0);
        int bestScore = Integer.MIN_VALUE;

        // estimate score for each move, and choose the best one
        for (ChessMove move : moves) {
            ChessGame newGame = new ChessGame(game);
            newGame.makeMove(move);
            int score = minimax(newGame, SEARCH_DEPTH);
            if (score > bestScore) {
                bestScore = score;
                bestMove = move;
            }
        }

        return bestMove;
    }

    private static int minimax(ChessGame game, int depth) {
        return minimax(game, depth, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    // alpha - best score for maximizing player
    // beta - best score for minimizing player
    private static int minimax(ChessGame game, int depth, int alpha, int beta) {

        // if depth reached or game over, return heuristic value
        if (depth == 0 || game.getGameState() != GameState.ACTIVE) {
            return evaluate(game);
        }
        
        // alternating moves, bot (black) is called first with depth = SEARCH_DEPTH
        boolean isBlackMove = (SEARCH_DEPTH - depth) % 2 == 0;

        int bestScore = isBlackMove ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        List<ChessMove> moves = game.getLegalMoves();

        for (ChessMove move : moves) {
            ChessGame newGame = makeMove(game, move);
            int score = minimax(newGame, depth - 1, alpha, beta);
            if (isBlackMove) {
                bestScore = Math.max(bestScore, score); // bot optimizes its score
                alpha = Math.max(alpha, score);
            } else {
                bestScore = Math.min(bestScore, score); // opponent minimizes bot's score
                beta = Math.min(beta, score);
            }
            if (beta <= alpha) {
                break; // alpha-beta pruning
            }
        }

        return bestScore;
    }

    /*
     * Gets the heuristic value of the game state from white's perspective.
     */
    private static int evaluate(ChessGame game) {
        GameState state = game.getGameState();

        // heuristic value of end game state
        if (state != GameState.ACTIVE) {
            return gameStateValues.get(state);
        }

        // heuristic value of material from black's perspective
        ChessBoard board = game.getBoard();
        Map<Byte, Integer> whiteMaterial = board.getMaterial(true);
        Map<Byte, Integer> blackMaterial = board.getMaterial(false);

        return scoreMaterial(blackMaterial) - scoreMaterial(whiteMaterial); 
    }

    /*
     * Gets the total value of one side's material.
     */
    private static int scoreMaterial(Map<Byte, Integer> material) {
        int score = 0;
        for (Map.Entry<Byte, Integer> entry : material.entrySet()) {
            score += pieceTypeValues.getOrDefault(entry.getKey(), 0) * entry.getValue();
        }
        return score;
    }

    /*
     * Makes a move on a copy of the game and returns the resulting game.
     */
    private static ChessGame makeMove(ChessGame game, ChessMove move) {
        ChessGame newGame = new ChessGame(game);
        newGame.makeMove(move);
        return newGame;
    }

    // FIXED HEURISTIC VALUES

    private static Map<Byte, Integer> pieceTypeValues = Map.of(
        ChessPiece.Pawn , 100,
        ChessPiece.Knight , 320,
        ChessPiece.Bishop , 330,
        ChessPiece.Rook , 500,
        ChessPiece.Queen , 900,
        ChessPiece.King , 9000
    );

    private static Map<GameState, Integer> gameStateValues = Map.of(
        GameState.WHITE_WINS, -100000,
        GameState.BLACK_WINS, 100000,
        GameState.STALEMATE, 0,
        GameState.DRAW, 0
    );
}
