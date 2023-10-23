package engine;

/**
 * Represents the board of a chess game.
 * Handles position indexing, printing, initialization from a FEN string.
 */
public class ChessBoard {
    // pieces stored as bytes (see ChessPiece.java)
    private final byte[] board1D; // 1D array for easier offsets.
    CastlingRights castlingRights;
    int whiteKingPos1D;
    int blackKingPos1D;
    int enPassantTarget1D; // -1 if no en passant target

    // for testing
    public static void main(String[] args) {
        ChessBoard board = new ChessBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", "kqKQ", "-");
        board.print();

        byte piece = board.getPiece(0, 0);
        System.out.println(ChessPiece.getFenCharacter(piece));
    }

    /**
     * Creates a chess board.
     * @param fenPieceChessPositions FEN string component representing the piece positions
     */
    public ChessBoard(String fenPiecePlacement, String fenCastlingRights, String fenEnPassantTarget) {
        board1D = new byte[64];
        fillBoard(fenPiecePlacement);
        castlingRights = new CastlingRights(fenCastlingRights);
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
        board1D = new byte[64];

        for (int pos1D = 0; pos1D < 64; pos1D++) {
            board1D[pos1D] = other.board1D[pos1D];
        }

        // immutable
        castlingRights = other.castlingRights; 
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
        board1D[row * 8 + col] = piece;
    }

    /**
     * Gets the piece at the given 2D position.
     * @param row the row index from top
     * @param col the column index from left
     * @return the piece
     */
    public byte getPiece(int row, int col) {
        return board1D[row * 8 + col];
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
     */
    private int findKing(boolean isWhite) {
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

        castlingRights = updateCastlingRights(castlingRights, move);
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
         * Gets the 1D index of the position.
         * @return
         */
        public int get1D() {
            return row * 8 + col;
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
     * Represents castling rights for both players.
     */
    public record CastlingRights(
        boolean whiteKingSide,
        boolean whiteQueenSide,
        boolean blackKingSide,
        boolean blackQueenSide
    ) {
        /**
         * Creates castling rights from a FEN string.
         * @param fen the FEN string castling rights component
         */
        public CastlingRights(String fen) {
            this(
                fen.contains("K"),
                fen.contains("Q"),
                fen.contains("k"),
                fen.contains("q")
            );
        }
    }
}