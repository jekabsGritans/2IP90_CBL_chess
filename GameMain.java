import java.util.ArrayList;

import engine.ChessPiece;

public class GameMain {
    enum SCENES {
        MENU,
        GAME
    }
    ArrayList<Scene> scenes = new ArrayList<Scene>();
    Scene menuScene;
    ChessScene playerVSPlayerScene;
    ChessScene playerVSBotScene;
    Scene currentScene;

    public static void main(String[] args) {
        (new GameMain()).startGame();
    }

    public void startGame() {
        initScenes();
        InputManager.init(currentScene.frame);
        while(true) {
            updateLoop();
            InputManager.update();
        }
    }

    public void updateLoop() {
        currentScene.update();
    }

    public void initScenes() {
        menuScene = new MenuScene();
        playerVSPlayerScene = new ChessScene(false);
        playerVSBotScene = new ChessScene(true);
        menuScene.game = this;
        playerVSPlayerScene.game = this;
        playerVSBotScene.game = this;

        bindScene(menuScene);
    }

    public void endPlayerChessGame() {
        playerVSPlayerScene.blackBanner.graphic.setVisible(false);
        playerVSPlayerScene.stalemateBanner.graphic.setVisible(false);
        playerVSPlayerScene.whiteBanner.graphic.setVisible(false);

        playerVSPlayerScene.initGame();
        playerVSPlayerScene.updateBoard();
        playerVSPlayerScene.turnColor = ChessPiece.White;
        for(int i = 0; i < playerVSPlayerScene.moveIndicators.size(); i++) {
            playerVSPlayerScene.moveIndicators.get(i).graphic.setVisible(false);
        }
        bindScene(menuScene);
    }

    public void bindScene(Scene newScene) {
        if(currentScene != null) {
            currentScene.frame.setVisible(false);
        }

        newScene.frame.setVisible(true);
        currentScene = newScene;
        InputManager.init(currentScene.frame);
    }
}
