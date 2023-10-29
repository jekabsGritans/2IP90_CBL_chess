package com.jekabsthomas.chess.engine;

import org.junit.*;
import static org.junit.Assert.*;

public class ZobristHashTest {
    @Test
    public void testGetHash() {
        // check that hash is calculated with no errors and is same for same game state
        // (not necessarily different for different games)

        // default starting position
        ChessGame game = new ChessGame();
        ChessGame copy = new ChessGame();

        ZobristHash hash = new ZobristHash();
        assertEquals(true, hash.getHash(game) == hash.getHash(copy));
    }
}
