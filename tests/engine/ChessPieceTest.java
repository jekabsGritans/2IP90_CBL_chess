package test.engine;

import org.junit.jupiter.api.Test;

import engine.ChessPiece;

import static org.junit.jupiter.api.Assertions.*;

public class ChessPieceTest {
    @Test
    void testGetType() {
        byte piece = ChessPiece.Knight | ChessPiece.White;
        byte type = ChessPiece.getType(piece);
        assertEquals(ChessPiece.Knight, type);
    }

    @Test
    void testGetColor() {
        byte piece = ChessPiece.Knight | ChessPiece.White;
        byte color = ChessPiece.getColor(piece);
        assertEquals(ChessPiece.White, color);
    }

    @Test
    void testIsType() {
        byte piece = ChessPiece.Knight | ChessPiece.White;
        boolean isKnight = ChessPiece.isType(piece, ChessPiece.Knight);
        assertTrue(isKnight);
    }

    @Test
    void testSetType() {
        byte piece = ChessPiece.Knight | ChessPiece.White;
        byte newPiece = ChessPiece.setType(piece, ChessPiece.Pawn);
        assertEquals(ChessPiece.Pawn | ChessPiece.White, newPiece);
    }

    @Test
    void testTypeToString() {
        assertEquals("Knight", ChessPiece.typeToString(ChessPiece.Knight));
        assertEquals("Empty", ChessPiece.typeToString(ChessPiece.Empty));
        assertEquals("Invalid", ChessPiece.typeToString(ChessPiece.Invalid));
        assertEquals("Invalid", ChessPiece.typeToString((byte) 42));
    }

    @Test
    void testIsColor() {
        byte piece = ChessPiece.Knight | ChessPiece.White;
        boolean isWhite = ChessPiece.isColor(piece, ChessPiece.White);
        assertTrue(isWhite);
    }

    @Test
    void testIsEmpty() {
        assertTrue(ChessPiece.isEmpty(ChessPiece.Empty));
        assertFalse(ChessPiece.isEmpty(ChessPiece.Knight));
        assertFalse(ChessPiece.isEmpty(ChessPiece.Invalid));
    }

    @Test
    void testIsInvalid() {
        assertTrue(ChessPiece.isInvalid(ChessPiece.Invalid));
        byte piece = ChessPiece.Knight | ChessPiece.White;
        assertFalse(ChessPiece.isInvalid(piece));
        assertFalse(ChessPiece.isInvalid(ChessPiece.Empty));
    }

    @Test
    void testIsWhite() {
        byte whitePiece = ChessPiece.Knight | ChessPiece.White;
        byte blackPiece = ChessPiece.Knight | ChessPiece.Black;
        assertTrue(ChessPiece.isWhite(whitePiece));
        assertFalse(ChessPiece.isWhite(blackPiece));
        assertFalse(ChessPiece.isWhite(ChessPiece.Empty));
    }

    @Test
    void testIsPiece() {
        byte piece = ChessPiece.Knight | ChessPiece.White;
        boolean isPiece = ChessPiece.isPiece(piece);
        assertTrue(isPiece);
        assertFalse(ChessPiece.isPiece(ChessPiece.Empty));
        assertFalse(ChessPiece.isPiece(ChessPiece.Invalid));
    }

    @Test
    void testGetFenCharacter() {
        assertEquals('P', ChessPiece.getFenCharacter((byte) (ChessPiece.Pawn | ChessPiece.White)));
        assertEquals('p', ChessPiece.getFenCharacter((byte) (ChessPiece.Pawn | ChessPiece.Black)));
        assertEquals('N', ChessPiece.getFenCharacter((byte) (ChessPiece.Knight | ChessPiece.White)));
        assertEquals('n', ChessPiece.getFenCharacter((byte) (ChessPiece.Knight | ChessPiece.Black)));
        assertEquals('B', ChessPiece.getFenCharacter((byte) (ChessPiece.Bishop | ChessPiece.White)));
        assertEquals('b', ChessPiece.getFenCharacter((byte) (ChessPiece.Bishop | ChessPiece.Black)));
        assertEquals('R', ChessPiece.getFenCharacter((byte) (ChessPiece.Rook | ChessPiece.White)));
        assertEquals('r', ChessPiece.getFenCharacter((byte) (ChessPiece.Rook | ChessPiece.Black)));
        assertEquals('Q', ChessPiece.getFenCharacter((byte) (ChessPiece.Queen | ChessPiece.White)));
        assertEquals('q', ChessPiece.getFenCharacter((byte) (ChessPiece.Queen | ChessPiece.Black)));
        assertEquals('K', ChessPiece.getFenCharacter((byte) (ChessPiece.King | ChessPiece.White)));
        assertEquals('k', ChessPiece.getFenCharacter((byte) (ChessPiece.King | ChessPiece.Black)));
        assertEquals('1', ChessPiece.getFenCharacter(ChessPiece.Empty));

        assertThrows(IllegalArgumentException.class, () -> {
            ChessPiece.getFenCharacter(ChessPiece.Invalid);
        });
    }

    @Test
    void testGetPieceFromFenCharacter() {
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

        assertThrows(IllegalArgumentException.class, () -> {
            ChessPiece.getPieceFromFenCharacter('A');
        });
    }
}
