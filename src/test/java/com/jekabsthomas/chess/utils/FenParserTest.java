package com.jekabsthomas.chess.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests for FenParser class.
 */
public class FenParserTest {
    @Test
    public void testParseFenValid() {
        String fenStr = "rnbqkbnr/pppKppQp/8/8/8/QQ6/PPPPPPPP/RNBQKBNR w KQkq a3 42 43";
        FenParser.FenResult fen = FenParser.parseFen(fenStr);
        assertEquals("rnbqkbnr/pppKppQp/8/8/8/QQ6/PPPPPPPP/RNBQKBNR", fen.piecePositions);
        assertEquals("w", fen.activeColor);
        assertEquals("KQkq", fen.castlingAvailability);
        assertEquals("a3", fen.enPassantTarget);
        assertEquals(42, fen.halfMoveClock);
        assertEquals(43, fen.fullMoveNumber);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testParseFenInvalid() {
        String fenStr = "rnbqkbnr/pppKppQp/8/8/8/QQ6/PPPPPPPP/RNBQKBNR w KQkq a3 42 43 44";
        FenParser.parseFen(fenStr);
    }
}
