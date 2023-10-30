package com.jekabsthomas.chess.engine;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for ChessPiece class.
 */
public class ChessPieceTest {
    @Test
    public void testGetType() {
        byte piece = ChessPiece.KNIGHT | ChessPiece.WHITE;
        byte type = ChessPiece.getType(piece);
        assertEquals(ChessPiece.KNIGHT, type);
    }

    @Test
    public void testGetColor() {
        byte piece = ChessPiece.KNIGHT | ChessPiece.WHITE;
        byte color = ChessPiece.getColor(piece);
        assertEquals(ChessPiece.WHITE, color);
    }

    @Test
    public void testIsType() {
        byte piece = ChessPiece.KNIGHT | ChessPiece.WHITE;
        boolean isKnight = ChessPiece.isType(piece, ChessPiece.KNIGHT);
        assertTrue(isKnight);
    }

    @Test
    public void testSetType() {
        byte piece = ChessPiece.KNIGHT | ChessPiece.WHITE;
        byte newPiece = ChessPiece.setType(piece, ChessPiece.PAWN);
        assertEquals(ChessPiece.PAWN | ChessPiece.WHITE, newPiece);
    }

    @Test
    public void testTypeToString() {
        assertEquals("Knight", ChessPiece.typeToString(ChessPiece.KNIGHT));
        assertEquals("Empty", ChessPiece.typeToString(ChessPiece.EMPTY));
        assertEquals("Invalid", ChessPiece.typeToString(ChessPiece.INVALID));
        assertEquals("Invalid", ChessPiece.typeToString((byte) 42));
    }

    @Test
    public void testIsColor() {
        byte piece = ChessPiece.KNIGHT | ChessPiece.WHITE;
        boolean isWhite = ChessPiece.isColor(piece, ChessPiece.WHITE);
        assertTrue(isWhite);
    }

    @Test
    public void testIsEmpty() {
        assertTrue(ChessPiece.isEmpty(ChessPiece.EMPTY));
        assertFalse(ChessPiece.isEmpty(ChessPiece.KNIGHT));
        assertFalse(ChessPiece.isEmpty(ChessPiece.INVALID));
    }

    @Test
    public void testIsInvalid() {
        assertTrue(ChessPiece.isInvalid(ChessPiece.INVALID));
        byte piece = ChessPiece.KNIGHT | ChessPiece.WHITE;
        assertFalse(ChessPiece.isInvalid(piece));
        assertFalse(ChessPiece.isInvalid(ChessPiece.EMPTY));
    }

    @Test
    public void testIsWhite() {
        byte whitePiece = ChessPiece.KNIGHT | ChessPiece.WHITE;
        byte blackPiece = ChessPiece.KNIGHT | ChessPiece.BLACK;
        assertTrue(ChessPiece.isWhite(whitePiece));
        assertFalse(ChessPiece.isWhite(blackPiece));
        assertFalse(ChessPiece.isWhite(ChessPiece.EMPTY));
    }

    @Test
    public void testIsPiece() {
        byte piece = ChessPiece.KNIGHT | ChessPiece.WHITE;
        boolean isPiece = ChessPiece.isPiece(piece);
        assertTrue(isPiece);
        assertFalse(ChessPiece.isPiece(ChessPiece.EMPTY));
        assertFalse(ChessPiece.isPiece(ChessPiece.INVALID));
    }

    @Test
    public void testGetFenCharacter() {

        // assertEquals is ambiguous for char
        assertTrue(
            ChessPiece.getFenCharacter((byte) (ChessPiece.PAWN | ChessPiece.WHITE)) == 'P');
        assertTrue(
            ChessPiece.getFenCharacter((byte) (ChessPiece.PAWN | ChessPiece.BLACK)) == 'p');
        assertTrue(
            ChessPiece.getFenCharacter((byte) (ChessPiece.KNIGHT | ChessPiece.WHITE)) == 'N');
        assertTrue(
            ChessPiece.getFenCharacter((byte) (ChessPiece.KNIGHT | ChessPiece.BLACK)) == 'n');
        assertTrue(
            ChessPiece.getFenCharacter((byte) (ChessPiece.BISHOP | ChessPiece.WHITE)) == 'B');
        assertTrue(
            ChessPiece.getFenCharacter((byte) (ChessPiece.BISHOP | ChessPiece.BLACK)) == 'b');
        assertTrue(
            ChessPiece.getFenCharacter((byte) (ChessPiece.ROOK | ChessPiece.WHITE)) == 'R');
        assertTrue(
            ChessPiece.getFenCharacter((byte) (ChessPiece.ROOK | ChessPiece.BLACK)) == 'r');
        assertTrue(
            ChessPiece.getFenCharacter((byte) (ChessPiece.QUEEN | ChessPiece.WHITE)) == 'Q');
        assertTrue(
            ChessPiece.getFenCharacter((byte) (ChessPiece.QUEEN | ChessPiece.BLACK)) == 'q');
        assertTrue(
            ChessPiece.getFenCharacter((byte) (ChessPiece.KING | ChessPiece.WHITE)) == 'K');
        assertTrue(
            ChessPiece.getFenCharacter((byte) (ChessPiece.KING | ChessPiece.BLACK)) == 'k');
        assertTrue(
            ChessPiece.getFenCharacter(ChessPiece.EMPTY) == '1');
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFenCharacterInvalid() {
        ChessPiece.getFenCharacter(ChessPiece.INVALID);
    }

    @Test
    public void testGetPieceFromFenCharacter() {
        assertEquals(
            ChessPiece.PAWN | ChessPiece.WHITE, ChessPiece.getPieceFromFenCharacter('P'));
        assertEquals(
            ChessPiece.PAWN | ChessPiece.BLACK, ChessPiece.getPieceFromFenCharacter('p'));
        assertEquals(
            ChessPiece.KNIGHT | ChessPiece.WHITE, ChessPiece.getPieceFromFenCharacter('N'));
        assertEquals(
            ChessPiece.KNIGHT | ChessPiece.BLACK, ChessPiece.getPieceFromFenCharacter('n'));
        assertEquals(
            ChessPiece.BISHOP | ChessPiece.WHITE, ChessPiece.getPieceFromFenCharacter('B'));
        assertEquals(
            ChessPiece.BISHOP | ChessPiece.BLACK, ChessPiece.getPieceFromFenCharacter('b'));
        assertEquals(
            ChessPiece.ROOK | ChessPiece.WHITE, ChessPiece.getPieceFromFenCharacter('R'));
        assertEquals(
            ChessPiece.ROOK | ChessPiece.BLACK, ChessPiece.getPieceFromFenCharacter('r'));
        assertEquals(
            ChessPiece.QUEEN | ChessPiece.WHITE, ChessPiece.getPieceFromFenCharacter('Q'));
        assertEquals(
            ChessPiece.QUEEN | ChessPiece.BLACK, ChessPiece.getPieceFromFenCharacter('q'));
        assertEquals(
            ChessPiece.KING | ChessPiece.WHITE, ChessPiece.getPieceFromFenCharacter('K'));
        assertEquals(
            ChessPiece.KING | ChessPiece.BLACK, ChessPiece.getPieceFromFenCharacter('k'));
        assertEquals(
            ChessPiece.EMPTY, ChessPiece.getPieceFromFenCharacter('1'));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenFenCharacterInvalid() {
        ChessPiece.getPieceFromFenCharacter('A');
    }
}
