import java.util.ArrayList;

public class GameMain {
    enum SCENES {
        MENU,
        GAME
    }
    ArrayList<Scene> scenes = new ArrayList<Scene>();
    Scene menuScene;
    Scene playerVSPlayerScene;
    Scene playerVSBotScene;
    Scene currentScene;

    public static void main(String[] args) {
        (new GameMain()).startGame();
    }

    public void startGame() {
        initScenes();
        InputManager.init(currentScene.frame);
        while(true) {
            renderLoop();
            updateLoop();
            InputManager.update();
        }
    }

    public void updateLoop() {
        currentScene.update();
    }

    public void renderLoop() {
        currentScene.render();
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

    public void endChessGame() {
        initScenes();
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
