import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JLabel;

import engine.ChessBoard;
import engine.ChessGame;
import engine.ChessPiece;
import engine.ChessBoard.ChessMove;
import engine.ChessBoard.ChessPosition;
import engine.ChessGame.GameState;
import java.util.concurrent.TimeUnit;

import java.util.*;

public class ChessScene extends Scene {
    Point boardPos = new Point(50, 50);
    Point boardSize = new Point(800, 800);
    int tileAmount = 8;
    double pieceSizeModifier = 0.8;
    ChessGame chessGame;
    byte turnColor = ChessPiece.White;
    ArrayList<ChessMove> currentPiecePossibleMoves = new ArrayList<ChessMove>();
    ArrayList<PieceEntity> pieces = new ArrayList<PieceEntity>();
    ArrayList<IndicatorEntity> moveIndicators = new ArrayList<IndicatorEntity>();
    Image indicatorImage;
    Entity whiteBanner;
    Entity blackBanner;
    Entity stalemateBanner;
    boolean ended = false;
    float endTime = 0.0f;

    public ChessScene() {
        super();
        frame.getContentPane().setBackground(Color.GRAY);
        initGame();
        initWinBanner();
        initPieces();
        initMoveIndicators();
        initBoard();
    }

    public void initWinBanner() {
        whiteBanner = new Entity();
        whiteBanner.setPos(new Point(200, 200));
        whiteBanner.setSize(new Point(600, 315));
        String imgPath = System.getProperty("user.dir") + "/textures/whiteWins.png";
        File imgFile = new File(imgPath);
        whiteBanner.loadTexture(imgFile);
        whiteBanner.graphic.setVisible(false);
        addEntity(whiteBanner);

        blackBanner = new Entity();
        blackBanner.setPos(new Point(200, 200));
        blackBanner.setSize(new Point(600, 315));
        imgPath = System.getProperty("user.dir") + "/textures/blackWins.png";
        imgFile = new File(imgPath);
        blackBanner.loadTexture(imgFile);
        blackBanner.graphic.setVisible(false);
        addEntity(blackBanner);

        stalemateBanner = new Entity();
        stalemateBanner.setPos(new Point(200, 200));
        stalemateBanner.setSize(new Point(600, 315));
        imgPath = System.getProperty("user.dir") + "/textures/stalemate.png";
        imgFile = new File(imgPath);
        stalemateBanner.loadTexture(imgFile);
        stalemateBanner.graphic.setVisible(false);
        addEntity(stalemateBanner);
    }

    public void showWinBanner(GameState state) {
        if(state == GameState.BLACK_WINS) {
            blackBanner.graphic.setVisible(true);
        }
        else if(state == GameState.WHITE_WINS) {
            whiteBanner.graphic.setVisible(true);
        }
        else if(state == GameState.STALEMATE) {
            stalemateBanner.graphic.setVisible(true);
        }
        ended = true;
    }



    public void initMoveIndicators() {
        for(int i = 0; i < 50; i++) {
            IndicatorEntity indicator = new IndicatorEntity();
            indicator.setPos(new Point(0, 0));
            indicator.setSize(new Point((int)((boardSize.x/tileAmount)*pieceSizeModifier), (int)((boardSize.y/tileAmount)*pieceSizeModifier)));
            moveIndicators.add(indicator);
            indicator.graphic.setVisible(false);
            addEntity(indicator);
        }
    }


    public ArrayList<Point> getPossibleMovePositions(PieceEntity piece) {
        ChessPosition piecepos = pointToChessPos(piece.getPos());
        ArrayList<ChessMove> possibleMoves = new ArrayList<ChessMove>(chessGame.getLegalMoves(piecepos));
        ArrayList<Point> possiblePositions = new ArrayList<Point>();

        for(int i = 0; i < possibleMoves.size(); i++) {
            possiblePositions.add(chessPosToPoint(possibleMoves.get(i).getTo()));
            System.out.println(possibleMoves.get(i).getTo());
        }
        showIndicators(possiblePositions);
        currentPiecePossibleMoves = possibleMoves;
        return possiblePositions;
    }
    // moveIndex being the index of the move made, which should correspond to a move kept in the currentPiecePossibleMoves list
    public void nextTurn(int moveIndex) {
        ChessMove madeMove = currentPiecePossibleMoves.get(moveIndex);
        GameState state = chessGame.makeMove(madeMove);
        if(state != GameState.ACTIVE) {
            showWinBanner(state);
        }
        updateBoardPieces(madeMove);
        turnColor = turnColor == ChessPiece.White ? ChessPiece.Black : ChessPiece.White;
    }

    public void updateBoardPieces(ChessMove move) {
        Point landedPos = chessPosToPoint(move.getTo());
        for(int i = 0; i < pieces.size(); i++) {
            if(pieces.get(i).pieceColor != turnColor && pieces.get(i).getPos().x == landedPos.x && pieces.get(i).getPos().y == landedPos.y) {   
                removeEntity(pieces.get(i));
            }
        }
    }

    public void showIndicators(List<Point> positions) {
        for(int i = 0; i < positions.size(); i++) {
            moveIndicators.get(i).setPos(positions.get(i));
            moveIndicators.get(i).graphic.setVisible(true);
        }
    }

    public void removeIndicators() {
        for(int i = 0; i < moveIndicators.size(); i++) {
            moveIndicators.get(i).graphic.setVisible(false);
        }
    }

    public ChessPosition pointToChessPos(Point point) {
        int posMod = (int)(((boardSize.x/tileAmount) - ((boardSize.x/tileAmount)*pieceSizeModifier))/2);
        int row = (point.y-posMod-boardPos.y)/(boardSize.y/tileAmount);
        int col = (point.x-posMod-boardPos.x)/(boardSize.x/tileAmount);
        return new ChessPosition(row, col);
    }

    public Point chessPosToPoint(ChessPosition pos) {
        int posMod = (int)(((boardSize.x/tileAmount) - ((boardSize.x/tileAmount)*pieceSizeModifier))/2);
        return new Point(pos.col()*(boardSize.x/tileAmount)+boardPos.x + posMod, pos.row()*(boardSize.y/tileAmount)+boardPos.y + posMod);
    }

    public void initGame() {
        chessGame = new ChessGame();
    }
 
    public void initPieces() {
        double tileSize = boardSize.x / tileAmount;
        Point pieceSize = new Point((int)(tileSize*pieceSizeModifier), (int)(tileSize*pieceSizeModifier));
        ArrayList<Point> startPositions = new ArrayList<Point>();
        byte[] startTypes = new byte[] {ChessPiece.Rook, ChessPiece.Knight, ChessPiece.Bishop, ChessPiece.Queen, ChessPiece.King, ChessPiece.Bishop, ChessPiece.Knight, ChessPiece.Rook};
        // Initialize white positions
        for(int y = tileAmount-1; y >= tileAmount-2; y--) {
            for(int x = 0 ; x < tileAmount; x++) {
                Point piecePos = new Point(boardPos.x + (boardSize.x/tileAmount)*x, boardPos.y + (boardSize.y/tileAmount)*y);
                piecePos.x += (tileSize-tileSize*pieceSizeModifier)*0.5;
                piecePos.y += (tileSize-tileSize*pieceSizeModifier)*0.5;
                startPositions.add(piecePos);
            }
        }

        // Initialize white pieces
        for(int i = 0; i < tileAmount*2; i++) {
            byte newType = i < tileAmount ? startTypes[i] : ChessPiece.Pawn;
            PieceEntity newPiece = new PieceEntity(newType, ChessPiece.White);
            newPiece.setSize(pieceSize);
            newPiece.setPos(startPositions.get(i));
            addEntity(newPiece);
        }
        // Initialize black positions
        startPositions = new ArrayList<Point>();
        for(int y = 0; y < 2; y++) {
            for(int x = 0; x < tileAmount; x++) {
                Point piecePos = new Point(boardPos.x + (boardSize.x/tileAmount)*x, boardPos.y + (boardSize.y/tileAmount)*y);
                piecePos.x += (tileSize-tileSize*pieceSizeModifier)*0.5;
                piecePos.y += (tileSize-tileSize*pieceSizeModifier)*0.5;
                startPositions.add(piecePos);
            }
        }
        // Initialize black pieces
        for(int i = 0; i < tileAmount*2; i++) {
            byte newType = i < tileAmount ? startTypes[i] : ChessPiece.Pawn;
            PieceEntity newPiece = new PieceEntity(newType, ChessPiece.Black);
            newPiece.setSize(pieceSize);
            newPiece.setPos(startPositions.get(i));
            addEntity(newPiece);
        }
    }

    public void initBoard() {
        // Initialize FloorTiles
        for(int x = 0; x < tileAmount; x++) {
            for(int y = 0; y < tileAmount; y++) {
                byte tileColor = (y+(x+1*tileAmount)) % 2 == 0 ? ChessPiece.Black : ChessPiece.White;
                FloorTileEntity tile = new FloorTileEntity(tileColor);
                tile.setPos(new Point(boardPos.x + (boardSize.x/tileAmount)*x, boardPos.y + (boardSize.y/tileAmount)*y));
                addEntity(tile);
            }
        }
    }

    public void update() {
        super.update();
        if(ended) {
            endTime += 0.01;
            if(endTime < 100) {
                game.endChessGame();
            }
        }
    }

    public void addEntity(PieceEntity entity) {
        super.addEntity(entity);
        entity.board = this;
        pieces.add(entity);
    }

    
    public void removeEntity(PieceEntity entity) {
        super.removeEntity(entity);
        pieces.remove(entity);
    }
}
