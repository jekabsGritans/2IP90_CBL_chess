package engine;

import java.util.HashMap;

/**
 * Represents the board of a chess game.
 * Handles position indexing, printing, initialization from a FEN string.
 */
public class ChessBoard {
    // pieces stored as bytes (see ChessPiece.java)
    // 1D array for easier offsets. 1D coordinates are never exposed outside of engine
    private final byte[] board1D; 
    private CastlingAvailability CastlingAvailability;
    private int whiteKingPos1D;
    private int blackKingPos1D;
    private int enPassantTarget1D; // -1 if no en passant target
    private HashMap <Byte, Integer> whiteMaterial;
    private HashMap <Byte, Integer> blackMaterial;

    // for testing
    public static void main(String[] args) {
        ChessBoard board = new ChessBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", "kqKQ", "-");
        board.print();

        byte piece = board.board1D[0]; // should be invalid
        System.out.println(piece);
    }

    /**
     * Creates a chess board.
     * @param fenPieceChessPositions FEN string component representing the piece positions
     */
    public ChessBoard(String fenPiecePlacement, String fenCastlingAvailability, String fenEnPassantTarget) {
        board1D = new byte[144]; // (2 + 8 + 2)^2 (for -1 double padding on edges)

        // initialize to Invalid
        for (int i = 0; i < 144; i++) {
            board1D[i] = ChessPiece.Invalid;;
        }

        whiteMaterial = new HashMap<Byte, Integer>();
        blackMaterial = new HashMap<Byte, Integer>();

        // fill whole middle (even if empty)
        fillBoard(fenPiecePlacement);

        CastlingAvailability = new CastlingAvailability(fenCastlingAvailability);
        whiteKingPos1D = findKing(true);
        blackKingPos1D = findKing(false);
        enPassantTarget1D = fenEnPassantTarget.equals("-") ?
            -1 : new ChessPosition(fenEnPassantTarget).get1D();
    }

    /**
     * Creates a deep copy of a chess board.
     * @param other the board to copy
     */
    public ChessBoard(ChessBoard other) {
        board1D = other.board1D.clone();

        // immutable
        CastlingAvailability = other.CastlingAvailability; 
        whiteKingPos1D = other.whiteKingPos1D;
        blackKingPos1D = other.blackKingPos1D;
    }

    /**
     * Fills the board with pieces from a FEN string.
     * @param fenPiecePlacement FEN string piece placement component 
     */
    public void fillBoard(String fenPiecePlacement) {
        String[] rows = fenPiecePlacement.split("/");

        for (int rowIdx = 0; rowIdx < 8; rowIdx++) {
            String row = rows[rowIdx];

            int colIdx = 0;
            for (char fenChar : row.toCharArray()) {
                if (Character.isDigit(fenChar)) {
                    // digit means N empty squares
                    int numEmptySquares = Character.getNumericValue(fenChar);
                    
                    // default is not empty, so must be set
                    for (int i = 0; i < numEmptySquares; i++) {
                        setPiece(rowIdx, colIdx, ChessPiece.Empty);
                        colIdx++;
                    }

                    colIdx += numEmptySquares;
                } else {
                    byte piece = ChessPiece.getPieceFromFenCharacter(fenChar);
                    setPiece(rowIdx, colIdx, piece);

                    HashMap<Byte, Integer> material = ChessPiece.isWhite(piece) ? whiteMaterial : blackMaterial;
                    material.put(piece, material.getOrDefault(piece, 0) + 1);

                    colIdx++;
                }
            }
        }
    }

    /**
     * Gets the fen piece placement component of the board.
     * @return the fen piece placement component of the board
     */
    public String getFenPiecePlacement() {
        StringBuilder fen = new StringBuilder();
        int emptyCounter = 0;

        for (int rowIdx = 0; rowIdx < 8; rowIdx++) {
            for (int colIdx = 0; colIdx < 8; colIdx++) {
                byte piece = getPiece(rowIdx, colIdx);
                char fenChar = ChessPiece.getFenCharacter(piece);

                if (fenChar == '1') {
                    emptyCounter++;
                } else {
                    if (emptyCounter != 0) {
                        fen.append(emptyCounter);
                        emptyCounter = 0;
                    }
                    fen.append(fenChar);
                }
            }

            if (emptyCounter != 0) {
                fen.append(emptyCounter);
                emptyCounter = 0;
            }

            if (rowIdx != 7) {
                fen.append('/');
            }
        }

        return fen.toString();
    }

    /**
     * Gets the fen castling availability component of the board.
     * @return the fen castling availability component of the board
     */
    public String getFenCastlingAvailability() {
        return CastlingAvailability.toString();
    }

    /**
     * Gets the fen en passant target component of the board.
     * @return the fen en passant target component of the board
     */
    public String getFenEnPassantTarget() {
        return enPassantTarget1D == -1 ? "-" : new ChessPosition(enPassantTarget1D).toString();
    }


    /**
     * Sets the piece at the given 2D position.
     * @param row the row index from top
     * @param col the column index from left
     * @param piece the piece
     * @throws IllegalArgumentException if row or col is out of bounds
     */
    public void setPiece(int row, int col, byte piece) {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            throw new IllegalArgumentException("Row or column out of bounds");
        }

        int idx = (row + 2) * 12 + col + 2;
        byte oldPiece = board1D[idx];
        board1D[idx] = piece;

        HashMap<Byte, Integer> material = ChessPiece.isWhite(piece) ? whiteMaterial : blackMaterial;
        material.put(piece, material.getOrDefault(piece, 0) + 1);
        material.put(oldPiece, material.getOrDefault(oldPiece, 0) - 1);
    }

    /**
     * Gets the piece at the given 2D position.
     * @param row the row index from top
     * @param col the column index from left
     * @return the piece
     * @throws IllegalArgumentException if row or col is out of bounds
     */
    public byte getPiece(int row, int col) {
        if (row < 0 || row > 7 || col < 0 || col > 7) {
            throw new IllegalArgumentException("Row or column out of bounds");
        }

        int idx = (row + 2) * 12 + col + 2;
        return board1D[idx];
    }

    /**
     * Prints the board to console.
     */
    public void print() {
        String colLabels = "  a b c d e f g h  ";

        System.out.println(colLabels); // column labels

        for (int row = 0; row < 8; row++) {
            System.out.print(8 - row + " "); // line number
            for (int col = 0; col < 8; col++) {
                // represent empty square as whitespace not '1'
                byte piece = getPiece(row, col);
                char c = ChessPiece.isEmpty(piece) ? ' ' : ChessPiece.getFenCharacter(piece);
                System.out.print(c + " "); // pieces
            }
            System.out.println(8 - row); // line number
        }

        System.out.println(colLabels); // column labels
    }

    /**
     * Makes a move on the board.
     * @param move the move to make
     */
    public void makeMove(ChessMove move) {
        byte piece = getPiece(move.from1D);

        if (ChessPiece.isEmpty(piece)) {
            throw new IllegalArgumentException("No piece at " + move.from1D);
        }

        setPiece(move.to1D, piece);
        setPiece(move.from1D, ChessPiece.Empty);

        // update king positions
        if (ChessPiece.isType(piece, ChessPiece.King)) {
            if (ChessPiece.isWhite(piece)) {
                whiteKingPos1D = move.to1D;
            } else {
                blackKingPos1D = move.to1D;
            }
        }

        // reset en passant target
        enPassantTarget1D = -1;

        // special moves
        if (move instanceof PawnDoubleMove) {
            PawnDoubleMove pawnDoubleMove = (PawnDoubleMove) move;
            enPassantTarget1D = pawnDoubleMove.enPassantTarget1D;

        } else if (move instanceof CastlingMove) {
            CastlingMove castlingMove = (CastlingMove) move;
            byte rook = getPiece(castlingMove.rookFrom1D);
            setPiece(castlingMove.rookTo1D, rook);
            setPiece(castlingMove.rookFrom1D, ChessPiece.Empty);

        } else if (move instanceof EnPassantMove) {
            EnPassantMove enPassantMove = (EnPassantMove) move;
            setPiece(enPassantMove.capturedPawn1D, ChessPiece.Empty);

        } else if (move instanceof PromotionMove) {
            PromotionMove promotionMove = (PromotionMove) move;
            byte promotionType = promotionMove.promotionType;
            byte promotionPiece = ChessPiece.setType(piece, promotionType);
            setPiece(move.to1D, promotionPiece);
        }

        CastlingAvailability = updateCastlingAvailability(CastlingAvailability, move);
    }

    /**
     * Gets the material count for the given piece type.
     * @param pieceType the piece type
     * @return the material count for the given piece type
     */
    public int getMaterialCount(byte pieceType) {
        return whiteMaterial.getOrDefault(pieceType, 0) + blackMaterial.getOrDefault(pieceType, 0);
    }

    // package private for ChessRules, shouldn't be exposed to client

    /*
     * Sets the piece at the given 1D position.
     */
    void setPiece(int pos1D, byte piece) {
        board1D[pos1D] = piece;
    }

    /*
     * Gets the piece at the given 1D position.
     */
    byte getPiece(int pos1D) {
        return board1D[pos1D];
    }

    /**
     * Gets the castling availability.
     */
    CastlingAvailability getCastlingAvailability() {
        return CastlingAvailability;
    }

    /**
     * Gets the 1D index of the white king.
     */
    int getWhiteKingPos1D() {
        return whiteKingPos1D;
    }

    /**
     * Gets the 1D index of the black king.
     */
    int getBlackKingPos1D() {
        return blackKingPos1D;
    }

    /**
     * Gets the 1D index of the en passant target.
     */
    int getEnPassantTarget1D() {
        return enPassantTarget1D;
    }

    /**
     * Gets the 1D position of the king.
     */
    private int findKing(boolean isWhite) {
        for (int pos1D = 0; pos1D < 144; pos1D++) {
            byte piece = board1D[pos1D];
            if (ChessPiece.isType(piece, ChessPiece.King) && ChessPiece.isWhite(piece) == isWhite) {
                return pos1D;
            }
        }

        throw new IllegalStateException("No king found");
    }

    // starting positions for castling pieces
    final static int WK_ROOK = new ChessPosition("h1").get1D();
    final static int WQ_ROOK = new ChessPosition("a1").get1D();
    final static int WK = new ChessPosition("e1").get1D();
    final static int BK_ROOK = new ChessPosition("h8").get1D();
    final static int BQ_ROOK = new ChessPosition("a8").get1D();
    final static int BK = new ChessPosition("e8").get1D();

    /*
     * Update castling availability after a move.
     */
    private CastlingAvailability updateCastlingAvailability(CastlingAvailability availability, ChessMove move) {
        return new CastlingAvailability(
            availability.whiteKingSide()
                && move.from1D != WK_ROOK && move.from1D != WK
                && move.to1D != WK_ROOK && move.to1D != WK,
            availability.whiteQueenSide()
                && move.from1D != WQ_ROOK && move.from1D != WK
                && move.to1D != WQ_ROOK && move.to1D != WK,
            availability.blackKingSide()
                && move.from1D != BK_ROOK && move.from1D != BK
                && move.to1D != BK_ROOK && move.to1D != BK,
            availability.blackQueenSide()
                && move.from1D != BQ_ROOK && move.from1D != BK
                && move.to1D != BQ_ROOK && move.to1D != BK
        );
    }

    
    /**
     * Represents a 2D position on the board.
     */
    public record ChessPosition(int row, int col) {
        /**
         * Creates a position from algebraic notation.
         * @param algPos the algebraic representation of the position
         */
        public ChessPosition(String algPos) {
            this(8 - Character.getNumericValue(algPos.charAt(1)), algPos.charAt(0) - 'a');
        }

        /**
         * Creates a position from 1D coordinates.
         */
        ChessPosition(int pos1D) {
            // package private constructor, don't expose 1d to client
            this(pos1D / 12 - 2, pos1D % 12 - 2);
        }

        /**
         * Gets the 1D index of the position.
         * @return
         */
        int get1D() {
            return (row + 2) * 12 + (col + 2);
        }

        @Override
        public String toString() {
            return (char) (col + 'a') + "" + (8 - row);
        }
    }

    /**
     * Represents a move on the board.
     */
    public class ChessMove {
        // engine can still access 1D coordinates
        final int from1D;
        final int to1D;

        /**
         * Creates a move from 1D coordinates.
         * @param from1D the 1D index of the piece to move
         * @param to1D the 1D index of the destination
         */
        public ChessMove(int from1D, int to1D) {
            this.from1D = from1D;
            this.to1D = to1D;
        }

        /**
         * Gets the position of the piece to move.
         * @return the position of the piece to move
         */
        public ChessPosition getFrom() {
            return new ChessPosition(from1D / 12 - 2, from1D % 12 - 2);
        }

        /**
         * Gets the destination of the move.
         * @return the destination of the move
         */
        public ChessPosition getTo() {
            return new ChessPosition(to1D / 12 - 2, to1D % 12 - 2);
        }

        @Override
        public String toString() {
            return getFrom() + " -> " + getTo();
        }
    }

    // SPECIAL MOVES
    // game client doesn't see difference between special and standard moves,
    // but private variables can be used within makeMove

    /*
     * Represents a castling move.
     */
    public class CastlingMove extends ChessMove {
        private final int rookFrom1D;
        private final int rookTo1D;

        /**
         * Creates a castling move.
         * @param from1D the 1D index of the king
         * @param to1D the 1D index of the king's destination
         * @param rookFrom1D the 1D index of the rook
         * @param rookTo1D the 1D index of the rook's destination
         */
        public CastlingMove(int from1D, int to1D, int rookFrom1D, int rookTo1D) {
            super(from1D, to1D);
            this.rookFrom1D = rookFrom1D;
            this.rookTo1D = rookTo1D;
        }
    }

    /*
     * Represents a Pawn move two squares forward.
     */
    public class PawnDoubleMove extends ChessMove {
        private final int enPassantTarget1D;

        /**
         * Creates a pawn double move.
         * @param from1D the 1D index of the pawn
         * @param to1D the 1D index of the pawn's destination
         * @param enPassantTarget1D the 1D index of the en passant target
         */
        public PawnDoubleMove(int from1D, int to1D, int enPassantTarget1D) {
            super(from1D, to1D);
            this.enPassantTarget1D = enPassantTarget1D;
        }
    }

    /*
     * Represents an en passant move.
     */
    public class EnPassantMove extends ChessMove {
        private final int capturedPawn1D;

        /**
         * Creates an en passant move.
         * @param from1D the 1D index of the pawn
         * @param to1D the 1D index of the pawn's destination
         * @param capturedPawn1D the 1D index of the captured pawn
         */
        public EnPassantMove(int from1D, int to1D, int capturedPawn1D) {
            super(from1D, to1D);
            this.capturedPawn1D = capturedPawn1D;
        }
    }

    /*
     * Represents a promotion move.
     */
    public class PromotionMove extends ChessMove {
        private final byte promotionType;

        /**
         * Creates a promotion move.
         * @param from1D the 1D index of the pawn
         * @param to1D the 1D index of the pawn's destination
         * @param promotionType the type of the promotion piece
         */
        public PromotionMove(int from1D, int to1D, byte promotionType) {
            super(from1D, to1D);
            this.promotionType = promotionType;
        }
    }

    /*
     * Represents castling availability for both players.
     */
    public record CastlingAvailability(
        boolean whiteKingSide,
        boolean whiteQueenSide,
        boolean blackKingSide,
        boolean blackQueenSide
    ) {
        /**
         * Creates castling availability from a FEN string.
         * @param fen the FEN string castling availability component
         */
        public CastlingAvailability(String fen) {
            this(
                fen.contains("K"),
                fen.contains("Q"),
                fen.contains("k"),
                fen.contains("q")
            );
        }

        @Override
        public String toString() {
            StringBuilder fen = new StringBuilder();

            if (whiteKingSide) {
                fen.append('K');
            }
            if (whiteQueenSide) {
                fen.append('Q');
            }
            if (blackKingSide) {
                fen.append('k');
            }
            if (blackQueenSide) {
                fen.append('q');
            }

            return fen.length() == 0 ? "-" : fen.toString();
        }
    }
}