package engine;

import engine.ChessGame.GameState;
import engine.ChessBoard.Move;
import java.util.ArrayList;
import java.util.List;

/**
 * All chess rules are implemented here.
 */
public class RuleEngine {

    // valid 1d move directions for each piece
    private static final int[] WHITE_PAWN_DIRS = new int[] {-1, 1, 8}; 
    private static final int[] BLACK_PAWN_DIRS = new int[] {-1, 1, -8};
    private static final int[] KNIGHT_DIRS = new int[] {-17, -15, -10, -6, 6, 10, 15, 17};
    private static final int[] BISHOP_DIRS = new int[] {-9, -7, 7, 9};
    private static final int[] ROOK_DIRS = new int[] {-8, -1, 1, 8};
    private static final int[] QUEEN_DIRS = new int[] {-9, -8, -7, -1, 1, 7, 8, 9};
    private static final int[] KING_DIRS = new int[] {-9, -8, -7, -1, 1, 7, 8, 9};

    /**
     * Gets a list of all legal moves for the current player.
     * @param state the game state
     * @return list of legal moves
     */
    public List<Move> getLegalMoves(GameState state) {
        List<Move> moves = getPseudoLegalMoves(state);
        moves = filterExposedKing(moves, state);
        return moves;
    }

    /**
     * Checks if the king can be captured if board stays the same, but it is the other player's turn.
     * @param state the game state
     * @return true if the king can be captured, false otherwise
     */
    public boolean isKingInCheck(GameState state) {
        // copy of state with opposite color
        GameState newState = new GameState(state.board(), !state.isWhiteMove(), null, null);
        return canCaptureKing(newState);
    }

    /**
     * Makes a move on a copy of the board, returning the new game state.
     * @param state the game state
     * @param move the move to make
     * @return the new game state
     */
    public GameState previewMove(GameState state, Move move) {
        ChessBoard boardCopy = new ChessBoard(state.board());
        boolean isWhiteMove = state.isWhiteMove();

        boardCopy.makeMove(move);
        GameState newState = new GameState(boardCopy, !isWhiteMove, move, state);

        return newState;
    }

    /*
     * Removes moves that expose the king.
     */
    private List<Move> filterExposedKing(List<Move> moves, GameState state) {
        List<Move> filteredMoves = new ArrayList<Move>(0);

        for (Move move : moves) {
            GameState newState = previewMove(state, move);
            if (!canCaptureKing(newState)) {
                filteredMoves.add(move);
            }
        }

        return filteredMoves;
    }

    /*
     * Gets valid moves, not checking for exposed king.
     */
    private List<Move> getPseudoLegalMoves(GameState state) {
        ChessBoard board = state.board();
        List<Move> moves = new ArrayList<Move>(0);

        for (int i = 0; i < 64; i++) {
            byte piece = board.getPiece(i);

            switch (ChessPiece.getType(piece)) {
                case ChessPiece.Empty:
                    break;
                case ChessPiece.Pawn:
                    addValidPawnMoves(i, state, moves);
                    break;
                case ChessPiece.Knight:
                    addValidKnightMoves(i, state, moves);
                    break;
                case ChessPiece.Bishop:
                    addValidBishopMoves(i, state, moves);
                    break;
                case ChessPiece.Rook:
                    addValidRookMoves(i, state, moves);
                    break;
                case ChessPiece.Queen:
                    addValidQueenMoves(i, state, moves);
                    break;
                case ChessPiece.King:
                    addValidKingMoves(i, state, moves);
                    break;
                default:
                    break;
            }
        }

        return moves;
    }

    private void addValidPawnMoves(int pos, GameState state, List<Move> moves) {
        boolean isWhite = state.isWhiteMove();
        ChessBoard board = state.board();

        int[] dirs = isWhite ? WHITE_PAWN_DIRS : BLACK_PAWN_DIRS;
        addValidNonSlidingMoves(pos, dirs, board, isWhite, moves);

        // double move
        if (isWhite) {
            // if in white starting row
            if (pos >= 8 && pos <= 15) {
                int newPos = pos + 16;
                // and if two spaces ahead are empty
                if (
                    ChessPiece.isEmpty(board.getPiece(newPos))
                    &&
                    ChessPiece.isEmpty(board.getPiece(newPos - 8))
                    ) {
                    moves.add(board.new Move(pos, newPos));
                }
            }
        } else {
            // if in black starting row
            if (pos >= 48 && pos <= 55) {
                int newPos = pos - 16;

                // and if two spaces ahead are empty
                if (
                    ChessPiece.isEmpty(board.getPiece(newPos))
                    &&
                    ChessPiece.isEmpty(board.getPiece(newPos + 8))
                ) {
                    moves.add(board.new Move(pos, newPos));
                }
            }
        }

        // TODO en passant. need to also kill enemy pawn
    }

    private void addValidKnightMoves(int pos, GameState state, List<Move> moves) {
        boolean isWhite = state.isWhiteMove();
        ChessBoard board = state.board();
        addValidNonSlidingMoves(pos, KNIGHT_DIRS, board, isWhite, moves);
    }

    private void addValidBishopMoves(int pos, GameState state, List<Move> moves) {
        boolean isWhite = state.isWhiteMove();
        ChessBoard board = state.board();
        addValidSlidingMoves(pos, BISHOP_DIRS, board, isWhite, moves);
    }

    private void addValidRookMoves(int pos, GameState state, List<Move> moves) {
        boolean isWhite = state.isWhiteMove();
        ChessBoard board = state.board();
        addValidSlidingMoves(pos, ROOK_DIRS, board, isWhite, moves);
    }

    private void addValidQueenMoves(int pos, GameState state, List<Move> moves) {
        boolean isWhite = state.isWhiteMove();
        ChessBoard board = state.board();
        addValidSlidingMoves(pos, QUEEN_DIRS, board, isWhite, moves);
    }

    private void addValidKingMoves(int pos, GameState state, List<Move> moves) {
        boolean isWhite = state.isWhiteMove();
        ChessBoard board = state.board();
        addValidNonSlidingMoves(pos, KING_DIRS, board, isWhite, moves);

        //TODO castling. need to also move rook
    }

    /*
     * Adds moves that move multiple steps to an empty/enemy square.
     * (Checks for blocking pieces)
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

    /*
     * Adds moves that move one step to an empty/enemy square.
     */
    private void addValidNonSlidingMoves(int pos, int[] directions, ChessBoard board, boolean isWhite, List<Move> moves) {
        for (int dir : directions) {
            int newPos = pos + dir;

            // while in bounds
            if (newPos >= 0 && newPos < 64) {
                byte piece = board.getPiece(newPos);
                
                // allow move to empty
                if (ChessPiece.isEmpty(piece)) {
                    moves.add(board.new Move(pos, newPos));
                } else {
                    // allow capture enemy piece
                    if (ChessPiece.isWhite(piece) != isWhite) {
                        moves.add(board.new Move(pos, newPos));
                    }
                }
            }
        }
    }

    /*
     * Checks if the enemy king can be captured in this move. Cannot happen in actual game.
     */
    private boolean canCaptureKing(GameState state) {

        // don't need (non-pseudo) legal moves as it does not matter
        // if I expose my king if I can capture the enemy king right now
        List<Move> moves = getPseudoLegalMoves(state);

        int enemyKingPos = state.board().getKingPos(!state.isWhiteMove());

        // check if any move is to the enemy king's position
        for (Move move : moves) {
            if (move.to1D == enemyKingPos) {
                return true;
            }
        }

        return false;
    }
}