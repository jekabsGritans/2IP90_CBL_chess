package test.engine;

import org.junit.jupiter.api.Test;
import engine.ZobristHash;
import engine.ChessGame;
import static org.junit.jupiter.api.Assertions.*;

public class ZobristHashTest {
    @Test
    void testGetHash() {
        // check that hash is calculated with no errors and is same for same game state
        // (not necessarily different for different games)

        // default starting position
        ChessGame game = new ChessGame();
        ChessGame copy = new ChessGame();

        ZobristHash hash = new ZobristHash();
        assertEquals(true, hash.getHash(game) == hash.getHash(copy));
    }
}
