package engine;

/**
 * Represents the board of a chess game.
 * Handles position indexing, printing, initialization from a FEN string.
 */
public class ChessBoard {
    // pieces stored as bytes (see ChessPiece.java)
    private final byte[] board1D; // 1D array for easier offsets.

    // for testing
    public static void main(String[] args) {
        ChessBoard board = new ChessBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
        board.print();
    }

    /**
     * Creates a chess board.
     * @param fenPieceChessPositions FEN string component representing the piece positions
     */
    public ChessBoard(String fenPiecePlacement) {
        board1D = new byte[64];
        fillBoard(fenPiecePlacement);
    }

    /**
     * Creates a deep copy of a chess board.
     * @param other the board to copy
     */
    public ChessBoard(ChessBoard other) {
        board1D = new byte[64];

        for (int pos1D = 0; pos1D < 64; pos1D++) {
            board1D[pos1D] = other.board1D[pos1D];
        }
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
                    colIdx += numEmptySquares;
                } else {
                    byte piece = ChessPiece.getPieceFromFenCharacter(fenChar);
                    setPiece(rowIdx, colIdx, piece);
                    colIdx++;
                }
            }
        }
    }

    /**
     * Sets the piece at the given 2D position.
     * @param row the row index from top
     * @param col the column index from left
     * @param piece the piece
     */
    public void setPiece(int row, int col, byte piece) {
        board1D[63 - row * 8 - col] = piece;
    }

    /**
     * Gets the piece at the given 2D position.
     * @param row the row index from top
     * @param col the column index from left
     * @return the piece
     */
    public byte getPiece(int row, int col) {
        return board1D[63 - row * 8 - col];
    }

    /**
     * Sets the piece at the given 1D position.
     * @param pos1D the 1D position (0-63)
     * @param piece the piece
     */
    public void setPiece(int pos1D, byte piece) {
        board1D[pos1D] = piece;
    }

    /**
     * Gets the piece at the given 1D position.
     * @param pos1D the 1D position (0-63)
     * @return the piece
     */
    public byte getPiece(int pos1D) {
        return board1D[pos1D];
    }

    /**
     * Gets the 1D position of the king.
     * @param isWhite whether to get the white king or black king
     * @return the 1D position of the king (0-63)
     */
    public int getKingPos(boolean isWhite) {
        //TODO cache
        for (int pos1D = 0; pos1D < 64; pos1D++) {
            byte piece = board1D[pos1D];
            if (ChessPiece.isType(piece, ChessPiece.King) && ChessPiece.isWhite(piece) == isWhite) {
                return pos1D;
            }
        }

        throw new IllegalStateException("No king found");
    }

    /**
     * Prints the board to console.
     */
    public void print() {
        String colLabels = "  a b d d e f g h  ";

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

        // handle special moves

        if (move instanceof CastlingMove) {
            CastlingMove castlingMove = (CastlingMove) move;
            byte rook = getPiece(castlingMove.rookFrom1D);
            setPiece(castlingMove.rookTo1D, rook);
            setPiece(castlingMove.rookFrom1D, ChessPiece.Empty);
        }

        if (move instanceof EnPassantMove) {
            EnPassantMove enPassantMove = (EnPassantMove) move;
            setPiece(enPassantMove.capturedPawn1D, ChessPiece.Empty);
        }

        if (move instanceof PromotionMove) {
            PromotionMove promotionMove = (PromotionMove) move;
            byte promotionType = promotionMove.promotionType;
            byte promotionPiece = ChessPiece.setType(piece, promotionType);
            setPiece(move.to1D, promotionPiece);
        }
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
            return new ChessPosition(from1D / 8, from1D % 8);
        }

        /**
         * Gets the destination of the move.
         * @return the destination of the move
         */
        public ChessPosition getTo() {
            return new ChessPosition(to1D / 8, to1D % 8);
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
}