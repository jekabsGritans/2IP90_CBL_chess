import engine.ChessBoard;
import engine.ChessGame;
import engine.ChessPiece;
import engine.ChessBoard.ChessMove;
import engine.ChessGame.GameState;

import java.util.Collections;
import java.util.Map;
import static java.util.Map.entry;
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

        // heuristic value of material
        ChessBoard board = game.getBoard();
        Map<Byte, Integer> material = board.getMaterial();

        return scoreMaterial(material);
    }

    /*
     * Gets the total value of the pieces on the board.
     */
    private static int scoreMaterial(Map<Byte, Integer> material) {
        int score = 0;
        for (Map.Entry<Byte, Integer> entry : material.entrySet()) {
            score += pieceValues.getOrDefault(entry.getKey(), 0) * entry.getValue();
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
    // black is positive, white is negative

    private static Map<Byte, Integer> pieceValues = Map.ofEntries(
        entry(ChessPiece.getPieceFromFenCharacter('p'), 100),
        entry(ChessPiece.getPieceFromFenCharacter('n'), 320),
        entry(ChessPiece.getPieceFromFenCharacter('b'), 330),
        entry(ChessPiece.getPieceFromFenCharacter('r'), 500),
        entry(ChessPiece.getPieceFromFenCharacter('q'), 900),
        entry(ChessPiece.getPieceFromFenCharacter('k'), 20000),
        entry(ChessPiece.getPieceFromFenCharacter('P'), -100),
        entry(ChessPiece.getPieceFromFenCharacter('N'), -320),
        entry(ChessPiece.getPieceFromFenCharacter('B'), -330),
        entry(ChessPiece.getPieceFromFenCharacter('R'), -500),
        entry(ChessPiece.getPieceFromFenCharacter('Q'), -900),
        entry(ChessPiece.getPieceFromFenCharacter('K'), -20000)
    );

    private static Map<GameState, Integer> gameStateValues = Map.of(
        GameState.WHITE_WINS, -100000,
        GameState.BLACK_WINS, 100000,
        GameState.STALEMATE, 0,
        GameState.DRAW, 0
    );
}
