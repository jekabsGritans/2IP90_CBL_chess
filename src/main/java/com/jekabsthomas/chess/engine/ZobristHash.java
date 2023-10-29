package com.jekabsthomas.chess.engine;

import com.jekabsthomas.chess.engine.ChessBoard.CastlingAvailability;
import java.util.Random;

/**
 * Zobrist hashing for chess game state.
 * Used for transposition table in the bot.
 * A Zobrist hash is constructed by xor-ing random bitstrings
 * that correspond to elements of the chess game state.
 */
public class ZobristHash {
    private final long[][][] table = new long[2][6][144];
    private final long whiteToMove;
    private final long[] castlingAvailability = new long[4]; //wk, wq, bk, bq
    private final long[] enPassantFiles = new long[8];

    /**
     * Returns the hash of the given board.
     * @param game the game
     * @return the hash of the board
     */
    public long getHash(ChessGame game) {
        ChessBoard board = game.getBoard();

        long hash = 0;

        // pieces
        for (int i = 0; i < 144; i++) {
            byte piece = board.getPiece(i);
            if (ChessPiece.isPiece(piece)) {
                int colorIndex = ChessPiece.isWhite(piece) ? 0 : 1;
                int typeIndex = ChessPiece.getType(piece) - 1; // depends on byte values
                hash ^= table[colorIndex][typeIndex][i]; 
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

    /**
     * Constructs a new Zobrist hash table.
     */
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

    /**
     * Generates a pseudo-random long.
     * @return a pseudo-random long
     */
    public long random() {
        Random random = new Random();
        return random.nextLong();
    }
}
