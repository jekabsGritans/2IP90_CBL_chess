package engine;

import java.util.Map;
import java.util.Set;
import java.util.HashSet;

/**
 * Represents the board of a chess game.
 * Handles position indexing, printing, initialization from a FEN string,
 * material caching, and making moves.
 */
public class ChessBoard {
    // pieces stored as bytes (see ChessPiece.java)
    // internal representation is a 1D array for easier offsets
    // 1D coordinates are never exposed outside of the engine
    private final byte[] board1D; 
    private CastlingAvailability CastlingAvailability;
    private int enPassantTarget1D; // -1 if no en passant target
    private Map <Byte, Set<ChessPosition>> whiteMaterial;
    private Map <Byte, Set<ChessPosition>> blackMaterial;

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

        whiteMaterial = initMaterialMap();
        blackMaterial = initMaterialMap();

        // fill with pieces and empty
        fillBoard(fenPiecePlacement);

        CastlingAvailability = new CastlingAvailability(fenCastlingAvailability);
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
        enPassantTarget1D = other.enPassantTarget1D;
        whiteMaterial = copyMaterialMap(other.whiteMaterial);
        blackMaterial = copyMaterialMap(other.blackMaterial);
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
                        int pos1D = (rowIdx + 2) * 12 + colIdx + 2;
                        board1D[pos1D] = ChessPiece.Empty; // don't add to material
                        colIdx++;
                    }
                } else {
                    byte piece = ChessPiece.getPieceFromFenCharacter(fenChar);
                    setPiece(rowIdx, colIdx, piece); // add to material
                    colIdx++;
                }
            }
        }
    }

    /**
     * Gets the FEN piece placement component of the board.
     * @return the FEN piece placement component of the board
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
     * Gets the FEN castling availability component of the board.
     * @return the FEN castling availability component of the board
     */
    public String getFenCastlingAvailability() {
        return CastlingAvailability.toString();
    }

    /**
     * Gets the FEN en passant target component of the board.
     * @return the FEN en passant target component of the board
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
        setPiece(idx, piece);
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
        return getPiece(idx);
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

        // reset en passant target by default
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
     * Gets the material for a player.
     * @param isWhite true if white material, false if black material
     * @return map of pieces to set of positions where such pieces are
     */
    public Map<Byte, Set<ChessPosition>> getMaterial(boolean isWhite) {
        return isWhite ? whiteMaterial : blackMaterial;
    }
 
    /*
     * Sets the piece at the given 1D position.
     */
    void setPiece(int pos1D, byte piece) {

        // update board
        byte capturedPiece = board1D[pos1D];
        board1D[pos1D] = piece;

        // update material
        Map<Byte, Set<ChessPosition>> material;
        byte type;

        if (ChessPiece.isPiece(piece)) {
            material= ChessPiece.isWhite(piece) ? whiteMaterial : blackMaterial;
            type = ChessPiece.getType(piece);
            material.get(type).add(new ChessPosition(pos1D));
        }

        if (ChessPiece.isPiece(capturedPiece)) {
            material = ChessPiece.isWhite(capturedPiece) ? whiteMaterial : blackMaterial;
            type = ChessPiece.getType(capturedPiece);
            material.get(type).remove(new ChessPosition(pos1D));
        }
    }

    /*
     * Gets the piece at the given 1D position.
     */
    byte getPiece(int pos1D) {
        return board1D[pos1D];
    }

    /*
     * Gets the castling availability.
     */
    CastlingAvailability getCastlingAvailability() {
        return CastlingAvailability;
    }

    /*
     * Gets the 1D position of the king.
     */
    int getKingPos1D(boolean isWhite) {
        return getMaterial(isWhite).get(ChessPiece.King).iterator().next().get1D();
    }

    /*
     * Gets the 1D position of the en passant target.
     */
    int getEnPassantTarget1D() {
        return enPassantTarget1D;
    }

    /*
     * Initializes a material map.
     */
    private Map<Byte, Set<ChessPosition>> initMaterialMap() {
        return Map.of(
            ChessPiece.Pawn, new HashSet<ChessPosition>(),
            ChessPiece.Knight, new HashSet<ChessPosition>(),
            ChessPiece.Bishop, new HashSet<ChessPosition>(),
            ChessPiece.Rook, new HashSet<ChessPosition>(),
            ChessPiece.Queen, new HashSet<ChessPosition>(),
            ChessPiece.King, new HashSet<ChessPosition>()
        );
    }

    /*
     * Creates a deep copy of a material map.
     */
    private Map<Byte, Set<ChessPosition>> copyMaterialMap(Map<Byte, Set<ChessPosition>> material) {
        Map<Byte, Set<ChessPosition>> copy = Map.of(
            ChessPiece.Pawn, new HashSet<ChessPosition>(material.get(ChessPiece.Pawn)),
            ChessPiece.Knight, new HashSet<ChessPosition>(material.get(ChessPiece.Knight)),
            ChessPiece.Bishop, new HashSet<ChessPosition>(material.get(ChessPiece.Bishop)),
            ChessPiece.Rook, new HashSet<ChessPosition>(material.get(ChessPiece.Rook)),
            ChessPiece.Queen, new HashSet<ChessPosition>(material.get(ChessPiece.Queen)),
            ChessPiece.King, new HashSet<ChessPosition>(material.get(ChessPiece.King))
        );
        
        return copy;
    }

    // starting positions for castling pieces
    private final static int WK_ROOK = new ChessPosition("h1").get1D();
    private final static int WQ_ROOK = new ChessPosition("a1").get1D();
    private final static int WK = new ChessPosition("e1").get1D();
    private final static int BK_ROOK = new ChessPosition("h8").get1D();
    private final static int BQ_ROOK = new ChessPosition("a8").get1D();
    private final static int BK = new ChessPosition("e8").get1D();

    /*
     * Updates castling availability after a move.
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

        /*
         * Creates a position from 1D coordinates.
         */
        ChessPosition(int pos1D) {
            this(pos1D / 12 - 2, pos1D % 12 - 2);
        }

        @Override
        public String toString() {
            return (char) (col + 'a') + "" + (8 - row);
        }

        /*
         * Gets the 1D index of the position.
         */
        int get1D() {
            return (row + 2) * 12 + (col + 2);
        }
    }

    /**
     * Represents a move on the board.
     * Moves cannot be instantiated outside of the engine
     * as the engine provides a list of immutable legal moves
     * from which the players can choose.
     */
    public class ChessMove {
        final int from1D;
        final int to1D;

        ChessMove(int from1D, int to1D) {
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

    // special moves only differ from regular moves within the engine,
    // to adjust other involved pieces accordingly when making a special move

    /*
     * Represents a castling move.
     */
    class CastlingMove extends ChessMove {
        final int rookFrom1D;
        final int rookTo1D;

        CastlingMove(int from1D, int to1D, int rookFrom1D, int rookTo1D) {
            super(from1D, to1D);
            this.rookFrom1D = rookFrom1D;
            this.rookTo1D = rookTo1D;
        }
    }

    /*
     * Represents a Pawn move two squares forward.
     */
    class PawnDoubleMove extends ChessMove {
        final int enPassantTarget1D;

        PawnDoubleMove(int from1D, int to1D, int enPassantTarget1D) {
            super(from1D, to1D);
            this.enPassantTarget1D = enPassantTarget1D;
        }
    }

    /*
     * Represents an en passant move.
     */
    class EnPassantMove extends ChessMove {
        final int capturedPawn1D;

        EnPassantMove(int from1D, int to1D, int capturedPawn1D) {
            super(from1D, to1D);
            this.capturedPawn1D = capturedPawn1D;
        }
    }

    /*
     * Represents a promotion move.
     */
    class PromotionMove extends ChessMove {
        final byte promotionType;

        PromotionMove(int from1D, int to1D, byte promotionType) {
            super(from1D, to1D);
            this.promotionType = promotionType;
        }
    }

    /*
     * Represents castling availability for both players.
     */
    record CastlingAvailability(
        boolean whiteKingSide,
        boolean whiteQueenSide,
        boolean blackKingSide,
        boolean blackQueenSide
    ) {
        CastlingAvailability(String fen) {
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