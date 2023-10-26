import engine.ChessBoard;
import engine.ChessGame;
import engine.ChessPiece;
import engine.ChessBoard.ChessMove;
import engine.ChessBoard.ChessPosition;
import engine.ChessGame.GameState;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.HashMap;

//TODO move to engine (access to 1d coords, no gui dependency)
/**
 * Chess bot that uses minimax with
 * - alpha-beta pruning 
 * - transposition table
 * - iterative deepening (allows to adhere to a time limit)
 */
public class ChessBot {
    private static long MAX_SEARCH_TIME = 1000; // ms
    private static long startTime;
    private static HashMap<ChessGame, Entry> transpoTable = new HashMap<ChessGame, Entry>();
    private static int searchDepth = 1;

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

        // iteratively search deeper until time limit reached
        // not inefficient because transposition table stores previous results
        // in fact it allows us to first search the best move from the previous search
        // which is likely to be the best move in the current search
        searchDepth = 1;
        startTime = System.currentTimeMillis();
        while (System.currentTimeMillis() - startTime < MAX_SEARCH_TIME) {
            minimax(game, searchDepth);
            System.out.println("depth %s searched in %s ms".formatted(searchDepth, System.currentTimeMillis() - startTime));
            searchDepth++;
        }

        ChessMove move = transpoTable.get(game).bestMove;
        System.out.println("best move: %s".formatted(move));

        return move;
    }

    /*
     * Entry point for minimax.
     */
    private static int minimax(ChessGame game, int depth) {
        return minimax(game, depth, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
    }

    // alpha - best score for maximizing player
    // beta - best score for minimizing player
    private static int minimax(ChessGame game, int depth, int alpha, int beta, boolean isBlackMove) {

        // don't recalculate if previously calculated at sufficient depth
        Entry tableEntry = transpoTable.getOrDefault(game, null);
        if (tableEntry != null && tableEntry.depth >= depth) {
            return tableEntry.score;
        }

        // if depth reached or game over, return heuristic value
        if (depth == 0 || game.getGameState() != GameState.ACTIVE) {
            return evaluate(game);
        }

        int bestScore = isBlackMove ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        List<ChessMove> moves = game.getLegalMoves();
        Collections.shuffle(moves);

        // first search the best move from the transposition table
        if (tableEntry != null && moves.contains(tableEntry.bestMove)) {
            if (!moves.contains(tableEntry.bestMove)) {
                throw new RuntimeException("Transposition table entry is invalid");
            }
            moves.remove(tableEntry.bestMove);
            moves.add(0, tableEntry.bestMove);
        }
        
        // this will be searched first in the future
        ChessMove bestMove = moves.get(0);

        for (ChessMove move : moves) {
            ChessGame newGame = makeMove(game, move);
            int score = minimax(newGame, depth - 1, alpha, beta, !isBlackMove);

            if (isBlackMove) {
                bestScore = Math.max(bestScore, score); // bot optimizes its score
                alpha = Math.max(alpha, score);
                if (score > bestScore) {
                    bestMove = move;
                }
            } else {
                bestScore = Math.min(bestScore, score); // opponent minimizes bot's score
                beta = Math.min(beta, score);
                if (score < bestScore) { // best from opponent's perspective not bot's
                    bestMove = move;
                }
            }
            if (beta <= alpha) {
                break; // alpha-beta pruning
            }
            if (System.currentTimeMillis() - startTime > MAX_SEARCH_TIME) {
                break; // time limit reached
            }
        }

        // update table entry
        tableEntry = new Entry(depth, bestMove, bestScore);
        // tableEntry = new Entry(depth, bestScore);
        transpoTable.put(game, tableEntry);

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
        Map<Byte, ArrayList<Integer>> whiteMaterial = board.getMaterial(true);
        Map<Byte, ArrayList<Integer>> blackMaterial = board.getMaterial(false);

        return scoreMaterial(blackMaterial, true) - scoreMaterial(whiteMaterial, false); 
    }

    /*
     * Gets the total value of one side's material.
     */
    private static int scoreMaterial(Map<Byte, ArrayList<Integer>> material, boolean isBlack) {
        int score = 0;
        for (Map.Entry<Byte, ArrayList<Integer>> entry : material.entrySet()) {
            byte type = entry.getKey();

            for (int pos : entry.getValue()) {
                score += pieceTypeValues.get(type);
                int[] positionBonuses = piecePositionBonuses.get(type);
                score += isBlack ? positionBonuses[pos] : positionBonuses[143 - pos];
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

    // POSITIONAL BONUSES

    private static int[] white2DToBlack1D(int[][] white2D) {
        // create white 1D
        int[] black1D = new int[144];
        for (int rowIdx = 0; rowIdx < 8; rowIdx++) {
            for (int colIdx = 0; colIdx < 8; colIdx++) {
                ChessPosition pos = new ChessPosition(rowIdx, colIdx);
                black1D[143 - pos.get1D()] = white2D[rowIdx][colIdx];
            }
        }
        return black1D;
    }

    // position bonus values from https://www.chessprogramming.org/Simplified_Evaluation_Function
    // these are from white's perspective and will be mapped to black's 1d indices upon init
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
        ChessPiece.Pawn, white2DToBlack1D(positionBonuses[0]),
        ChessPiece.Knight, white2DToBlack1D(positionBonuses[1]),
        ChessPiece.Bishop, white2DToBlack1D(positionBonuses[2]),
        ChessPiece.Rook, white2DToBlack1D(positionBonuses[3]),
        ChessPiece.Queen, white2DToBlack1D(positionBonuses[4]),
        ChessPiece.King, white2DToBlack1D(positionBonuses[5])
    );
    // private static Map<Byte, Integer[]> 

    /*
     * Represents a transposition table entry.
     */
    private record Entry(
        int depth,
        // NodeType nodeType,
        ChessMove bestMove,
        int score
        // boolean old
    ) {};

    // private enum NodeType {
    //     EXACT,
    //     LOWERBOUND,
    //     UPPERBOUND
    // }
}
