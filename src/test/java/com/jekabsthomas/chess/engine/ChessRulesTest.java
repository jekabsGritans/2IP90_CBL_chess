package com.jekabsthomas.chess.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.jekabsthomas.chess.engine.ChessBoard.ChessMove;
import com.jekabsthomas.chess.engine.ChessBoard.ChessPosition;
import java.util.List;
import org.junit.Test;

/**
 * Tests for ChessRules class.
 */
public class ChessRulesTest {
    @Test
    public void testCanCaptureKing() {
        // test can capture king
        ChessBoard board = new ChessBoard("Qk6/8/8/8/8/8/8/8", "-", "-");
        assertTrue(ChessRules.canCaptureKing(board, true));

        // test cannot capture king
        board = new ChessBoard("QPk5/8/8/8/8/8/8/8", "-", "-");
        assertFalse(ChessRules.canCaptureKing(board, true));
    }

    @Test
    public void testGetLegalMoves() {
        // in checkmate 0 legal moves for black
        /*
           a b c d e f g h  
         8 Q             k 8
         7 Q               7
         6   Q             6
         5     Q           5
         4       Q         4
         3         Q       3
         2           Q     2
         1 K           Q p 1
           a b c d e f g h  
        */
        ChessBoard board = new ChessBoard("Q6k/Q7/1Q6/2Q5/3Q4/4Q3/5Q2/K5Qp", "-", "-");
        List<ChessMove> legalMoves = ChessRules.getLegalMoves(board, false);
        assertEquals(0, legalMoves.size());

        // 1 legal move
        /* 
           a b c d e f g h  
         8 Q         B   B 8
         7 Q         B B k 7
         6   Q           B 6
         5     Q           5
         4                 4
         3         Q       3
         2           Q     2
         1 K           Q   1
           a b c d e f g h  
        */
        board = new ChessBoard("Q4B1B/Q5Bk/1Q4BB/2Q5/8/4Q3/5Q2/K5Qp", "-", "-");
        legalMoves = ChessRules.getLegalMoves(board, false);
        ChessPosition from = new ChessPosition("h7");
        ChessPosition to = new ChessPosition("g8");
        ChessMove move = board.new ChessMove(from.get1D(), to.get1D());
        assertEquals(1, legalMoves.size());
        assertEquals(move, legalMoves.get(0));
    }

    @Test
    public void testGetLegalMovesForPiece() {
        // in checkmate 0 legal moves from  h8 (black king)
        /*
           a b c d e f g h  
         8 Q             k 8
         7 Q               7
         6   Q             6
         5     Q           5
         4       Q         4
         3         Q       3
         2           Q     2
         1 K           Q p 1
           a b c d e f g h  
        */
        ChessBoard board = new ChessBoard("Q6k/Q7/1Q6/2Q5/3Q4/4Q3/5Q2/K5Qp", "-", "-");
        ChessPosition from = new ChessPosition("h8");
        List<ChessMove> legalMoves = ChessRules.getLegalMoves(board, false, from.get1D());
        assertEquals(0, legalMoves.size());

        // 1 legal move from h7
        /* 
           a b c d e f g h  
         8 Q         B   B 8
         7 Q         B B k 7
         6   Q           B 6
         5     Q           5
         4                 4
         3         Q       3
         2           Q     2
         1 K           Q   1
           a b c d e f g h  
        */
        board = new ChessBoard("Q4B1B/Q5Bk/1Q4BB/2Q5/8/4Q3/5Q2/K5Qp", "-", "-");
        from = new ChessPosition("h7");
        ChessPosition to = new ChessPosition("g8");
        legalMoves = ChessRules.getLegalMoves(board, false, from.get1D());
        ChessMove move = board.new ChessMove(from.get1D(), to.get1D());
        assertEquals(1, legalMoves.size());
        assertEquals(move, legalMoves.get(0));
    }

    @Test
    public void testIsInsufficientMaterial() {
        // lone kings
        ChessBoard board = new ChessBoard("kK6/8/8/8/8/8/8/8", "-", "-");
        assertTrue(ChessRules.isInsufficientMaterial(board));

        // one bishop
        board = new ChessBoard("kKb5/8/8/8/8/8/8/8", "-", "-");
        assertTrue(ChessRules.isInsufficientMaterial(board));

        // one knight
        board = new ChessBoard("kKn5/8/8/8/8/8/8/8", "-", "-");
        assertTrue(ChessRules.isInsufficientMaterial(board));

        // two knights
        board = new ChessBoard("kKnn4/8/8/8/8/8/8/8", "-", "-");
        assertFalse(ChessRules.isInsufficientMaterial(board));

        // two bishops
        board = new ChessBoard("kKbb4/8/8/8/8/8/8/8", "-", "-");
        assertFalse(ChessRules.isInsufficientMaterial(board));

        // bishop and knight
        board = new ChessBoard("kKbn4/8/8/8/8/8/8/8", "-", "-");
        assertFalse(ChessRules.isInsufficientMaterial(board));

        // pawn
        board = new ChessBoard("kKp5/8/8/8/8/8/8/8", "-", "-");
        assertFalse(ChessRules.isInsufficientMaterial(board));

        // queen
        board = new ChessBoard("kKq5/8/8/8/8/8/8/8", "-", "-");
        assertFalse(ChessRules.isInsufficientMaterial(board));

        // rook
        board = new ChessBoard("kKr5/8/8/8/8/8/8/8", "-", "-");
        assertFalse(ChessRules.isInsufficientMaterial(board));
    }
}
