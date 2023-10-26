package engine;

import java.util.Random;

import engine.ChessBoard.CastlingAvailability;

/**
 * Zobrist hashing for chess game state.
 */
public class ZobristHash {
    private final long[][][] table = new long[2][6][144];
    private final long whiteToMove;
    private final long[] castlingAvailability = new long[4]; //wk, wq, bk, bq
    private final long[] enPassantFiles = new long[8];

    /**
     * Returns the hash of the given board.
     * @param board the board
     * @return the hash of the board
     */
    public long getHash(ChessGame game) {
        ChessBoard board = game.getBoard();

        long hash = 0;

        // pieces
        for (int i = 0; i < 144; i++) {
            byte piece = board.getPiece(i);
            if (ChessPiece.isPiece(piece)) {
                hash ^= table[ChessPiece.getColor(piece)][ChessPiece.getType(piece)][i];
            }
        }

        // to move
        if (game.isWhiteMove()) {
            hash ^= whiteToMove;
        }

        // castling availability
        CastlingAvailability castling = board.getCastlingAvailability();
        if (castling.whiteKingSide()) {
            hash ^= castlingAvailability[0];
        }
        if (castling.whiteQueenSide()) {
            hash ^= castlingAvailability[1];
        }
        if (castling.blackKingSide()) {
            hash ^= castlingAvailability[2];
        }
        if (castling.blackQueenSide()) {
            hash ^= castlingAvailability[3];
        }

        // en passant
        int enPassant = board.getEnPassantTarget1D();
        if (enPassant != -1) {
            int file = enPassant % 12 - 2; // 0-7
            hash ^= enPassantFiles[file];
        }

        return hash;
    }

    public ZobristHash() {
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 6; j++) {
                for (int k = 0; k < 144; k++) {
                    table[i][j][k] = random();
                }
            }
        }

        whiteToMove = random();

        for (int i = 0; i < 4; i++) {
            castlingAvailability[i] = random();
        }

        for (int i = 0; i < 8; i++) {
            enPassantFiles[i] = random();
        }
    }

    public long random() {
        //TODO replace with something better
        Random random = new Random();
        return random.nextLong();
    }
}
