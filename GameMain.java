import java.util.ArrayList;

import engine.ChessPiece;

public class GameMain {
    ArrayList<Scene> scenes = new ArrayList<Scene>();
    Scene menuScene;
    ChessScene playerVSPlayerScene;
    ChessScene playerVSBotScene;
    Scene currentScene;

    public static void main(String[] args) {
        (new GameMain()).startGame();
    }

    /**
     * Main game loop, initializes the scenes and calls the update function on every entity
     */
    public void startGame() {
        initScenes();
        InputManager.init(currentScene.frame);
        while(true) {
            updateLoop();
            InputManager.update();
        }
    }

    /**
     * Calls the update function on the current scene, which then also calls update on all the scene's entities
     */
    public void updateLoop() {
        currentScene.update();
    }

    /**
     * Initializes the scenes, and binds the menu scene
     */
    public void initScenes() {
        menuScene = new MenuScene();
        playerVSPlayerScene = new ChessScene(false);
        playerVSBotScene = new ChessScene(true);
        menuScene.game = this;
        playerVSPlayerScene.game = this;
        playerVSBotScene.game = this;

        bindScene(menuScene);
    }

    /**
     * Resets the player chess scene without reloading all its entites and binds the menu scene
     */
    public void endPlayerChessGame() {
        playerVSPlayerScene.blackBanner.graphic.setVisible(false);
        playerVSPlayerScene.stalemateBanner.graphic.setVisible(false);
        playerVSPlayerScene.whiteBanner.graphic.setVisible(false);

        playerVSPlayerScene.initGame();
        playerVSPlayerScene.updateBoardPieces(playerVSPlayerScene.chessGame.getBoard());
        playerVSPlayerScene.turnColor = ChessPiece.White;
        for(int i = 0; i < playerVSPlayerScene.moveIndicators.size(); i++) {
            playerVSPlayerScene.moveIndicators.get(i).graphic.setVisible(false);
        }
        bindScene(menuScene);
    }

    /**
     * Resets the bot chess scene without reloading all its entites and binds the menu scene
     */
    public void endBotChessGame() {
        playerVSBotScene.blackBanner.graphic.setVisible(false);
        playerVSBotScene.stalemateBanner.graphic.setVisible(false);
        playerVSBotScene.whiteBanner.graphic.setVisible(false);

        playerVSBotScene.initGame();
        playerVSBotScene.updateBoardPieces(playerVSBotScene.chessGame.getBoard());
        playerVSBotScene.turnColor = ChessPiece.White;
        for(int i = 0; i < playerVSBotScene.moveIndicators.size(); i++) {
            playerVSBotScene.moveIndicators.get(i).graphic.setVisible(false);
        }
        bindScene(menuScene);
    }

    /**
     * Binds a scene to the current scene
     * @param newScene the new scene to bind
     */
    public void bindScene(Scene newScene) {
        if(currentScene != null) {
            currentScene.frame.setVisible(false);
        }

        newScene.frame.setVisible(true);
        currentScene = newScene;
        InputManager.init(currentScene.frame);
    }
}
