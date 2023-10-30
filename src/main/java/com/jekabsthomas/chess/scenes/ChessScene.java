package com.jekabsthomas.chess.scenes;

import com.jekabsthomas.chess.core.ChessBot;
import com.jekabsthomas.chess.engine.ChessBoard;
import com.jekabsthomas.chess.engine.ChessBoard.ChessMove;
import com.jekabsthomas.chess.engine.ChessBoard.ChessPosition;
import com.jekabsthomas.chess.engine.ChessGame;
import com.jekabsthomas.chess.engine.ChessGame.GameState;
import com.jekabsthomas.chess.engine.ChessPiece;
import com.jekabsthomas.chess.entities.EndingEntity;
import com.jekabsthomas.chess.entities.FloorTileEntity;
import com.jekabsthomas.chess.entities.IndicatorEntity;
import com.jekabsthomas.chess.entities.PieceEntity;
import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * Scene for the main chess game.
 * @author Thomas de Bock
 */
public class ChessScene extends Scene {
    public Point boardPos = new Point(50, 50);
    public Point boardSize = new Point(800, 800);
    public int tileAmount = 8;
    public ChessGame chessGame;
    public byte turnColor = ChessPiece.WHITE;
    public ArrayList<ChessMove> currentPiecePossibleMoves = new ArrayList<ChessMove>();
    public ArrayList<PieceEntity> pieces = new ArrayList<PieceEntity>();
    public ArrayList<IndicatorEntity> moveIndicators = new ArrayList<IndicatorEntity>();
    public Image indicatorImage;
    public EndingEntity whiteBanner;
    public EndingEntity blackBanner;
    public EndingEntity stalemateBanner;
    public EndingEntity drawBanner;
    public Clip moveClip;
    public boolean withBot = false;
    public boolean ended = false;

    /** 
     * Constructs and initializes the ChessScene object.
     * @param withBot decides if the black side will be played by a player or bot
     */
    public ChessScene(boolean withBot) {
        super();
        this.withBot = withBot;
        frame.getContentPane().setBackground(Color.gray);
        initGame();
        initWinBanner();
        initPieces();
        initIndicators();
        initBoard();
        try {
            initSounds();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Failed to load sound effects ");
            e.printStackTrace();
        }
    }

    /** 
     * Instantiates the ChessGame object.
     * This is done in a function as it is also called when restarting a game.
     */
    public void initGame() {
        chessGame = new ChessGame();
    }

    /** 
     * Initializes the move.wav audio file into a Clip object.
     * @throws UnsupportedAudioFileException when the audio file is not supported
     * @throws IOException when the audio file is not found
     * @throws LineUnavailableException when the audio file is not available
     */
    public void initSounds() throws UnsupportedAudioFileException,
            IOException,
            LineUnavailableException {

        String pathString = System.getProperty("user.dir") + "/sounds/move.wav";
        File moveFile = new File(pathString);
        AudioInputStream moveSound;
        moveSound = AudioSystem.getAudioInputStream(moveFile);
        moveClip = AudioSystem.getClip();
        moveClip.open(moveSound);
        moveClip.start();
    }

    /** 
     * Creates, initializes and hides the banners shown when the game has ended,
     * this is done beforehand as loading these textures takes a bit.
     */
    public void initWinBanner() {
        whiteBanner = new EndingEntity();
        whiteBanner.setPos(new Point(200, 200));
        whiteBanner.setSize(new Point(600, 315));
        String imgPath = System.getProperty("user.dir") + "/textures/whiteWins.png";
        File imgFile = new File(imgPath);
        whiteBanner.loadTexture(imgFile);
        whiteBanner.graphic.setVisible(false);
        whiteBanner.scene = this;
        addEntity(whiteBanner, 10);

        blackBanner = new EndingEntity();
        blackBanner.setPos(new Point(200, 200));
        blackBanner.setSize(new Point(600, 315));
        imgPath = System.getProperty("user.dir") + "/textures/blackWins.png";
        imgFile = new File(imgPath);
        blackBanner.loadTexture(imgFile);
        blackBanner.graphic.setVisible(false);
        blackBanner.scene = this;
        addEntity(blackBanner, 10);

        stalemateBanner = new EndingEntity();
        stalemateBanner.setPos(new Point(200, 200));
        stalemateBanner.setSize(new Point(600, 315));
        imgPath = System.getProperty("user.dir") + "/textures/stalemate.png";
        imgFile = new File(imgPath);
        stalemateBanner.loadTexture(imgFile);
        stalemateBanner.graphic.setVisible(false);
        stalemateBanner.scene = this;
        addEntity(stalemateBanner, 10);

        drawBanner = new EndingEntity();
        drawBanner.setPos(new Point(200, 200));
        drawBanner.setSize(new Point(600, 315));
        imgPath = System.getProperty("user.dir") + "/textures/draw.png";
        imgFile = new File(imgPath);
        drawBanner.loadTexture(imgFile);
        drawBanner.graphic.setVisible(false);
        drawBanner.scene = this;
        addEntity(drawBanner, 10);
    }

    /** 
     * Shows the banner that is applicable to the current passed gamestate.
     * @param state this decides what banner is shown
     */
    public void showWinBanner(GameState state) {
        if (state == GameState.BLACK_WINS) {
            blackBanner.graphic.setVisible(true);
        } else if (state == GameState.WHITE_WINS) {
            whiteBanner.graphic.setVisible(true);
        } else if (state == GameState.STALEMATE) {
            stalemateBanner.graphic.setVisible(true);
        } else if (state == GameState.DRAW) {
            stalemateBanner.graphic.setVisible(true);
        }
        ended = true;
    }

    /** 
     * Creates, initializes and places the visible black and white pieces.
     */
    public void initPieces() {
        double tileSize = boardSize.x / tileAmount;
        Point pieceSize = new Point((int) tileSize, (int) tileSize);
        ArrayList<Point> startPositions = new ArrayList<Point>();
        byte[] startTypes = new byte[] {ChessPiece.ROOK, ChessPiece.KNIGHT, ChessPiece.BISHOP,
            ChessPiece.QUEEN, ChessPiece.KING, ChessPiece.BISHOP, ChessPiece.KNIGHT,
            ChessPiece.ROOK};

        // Initialize white positions
        for (int y = tileAmount - 1; y >= tileAmount - 2; y--) {
            for (int x = 0; x < tileAmount; x++) {
                Point piecePos = new Point(boardPos.x + (boardSize.x / tileAmount) * x,
                        boardPos.y + (boardSize.y / tileAmount) * y);
                startPositions.add(piecePos);
            }
        }

        // Initialize white pieces
        for (int i = 0; i < tileAmount * 2; i++) {
            byte newType = i < tileAmount ? startTypes[i] : ChessPiece.PAWN;
            PieceEntity newPiece = new PieceEntity(newType, ChessPiece.WHITE);
            newPiece.setSize(pieceSize);
            newPiece.setPos(startPositions.get(i));
            addEntity(newPiece);
        }

        // Initialize black positions
        startPositions = new ArrayList<Point>();
        for (int y = 0; y < 2; y++) {
            for (int x = 0; x < tileAmount; x++) {
                Point piecePos = new Point(boardPos.x + (boardSize.x / tileAmount) * x,
                         boardPos.y + (boardSize.y / tileAmount) * y);
                startPositions.add(piecePos);
            }
        }

        // Initialize black pieces
        for (int i = 0; i < tileAmount * 2; i++) {
            byte newType = i < tileAmount ? startTypes[i] : ChessPiece.PAWN;
            PieceEntity newPiece = new PieceEntity(newType, ChessPiece.BLACK);
            newPiece.setSize(pieceSize);
            newPiece.setPos(startPositions.get(i));
            addEntity(newPiece);
        }
    }

    /** 
     * Initializes board tile entities, this has no non-aesthetic purposes.
     */
    public void initBoard() {
        // Initialize FloorTiles
        for (int x = 0; x < tileAmount; x++) {
            for (int y = 0; y < tileAmount; y++) {
                byte tileColor = (y + (x + 1 * tileAmount)) % 2 == 0
                    ? ChessPiece.BLACK : ChessPiece.WHITE;
                FloorTileEntity tile = new FloorTileEntity(tileColor);
                tile.setPos(new Point(boardPos.x + (boardSize.x / tileAmount) * x,
                        boardPos.y + (boardSize.y / tileAmount) * y));
                tile.setSize(new Point(boardSize.x / tileAmount, boardSize.y / tileAmount + 5));
                tile.board = this;
                addEntity(tile);
            }
        }
    }

    /** 
     * Creates and Initializes the movement indicators. 
     * This is done at the start as instantiating them each time would be unnecesarrily costly.
     */
    public void initIndicators() {
        for (int i = 0; i < 50; i++) {
            IndicatorEntity indicator = new IndicatorEntity();
            indicator.setPos(new Point(0, 0));
            indicator.setSize(new Point((int) (boardSize.x / tileAmount),
                (int) ((boardSize.y / tileAmount))));
            moveIndicators.add(indicator);
            indicator.graphic.setVisible(false);
            addEntity(indicator, 5);
        }
    }

    /** 
     * Sets the indicators to the current possible positions 
     *     the held piece can move to, and makes them visible.
     * @param positions this is the list of possible points where the currently
     *     held piece can move to, this is where the indicators areplaced
     */
    public void showIndicators(List<Point> positions) {
        for (int i = 0; i < positions.size(); i++) {
            moveIndicators.get(i).setPos(positions.get(i));
            moveIndicators.get(i).graphic.setVisible(true);
        }
    }

    /** 
     * Hides the movement indicators, after piece is released.
     */
    public void removeIndicators() {
        for (int i = 0; i < moveIndicators.size(); i++) {
            moveIndicators.get(i).graphic.setVisible(false);
        }
    }

    /** 
     * This function is called when a piece is dropped into its new position,
     *    it applies the made move to the chessGame object and switches turn colors.
     * @param moveIndex this is the index of the list of the generated possible moves,
     *     when generated this list is stored as to not
     *     have to convert real coordinates back to ChessMove objects
     */
    public void nextTurn(int moveIndex) {
        moveClip.stop();
        ChessMove madeMove = currentPiecePossibleMoves.get(moveIndex);
        GameState state = chessGame.makeMove(madeMove);
        if (state != GameState.ACTIVE) {
            showWinBanner(state);
        }
        updateBoardPieces(chessGame.getBoard());
        turnColor = turnColor == ChessPiece.WHITE ? ChessPiece.BLACK : ChessPiece.WHITE;
        if (!ended && ChessPiece.isColor(turnColor, ChessPiece.BLACK) && withBot) {
            doBotTurn();
        }
        moveClip.setFramePosition(0);
        moveClip.start();
        frame.repaint(0, 0, 900, 900);
    } 

    /** 
     * Creates a new thread for the bot to run on,
     *    as not to make the gui pause while the bot is thinking up a new move.
     */
    public void doBotTurn() {
        ChessBot bot = new ChessBot();
        ChessBot.currentGame = chessGame;
        bot.start();
    }

    /** 
     * Creates, repositions and deletes any pieces that do
     *    not correspond to the state of the board, this is called after every turn.
     * @param board this is the board structure which the gui changes itself to
     */
    public void updateBoardPieces(ChessBoard board) {
        Point pieceSize = new Point((int) ((boardSize.x / tileAmount)),
            (int) ((boardSize.y / tileAmount)));
        for (int row = 0; row < tileAmount; row++) {
            for (int col = 0; col < tileAmount; col++) {
                PieceEntity crntPiece = null;
                Point realPos = chessPosToPoint(new ChessPosition(row, col));

                ArrayList<PieceEntity> posPieces = getPieceFromPoint(realPos);
                // If more than 1 piece at a position, 
                // both are removed and goes over the same spot again if there is a piece there
                if (posPieces.size() > 1) {
                    for (int i = 0; i < posPieces.size(); i++) {
                        removeEntity(posPieces.get(i));
                        posPieces.get(i).graphic.setVisible(false);
                        posPieces.get(i).active = false;
                        pieces.remove(posPieces.get(i));
                    }
                    if (!ChessPiece.isEmpty(board.getPiece(row, col))) {
                        col--;
                    }
                    continue;
                }
                // If there is a single piece, the crntPiece will be set to it
                if (posPieces.size() == 1) {
                    crntPiece = posPieces.get(0);
                }

                // If there is no piece at the current point, and there is one on the physical board
                // It is removed from the physical board
                if (ChessPiece.isEmpty(board.getPiece(row, col))) {
                    if (crntPiece != null) {
                        crntPiece.graphic.setVisible(false);
                        crntPiece.active = false;
                        pieces.remove(crntPiece);
                        removeEntity(crntPiece);
                    }
                    continue;
                }
                // If the crntPiece is null
                // (and it can only reach this when there should be a piece here)
                // A new one is created and placed at the current position
                if (crntPiece == null) {
                    PieceEntity newPiece = new PieceEntity(
                        ChessPiece.getType(board.getPiece(row, col)),
                        ChessPiece.getColor(board.getPiece(row, col)));
                    newPiece.setSize(pieceSize);
                    newPiece.setPos(realPos);
                    addEntity(newPiece);
                    continue;
                }
                // Updates if the colors dont match
                if (ChessPiece.isColor(crntPiece.pieceColor, ChessPiece.WHITE) 
                    != ChessPiece.isColor(board.getPiece(row, col), ChessPiece.WHITE)) {
                    crntPiece.pieceColor = ChessPiece.getColor(board.getPiece(row, col));
                    crntPiece.initTexture();
                }
                // Updates if the types dont match
                if (ChessPiece.getType(board.getPiece(row, col)) != crntPiece.pieceType) {
                    crntPiece.pieceType = ChessPiece.getType(board.getPiece(row, col));
                    crntPiece.initTexture();
                }
            }
        }
        frame.setVisible(true);
    }

    /** 
     * Gets a list of the possible positions a given piece can move to.
     * @param piece this is the piece of which the possible move positions are returned
     * @return list of point where the specified piece can move to
     */
    public ArrayList<Point> getPossibleMovePositions(PieceEntity piece) {
        ChessPosition piecepos = pointToChessPos(piece.getPos());
        ArrayList<ChessMove> possibleMoves = 
            new ArrayList<ChessMove>(chessGame.getLegalMoves(piecepos));
        ArrayList<Point> possiblePositions = new ArrayList<Point>();

        for (int i = 0; i < possibleMoves.size(); i++) {
            possiblePositions.add(chessPosToPoint(possibleMoves.get(i).getTo()));
        }

        showIndicators(possiblePositions);
        currentPiecePossibleMoves = possibleMoves;
        return possiblePositions;
    }


    /** 
     * Gets the piece at a given point.
     * @param point this is the point where a piece is looked for
     * @return returns a list of all pieces at said point, this is a list instead of a
     *     single object as this property is used in the updateBoardPieces function
     */
    public ArrayList<PieceEntity> getPieceFromPoint(Point point) {
        ArrayList<PieceEntity> returnPieces = new ArrayList<PieceEntity>();
        for (int i = 0; i < pieces.size(); i++) {
            if (pieces.get(i).getPos().x >= point.x - 40 && pieces.get(i).getPos().x <= point.x + 40
                     && pieces.get(i).getPos().y >= point.y - 40
                        && pieces.get(i).getPos().y <= point.y + 40) {
                returnPieces.add(pieces.get(i));
            }
        }
        return returnPieces;
    }

    /** 
     * Gets the piece at a given point.
     * @param pos position in the ChessPosition format
     * @return returns the ChessPosition format converted to Point format as absolute position
     */
    public Point chessPosToPoint(ChessPosition pos) {
        return new Point(pos.col() * (boardSize.x / tileAmount) + boardPos.x,
                 pos.row() * (boardSize.y / tileAmount) + boardPos.y);
    }

    /** 
     * Gets the piece at a given point.
     * @param point position in Point format as absolute position
     * @return returns the point value, converted to the pos in ChessPosition format
     */
    public ChessPosition pointToChessPos(Point point) {
        int row = (int) Math.rint(((double) (point.y - boardPos.y)) 
            / ((double) (boardSize.y / tileAmount)));
        int col = (int) Math.rint(((double) (point.x - boardPos.x))
            / ((double) (boardSize.x / tileAmount)));
        return new ChessPosition(row, col);
    }
 
    /** 
     * Overwrites Scene update function, 
     *     handles the ChessBot thread by checking if it is finished generating the move.
     */
    public void update() {
        super.update();
        if (withBot && ChessPiece.isColor(turnColor, ChessPiece.BLACK)) {
            if (ChessBot.currentMove == null) {
                return;
            }

            updateBoardPieces(chessGame.getBoard());
            turnColor = turnColor == ChessPiece.WHITE ? ChessPiece.BLACK : ChessPiece.WHITE;
            GameState botState = chessGame.makeMove(ChessBot.currentMove);
            ChessBot.currentMove = null;
            if (botState != GameState.ACTIVE) {
                showWinBanner(botState);
            }
            moveClip.setFramePosition(0);
            moveClip.start();
        }
    }

    /** 
     * Adds entity to the scene, and sets the board variable.
     * @param entity the entity to be added
     */
    public void addEntity(PieceEntity entity) {
        super.addEntity(entity, 2);
        entity.board = this;
        pieces.add(entity);
    }
    
    /** 
     * Removes entity from the scene.
     * @param entity the entity to be removed
     */
    public void removeEntity(PieceEntity entity) {
        super.removeEntity(entity);
        pieces.remove(entity);
    }
}
