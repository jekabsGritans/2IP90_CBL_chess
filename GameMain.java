import java.util.ArrayList;

public class GameMain {
    enum SCENES {
        MENU,
        GAME
    }
    ArrayList<Scene> scenes = new ArrayList<Scene>();
    Scene currentScene;

    public static void main(String[] args) {
        (new GameMain()).startGame();
    }

    public void startGame() {
        initScenes();
        InputManager.init();
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
        MenuScene menuScene = new MenuScene();
        ChessScene gameScene = new ChessScene();
        menuScene.game = this;
        gameScene.game = this;

        scenes.add(menuScene);
        scenes.add(gameScene);

        loadScene(SCENES.GAME);
    }

    public void bindScene(Scene newScene) {
        if(currentScene != null) {
            currentScene.frame.setVisible(false);
        }

        newScene.frame.setVisible(true);
        currentScene = newScene;
    }

    public void loadScene(SCENES newScene) {
        bindScene(scenes.get(newScene.ordinal()));
    }
}
