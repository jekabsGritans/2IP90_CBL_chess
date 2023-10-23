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
     * @param fenPiecePositions FEN string component representing the piece positions
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
    public void makeMove(Move move) {
        byte piece = getPiece(move.from1D);

        if (ChessPiece.isEmpty(piece)) {
            throw new IllegalArgumentException("No piece at " + move.from1D);
        }

        setPiece(move.to1D, piece);
        setPiece(move.from1D, ChessPiece.Empty);
    }

    /**
     * Represents a 2D position on the board.
     */
    public record Position(int row, int col) {
        /**
         * Creates a position from algebraic notation.
         * @param algPos the algebraic representation of the position
         */
        public Position(String algPos) {
            this(8 - Character.getNumericValue(algPos.charAt(1)), algPos.charAt(0) - 'a');
        }
    }

    /**
     * Represents a move on the board.
     */
    public class Move {
        // engine can still access 1D coordinates
        final int from1D;
        final int to1D;

        /**
         * Creates a move from 1D coordinates.
         * @param from1D the 1D index of the piece to move
         * @param to1D the 1D index of the destination
         */
        public Move(int from1D, int to1D) {
            this.from1D = from1D;
            this.to1D = to1D;
        }

        /**
         * Gets the position of the piece to move.
         * @return the position of the piece to move
         */
        public Position getFrom() {
            return new Position(from1D / 8, from1D % 8);
        }

        /**
         * Gets the destination of the move.
         * @return the destination of the move
         */
        public Position getTo() {
            return new Position(to1D / 8, to1D % 8);
        }
    }
}