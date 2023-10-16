package engine;

import engine.board.ChessBoard.Position;
import engine.board.ChessPiece;

/**
 * Represents an arbitrary move on a chess board.
 */
public final class ChessMove {
    // all variables are public since this is an immutable data class
    // (no need for getters and setters)

    // Builder pattern is used to construct moves since there are many optional parameters

    // required parameters
    public final Position from;
    public final Position to;
    public final ChessPiece movedPiece;

    // optional parameters
    public final ChessPiece capturedPiece; // default null
    public final ChessPiece promotionPiece; // default null
    public final Position enPassantAttack; // default null
    public final Position castlingRookFrom; // default null
    public final Position castlingRookTo; // default null

    /**
     * Construct a chess move.
     */
    public ChessMove(MoveBuilder builder) {
        this.from = builder.from;
        this.to = builder.to;
        this.movedPiece = builder.movedPiece;
        this.capturedPiece = builder.capturedPiece;
        this.promotionPiece = builder.promotionPiece;
        this.enPassantAttack = builder.enPassantAttack;
        this.castlingRookFrom = builder.castlingRookFrom;
        this.castlingRookTo = builder.castlingRookTo; 
    }

    /**
     * Builder class for ChessMove.
     */
    public static class MoveBuilder {
        // required parameters
        private Position from;
        private Position to;
        private ChessPiece movedPiece;

        // optional parameters
        private ChessPiece capturedPiece = null;
        private ChessPiece promotionPiece = null;
        private Position enPassantAttack = null;
        private Position castlingRookFrom = null;
        private Position castlingRookTo = null;


        /**
         * Creates a move builder.
         * @param from the position from which the piece is moved
         * @param to the position to which the piece is moved
         * @param movedPiece the piece that is moved
         */
        public MoveBuilder(Position from, Position to, ChessPiece movedPiece) {
            this.from = from;
            this.to = to;
            this.movedPiece = movedPiece;
        }

        /**
         * Sets the captured piece.
         * @param capturedPiece the piece that is captured
         * @return this move builder
         */
        public MoveBuilder capturedPiece(ChessPiece capturedPiece) {
            this.capturedPiece = capturedPiece;
            return this;
        }

        /**
         * Sets the promotion piece.
         * @param promotionPiece the piece that the pawn is promoted to
         * @return this move builder
         */
        public MoveBuilder promotionPiece(ChessPiece promotionPiece) {
            this.promotionPiece = promotionPiece;
            return this;
        }

        /**
         * Sets the en passant attack.
         * @param enPassantAttack the position of the pawn that is captured en passant
         * @return this move builder
         */
        public MoveBuilder enPassantAttack(Position enPassantAttack) {
            this.enPassantAttack = enPassantAttack;
            return this;
        }

        /**
         * Sets the rook castling move.
         * @param rookFrom the position from which the rook is moved
         * @param rookTo the position to which the rook is moved
         * @return this move builder
         */
        public MoveBuilder castlingMove(Position rookFrom, Position rookTo, ChessPiece rook) {
            this.castlingRookFrom = rookFrom;
            this.castlingRookTo = rookTo;
            return this;
        }

        /**
         * Builds the move.
         * @return the built move
         */
        public ChessMove build() {
            return new ChessMove(this);
        }
    }
}