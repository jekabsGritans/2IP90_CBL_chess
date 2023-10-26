import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
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
    ChessGame chessGame;
    byte turnColor = ChessPiece.White;
    ArrayList<ChessMove> currentPiecePossibleMoves = new ArrayList<ChessMove>();
    ArrayList<PieceEntity> pieces = new ArrayList<PieceEntity>();
    ArrayList<IndicatorEntity> moveIndicators = new ArrayList<IndicatorEntity>();
    Image indicatorImage;
    EndingEntity whiteBanner;
    EndingEntity blackBanner;
    EndingEntity stalemateBanner;
    Clip moveClip;
    boolean withBot = false;


    public ChessScene(boolean withBot) {
        super();
        this.withBot = withBot;
        frame.getContentPane().setBackground(Color.gray);
        initGame();
        initWinBanner();
        initPieces();
        initMoveIndicators();
        initBoard();
        initSounds();
    }

    public void initSounds() {
        String pathString = System.getProperty("user.dir") + "/sounds/move.wav";
        File moveFile = new File(pathString);
        AudioInputStream moveSound;
        try {
            moveSound = AudioSystem.getAudioInputStream(moveFile);
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        try {
            moveClip = AudioSystem.getClip();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            return;
        }
        try {
            moveClip.open(moveSound);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        moveClip.start();
    }

    public void initWinBanner() {
        whiteBanner = new EndingEntity();
        whiteBanner.setPos(new Point(200, 200));
        whiteBanner.setSize(new Point(600, 315));
        String imgPath = System.getProperty("user.dir") + "/textures/whiteWins.png";
        File imgFile = new File(imgPath);
        whiteBanner.loadTexture(imgFile);
        whiteBanner.graphic.setVisible(false);
        whiteBanner.scene = this;
        addEntity(whiteBanner);

        blackBanner = new EndingEntity();
        blackBanner.setPos(new Point(200, 200));
        blackBanner.setSize(new Point(600, 315));
        imgPath = System.getProperty("user.dir") + "/textures/blackWins.png";
        imgFile = new File(imgPath);
        blackBanner.loadTexture(imgFile);
        blackBanner.graphic.setVisible(false);
        blackBanner.scene = this;
        addEntity(blackBanner);

        stalemateBanner = new EndingEntity();
        stalemateBanner.setPos(new Point(200, 200));
        stalemateBanner.setSize(new Point(600, 315));
        imgPath = System.getProperty("user.dir") + "/textures/stalemate.png";
        imgFile = new File(imgPath);
        stalemateBanner.loadTexture(imgFile);
        stalemateBanner.graphic.setVisible(false);
        stalemateBanner.scene = this;
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
    }

    public void initMoveIndicators() {
        for(int i = 0; i < 50; i++) {
            IndicatorEntity indicator = new IndicatorEntity();
            indicator.setPos(new Point(0, 0));
            indicator.setSize(new Point((int)(boardSize.x/tileAmount), (int)((boardSize.y/tileAmount))));
            moveIndicators.add(indicator);
            indicator.graphic.setVisible(false);
            addEntity(indicator, 5);
        }
    }


    public ArrayList<Point> getPossibleMovePositions(PieceEntity piece) {
        System.out.println("pos: " + piece.getPos());
        System.out.println("chesspos: " + pointToChessPos(piece.getPos()).col() + ", " + pointToChessPos(piece.getPos()).row());
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
        moveClip.stop();
        ChessMove madeMove = currentPiecePossibleMoves.get(moveIndex);
        GameState state = chessGame.makeMove(madeMove);
        if(state != GameState.ACTIVE) {
            showWinBanner(state);
        }
        chessGame.getBoard().print();
        updateBoard();
        turnColor = turnColor == ChessPiece.White ? ChessPiece.Black : ChessPiece.White;
        if(ChessPiece.isColor(turnColor, ChessPiece.Black) && withBot) {
            chessGame.makeMove(ChessBot.generateMove(chessGame));
            updateBoard();
            turnColor = turnColor == ChessPiece.White ? ChessPiece.Black : ChessPiece.White;
        }
        moveClip.setFramePosition(0);
        moveClip.start();
    }

    public void updateBoard() {
        updateBoardPieces(chessGame.getBoard());
    }
    public void updateBoardPieces(ChessBoard board) {
        Point pieceSize = new Point((int)((boardSize.x/tileAmount)), (int)((boardSize.y/tileAmount)));
        for(int row = 0; row < tileAmount; row++) {
            for(int col = 0; col < tileAmount; col++) {
                PieceEntity crntPiece = null;
                Point realPos = chessPosToPoint(new ChessPosition(row, col));


                ArrayList<PieceEntity> posPieces = getPieceFromPoint(realPos);
                if(posPieces.size() > 1) {
                    for(int i = 0; i < posPieces.size(); i++) {
                        removeEntity(posPieces.get(i));
                        posPieces.get(i).graphic.setVisible(false);
                        posPieces.get(i).active = false;
                        pieces.remove(posPieces.get(i));
                    }
                    if(!ChessPiece.isEmpty(board.getPiece(row, col))) {
                        col--;
                    }
                    continue;
                }
                if(posPieces.size() == 1) {
                    crntPiece = posPieces.get(0);
                }


                if(ChessPiece.isEmpty(board.getPiece(row, col))) {
                    if(crntPiece != null) {
                        crntPiece.graphic.setVisible(false);
                        crntPiece.active = false;
                        pieces.remove(crntPiece);
                        removeEntity(crntPiece);
                    }
                    continue;
                }
                if(crntPiece == null) {
                    PieceEntity newPiece = new PieceEntity(ChessPiece.getType(board.getPiece(row, col)), ChessPiece.getColor(board.getPiece(row, col)));
                    newPiece.setSize(pieceSize);
                    newPiece.setPos(realPos);
                    addEntity(newPiece);
                    continue;
                }
                if(ChessPiece.isColor(crntPiece.pieceColor, ChessPiece.White) != ChessPiece.isColor(board.getPiece(row, col), ChessPiece.White)) {
                    crntPiece.pieceColor = ChessPiece.getColor(board.getPiece(row, col));
                    crntPiece.initTexture();
                }
                if(ChessPiece.getType(board.getPiece(row, col)) != crntPiece.pieceType) {
                    crntPiece.pieceType = ChessPiece.getType(board.getPiece(row, col));
                    crntPiece.initTexture();
                }
            }
        }
        frame.setVisible(true);
    }

    public ArrayList<PieceEntity> getPieceFromPoint(Point point) {
        ArrayList<PieceEntity> returnPieces = new ArrayList<PieceEntity>();
        for(int i = 0; i < pieces.size(); i++) {
            if(pieces.get(i).getPos().x >= point.x-40 &&  pieces.get(i).getPos().x <= point.x+40 && pieces.get(i).getPos().y >= point.y-40 && pieces.get(i).getPos().y <= point.y + 40) {
                returnPieces.add(pieces.get(i));
            }
        }
        return returnPieces;
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
        int row = (int)Math.rint(((double)(point.y-boardPos.y))/((double)(boardSize.y/tileAmount)));
        int col = (int)Math.rint(((double)(point.x-boardPos.x))/((double)(boardSize.x/tileAmount)));
        return new ChessPosition(row, col);
    }

    public Point chessPosToPoint(ChessPosition pos) {
        return new Point(pos.col()*(boardSize.x/tileAmount)+boardPos.x, pos.row()*(boardSize.y/tileAmount)+boardPos.y);
    }

    public void initGame() {
        chessGame = new ChessGame();
    }
 
    public void initPieces() {
        double tileSize = boardSize.x / tileAmount;
        Point pieceSize = new Point((int)tileSize, (int)tileSize);
        ArrayList<Point> startPositions = new ArrayList<Point>();
        byte[] startTypes = new byte[] {ChessPiece.Rook, ChessPiece.Knight, ChessPiece.Bishop, ChessPiece.Queen, ChessPiece.King, ChessPiece.Bishop, ChessPiece.Knight, ChessPiece.Rook};
        // Initialize white positions
        for(int y = tileAmount-1; y >= tileAmount-2; y--) {
            for(int x = 0 ; x < tileAmount; x++) {
                Point piecePos = new Point(boardPos.x + (boardSize.x/tileAmount)*x, boardPos.y + (boardSize.y/tileAmount)*y);
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
                tile.setSize(new Point(boardSize.x/tileAmount, boardSize.y/tileAmount+5));
                addEntity(tile);
            }
        }
    }

    public void update() {
        super.update();
    }

    public void addEntity(PieceEntity entity) {
        super.addEntity(entity, 2);
        entity.board = this;
        pieces.add(entity);
    }

    
    public void removeEntity(PieceEntity entity) {
        super.removeEntity(entity);
        pieces.remove(entity);
    }
}
