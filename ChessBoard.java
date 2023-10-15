import pieces.ChessPiece;

/**
 * Container for chess positions.
 */
public class ChessBoard {
    private ChessPosition[][] board; // 8x8 board

    /**
     * Creates a chess board.
     * @param fenPiecePositions FEN string component representing the piece positions
     */
    public ChessBoard(String fenPiecePositions) {
        this.board = new ChessPosition[8][8];
        this.initializeBoard(fenPiecePositions);
    }

    /**
     * Initialize chess board from FEN string positions component.
     * @param fenPiecePositions FEN string component representing the piece positions
     */
    public void initializeBoard(String fenPiecePositions) {
        String[] rows = fenPiecePositions.split("/");

        for (int i = 0; i < 8; i++) {
            String row = rows[i];
            int col = 0;

            for (int j = 0; j < row.length(); j++) {
                char piece = row.charAt(j);

                if (Character.isDigit(piece)) {
                    // digit means N empty squares
                    int numEmptySquares = Character.getNumericValue(piece);
                    for (int k = 0; k < numEmptySquares; k++) {
                        board[i][col] = new ChessPosition(i, col, null);
                        col++;
                    }
                } else {
                    // letter corresponds to a piece
                    ChessPiece chessPiece = ChessPiece.initializeChessPiece(piece);
                    board[i][col] = new ChessPosition(i, col, chessPiece);
                    col++;
                }
            }
        }
    }

    /**
     * Print board to console.
     */
    public void print() {
        String colLabels = "  a b d d e f g h  ";

        System.out.println(colLabels); // column labels

        for (int i = 0; i < 8; i++) {
            System.out.print(8 - i + " "); // line number
            for (ChessPosition pos : board[i]) {
                System.out.print(pos + " "); // pieces
            }
            System.out.println(8 - i); // line number
        }

        System.out.println(colLabels); // column lavels
    }

    /**
     * Get the position at the given coordinates.
     * @param x x coordinate
     * @param y y coordinate
     * @return the position at the given coordinates
     */
    public ChessPosition getPosition(int x, int y) {
        return board[x][y];
    }

    /**
     * Get the position at the given FEN coordinates.
     * @param fenPosition FEN string representation of the position
     */
    public ChessPosition getPosition(String fenPosition) {
        if (fenPosition.equals("-")) {
            return null;
        }

        int x = 8 - Character.getNumericValue(fenPosition.charAt(1));
        int y = fenPosition.charAt(0) - 'a';
        return board[x][y];
    }
}
