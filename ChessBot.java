import engine.ChessBoard;
import engine.ChessGame;
import engine.ChessPiece;
import engine.ChessBoard.ChessMove;
import engine.ChessBoard.ChessPosition;
import engine.ChessGame.GameState;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;

/**
 * Chess bot that uses minimax with
 * - alpha-beta pruning 
 * - transposition table
 * - iterative deepening (allows to adhere to a time limit)
 */
public class ChessBot extends Thread {
    private static long MAX_SEARCH_TIME = 1000; // ms
    private static long startTime;
    private static HashMap<ChessGame, TableEntry> transpoTable = new HashMap<ChessGame, TableEntry>();
    public static ChessMove currentMove = null;
    public static ChessGame currentGame = null;

    public void run() {
        currentMove = generateMove(currentGame);
    }

    /**
     * Finds the best legal move for the current player.
     * @param game the game to search
     * @return the best legal move
     */
    public static ChessMove generateMove(ChessGame game) {

        // iteratively search deeper until time limit reached
        // not inefficient because transposition table stores previous results
        // in fact it allows us to first search the best move from the previous search
        // which is likely to be the best move in the current search
        int searchDepth = 1;
        startTime = System.currentTimeMillis();
        while (true) {
            try {
                minimax(game, searchDepth);
                System.out.println("depth %s searched in %s ms".formatted(searchDepth, System.currentTimeMillis() - startTime));
                searchDepth++;
            } catch (TimeLimitReachedException e) {
                // immediately stop searching and use last result
                searchDepth--;
                break;
            }
        }

        ChessMove move = transpoTable.get(game).bestMove;
        System.out.println("best move at depth %d: %s".formatted(searchDepth, move));

        return move;
    }

    /**
     * Entry point for minimax.
     * @param game the game to search
     * @param depth the depth to search
     */
    private static int minimax(ChessGame game, int depth) {
        return minimax(game, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
    }

    /**
     * Recursive minimax function.
     * @param game the game to search
     * @param depth the remaining depth to search
     * @param alpha the alpha value (best guaranteed value for maximizer)
     * @param beta the beta value (best guaranteed value for minimizer)
     * @param isMaximizer whether the current node is a maximizer
     * @return the heuristic value of the game
     */
    private static int minimax(ChessGame game, int depth, int alpha, int beta, boolean isMaximizer) {
        // exit search if time limit reached
        if (System.currentTimeMillis() - startTime > MAX_SEARCH_TIME) {
            throw new TimeLimitReachedException();
        }

        // don't recalculate if previously calculated at sufficient depth
        TableEntry entry = transpoTable.getOrDefault(game, null);
        if (entry != null && entry.depth >= depth) {
            return entry.score;
        }
        
        // if depth reached or game over, return heuristic value from maximizer's perspective
        if (depth == 0 || game.getGameState() != GameState.ACTIVE) {
            boolean isWhiteMaximizer = game.isWhiteMove() == isMaximizer;
            return evaluate(game, isWhiteMaximizer);
        }

        List<ChessMove> moves = game.getLegalMoves();
        Collections.shuffle(moves);

        // first search the best move from lower depth search
        // this move might not be the best but it's a good first guess for pruning
        if (entry != null) {
            moves.remove(entry.bestMove);
            moves.add(0, entry.bestMove);
        }
        
        int bestScore = isMaximizer ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        ChessMove bestMove = null;

        for (ChessMove move : moves) {

            ChessGame newGame = makeMove(game, move);
            int score = minimax(newGame, depth - 1, alpha, beta, !isMaximizer);

            if (isMaximizer) {
                if (score > bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
                // maximizer is guaranteed at least this score
                alpha = Math.max(alpha, bestScore); 
            } else {
                if (score < bestScore) {
                    bestScore = score;
                    bestMove = move;
                }
                // minimizer is guaranteed at most this score
                beta = Math.min(beta, bestScore); 
            }
            // if, on this branch, minimizer can guarantee a score
            // lower than what maximizer can already guarantee on a different branch
            // then this branch should be pruned
            if (beta <= alpha) {
                break; 
            }
        }

        // update table entry
        entry = new TableEntry(depth, bestMove, bestScore);
        transpoTable.put(game, entry);

        return bestScore;
    }

    /*
     * Gets the heuristic value of the game from the perspective of one player.
     */
    private static int evaluate(ChessGame game, boolean isWhitePerspective) {
        // heuristic value of end game state
        GameState state = game.getGameState();
        if (state != GameState.ACTIVE) {
            return isWhitePerspective ? gameStateValues.get(state) : -gameStateValues.get(state);
        }

        // heuristic value of material
        ChessBoard board = game.getBoard();
        Map<Byte, Set<Integer>> whiteMaterial = board.getMaterial(true);
        Map<Byte, Set<Integer>> blackMaterial = board.getMaterial(false);
        int materialScore = scoreMaterial(whiteMaterial, true) - scoreMaterial(blackMaterial, false);

        return isWhitePerspective ? materialScore : -materialScore;
    }

    /*
     * Gets the total value of one side's material.
     */
    private static int scoreMaterial(Map<Byte, Set<Integer>> material, boolean isWhiteMaterial) {
        int score = 0;
        for (Map.Entry<Byte, Set<Integer>> entry : material.entrySet()) {
            byte pieceType = entry.getKey();

            for (int pos : entry.getValue()) {
                score += pieceTypeValues.get(pieceType);
                int[] positionBonuses = piecePositionBonuses.get(pieceType);

                // mirror positions for black
                score += isWhiteMaterial ? positionBonuses[pos] : positionBonuses[143 - pos];
            }
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
    // from https://www.chessprogramming.org/Simplified_Evaluation_Function

    private static Map<Byte, Integer> pieceTypeValues = Map.of(
        ChessPiece.Pawn , 100,
        ChessPiece.Knight , 320,
        ChessPiece.Bishop , 330,
        ChessPiece.Rook , 500,
        ChessPiece.Queen , 900,
        ChessPiece.King , 20000
    );

    private static Map<GameState, Integer> gameStateValues = Map.of(
        GameState.WHITE_WINS, 100000,
        GameState.BLACK_WINS, -100000,
        GameState.STALEMATE, 0,
        GameState.DRAW, 0
    );

    /*
     * Maps position values from a 2D board representation to 1D.
     */
    private static int[] board2DTo1D(int[][] board2D) {
        int[] board1D = new int[144];
        for (int rowIdx = 0; rowIdx < 8; rowIdx++) {
            for (int colIdx = 0; colIdx < 8; colIdx++) {
                // find 1D position
                ChessPosition pos = new ChessPosition(rowIdx, colIdx);
                board1D[pos.get1D()] = board2D[rowIdx][colIdx];
            }
        }
        return board1D;
    }

    // incentivize optimal piece positioning
    // these are from white's perspective (flipped for black)
    private static int[][][] positionBonuses = {
        { // Pawn
            {0,  0,  0,  0,  0,  0,  0,  0},
            {50, 50, 50, 50, 50, 50, 50, 50},
            {10, 10, 20, 30, 30, 20, 10, 10},
            {5,  5, 10, 25, 25, 10,  5,  5},
            {0,  0,  0, 20, 20,  0,  0,  0},
            {5, -5,-10,  0,  0,-10, -5,  5},
            {5, 10, 10,-20,-20, 10, 10,  5},
            {0,  0,  0,  0,  0,  0,  0,  0},
        },
        { // Knight
            {-50,-40,-30,-30,-30,-30,-40,-50},
            {-40,-20,  0,  0,  0,  0,-20,-40},
            {-30,  0, 10, 15, 15, 10,  0,-30},
            {-30,  5, 15, 20, 20, 15,  5,-30},
            {-30,  0, 15, 20, 20, 15,  0,-30},
            {-30,  5, 10, 15, 15, 10,  5,-30},
            {-40,-20,  0,  5,  5,  0,-20,-40},
            {-50,-40,-30,-30,-30,-30,-40,-50},
        },
        { // Bishop
            {-20,-10,-10,-10,-10,-10,-10,-20},
            {-10,  0,  0,  0,  0,  0,  0,-10},
            {-10,  0,  5, 10, 10,  5,  0,-10},
            {-10,  5,  5, 10, 10,  5,  5,-10},
            {-10,  0, 10, 10, 10, 10,  0,-10},
            {-10, 10, 10, 10, 10, 10, 10,-10},
            {-10,  5,  0,  0,  0,  0,  5,-10},
            {-20,-10,-10,-10,-10,-10,-10,-20},
        },
        { // Rook
            {0,  0,  0,  0,  0,  0,  0,  0},
            {5, 10, 10, 10, 10, 10, 10,  5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {-5,  0,  0,  0,  0,  0,  0, -5},
            {0,  0,  0,  5,  5,  0,  0,  0},
        },
        { // Queen
            {-20,-10,-10, -5, -5,-10,-10,-20},
            {-10,  0,  0,  0,  0,  0,  0,-10},
            {-10,  0,  5,  5,  5,  5,  0,-10},
            {-5,  0,  5,  5,  5,  5,  0, -5},
            {0,  0,  5,  5,  5,  5,  0, -5},
            {-10,  5,  5,  5,  5,  5,  0,-10},
            {-10,  0,  5,  0,  0,  0,  0,-10},
            {-20,-10,-10, -5, -5,-10,-10,-20},
        },
        { // King
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-30,-40,-40,-50,-50,-40,-40,-30},
            {-20,-30,-30,-40,-40,-30,-30,-20},
            {-10,-20,-20,-20,-20,-20,-20,-10},
            {20, 20,  0,  0,  0,  0, 20, 20},
            {20, 30, 10,  0,  0, 10, 30, 20},
        }
    };

    private static Map<Byte, int[]> piecePositionBonuses = Map.of(
        ChessPiece.Pawn, board2DTo1D(positionBonuses[0]),
        ChessPiece.Knight, board2DTo1D(positionBonuses[1]),
        ChessPiece.Bishop, board2DTo1D(positionBonuses[2]),
        ChessPiece.Rook, board2DTo1D(positionBonuses[3]),
        ChessPiece.Queen, board2DTo1D(positionBonuses[4]),
        ChessPiece.King, board2DTo1D(positionBonuses[5])
    );

    /*
     * Represents a transposition table entry.
     */
    private record TableEntry(
        int depth,
        ChessMove bestMove,
        int score
    ) {};

    /*
     * Exception thrown when time limit reached.
     */
    private static class TimeLimitReachedException extends RuntimeException {
        public TimeLimitReachedException() {
            super();
        }
    }
}
