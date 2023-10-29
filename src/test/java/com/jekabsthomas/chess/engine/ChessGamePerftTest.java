package com.jekabsthomas.chess.engine;

import com.jekabsthomas.chess.engine.ChessBoard.ChessMove;
import org.junit.*;
import static org.junit.Assert.*;

public class ChessGamePerftTest {

    /*
     * Counts number of nodes in the game tree at the given depth.
     * Comparing result with known hard cases allows us to verify that the game tree is being
     * generated correctly. Hence, that the legal moves are being generated correctly.
     * 
     * Depth is limited because of runtime.
     * 
     * Data from https://www.chessprogramming.org/Perft_Results
     */

    @Test
    public void testStartingPosition() {
        ChessGame game = new ChessGame("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");
        assertEquals(20, perft(game, 1));
        assertEquals(400, perft(game, 2));
        assertEquals(8902, perft(game, 3));
    }

    @Test
    public void testPositionTwo() {
        ChessGame game = new ChessGame("r3k2r/p1ppqpb1/bn2pnp1/3PN3/1p2P3/2N2Q1p/PPPBBPPP/R3K2R w KQkq - 0 1");
        assertEquals(48, perft(game, 1));
        assertEquals(2039, perft(game, 2));
    }

    @Test
    public void testPositionThree() {
        ChessGame game = new ChessGame("8/2p5/3p4/KP5r/1R3p1k/8/4P1P1/8 w - - 0 1");
        assertEquals(14, perft(game, 1));
        assertEquals(191, perft(game, 2));
        assertEquals(2812, perft(game, 3));
    }

    @Test
    public void testPositionFour() {
        ChessGame game = new ChessGame("r3k2r/Pppp1ppp/1b3nbN/nP6/BBP1P3/q4N2/Pp1P2PP/R2Q1RK1 w kq - 0 1");
        assertEquals(6, perft(game, 1));
        assertEquals(264, perft(game, 2));
        assertEquals(9467, perft(game, 3));
    }

    @Test
    public void testPositionFive() {
        ChessGame game = new ChessGame("rnbq1k1r/pp1Pbppp/2p5/8/2B5/8/PPP1NnPP/RNBQK2R w KQ - 1 8");
        assertEquals(44, perft(game, 1));
        assertEquals(1486, perft(game, 2));
    }

    @Test
    public void testPositionSix() {
        ChessGame game = new ChessGame("r4rk1/1pp1qppp/p1np1n2/2b1p1B1/2B1P1b1/P1NP1N2/1PP1QPPP/R4RK1 w - - 0 10");
        assertEquals(46, perft(game, 1));
        assertEquals(2079, perft(game, 2));
    }

    /*
     * Counts the number of nodes in the game tree at the given depth.
     */
    private int perft(ChessGame game, int depth) {
        if (depth == 0) {
            return 1;
        }

        int nodes = 0;
        for (ChessMove move : game.getLegalMoves()) {
            ChessGame newGame = makeMove(game, move);
            nodes += perft(newGame, depth - 1);
        }

        return nodes;
    }

    /*
     * Makes a move on a copy of the game and return the copy.
     */
    private ChessGame makeMove(ChessGame game, ChessMove move) {
        ChessGame newGame = new ChessGame(game);
        newGame.makeMove(move);
        return newGame;
    }
}
