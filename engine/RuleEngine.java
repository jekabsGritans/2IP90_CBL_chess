package engine;

import engine.ChessGame.CastlingRights;
import engine.ChessGame.GameState;
import engine.ChessBoard.ChessMove;
import java.util.ArrayList;
import java.util.List;

/**
 * All chess rules are implemented here.
 */
public class RuleEngine {
    /**
     * Gets a list of all legal moves for the current player.
     * @param state the game state
     * @return list of legal moves
     */
    public List<ChessMove> getLegalMoves(GameState state) {
        List<ChessMove> moves = getPseudoLegalMoves(state);
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
        GameState newState = new GameState(
            state.board(), !state.isWhiteMove(), state.castlingRights(), null, null);

        return canCaptureKing(newState);
    }

    /**
     * Makes a move on a copy of the board, returning the new game state.
     * @param state the game state
     * @param move the move to make
     * @return the new game state
     */
    public GameState makeMove(GameState state, ChessMove move) {
        ChessBoard boardCopy = new ChessBoard(state.board());
        boolean isWhiteMove = state.isWhiteMove();
        CastlingRights castlingRights = state.castlingRights();

        boardCopy.makeMove(move);

        CastlingRights newCastlingRights = updateCastlingRights(castlingRights, move);
        GameState newState = new GameState(boardCopy, !isWhiteMove, newCastlingRights, move, state);

        return newState;
    }
    
    /**
     * Infers the last move from the en passant target square.
     * @param board the board
     * @param target the en passant target square in algebraic notation
     * @param isWhiteMove whether it is white's move
     * @return the last move
     */
    public ChessMove inferEnPassantMove(ChessBoard board, String target, boolean isWhiteMove) {
        // the position that the enemy pawn skipped
        ChessBoard.ChessPosition skipped = new ChessBoard.ChessPosition(target);
        int skipped1D = skipped.row() * 8 + skipped.col();

        // the positions that the enemy pawn moved to and from
        // note: isWhiteMove means that enemy was black
        int from = isWhiteMove ? skipped1D - 8 : skipped1D + 8; // one piece behind skipped
        int to = isWhiteMove ? skipped1D + 8 : skipped1D - 8; // one piece ahead skipped

        return board.new ChessMove(from, to);
    }

    // starting positions for castling pieces
    final static int WK_ROOK = 7;
    final static int WQ_ROOK = 0;
    final static int WK = 4;
    final static int BK_ROOK = 63;
    final static int BQ_ROOK = 56;
    final static int BK = 60;

    /*
     * Update castling rights after a move.
     */
    private CastlingRights updateCastlingRights(CastlingRights rights, ChessMove move) {
        return new CastlingRights(
            rights.whiteKingSide()
                && move.from1D != WK_ROOK && move.from1D != WK
                && move.to1D != WK_ROOK && move.to1D != WK,
            rights.whiteQueenSide()
                && move.from1D != WQ_ROOK && move.from1D != WK
                && move.to1D != WQ_ROOK && move.to1D != WK,
            rights.blackKingSide()
                && move.from1D != BK_ROOK && move.from1D != BK
                && move.to1D != BK_ROOK && move.to1D != BK,
            rights.blackQueenSide()
                && move.from1D != BQ_ROOK && move.from1D != BK
                && move.to1D != BQ_ROOK && move.to1D != BK
        );
    }

    /*
     * Removes moves that expose the king.
     */
    private List<ChessMove> filterExposedKing(List<ChessMove> moves, GameState state) {
        List<ChessMove> filteredMoves = new ArrayList<ChessMove>(0);

        for (ChessMove move : moves) {
            GameState newState = makeMove(state, move);
            if (!canCaptureKing(newState)) {
                filteredMoves.add(move);
            }
        }

        return filteredMoves;
    }

    /*
     * Gets valid moves, not checking for exposed king.
     */
    private List<ChessMove> getPseudoLegalMoves(GameState state) {
        ChessBoard board = state.board();
        List<ChessMove> moves = new ArrayList<ChessMove>(0);

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

    // VALID 1D MOVE DIRECTIONS FOR EACH PIECE TYPE

    private static final int[] WHITE_PAWN_DIRS = new int[] {-1, 1, 8}; 
    private static final int[] BLACK_PAWN_DIRS = new int[] {-1, 1, -8};
    private static final int[] KNIGHT_DIRS = new int[] {-17, -15, -10, -6, 6, 10, 15, 17};
    private static final int[] BISHOP_DIRS = new int[] {-9, -7, 7, 9};
    private static final int[] ROOK_DIRS = new int[] {-8, -1, 1, 8};
    private static final int[] QUEEN_DIRS = new int[] {-9, -8, -7, -1, 1, 7, 8, 9};
    private static final int[] KING_DIRS = new int[] {-9, -8, -7, -1, 1, 7, 8, 9};

    // METHODS FOR EACH PIECE TYPE

    private void addValidPawnMoves(int pos, GameState state, List<ChessMove> moves) {
        boolean isWhite = state.isWhiteMove();
        ChessBoard board = state.board();

        int[] dirs = isWhite ? WHITE_PAWN_DIRS : BLACK_PAWN_DIRS;
        addValidNonSlidingMoves(pos, dirs, board, isWhite, moves);

        // double move
        if (isWhite) {
            int forwardDiff = isWhite ? -8 : 8;
            int startingRow = isWhite ? 6 : 1;

            boolean clearForDoubleMove = pos / 8 == startingRow // in starting row
                && ChessPiece.isEmpty(board.getPiece(pos + forwardDiff)) // one space ahead is empty
                && ChessPiece.isEmpty(board.getPiece(pos + 2 * forwardDiff)); // two spaces ahead is empty

            if (clearForDoubleMove) {
                moves.add(board.new ChessMove(pos, pos + 2 * forwardDiff));
            }
        }

        // en passant
        ChessMove lastMove = state.lastMove();
        boolean lastMoveWasDoublePawn = lastMove != null
            && ChessPiece.isType(board.getPiece(lastMove.to1D), ChessPiece.Pawn)
            && Math.abs(lastMove.to1D - lastMove.from1D) == 16;

        if (lastMoveWasDoublePawn) {
            boolean sameRowAdjacentColumn = pos / 8 == lastMove.to1D / 8
                && Math.abs(pos - lastMove.to1D) == 1;

            if (sameRowAdjacentColumn) {
                int skippedPawnPos = (lastMove.from1D + lastMove.to1D) / 2;
                moves.add(board.new EnPassantMove(pos, skippedPawnPos, lastMove.to1D));
            }
        }

        // promotion
        int promotionRow = isWhite ? 0 : 7;
        if (pos / 8 == promotionRow) {
            moves.add(board.new PromotionMove(pos, promotionRow, ChessPiece.Pawn));
            moves.add(board.new PromotionMove(pos, promotionRow, ChessPiece.Knight));
            moves.add(board.new PromotionMove(pos, promotionRow, ChessPiece.Bishop));
            moves.add(board.new PromotionMove(pos, promotionRow, ChessPiece.Rook));
            moves.add(board.new PromotionMove(pos, promotionRow, ChessPiece.Queen));
        }
    }

    private void addValidKnightMoves(int pos, GameState state, List<ChessMove> moves) {
        boolean isWhite = state.isWhiteMove();
        ChessBoard board = state.board();
        addValidNonSlidingMoves(pos, KNIGHT_DIRS, board, isWhite, moves);
    }

    private void addValidBishopMoves(int pos, GameState state, List<ChessMove> moves) {
        boolean isWhite = state.isWhiteMove();
        ChessBoard board = state.board();
        addValidSlidingMoves(pos, BISHOP_DIRS, board, isWhite, moves);
    }

    private void addValidRookMoves(int pos, GameState state, List<ChessMove> moves) {
        boolean isWhite = state.isWhiteMove();
        ChessBoard board = state.board();
        addValidSlidingMoves(pos, ROOK_DIRS, board, isWhite, moves);
    }

    private void addValidQueenMoves(int pos, GameState state, List<ChessMove> moves) {
        boolean isWhite = state.isWhiteMove();
        ChessBoard board = state.board();
        addValidSlidingMoves(pos, QUEEN_DIRS, board, isWhite, moves);
    }

    private void addValidKingMoves(int pos, GameState state, List<ChessMove> moves) {
        boolean isWhite = state.isWhiteMove();
        ChessBoard board = state.board();
        addValidNonSlidingMoves(pos, KING_DIRS, board, isWhite, moves);

        // castling
    }

    // END OF METHODS FOR EACH PIECE TYPE

    /*
     * Adds moves that move multiple steps to an empty/enemy square.
     * (Checks for blocking pieces)
     */
    private void addValidSlidingMoves(int pos, int[] directions, ChessBoard board, boolean isWhite, List<ChessMove> moves) {
        for (int dir : directions) {
            int newPos = pos + dir;

            // while in bounds
            while (newPos >= 0 && newPos < 64) {
                byte piece = board.getPiece(newPos);

                if (piece == ChessPiece.Empty) {
                    moves.add(board.new ChessMove(pos, newPos));
                } else {
                    if (ChessPiece.isWhite(piece) != isWhite) {
                        moves.add(board.new ChessMove(pos, newPos));
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
    private void addValidNonSlidingMoves(int pos, int[] directions, ChessBoard board, boolean isWhite, List<ChessMove> moves) {
        for (int dir : directions) {
            int newPos = pos + dir;

            // while in bounds
            if (newPos >= 0 && newPos < 64) {
                byte piece = board.getPiece(newPos);
                
                // allow move to empty
                if (ChessPiece.isEmpty(piece)) {
                    moves.add(board.new ChessMove(pos, newPos));
                } else {
                    // allow capture enemy piece
                    if (ChessPiece.isWhite(piece) != isWhite) {
                        moves.add(board.new ChessMove(pos, newPos));
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
        List<ChessMove> moves = getPseudoLegalMoves(state);

        int enemyKingPos = state.board().getKingPos(!state.isWhiteMove());

        // check if any move is to the enemy king's position
        for (ChessMove move : moves) {
            if (move.to1D == enemyKingPos) {
                return true;
            }
        }

        return false;
    }
}