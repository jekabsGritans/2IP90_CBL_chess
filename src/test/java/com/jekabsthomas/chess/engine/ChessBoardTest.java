package com.jekabsthomas.chess.engine;

import com.jekabsthomas.chess.engine.ChessBoard.CastlingAvailability;
import com.jekabsthomas.chess.engine.ChessBoard.CastlingMove;
import com.jekabsthomas.chess.engine.ChessBoard.ChessMove;
import com.jekabsthomas.chess.engine.ChessBoard.ChessPosition;
import com.jekabsthomas.chess.engine.ChessBoard.EnPassantMove;
import com.jekabsthomas.chess.engine.ChessBoard.PawnDoubleMove;
import com.jekabsthomas.chess.engine.ChessBoard.PromotionMove;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Map;
import java.util.Set;

import org.junit.*;
import static org.junit.Assert.*;

public class ChessBoardTest {
    @Test
    public void testChessPosition() {
        // tests translation between the algebraic notation and the internal  representation

        // initialize from 2D array indices
        ChessPosition pos = new ChessPosition(0, 0);
        assertEquals(pos.toString(), "a8");
        pos = new ChessPosition(7, 7);
        assertEquals(pos.toString(), "h1");

        // initialize from algebraic notation
        pos = new ChessPosition("a8");
        assertEquals(pos.toString(), "a8");
        pos = new ChessPosition("h1");
        assertEquals(pos.toString(), "h1");
    }

    @Test
    public void testGetFenPiecePlacement() {
        // tests if the piece placement is correctly loaded into the board and then regenerated
        String piecePlacement = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
        ChessBoard board = new ChessBoard(piecePlacement, "kqKQ", "-");
        assertEquals(board.getFenPiecePlacement(), piecePlacement);
    }

    @Test
    public void testGetFenCastlingAvailability() {
        // tests if the castling availability is correctly loaded into the board and then regenerated
        ChessBoard board = new ChessBoard("8/8/8/8/8/8/8/8", "KQkq", "-");
        assertEquals(board.getFenCastlingAvailability(), "KQkq");
        board = new ChessBoard("8/8/8/8/8/8/8/8", "KQ", "-");
        assertEquals(board.getFenCastlingAvailability(), "KQ");
        board = new ChessBoard("8/8/8/8/8/8/8/8", "-", "-");
        assertEquals(board.getFenCastlingAvailability(), "-");
    }

    @Test
    public void testGetFenEnPassantTarget() {
        // tests if the en passant target is correctly loaded into the board and then regenerated
        ChessBoard board = new ChessBoard("8/8/8/8/8/8/8/8", "-", "-");
        assertEquals(board.getFenEnPassantTarget(), "-");
        board = new ChessBoard("8/8/8/8/8/8/8/8", "-", "a3");
        assertEquals(board.getFenEnPassantTarget(), "a3");
    }

    @Test
    public void testGetPiece() {
        // tests if pieces are correctly retrieved
        ChessBoard board = new ChessBoard("8/8/8/8/8/8/8/8", "-", "-");
        byte piece = ChessPiece.White | ChessPiece.Pawn;
        board.setPiece(0, 0, piece);
        assertEquals(board.getPiece(0, 0), piece);
        assertEquals(board.getPiece(0, 1), ChessPiece.Empty);
        assertEquals(board.getPiece(1, 0), ChessPiece.Empty);
    }

    @Test
    public void testSetPiece() {
        // tests if the piece is correctly set
        ChessBoard board = new ChessBoard("8/8/8/8/8/8/8/8", "-", "-");
        byte piece = ChessPiece.White | ChessPiece.Pawn;
        board.setPiece(0, 0, piece);
        assertEquals(board.getPiece(0, 0), piece);
        board.setPiece(0, 0, ChessPiece.Empty);
        assertEquals(board.getPiece(0, 0), ChessPiece.Empty);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testSetPieceInvalid() {
        // tests if illegal positions are caught
        ChessBoard board = new ChessBoard("8/8/8/8/8/8/8/8", "-", "-");
        byte piece = ChessPiece.White | ChessPiece.Pawn;
        board.setPiece(0, 8, piece);
    }

    @Test
    public void testPrint() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        // tests if no exceptions are thrown when printing
        ChessBoard board = new ChessBoard("8/8/8/8/8/8/8/8", "-", "-");
        board.print();
        board = new ChessBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR", "KQkq", "-");
        board.print();

        System.setOut(System.out);
    }

    @Test
    public void testGetMaterial() {
        // tests if the material is correctly calculated
        ChessBoard board = new ChessBoard("8/8/8/8/8/8/8/P7", "-", "-");
        Map<Byte, Set<ChessPosition>> whiteMaterial = board.getMaterial(true);
        Map<Byte, Set<ChessPosition>> blackMaterial = board.getMaterial(false);
        assertEquals(1, whiteMaterial.get(ChessPiece.Pawn).size());
        System.out.println(whiteMaterial.get(ChessPiece.Pawn));
        assertEquals(true, whiteMaterial.get(ChessPiece.Pawn).contains(new ChessPosition("a1")));
        assertEquals(0, blackMaterial.get(ChessPiece.Pawn).size());
    }

    @Test
    public void testMakeMoveStandard() {
        ChessBoard board = new ChessBoard("8/8/8/8/8/8/8/P7", "-", "-");

        // tests a standard move
        ChessPosition from = new ChessPosition("a1");
        ChessPosition to = new ChessPosition("a2");
        ChessMove move = board.new ChessMove(from.get1D(), to.get1D());
        board.makeMove(move);
        // piece moved correctly
        assertEquals(ChessPiece.Empty, board.getPiece(from.row(), from.col()));
        assertEquals(ChessPiece.White | ChessPiece.Pawn, board.getPiece(to.row(), to.col()));
        // material updated correctly
        Map<Byte, Set<ChessPosition>> whiteMaterial = board.getMaterial(true);
        assertEquals(1, whiteMaterial.get(ChessPiece.Pawn).size());
        assertEquals(true, whiteMaterial.get(ChessPiece.Pawn).contains(to));
    }

    // Note: we dont test for move legality here, as legality validation is not done
    // instead, legal moves are generated and this generation is tested elsewhere elsewhere elsewhere elsewhere

    @Test
    public void testMakeMoveCapture() {
        ChessBoard board = new ChessBoard("8/8/8/8/8/8/1p6/P7", "-", "-");

        // white pawn captures black pawn
        ChessPosition from = new ChessPosition("a1");
        ChessPosition to = new ChessPosition("b2");
        ChessMove move = board.new ChessMove(from.get1D(), to.get1D());
        board.makeMove(move);
        // piece moved correctly
        assertEquals(ChessPiece.Empty, board.getPiece(from.row(), from.col()));
        assertEquals(ChessPiece.White | ChessPiece.Pawn, board.getPiece(to.row(), to.col()));
        // material updated correctly (black pawn captured)
        Map<Byte, Set<ChessPosition>> whiteMaterial = board.getMaterial(true);
        assertEquals(1, whiteMaterial.get(ChessPiece.Pawn).size());
        assertEquals(true, whiteMaterial.get(ChessPiece.Pawn).contains(to));
        Map<Byte, Set<ChessPosition>> blackMaterial = board.getMaterial(false);
        assertEquals(0, blackMaterial.get(ChessPiece.Pawn).size());
    }

    @Test
    public void testMakeMoveCastling() {
        // king is moved and rook is moved
        ChessBoard board = new ChessBoard("8/8/8/8/8/8/8/R3K3", "KQkq", "-");
        ChessPosition from = new ChessPosition("e1");
        ChessPosition to = new ChessPosition("c1");
        ChessPosition rookFrom = new ChessPosition("a1");
        ChessPosition rookTo = new ChessPosition("d1");
        CastlingMove move = board.new CastlingMove(from.get1D(), to.get1D(), rookFrom.get1D(), rookTo.get1D());
        board.makeMove(move);

        // pieces are moved correctly
        assertEquals(ChessPiece.Empty, board.getPiece(from.row(), from.col()));
        assertEquals(ChessPiece.White | ChessPiece.King, board.getPiece(to.row(), to.col()));
        assertEquals(ChessPiece.Empty, board.getPiece(rookFrom.row(), rookFrom.col()));
        assertEquals(ChessPiece.White | ChessPiece.Rook, board.getPiece(rookTo.row(), rookTo.col()));
        CastlingAvailability castling = board.getCastlingAvailability();

        // castling availability is updated correctly
        assertEquals(false, castling.whiteKingSide());
        assertEquals(false, castling.whiteQueenSide());
        assertEquals(true, castling.blackKingSide());
        assertEquals(true, castling.blackQueenSide());
    }

    @Test
    public void testMakeMoveDoublePawn() {
        // white pawn moves two squares, en passant target is set
        ChessBoard board = new ChessBoard("8/8/8/8/8/8/P7/8", "-", "-");
        ChessPosition from = new ChessPosition("a2");
        ChessPosition to = new ChessPosition("a4");
        ChessPosition enPassantTarget = new ChessPosition("a3");
        PawnDoubleMove move = board.new PawnDoubleMove(from.get1D(), to.get1D(), enPassantTarget.get1D());
        board.makeMove(move);
        assertEquals(ChessPiece.Empty, board.getPiece(from.row(), from.col()));
        assertEquals(ChessPiece.White | ChessPiece.Pawn, board.getPiece(to.row(), to.col()));
        assertEquals(enPassantTarget.get1D(), board.getEnPassantTarget1D());
    }

    @Test
    public void testMakeMoveEnPassant() {
        // white pawn moves diagonally, black pawn is captured
        ChessBoard board = new ChessBoard("8/8/8/3pP/8/8/8/8", "-", "d5");
        ChessPosition from = new ChessPosition("e5");
        ChessPosition to = new ChessPosition("d6");
        ChessPosition captured = new ChessPosition("d5");

        EnPassantMove move = board.new EnPassantMove(from.get1D(), to.get1D(), captured.get1D());
        board.makeMove(move);

        // pieces are moved correctly
        assertEquals(ChessPiece.Empty, board.getPiece(from.row(), from.col()));
        assertEquals(ChessPiece.White | ChessPiece.Pawn, board.getPiece(to.row(), to.col()));
        assertEquals(ChessPiece.Empty, board.getPiece(captured.row(), captured.col()));

        // material is updated correctly
        Map<Byte, Set<ChessPosition>> blackMaterial = board.getMaterial(false);
        assertEquals(0, blackMaterial.get(ChessPiece.Pawn).size());

        // en passant target is reset
        assertEquals("-", board.getFenEnPassantTarget());
    }

    @Test
    public void testMakeMovePromotion() {
        // white pawn promotes to queen
        ChessBoard board = new ChessBoard("8/P7/8/8/8/8/8/8", "-", "-");
        ChessPosition from = new ChessPosition("a7");
        ChessPosition to = new ChessPosition("a8");
        byte promotionType = ChessPiece.Queen;
        PromotionMove move = board.new PromotionMove(from.get1D(), to.get1D(), promotionType);
        board.makeMove(move);

        // piece moved correctly and promoted
        assertEquals(ChessPiece.Empty, board.getPiece(from.row(), from.col()));
        assertEquals(ChessPiece.White | ChessPiece.Queen, board.getPiece(to.row(), to.col()));

        // material updated correctly
        Map<Byte, Set<ChessPosition>> whiteMaterial = board.getMaterial(true);
        assertEquals(true, whiteMaterial.get(ChessPiece.Queen).contains(to));
    }
}