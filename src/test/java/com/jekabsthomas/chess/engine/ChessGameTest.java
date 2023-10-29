package com.jekabsthomas.chess.engine;

import org.junit.*;
import static org.junit.Assert.*;

import com.jekabsthomas.chess.engine.ChessGame.GameState;
import java.util.List;
import com.jekabsthomas.chess.engine.ChessBoard.ChessMove;
import com.jekabsthomas.chess.engine.ChessBoard.ChessPosition;

public class ChessGameTest {
    @Test
    public void testGetFenString() {
        // tests whether the reconstructed FEN string is the same as the input
        String fenStr = "rnbqkbnr/pppKppQp/8/8/8/QQ6/PPPPPPPP/RNBQKBNR w KQkq a3 42 43";
        ChessGame game = new ChessGame(fenStr);
        assertEquals(fenStr, game.getFenString());
    }

    @Test
    public void testGetLegalMoves() {
        // tests whether the legal moves all move pieces of the current player
        ChessGame game = new ChessGame();
        List<ChessMove> legalMoves = game.getLegalMoves();
        boolean isWhiteMove = game.isWhiteMove();
        for (ChessMove move : legalMoves) {
            byte fromPiece = game.getBoard().getPiece(move.from1D);
            assertEquals(true, ChessPiece.isWhite(fromPiece) == isWhiteMove);
        }
    }

    @Test
    public void testEnterCheckmate() {
        /*
           a b c d e f g h  
         8 Q             k 8
         7 Q               7
         6   Q             6
         5     Q           5
         4       Q         4
         3         Q       3
         2           Q     2
         1 K           Q   1
           a b c d e f g h  
        */
        // white's turn before nullMove
        ChessGame game = new ChessGame("Q6k/Q7/1Q6/2Q5/3Q4/4Q3/5Q2/K5Qp w - - 0 1");
        ChessBoard board = game.getBoard();
        ChessPosition pos = new ChessPosition ("h1");
        ChessMove nullMove = board.new ChessMove(pos.get1D(), pos.get1D()); // move black pawn to itself
        ChessGame.GameState state = game.makeMove(nullMove);
        assertEquals(GameState.WHITE_WINS, state);
    }

    @Test
    public void testEnterStalemate() {
        /* 
           a b c d e f g h  
         8 Q             B 8
         7 Q           B k 7
         6   Q           B 6
         5     Q           5
         4                 4
         3         Q       3
         2           Q     2
         1 K           Q   1
           a b c d e f g h  
        */
        // white's turn before nullMove
        ChessGame game = new ChessGame("Q6B/Q5Bk/1Q5B/2Q5/8/4Q3/5Q2/K5Qp w - - 0 1");
        ChessBoard board = game.getBoard();
        ChessPosition pos = new ChessPosition ("h1");
        ChessMove nullMove = board.new ChessMove(pos.get1D(), pos.get1D()); // move black pawn to itself
        ChessGame.GameState state = game.makeMove(nullMove);
        assertEquals(GameState.STALEMATE, state);
    }

    @Test
    public void testDrawByInsufficientMaterial() {
        // don't need to test all possible combinations of insufficient material
        // as we're just testing whether the game enters a draw state
        ChessGame game = new ChessGame("k7/8/K7/8/8/8/8/7p w - - 0 1");
        ChessBoard board = game.getBoard();
        ChessPosition pos = new ChessPosition ("h1");
        ChessMove nullMove = board.new ChessMove(pos.get1D(), pos.get1D()); // move black pawn to itself
        ChessGame.GameState state = game.makeMove(nullMove);
        assertEquals(GameState.DRAW, state);
    }

    @Test
    public void testDrawByThreefoldRepetition() { //TODO add game state to map upon initialization
        ChessGame game = new ChessGame("KQ5R/QQ6/8/8/8/8/6qq/r5qk w - - 0 1");
        ChessBoard board = game.getBoard();
        ChessPosition whiteRookA = new ChessPosition("h8");
        ChessPosition whiteRookB = new ChessPosition("h7");
        ChessPosition blackRookA = new ChessPosition("a1");
        ChessPosition blackRookB = new ChessPosition("a2");

        ChessMove whiteRookAMove = board.new ChessMove(whiteRookA.get1D(), whiteRookB.get1D());
        ChessMove whiteRookBMove = board.new ChessMove(whiteRookB.get1D(), whiteRookA.get1D());
        ChessMove blackRookAMove = board.new ChessMove(blackRookA.get1D(), blackRookB.get1D());
        ChessMove blackRookBMove = board.new ChessMove(blackRookB.get1D(), blackRookA.get1D());

        game.makeMove(whiteRookAMove);
        game.makeMove(blackRookAMove);
        game.makeMove(whiteRookBMove);
        game.makeMove(blackRookBMove);
        // back at initial position

        game.makeMove(whiteRookAMove);
        game.makeMove(blackRookAMove);
        game.makeMove(whiteRookBMove);
        GameState state = game.makeMove(blackRookBMove);
        // back at initial position

        assertEquals(GameState.DRAW, state);
    }

    @Test
    public void testDrawByFiftyMoveRule() {
        ChessGame game = new ChessGame("KQ5R/QQ6/8/8/8/8/6qq/r5qk w - - 99 1");
        ChessBoard board = game.getBoard();
        ChessPosition from = new ChessPosition("h8");
        ChessPosition to = new ChessPosition("h7");
        ChessMove move = board.new ChessMove(from.get1D(), to.get1D());
        GameState state = game.makeMove(move);
        assertEquals(GameState.DRAW, state);
    }
}
