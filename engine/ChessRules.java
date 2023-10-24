package engine;

import engine.ChessBoard.CastlingRights;
import engine.ChessBoard.ChessMove;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Static methods for legal moves and checking if king is in check.
 */
public class ChessRules {
    /**
     * Checks if the enemy king can be captured in this move. Cannot happen in actual game.
     * @param board the board
     * @param isWhiteMove whether it is white's move
     * @return true if enemy king can be captured
     */
    public static boolean canCaptureKing(ChessBoard board, boolean isWhiteMove) {

        // pseudo-legal is fine as it does not matter
        // if I expose my king if I can first capture the enemy king
        List<ChessMove> moves = getPseudoLegalMoves(board, isWhiteMove);

        int enemyKingPos = isWhiteMove ? board.blackKingPos1D : board.whiteKingPos1D;

        // check if any move is to the enemy king's position
        for (ChessMove move : moves) {
            if (move.to1D == enemyKingPos) {
                return true;
            }
        }

        return false;
    }

    /**
     * Gets a list of all legal moves for the current player.
     * @param board the board
     * @param isWhiteMove whether it is white's move
     * @param from the position of the piece to move
     * @return list of legal moves
     */
    public static List<ChessMove> getLegalMoves(ChessBoard board, boolean isWhiteMove, int from) {
        List<ChessMove> moves = getPseudoLegalMoves(board, isWhiteMove, from);
        moves = filterExposedKing(moves, board, isWhiteMove);
        return moves;
    }

    /**
     * Gets a list of all legal moves for the current player.
     * @param board the board
     * @param isWhiteMove whether it is white's move
     * @return list of legal moves
     */
    public static List<ChessMove> getLegalMoves(ChessBoard board, boolean isWhiteMove) {
        List<ChessMove> moves = getPseudoLegalMoves(board, isWhiteMove);
        moves = filterExposedKing(moves, board, isWhiteMove);
        return moves;
    }
    
    /*
     * Removes moves that expose the friendly king.
     */
    private static List<ChessMove> filterExposedKing(List<ChessMove> moves, ChessBoard board, boolean isWhiteMove) {
        List<ChessMove> filteredMoves = new ArrayList<ChessMove>(0);

        for (ChessMove move : moves) {
            ChessBoard newBoard = new ChessBoard(board);
            newBoard.makeMove(move);
            
            // if enemy can capture my king
            if (!canCaptureKing(board, !isWhiteMove)) {
                filteredMoves.add(move);
            }
        }
        return filteredMoves;
    }

    /*
     * Gets valid moves from a position, not checking for exposed king.
     */
    private static List<ChessMove> getPseudoLegalMoves(ChessBoard board, boolean isWhiteMove, int from) {
        byte piece = board.getPiece(from);
            switch (ChessPiece.getType(piece)) {
                case ChessPiece.Pawn:
                    return getValidPawnMoves(board, isWhiteMove, from);
                case ChessPiece.Knight:
                    return getValidKnightMoves(board, isWhiteMove, from);
                case ChessPiece.Bishop:
                    return getValidBishopMoves(board, isWhiteMove, from);
                case ChessPiece.Rook:
                    return getValidRookMoves(board, isWhiteMove, from);
                case ChessPiece.Queen:
                    return getValidQueenMoves(board, isWhiteMove, from);
                case ChessPiece.King:
                    return getValidKingMoves(board, isWhiteMove, from);
                default:
                    return Arrays.asList();
        }
    }

    /*
     * Gets valid moves, not checking for exposed king.
     */
    private static List<ChessMove> getPseudoLegalMoves(ChessBoard board, boolean isWhiteMove) {
        List<ChessMove> moves = new ArrayList<ChessMove>(0);

        for (int pos = 0; pos < 144; pos++) {
            byte piece = board.getPiece(pos);
            if (piece != ChessPiece.Invalid && ChessPiece.isWhite(piece) == isWhiteMove) {
                moves.addAll(getPseudoLegalMoves(board, isWhiteMove, pos));
            }
        }

        return moves;
    }

    // VALID 1D MOVE DIRECTIONS FOR EACH PIECE TYPE

    private static final int[] WHITE_PAWN_DIRS = new int[] {-12}; 
    private static final int[] BLACK_PAWN_DIRS = new int[] {12};
    private static final int[] KNIGHT_DIRS = new int[] {-23, -25, -14, -10, 10, 14, 23, 25};
    private static final int[] BISHOP_DIRS = new int[] {-13, -11, 11, 13};
    private static final int[] ROOK_DIRS = new int[] {-12, -1, 1, 12};
    private static final int[] QUEEN_DIRS = new int[] {-13, -12, -11, -1, 1, 11, 12, 13};
    private static final int[] KING_DIRS = new int[] {-13, -12, -11, -1, 1, 11, 12, 13};

    // METHODS FOR EACH PIECE TYPE

    private static List<ChessMove> getValidPawnMoves(ChessBoard board, boolean isWhiteMove, int from) {
        int[] dirs = isWhiteMove ? WHITE_PAWN_DIRS : BLACK_PAWN_DIRS;

        List<ChessMove> moves = getValidNonSlidingMoves(board, isWhiteMove, from, dirs, false);

        // diagonal capture TODO make neater
        int[] diagDirs = isWhiteMove ? new int[] {-11, -13} : new int[] {11, 13};
        for (int dir : diagDirs) {
            int to = from + dir;
            if (to >= 0 && to < 144) {
                byte piece = board.getPiece(to);
                if (ChessPiece.isPiece(piece) && ChessPiece.isWhite(piece) != isWhiteMove) {
                    moves.add(board.new ChessMove(from, to));
                }
            }
        }

        // replace with promotion moves if at end
        List<ChessMove> newMoves = new ArrayList<ChessMove>(0);
        for (ChessMove move : moves) {
            // if at any end
            if (move.to1D / 12 == 2 || move.to1D / 12 == 9) {
                newMoves.add(board.new PromotionMove(move.from1D, move.to1D, ChessPiece.Queen));
                newMoves.add(board.new PromotionMove(move.from1D, move.to1D, ChessPiece.Rook));
                newMoves.add(board.new PromotionMove(move.from1D, move.to1D, ChessPiece.Bishop));
                newMoves.add(board.new PromotionMove(move.from1D, move.to1D, ChessPiece.Knight));
            } else {
                newMoves.add(move);
            }
        }
        moves = newMoves;

        // double move
        int forward = isWhiteMove ? -12 : 12;
        int startingRow = isWhiteMove ? 8 : 3;

        boolean clearForDoubleMove = from / 12 == startingRow // in starting row
            && ChessPiece.isEmpty(board.getPiece(from + forward)) // one space ahead is empty
            && ChessPiece.isEmpty(board.getPiece(from + 2 * forward)); // two spaces ahead is empty

        if (clearForDoubleMove) {
            int enPassantTarget = from + forward;
            int to = from + 2 * forward;
            moves.add(board.new PawnDoubleMove(from, to, enPassantTarget));
        }

        // en passant
        if (board.enPassantTarget1D != -1) {

            // 2 options for forward diagonal difference
            int diffA = isWhiteMove ? -13 : 13;
            int diffB = isWhiteMove ? -11 : 11;

            if (from + diffA == board.enPassantTarget1D || from + diffB == board.enPassantTarget1D) {
                int to = board.enPassantTarget1D;
                int captuedPawn = to - forward; // 1 behind en passant target
                moves.add(board.new EnPassantMove(from, to, captuedPawn));
            }
        }

        return moves;
    }

    private static List<ChessMove> getValidKnightMoves(ChessBoard board, boolean isWhiteMove, int from) {
        return getValidNonSlidingMoves(board, isWhiteMove, from, KNIGHT_DIRS);
    }

    private static List<ChessMove> getValidBishopMoves(ChessBoard board, boolean isWhiteMove, int from) {
        return getValidSlidingMoves(board, isWhiteMove, from, BISHOP_DIRS);
    }

    private static List<ChessMove> getValidRookMoves(ChessBoard board, boolean isWhiteMove, int from) {
        return getValidSlidingMoves(board, isWhiteMove, from, ROOK_DIRS);
    }

    private static List<ChessMove> getValidQueenMoves(ChessBoard board, boolean isWhiteMove, int from) {
        return getValidSlidingMoves(board, isWhiteMove, from, QUEEN_DIRS);
    }

    private static List<ChessMove> getValidKingMoves(ChessBoard board, boolean isWhiteMove, int from) {
        List<ChessMove> moves =  getValidNonSlidingMoves(board, isWhiteMove, from, KING_DIRS);

        // castling TODO check constants
        CastlingRights castlingRights = board.castlingRights;
        if (isWhiteMove) {
            boolean canKingside = castlingRights.whiteKingSide()
                && ChessPiece.isEmpty(board.getPiece(87))
                && ChessPiece.isEmpty(board.getPiece(88));
            if (canKingside) {  
                moves.add(board.new CastlingMove(from, 88, 89, 87));
            }

            boolean canQueenside = castlingRights.whiteQueenSide()
                && ChessPiece.isEmpty(board.getPiece(85))
                && ChessPiece.isEmpty(board.getPiece(84))
                && ChessPiece.isEmpty(board.getPiece(83));
            if (canQueenside) {
                moves.add(board.new CastlingMove(from, 84, 82, 85));
            }

        } else {
            boolean canKingside = castlingRights.blackKingSide()
                && ChessPiece.isEmpty(board.getPiece(31))
                && ChessPiece.isEmpty(board.getPiece(32));
            if (canKingside) {
                moves.add(board.new CastlingMove(from, 31, 33, 32));
            }

            boolean canQueenside = castlingRights.blackQueenSide()
                && ChessPiece.isEmpty(board.getPiece(29))
                && ChessPiece.isEmpty(board.getPiece(28))
                && ChessPiece.isEmpty(board.getPiece(27));
            if (canQueenside) {
                moves.add(board.new CastlingMove(from, 28, 26,29));
            }
        }

        return moves;
    }

    // END OF METHODS FOR EACH PIECE TYPE

    private static List<ChessMove> getValidSlidingMoves(ChessBoard board, boolean isWhiteMove, int from, int[] directions) {
        List<ChessMove> moves = new ArrayList<ChessMove>(0);

        for (int dir : directions) {
            int to = from + dir;

            while (to >= 0 && to < 144) {
                byte piece = board.getPiece(to);

                // allow move to empty
                if (ChessPiece.isEmpty(piece)) {
                    moves.add(board.new ChessMove(from, to));
                } else {
                    // allow capture enemy piece
                    if (!ChessPiece.isInvalid(piece) && ChessPiece.isWhite(piece) != isWhiteMove) {
                        moves.add(board.new ChessMove(from, to));
                    }
                    // continue until blocked by piece
                    break;
                }
                to += dir;
            }
        }

        return moves;
    }


    // allowCapture is true by default, only false for pawn forward moves
    private static List<ChessMove> getValidNonSlidingMoves(ChessBoard board, boolean isWhiteMove, int from, int[] directions) {
        return getValidNonSlidingMoves(board, isWhiteMove, from, directions, true);
    }

    private static List<ChessMove> getValidNonSlidingMoves(ChessBoard board, boolean isWhiteMove, int from, int[] directions, boolean allowCapture) {
        List<ChessMove> moves = new ArrayList<ChessMove>(0);

        for (int dir : directions) {
            int to = from + dir;

            if (to >= 0 && to < 144) {
                byte piece = board.getPiece(to);
                
                // allow move to empty or capture enemy piece
                if (!ChessPiece.isInvalid(piece) && (ChessPiece.isEmpty(piece) || (allowCapture && ChessPiece.isWhite(piece) != isWhiteMove))) {
                    moves.add(board.new ChessMove(from, to));
                }
            }
        }

        return moves;
    }
}