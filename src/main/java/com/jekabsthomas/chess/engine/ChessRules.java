package com.jekabsthomas.chess.engine;

import com.jekabsthomas.chess.engine.ChessBoard.CastlingAvailability;
import com.jekabsthomas.chess.engine.ChessBoard.ChessMove;
import com.jekabsthomas.chess.engine.ChessBoard.ChessPosition;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Static methods for legal move generation and checking if king is in check.
 */
public class ChessRules {
    /**
     * Checks if the enemy king can be captured in this move. Cannot happen in actual game.
     * @param board the board
     * @param isWhiteMove whether it is white's move
     * @return true if enemy king can be captured
     */
    public static boolean canCaptureKing(ChessBoard board, boolean isWhiteMove) {
        int enemyKingPos = board.getKingPos1D(!isWhiteMove);
        return isUnderAttack(board, enemyKingPos, isWhiteMove);
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

    /**
     * Checks if a board has insufficient material for checkmate.
     * @param board the board
     */
    public static boolean isInsufficientMaterial(ChessBoard board) {
        Map<Byte, Set<ChessPosition>> whiteMaterial = board.getMaterial(true);
        Map<Byte, Set<ChessPosition>> blackMaterial = board.getMaterial(false);

        return isInsufficientMaterial(whiteMaterial) && isInsufficientMaterial(blackMaterial);
    }

    /*
     * Unidirectional check for insufficient material.
     * Catches most but not all cases according to tournament rules
     * (e.g. bishops on same color are not detected)
     * But other cases are caught by the 50 move rule, so infinite loops are not possible.
     */
    private static boolean isInsufficientMaterial(Map<Byte, Set<ChessPosition>> material) {

        // if any pawn, queen, or rook, not insufficient material
        byte[] types = new byte[] {ChessPiece.Pawn, ChessPiece.Queen, ChessPiece.Rook};
        for (byte type : types) {
            if (material.get(type).size() > 0) {
                return false;
            }
        }

        int numBishops = material.get(ChessPiece.Bishop).size();
        int numKnights = material.get(ChessPiece.Knight).size();

        return numBishops + numKnights <= 1;
    }

    /*
     * Check if any enemy pieces can move to the position.
     */
    private static boolean isUnderAttack(ChessBoard board, int pos1D, boolean isWhiteMove) {
        List<ChessMove> moves = getPseudoLegalMoves(board, isWhiteMove, true);

        // check if any move is to the position
        for (ChessMove move : moves) {
            if (move.to1D == pos1D) {
                return true;
            }
        }

        return false;
    }

    /*
     * Removes moves that expose the friendly king.
     */
    private static List<ChessMove> filterExposedKing(List<ChessMove> moves, ChessBoard board,
        boolean isWhiteMove) {
        List<ChessMove> filteredMoves = new ArrayList<ChessMove>(0);

        for (ChessMove move : moves) {
            ChessBoard newBoard = new ChessBoard(board);

            // I make my move
            newBoard.makeMove(move);
            
            // if enemy cannot capture my king now, move is legal
            if (!canCaptureKing(newBoard, !isWhiteMove)) {
                filteredMoves.add(move);
            }
        }

        return filteredMoves;
    }

    /*
     * Gets valid moves from a position, not checking for exposed king.
     */
    private static List<ChessMove> getPseudoLegalMoves(ChessBoard board, boolean isWhiteMove,
        int from, boolean isCastlingRecursion) {
        byte piece = board.getPiece(from);
        
        boolean isFriendlyPiece = ChessPiece.isPiece(piece)
            && ChessPiece.isWhite(piece) == isWhiteMove;

        if (!isFriendlyPiece) {
            return Arrays.asList();
        }

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
                return getValidKingMoves(board, isWhiteMove, from, isCastlingRecursion);
            default:
                return Arrays.asList();
        }
    }

    private static List<ChessMove> getPseudoLegalMoves(ChessBoard board, boolean isWhiteMove,
        int from) {
        return getPseudoLegalMoves(board, isWhiteMove, from, false);
    }

    /*
     * Gets valid moves, not checking for exposed king.
     */
    private static List<ChessMove> getPseudoLegalMoves(ChessBoard board, boolean isWhiteMove,
        boolean isCastlingRecursion) {
        List<ChessMove> moves = new ArrayList<ChessMove>(0);

        for (int pos = 0; pos < 144; pos++) {
            byte piece = board.getPiece(pos);
            boolean isFriendlyPiece = ChessPiece.isPiece(piece)
                && ChessPiece.isWhite(piece) == isWhiteMove;

            if (isFriendlyPiece) {
                moves.addAll(getPseudoLegalMoves(board, isWhiteMove, pos, isCastlingRecursion));
            }
        }

        return moves;
    }

    private static List<ChessMove> getPseudoLegalMoves(ChessBoard board, boolean isWhiteMove) {
        return getPseudoLegalMoves(board, isWhiteMove, false);
    }

    // VALID 1D MOVE DIRECTIONS FOR EACH PIECE TYPE

    private static final int[] WHITE_PAWN_FORWARD_DIRS = new int[] {-12}; 
    private static final int[] BLACK_PAWN_FORWARD_DIRS = new int[] {12};
    private static final int[] WHITE_PAWN_DIAGONAL_DIRS = new int[] {-11, -13};
    private static final int[] BLACK_PAWN_DIAGONAL_DIRS = new int[] {11, 13}; 
    private static final int[] KNIGHT_DIRS = new int[] {-23, -25, -14, -10, 10, 14, 23, 25};
    private static final int[] BISHOP_DIRS = new int[] {-13, -11, 11, 13};
    private static final int[] ROOK_DIRS = new int[] {-12, -1, 1, 12};
    private static final int[] QUEEN_DIRS = new int[] {-13, -12, -11, -1, 1, 11, 12, 13};
    private static final int[] KING_DIRS = new int[] {-13, -12, -11, -1, 1, 11, 12, 13};

    // POSITIONS FOR CASTLING

    private static final int CASTLING_WHITE_KING_TO = new ChessPosition("g1").get1D();
    private static final int CASTLING_WHITE_KING_ROOK_FROM = new ChessPosition("h1").get1D();
    private static final int CASTLING_WHITE_KING_ROOK_TO = new ChessPosition("f1").get1D();

    private static final int CASTLING_BLACK_KING_TO = new ChessPosition("g8").get1D();
    private static final int CASTLING_BLACK_KING_ROOK_FROM = new ChessPosition("h8").get1D();
    private static final int CASTLING_BLACK_KING_ROOK_TO = new ChessPosition("f8").get1D();

    private static final int CASTLING_WHITE_QUEEN_TO = new ChessPosition("c1").get1D();
    private static final int CASTLING_WHITE_QUEEN_ROOK_FROM = new ChessPosition("a1").get1D();
    private static final int CASTLING_WHITE_QUEEN_ROOK_TO = new ChessPosition("d1").get1D();
    private static final int CASTLING_WHITE_QUEEN_BLOCKING = new ChessPosition("b1").get1D();

    private static final int CASTLING_BLACK_QUEEN_TO = new ChessPosition("c8").get1D();
    private static final int CASTLING_BLACK_QUEEN_ROOK_FROM = new ChessPosition("a8").get1D();
    private static final int CASTLING_BLACK_QUEEN_ROOK_TO = new ChessPosition("d8").get1D();
    private static final int CASTLING_BLACK_QUEEN_BLOCKING =  new ChessPosition("b8").get1D();

    // METHODS FOR EACH PIECE TYPE

    private static List<ChessMove> getValidPawnMoves(ChessBoard board, boolean isWhiteMove,
        int from) {
        int[] forwardDirs = isWhiteMove ? WHITE_PAWN_FORWARD_DIRS : BLACK_PAWN_FORWARD_DIRS;
        int[] diagDirs = isWhiteMove ? WHITE_PAWN_DIAGONAL_DIRS : BLACK_PAWN_DIAGONAL_DIRS;

        // forward non-capture
        List<ChessMove> moves = getValidNonSlidingMoves(board, isWhiteMove, from, forwardDirs, true,
            false);

        // diagonal capture
        moves.addAll(getValidNonSlidingMoves(board, isWhiteMove, from, diagDirs, false, true));

        // replace with promotion moves if at end row
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

        int enPassantTarget1D = board.getEnPassantTarget1D();
        // en passant
        if (enPassantTarget1D != -1) {

            // 2 options for forward diagonal difference
            int diffA = isWhiteMove ? -13 : 13;
            int diffB = isWhiteMove ? -11 : 11;

            if (from + diffA == enPassantTarget1D || from + diffB == enPassantTarget1D) {
                int to = enPassantTarget1D;
                int captuedPawn = to - forward; // 1 behind en passant target
                moves.add(board.new EnPassantMove(from, to, captuedPawn));
            }
        }

        return moves;
    }

    private static List<ChessMove> getValidKnightMoves(ChessBoard board, boolean isWhiteMove,
        int from) {
        return getValidNonSlidingMoves(board, isWhiteMove, from, KNIGHT_DIRS);
    }

    private static List<ChessMove> getValidBishopMoves(ChessBoard board, boolean isWhiteMove,
        int from) {
        return getValidSlidingMoves(board, isWhiteMove, from, BISHOP_DIRS);
    }

    private static List<ChessMove> getValidRookMoves(ChessBoard board, boolean isWhiteMove,
        int from) {
        return getValidSlidingMoves(board, isWhiteMove, from, ROOK_DIRS);
    }

    private static List<ChessMove> getValidQueenMoves(ChessBoard board, boolean isWhiteMove,
        int from) {
        return getValidSlidingMoves(board, isWhiteMove, from, QUEEN_DIRS);
    }

    private static List<ChessMove> getValidKingMoves(ChessBoard board, boolean isWhiteMove,
        int from, boolean isCastlingRecursion) {
        List<ChessMove> moves =  getValidNonSlidingMoves(board, isWhiteMove, from, KING_DIRS);

        // castling
        CastlingAvailability castlingAvailability = board.getCastlingAvailability();

        // king being in starting position is embedded in castling availability
        boolean canKingside = isWhiteMove ? castlingAvailability.whiteKingSide()
            : castlingAvailability.blackKingSide();
        boolean canQueenside = isWhiteMove ? castlingAvailability.whiteQueenSide()
            : castlingAvailability.blackQueenSide();

        int kingTo = isWhiteMove ? CASTLING_WHITE_KING_TO : CASTLING_BLACK_KING_TO;
        int kingRookFrom = isWhiteMove ? CASTLING_WHITE_KING_ROOK_FROM
            : CASTLING_BLACK_KING_ROOK_FROM;
        int kingRookTo = isWhiteMove ? CASTLING_WHITE_KING_ROOK_TO : CASTLING_BLACK_KING_ROOK_TO;

        int queenTo = isWhiteMove ? CASTLING_WHITE_QUEEN_TO : CASTLING_BLACK_QUEEN_TO;
        int queenRookFrom = isWhiteMove ? CASTLING_WHITE_QUEEN_ROOK_FROM
            : CASTLING_BLACK_QUEEN_ROOK_FROM;
        int queenRookTo = isWhiteMove ? CASTLING_WHITE_QUEEN_ROOK_TO : CASTLING_BLACK_QUEEN_ROOK_TO;
        int queenBlocking = isWhiteMove ? CASTLING_WHITE_QUEEN_BLOCKING
            : CASTLING_BLACK_QUEEN_BLOCKING;

        canKingside = canKingside
            && !isCastlingRecursion
            && ChessPiece.isEmpty(board.getPiece(kingTo))
            && ChessPiece.isEmpty(board.getPiece(kingRookTo))
            && !isUnderAttack(board, kingRookTo, !isWhiteMove);

        canQueenside = canQueenside
            && !isCastlingRecursion
            && ChessPiece.isEmpty(board.getPiece(queenTo))
            && ChessPiece.isEmpty(board.getPiece(queenRookTo))
            && ChessPiece.isEmpty(board.getPiece(queenBlocking))
            && !isUnderAttack(board, queenRookTo, !isWhiteMove);

        if (canKingside) {
            moves.add(board.new CastlingMove(from, kingTo, kingRookFrom, kingRookTo));
        }

        if (canQueenside) {
            moves.add(board.new CastlingMove(from, queenTo, queenRookFrom, queenRookTo));
        }

        return moves;
    }

    // END OF METHODS FOR EACH PIECE TYPE

    private static List<ChessMove> getValidSlidingMoves(ChessBoard board, boolean isWhiteMove,
        int from, int[] directions) {
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
                    if (ChessPiece.isPiece(piece) && (ChessPiece.isWhite(piece) != isWhiteMove)) {
                        moves.add(board.new ChessMove(from, to));
                    }
                    // continue until blocked by piece or edge of board
                    break;
                }
                to += dir;
            }
        }

        return moves;
    }

    // by default allow both moves to empty and capture since the only exception is pawn
    private static List<ChessMove> getValidNonSlidingMoves(ChessBoard board, boolean isWhiteMove,
        int from, int[] directions) {
        return getValidNonSlidingMoves(board, isWhiteMove, from, directions, true, true);
    }

    private static List<ChessMove> getValidNonSlidingMoves(ChessBoard board, boolean isWhiteMove,
        int from, int[] directions, boolean allowEmpty, boolean allowCapture) {
        List<ChessMove> moves = new ArrayList<ChessMove>(0);

        for (int dir : directions) {
            int to = from + dir;

            if (to >= 0 && to < 144) {
                byte piece = board.getPiece(to);

                // allow empty or capture
                if ((allowEmpty && ChessPiece.isEmpty(piece))
                    || (allowCapture && ChessPiece.isPiece(piece)
                    && (ChessPiece.isWhite(piece) != isWhiteMove))) {
                    moves.add(board.new ChessMove(from, to));
                }
            }
        }

        return moves;
    }
}