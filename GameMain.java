import java.util.ArrayList;

public class GameMain {
    ArrayList<Scene> scenes = new ArrayList<Scene>();
    Scene currentScene;

    public static void main(String[] args) {
        (new GameMain()).startGame();
    }

    public void startGame() {
        initScenes();
        while(true) {
            renderLoop();
        }
    }

    public void updateLoop() {
        while(true) {
            currentScene.update();
        }
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

        menuScene.init();
        currentScene = menuScene;
    }
}
