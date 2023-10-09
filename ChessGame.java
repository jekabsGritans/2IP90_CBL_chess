import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import pieces.Bishop;
import pieces.ChessPiece;
import pieces.King;
import pieces.Knight;
import pieces.Pawn;
import pieces.Queen;
import pieces.Rook;

/**
 * Represents a chess game.
 */
class ChessGame {
    ChessPosition[][] board; // 8x8 board
    ArrayList<ChessPiece> whitePieces;
    ArrayList<ChessPiece> blackPieces;
    ArrayList<ChessPiece> capturedPieces;
    boolean isWhiteMove;

    // number of moves both players have made since last pawn advance or piece capture.
    // used for 50 move rule - if this number reaches 100, the game is a draw.
    int numHalfMoves; 

    // number of moves white has made. just to keep track
    int numFullMoves; 

    // store unique square where en passant capture is possible, null if not possible
    ChessPosition enPassantTargetSquare;


    /**
     * Initialize chess piece.
     * @param piece FEN character for the piece
     */
    public ChessPiece initChessPiece(char piece) {
        switch (piece) {
            case 'p':
                return new Pawn(true, false);
            case 'P':
                return new Pawn(false, false);
            case 'r':
                return new Rook(true, false);
            case 'R':
                return new Rook(false, false);
            case 'n':
                return new Knight(true, false);
            case 'N':
                return new Knight(false, false);
            case 'b':
                return new Bishop(true, false);
            case 'B':
                return new Bishop(false, false);
            case 'q':
                return new Queen(true, false);
            case 'Q':
                return new Queen(false, false);
            case 'k':
                return new King(true, false);
            case 'K':
                return new King(false, false);
            default:
                return null;
        }
    }

    /**
     * Initialize chess game from a FEN string.
     * @param fen FEN representation of the chess game state
     */
    public ChessGame(String fen) {
        // FEN string format
        String regex = "([rnbqkpRNBQKP1-8\\/]+) ([bw]) ([-KQkq]+) ([-a-h1-8]+) (\\d+) (\\d+)";  
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(fen);

        //validate FEN string
        if (matcher.find()) {
            String piecePositions = matcher.group(1);
            String activeColor = matcher.group(2);
            String castlingAvailability = matcher.group(3);
            String enPassantTargetSquareStr = matcher.group(4);
            String halfmoveClock = matcher.group(5);
            String fullmoveNumber = matcher.group(6);

            board = initBoard(piecePositions, castlingAvailability);
            enPassantTargetSquare = getPosition(enPassantTargetSquareStr);
            isWhiteMove = activeColor.equals("w");
            numHalfMoves = Integer.parseInt(halfmoveClock);
            numFullMoves = Integer.parseInt(fullmoveNumber);
    
        } else {
            System.out.println("Invalid FEN string");
            System.exit(1);
        }
    }

    /**
     * Get the board position object from a FEN position string.
     * @param fenPosition FEN string representation of the position
     * @return the board position object
     */
    public ChessPosition getPosition(String fenPosition) {
        int x = 8 - Character.getNumericValue(fenPosition.charAt(1));
        int y = fenPosition.charAt(0) - 'a';
        return board[x][y];
    }

    /**
     * Initialize a filled board from FEN string components.
     * @param piecePositions FEN string component for piece positions
     * @param castlingAvailability FEN string component for castling availability
     */
    public ChessPosition[][] initBoard(String piecePositions, String castlingAvailability) {
        ChessPosition[][] board = new ChessPosition[8][8];
        String[] rows = piecePositions.split("/");
        int row = 0;
        int col = 0;
        for (String r : rows) {
            for (char c : r.toCharArray()) {
                if (Character.isDigit(c)) {
                    //digit means N empty squares
                    int numEmpty = Character.getNumericValue(c);
                    for (int i = 0; i < numEmpty; i++) {
                        board[row][col] = new ChessPosition(row, col, null);
                        col++;
                    }
                } else {
                    //letter corresponds to a piece
                    ChessPiece piece = initChessPiece(c);
                    
                    // set hasMoved for rooks/kings to load castling availability. WILL REFACTOR
                    if (piece instanceof King) {
                        if (piece.isWhite()) {
                            //if white can't castle kingside or queenside, white king has moved
                            if (!castlingAvailability.contains("K") || !castlingAvailability.contains("Q")) {
                                piece.setHasMoved(true);
                            }
                        } else {
                            //if black can't castle kingside or queenside, black king has moved
                            if (!castlingAvailability.contains("k") || !castlingAvailability.contains("q")) {
                                piece.setHasMoved(true);
                            }
                        }
                    }

                    if (piece instanceof Rook) {
                        if (piece.isWhite()) {
                            //if white can't castle kingside, white kingside rook has moved
                            if (col == 7 && !castlingAvailability.contains("K")) {
                                piece.setHasMoved(true);
                            }
                            //if white can't castle queenside, white queenside rook has moved
                            if (col == 0 && !castlingAvailability.contains("Q")) {
                                piece.setHasMoved(true);
                            }
                        } else {
                            //if black can't castle kingside, black kingside rook has moved
                            if (col == 7 && !castlingAvailability.contains("k")) {
                                piece.setHasMoved(true);
                            }
                            //if black can't castle queenside, black queenside rook has moved
                            if (col == 0 && !castlingAvailability.contains("q")) {
                                piece.setHasMoved(true);
                            }
                        }
                    }

                    board[row][col] = new ChessPosition(row, col, piece);
                    col++;
                }
            }
            row++;
            col = 0;
        }
        return board;
    }

    /**
     * Default constructor for ChessGame.
     * Initializes the board to the starting position.
     */
    public ChessGame() {
        this("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq");
    }
}