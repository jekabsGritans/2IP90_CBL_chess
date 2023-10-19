package engine;

/**
 * Represents the board of a chess game.
 * Handles position indexing, printing, initialization from a FEN string.
 */
public class ChessBoard {
    // pieces stored as bytes (see ChessPiece.java)
    private final byte[][] board; // 8x8 board

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
        this.board = new byte[8][8];
        this.fillBoard(fenPiecePlacement);
    }

    /**
     * Creates a deep copy of a chess board.
     * @param board the board to copy
     */
    public ChessBoard(ChessBoard board) {
        this.board = new byte[8][8];

        for (int rowIdx = 0; rowIdx < 8; rowIdx++) {
            for (int colIdx = 0; colIdx < 8; colIdx++) {
                this.board[rowIdx][colIdx] = board.board[rowIdx][colIdx];
            }
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
                    board[rowIdx][colIdx] = piece;
                    colIdx++;
                }
            }
        }
    }

    /**
     * Sets the piece at the given position.
     * @param row the row index from top
     * @param col the column index from left
     * @param piece the piece
     */
    public void setPiece(int row, int col, byte piece) {
        board[row][col] = piece;
    }

    /**
     * Gets the piece at the given position.
     * @param row the row index from top
     * @param col the column index from left
     * @return the piece
     */
    public byte getPiece(int row, int col) {
        return board[row][col];
    }

    /**
     * Sets the piece at the given position.
     * @param pos the position
     * @param piece the piece
     */
    public void setPiece(Position pos, byte piece) {
        board[pos.row()][pos.col()] = piece;
    }

    /**
     * Gets the piece at the given position.
     * @param pos the position
     * @return the piece
     */
    public byte getPiece(Position pos) {
        return board[pos.row()][pos.col()];
    }

    /**
     * Sets the piece at the given position.
     * @param algPos the algebraic representation of the position
     * @param piece the piece
     */
    public void setPiece(String algPos, byte piece) {
        Position pos = new Position(algPos);
        setPiece(pos, piece);
    }

    /**
     * Gets the piece at the given position.
     * @param algPos the algebraic representation of the position
     * @return the piece
     */
    public byte getPiece(String algPos) {
        Position pos = new Position(algPos);
        return getPiece(pos);
    }

    /**
     * Prints the board to console.
     */
    public void print() {
        String colLabels = "  a b d d e f g h  ";

        System.out.println(colLabels); // column labels

        for (int i = 0; i < 8; i++) {
            System.out.print(8 - i + " "); // line number
            for (byte piece : board[i]) {
                // represent empty square as whitespace not '1'
                char c = ChessPiece.isEmpty(piece) ? ' ' : ChessPiece.getFenCharacter(piece);
                System.out.print(c + " "); // pieces
            }
            System.out.println(8 - i); // line number
        }

        System.out.println(colLabels); // column labels
    }

    /**
     * Checks if coordinates are in bounds of the board.
     * @param row row index from top
     * @param col column index from left
     * @return true if position is in bounds
     */
    public boolean checkInBounds(int row, int col) {
        return (row >= 0 && row <= 7 && col >= 0 && col <= 7);
    }

    /**
     * Makes a move on the board.
     * @param move the move to make
     */
    public void makeMove(Move move) {
        byte piece = getPiece(move.from);

        if (ChessPiece.isEmpty(piece)) {
            throw new IllegalArgumentException("No piece at " + move.from);
        }

        setPiece(move.to, piece);
        setPiece(move.from, ChessPiece.Empty);
    }

    /**
     * Represents a position on the board.
     */
    public record Position(int row, int col) {
        /**
         * Creates a position from algebraic notation.
         * @param algPos the algebraic representation of the position
         */
        public Position(String algPos) {
            this(8 - Character.getNumericValue(algPos.charAt(1)), algPos.charAt(0) - 'a');
        }

        /**
         * Checks if a position equals another position.
         * @param algPos the algebraic representation of the position
         * @return true if the positions are equal
         */
        public boolean equals(String algPos) {
            return this.equals(new Position(algPos));
        }
    }

    /**
     * Represents a move on the board.
     */
    public record Move(Position from, Position to) {}
}