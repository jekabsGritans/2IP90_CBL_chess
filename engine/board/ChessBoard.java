package engine.board;

/**
 * Represents the board of a chess game.
 * Handles position indexing, printing, initialization from a FEN string.
 */
public class ChessBoard {
    private final ChessPiece[][] board; // 8x8 board

    /**
     * Creates a chess board.
     * @param fenPiecePositions FEN string component representing the piece positions
     */
    public ChessBoard(String fenPiecePositions) {
        this.board = new ChessPiece[8][8];
        this.fillBoard(fenPiecePositions);
    }

    /**
     * Fills the board with pieces from a FEN string.
     * @param fenPiecePositions FEN string component representing the piece positions
     */
    public void fillBoard(String fenPiecePositions) {
        String[] rows = fenPiecePositions.split("/");

        for (int i = 0; i < 8; i++) {
            String row = rows[i];
            int col = 0;

            for (int j = 0; j < row.length(); j++) {
                char piece = row.charAt(j);

                if (Character.isDigit(piece)) {
                    // digit means N empty squares
                    int numEmptySquares = Character.getNumericValue(piece);
                    col += numEmptySquares;
                } else {
                    // letter corresponds to a piece
                    ChessPiece chessPiece = new ChessPiece(piece);
                    board[i][col] = chessPiece;
                    col++;
                }
            }
        }
    }

    /**
     * Moves a piece on the board.
     * @param from the position from which the piece is moved
     * @param to the position to which the piece is moved
     */
    public void movePiece(BoardPosition from, BoardPosition to) {
        ChessPiece fromPiece = getPiece(from);

        if (fromPiece == null) {
            throw new IllegalArgumentException(
                "No piece at position " + from.getAlgebraicPosition());
        }

        removePiece(to);
        placePiece(fromPiece, to);
    }

    /**
     * Places a piece on the board.
     * @param piece the piece to place
     * @param pos the position to place the piece at
     */
    public void placePiece(ChessPiece piece, BoardPosition pos) {
        board[pos.x][pos.y] = piece;
        piece.setPosition(pos);
    }

    /**
     * Removes a piece from the board.
     * @param pos the position to remove the piece from
     */
    public void removePiece(BoardPosition pos) {
        ChessPiece piece = getPiece(pos);

        if (piece != null) {
            board[pos.x][pos.y] = null;
            piece.setPosition(null);
        }
    }

    /**
     * Prints board to console.
     */
    public void print() {
        String colLabels = "  a b d d e f g h  ";

        System.out.println(colLabels); // column labels

        for (int i = 0; i < 8; i++) {
            System.out.print(8 - i + " "); // line number
            for (ChessPiece piece : board[i]) {
                System.out.print(piece.getFenCharacter() + " "); // pieces
            }
            System.out.println(8 - i); // line number
        }

        System.out.println(colLabels); // column labels
    }

    /**
     * Gets the piece at the given position.
     * @param x x coordinate
     * @param y y coordinate
     * @return the piece at the given position
     */
    public ChessPiece getPiece(int x, int y) {
        return board[x][y];
    }

    /**
     * Gets the piece at the given position.
     * @param pos the position 
     * @return the piece at the given position
     */
    public ChessPiece getPiece(BoardPosition pos) {
        return getPiece(pos.x, pos.y);
    }

    /**
     * Gets the piece at the given position.
     * @param algPos the algebraic representation of the position
     * @return the piece at the given position
     */
    public ChessPiece getPiece(String algPos) {
        BoardPosition pos = new BoardPosition(algPos);
        return getPiece(pos.x, pos.y);
    }

    /**
     * Checks if coordinates are in bounds of the board.
     * @param x x coordinate
     * @param y y coordinate
     * @return true if position is in bounds
     */
    public boolean checkInBounds(int x, int y) {
        return (x >= 0 && x <= 7 && y >= 0 && y <= 7);
    }

    /**
     * Represents a position on the board.
     */
    public class BoardPosition {
        public final int x;
        public final int y;

        /**
         * Creates a position.
         * @param x x coordinate
         * @param y y coordinate
         */
        public BoardPosition(int x, int y) {
            if (!checkInBounds(x, y)) {
                throw new IllegalArgumentException("Invalid position");
            }
            this.x = x;
            this.y = y;
        }

        /**
         * Creates a position.
         * @param algPos the algebraic representation of the position
         */
        public BoardPosition(String algPos) {
            this.x = 8 - Character.getNumericValue(algPos.charAt(1));
            this.y = algPos.charAt(0) - 'a';
        }

        /**
         * Gets the algebraic representation of the position.
         * @return the algebraic representation of the position
         */
        public String getAlgebraicPosition() {
            return "" + (char) ('a' + y) + (8 - x);
        }

        /**
         * Checks whether the position matches another position.
         * @param algPos the algebraic representation of the other position
         * @return true if the position matches, false otherwise
         */
        public boolean equals(String algPos) {
            return this.getAlgebraicPosition().equals(algPos);
        }

        /**
         * Checks whether the position matches another position.
         * @param pos the other position
         * @return true if the position matches, false otherwise
         */
        public boolean equals(BoardPosition pos) {
            return this.x == pos.x && this.y == pos.y;
        }
    }
}