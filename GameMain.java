import java.util.ArrayList;

public class GameMain {
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
        if(!currentScene.frame.isVisible()) {
            currentScene.frame.setVisible(true);
        }
        currentScene.render();
    }

    public void initScenes() {
        MenuScene menuScene = new MenuScene();
        ChessScene gameScene = new ChessScene();


        scenes.add(menuScene);
        scenes.add(gameScene);
        bindScene(gameScene);
    }

    public void bindScene(Scene newScene) {
        if(currentScene != null) {
            currentScene.frame.setVisible(false);
        }

        newScene.frame.setVisible(true);
        currentScene = newScene;
    }
}
