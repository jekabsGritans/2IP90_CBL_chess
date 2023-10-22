package engine;

import engine.ChessGame.GameState;
import engine.ChessBoard.Move;
import java.util.ArrayList;
import java.util.List;

/**
 * All chess rules are implemented here.
 */
public class RuleEngine {

    //TODO filter for check

    // valid 1d move directions for each piece
    private static final int[] WHITE_PAWN_DIRS = new int[] {-1, 1, 8}; 
    private static final int[] BLACK_PAWN_DIRS = new int[] {-1, 1, -8};
    private static final int[] KNIGHT_DIRS = new int[] {-17, -15, -10, -6, 6, 10, 15, 17};
    private static final int[] BISHOP_DIRS = new int[] {-9, -7, 7, 9};
    private static final int[] ROOK_DIRS = new int[] {-8, -1, 1, 8}; // TODO add castling
    private static final int[] QUEEN_DIRS = new int[] {-9, -8, -7, -1, 1, 7, 8, 9};
    private static final int[] KING_DIRS = new int[] {-9, -8, -7, -1, 1, 7, 8, 9}; // TODO add castling, filter by check separately

    /**
     * Gets all valid moves that the current player can make.
     * @param state the game state
     * @return list of valid moves
     */
    public List<Move> getValidMoves(GameState state) {
        ChessBoard board = state.board();
        boolean isWhiteMove = state.isWhiteMove();
        List<Move> moves = new ArrayList<Move>(0);

        for (int i = 0; i < 64; i++) {
            byte piece = board.getPiece(i);

            switch (ChessPiece.getType(piece)) {
                case ChessPiece.Empty:
                    break;
                case ChessPiece.Pawn:
                    addValidPawnMoves(i, board, isWhiteMove, moves);
                    break;
                case ChessPiece.Knight:
                    addValidKnightMoves(i, board, isWhiteMove, moves);
                    break;
                case ChessPiece.Bishop:
                    addValidBishopMoves(i, board, isWhiteMove, moves);
                    break;
                case ChessPiece.Rook:
                    addValidRookMoves(i, board, isWhiteMove, moves);
                    break;
                case ChessPiece.Queen:
                    addValidQueenMoves(i, board, isWhiteMove, moves);
                    break;
                case ChessPiece.King:
                    addValidKingMoves(i, board, isWhiteMove, moves);
                    break;
                default:
                    break;
            }
        }

        return moves;
    }

    /**
     * Adds all valid pawn moves to the list.
     * @param pos 1d position of the piece
     * @param board the board
     * @param isWhite whether the piece is white
     * @param moves list of valid moves
     */
    private void addValidPawnMoves(int pos, ChessBoard board, boolean isWhite, List<Move> moves) {
        int[] dirs = isWhite ? WHITE_PAWN_DIRS : BLACK_PAWN_DIRS;
        addValidNonSlidingMoves(pos, dirs, board, isWhite, moves);

        //TODO double move

        //TODO en passant
    }

    /**
     * Adds all valid knight moves to the list.
     * @param pos 1d position of the piece
     * @param board the board
     * @param isWhite whether the piece is white
     * @param moves list of valid moves
     */
    private void addValidKnightMoves(int pos, ChessBoard board, boolean isWhite, List<Move> moves) {
        addValidNonSlidingMoves(pos, KNIGHT_DIRS, board, isWhite, moves);
    }

    /**
     * Adds all valid bishop moves to the list.
     * @param pos 1d position of the piece
     * @param board the board
     * @param isWhite whether the piece is white
     * @param moves list of valid moves
     */
    private void addValidBishopMoves(int pos, ChessBoard board, boolean isWhite, List<Move> moves) {
        addValidSlidingMoves(pos, BISHOP_DIRS, board, isWhite, moves);
    }

    /**
     * Adds all valid rook moves to the list.
     * @param pos 1d position of the piece
     * @param board the board
     * @param isWhite whether the piece is white
     * @param moves list of valid moves
     */
    private void addValidRookMoves(int pos, ChessBoard board, boolean isWhite, List<Move> moves) {
        addValidSlidingMoves(pos, ROOK_DIRS, board, isWhite, moves);
    }

    /**
     * Adds all valid queen moves to the list.
     * @param pos 1d position of the piece
     * @param board the board
     * @param isWhite whether the piece is white
     * @param moves list of valid moves
     */
    private void addValidQueenMoves(int pos, ChessBoard board, boolean isWhite, List<Move> moves) {
        addValidSlidingMoves(pos, QUEEN_DIRS, board, isWhite, moves);
    }

    /**
     * Adds all valid king moves to the list.
     * @param pos 1d position of the piece
     * @param board the board
     * @param isWhite whether the piece is white
     * @param moves list of valid moves
     */
    private void addValidKingMoves(int pos, ChessBoard board, boolean isWhite, List<Move> moves) {
        addValidNonSlidingMoves(pos, KING_DIRS, board, isWhite, moves);

        //TODO castling
    }

    /**
     * Adds all valid sliding moves for a piece.
     * @param pos 1d position of the piece
     * @param directions array of 1d directions
     * @param board the board
     * @param isWhite whether the piece is white
     * @param moves list of valid moves
     */
    private void addValidSlidingMoves(int pos, int[] directions, ChessBoard board, boolean isWhite, List<Move> moves) {
        for (int dir : directions) {
            int newPos = pos + dir;

            // while in bounds
            while (newPos >= 0 && newPos < 64) {
                byte piece = board.getPiece(newPos);

                if (piece == ChessPiece.Empty) {
                    moves.add(board.new Move(pos, newPos));
                } else {
                    if (ChessPiece.isWhite(piece) != isWhite) {
                        moves.add(board.new Move(pos, newPos));
                    }

                    // continue until blocked by piece
                    break;
                }
                newPos += dir;
            }
        }
    }

    /**
     * Adds all valid non-sliding moves for a piece.
     * @param pos 1d position of the piece
     * @param directions array of 1d directions
     * @param board the board
     * @param isWhite whether the piece is white
     * @param moves list of valid moves
     */
    private void addValidNonSlidingMoves(int pos, int[] directions, ChessBoard board, boolean isWhite, List<Move> moves) {
        for (int dir : directions) {
            int newPos = pos + dir;

            // while in bounds
            if (newPos >= 0 && newPos < 64) {
                byte piece = board.getPiece(newPos);

                if (piece == ChessPiece.Empty) {
                    moves.add(board.new Move(pos, newPos));
                } else {
                    if (ChessPiece.isWhite(piece) != isWhite) {
                        moves.add(board.new Move(pos, newPos));
                    }
                }
            }
        }
    }

    /**
     * Checks if the board is in checkmate.
     */
    public boolean isCheckmate(GameState state) {
        //TODO
        return false;
    }
}