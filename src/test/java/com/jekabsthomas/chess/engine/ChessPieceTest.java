package com.jekabsthomas.chess.engine;

import org.junit.*;
import static org.junit.Assert.*;

public class ChessPieceTest {
    @Test
    public void testGetType() {
        byte piece = ChessPiece.Knight | ChessPiece.White;
        byte type = ChessPiece.getType(piece);
        assertEquals(ChessPiece.Knight, type);
    }

    @Test
    public void testGetColor() {
        byte piece = ChessPiece.Knight | ChessPiece.White;
        byte color = ChessPiece.getColor(piece);
        assertEquals(ChessPiece.White, color);
    }

    @Test
    public void testIsType() {
        byte piece = ChessPiece.Knight | ChessPiece.White;
        boolean isKnight = ChessPiece.isType(piece, ChessPiece.Knight);
        assertTrue(isKnight);
    }

    @Test
    public void testSetType() {
        byte piece = ChessPiece.Knight | ChessPiece.White;
        byte newPiece = ChessPiece.setType(piece, ChessPiece.Pawn);
        assertEquals(ChessPiece.Pawn | ChessPiece.White, newPiece);
    }

    @Test
    public void testTypeToString() {
        assertEquals("Knight", ChessPiece.typeToString(ChessPiece.Knight));
        assertEquals("Empty", ChessPiece.typeToString(ChessPiece.Empty));
        assertEquals("Invalid", ChessPiece.typeToString(ChessPiece.Invalid));
        assertEquals("Invalid", ChessPiece.typeToString((byte) 42));
    }

    @Test
    public void testIsColor() {
        byte piece = ChessPiece.Knight | ChessPiece.White;
        boolean isWhite = ChessPiece.isColor(piece, ChessPiece.White);
        assertTrue(isWhite);
    }

    @Test
    public void testIsEmpty() {
        assertTrue(ChessPiece.isEmpty(ChessPiece.Empty));
        assertFalse(ChessPiece.isEmpty(ChessPiece.Knight));
        assertFalse(ChessPiece.isEmpty(ChessPiece.Invalid));
    }

    @Test
    public void testIsInvalid() {
        assertTrue(ChessPiece.isInvalid(ChessPiece.Invalid));
        byte piece = ChessPiece.Knight | ChessPiece.White;
        assertFalse(ChessPiece.isInvalid(piece));
        assertFalse(ChessPiece.isInvalid(ChessPiece.Empty));
    }

    @Test
    public void testIsWhite() {
        byte whitePiece = ChessPiece.Knight | ChessPiece.White;
        byte blackPiece = ChessPiece.Knight | ChessPiece.Black;
        assertTrue(ChessPiece.isWhite(whitePiece));
        assertFalse(ChessPiece.isWhite(blackPiece));
        assertFalse(ChessPiece.isWhite(ChessPiece.Empty));
    }

    @Test
    public void testIsPiece() {
        byte piece = ChessPiece.Knight | ChessPiece.White;
        boolean isPiece = ChessPiece.isPiece(piece);
        assertTrue(isPiece);
        assertFalse(ChessPiece.isPiece(ChessPiece.Empty));
        assertFalse(ChessPiece.isPiece(ChessPiece.Invalid));
    }

    @Test
    public void testGetFenCharacter() {

        // assertEquals is ambiguous for char
        assertTrue(ChessPiece.getFenCharacter((byte) (ChessPiece.Pawn | ChessPiece.White)) == 'P');
        assertTrue(ChessPiece.getFenCharacter((byte) (ChessPiece.Pawn | ChessPiece.Black)) == 'p');
        assertTrue(ChessPiece.getFenCharacter((byte) (ChessPiece.Knight | ChessPiece.White)) == 'N');
        assertTrue(ChessPiece.getFenCharacter((byte) (ChessPiece.Knight | ChessPiece.Black)) == 'n');
        assertTrue(ChessPiece.getFenCharacter((byte) (ChessPiece.Bishop | ChessPiece.White)) == 'B');
        assertTrue(ChessPiece.getFenCharacter((byte) (ChessPiece.Bishop | ChessPiece.Black)) == 'b');
        assertTrue(ChessPiece.getFenCharacter((byte) (ChessPiece.Rook | ChessPiece.White)) == 'R');
        assertTrue(ChessPiece.getFenCharacter((byte) (ChessPiece.Rook | ChessPiece.Black)) == 'r');
        assertTrue(ChessPiece.getFenCharacter((byte) (ChessPiece.Queen | ChessPiece.White)) == 'Q');
        assertTrue(ChessPiece.getFenCharacter((byte) (ChessPiece.Queen | ChessPiece.Black)) == 'q');
        assertTrue(ChessPiece.getFenCharacter((byte) (ChessPiece.King | ChessPiece.White)) == 'K');
        assertTrue(ChessPiece.getFenCharacter((byte) (ChessPiece.King | ChessPiece.Black)) == 'k');
        assertTrue(ChessPiece.getFenCharacter(ChessPiece.Empty) == '1');
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetFenCharacterInvalid() {
        ChessPiece.getFenCharacter(ChessPiece.Invalid);
    }

    @Test
    public void testGetPieceFromFenCharacter() {
        assertEquals(ChessPiece.Pawn | ChessPiece.White, ChessPiece.getPieceFromFenCharacter('P'));
        assertEquals(ChessPiece.Pawn | ChessPiece.Black, ChessPiece.getPieceFromFenCharacter('p'));
        assertEquals(ChessPiece.Knight | ChessPiece.White, ChessPiece.getPieceFromFenCharacter('N'));
        assertEquals(ChessPiece.Knight | ChessPiece.Black, ChessPiece.getPieceFromFenCharacter('n'));
        assertEquals(ChessPiece.Bishop | ChessPiece.White, ChessPiece.getPieceFromFenCharacter('B'));
        assertEquals(ChessPiece.Bishop | ChessPiece.Black, ChessPiece.getPieceFromFenCharacter('b'));
        assertEquals(ChessPiece.Rook | ChessPiece.White, ChessPiece.getPieceFromFenCharacter('R'));
        assertEquals(ChessPiece.Rook | ChessPiece.Black, ChessPiece.getPieceFromFenCharacter('r'));
        assertEquals(ChessPiece.Queen | ChessPiece.White, ChessPiece.getPieceFromFenCharacter('Q'));
        assertEquals(ChessPiece.Queen | ChessPiece.Black, ChessPiece.getPieceFromFenCharacter('q'));
        assertEquals(ChessPiece.King | ChessPiece.White, ChessPiece.getPieceFromFenCharacter('K'));
        assertEquals(ChessPiece.King | ChessPiece.Black, ChessPiece.getPieceFromFenCharacter('k'));
        assertEquals(ChessPiece.Empty, ChessPiece.getPieceFromFenCharacter('1'));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenFenCharacterInvalid() {
        ChessPiece.getPieceFromFenCharacter('A');
    }
}
