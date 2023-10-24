import java.awt.Color;
import java.awt.Point;
import java.io.File;

public class MenuScene extends Scene {
    public MenuScene() {
        super();
        frame.getContentPane().setBackground(Color.gray);
        initButtons();
        initBanner();
    }

    public void initButtons() {
        PlayerVSPlayerButton startButton = new PlayerVSPlayerButton();
        startButton.setPos(new Point(130, 500));
        addEntity(startButton);
    }

    public void initBanner() {
        Entity banner = new Entity();
        banner.setPos(new Point(280, 50));
        banner.setSize(new Point(300, 200));
        String bannerPath = System.getProperty("user.dir") + "/textures/banner.png";
        File bannerFile = new File(bannerPath);
        banner.loadTexture(bannerFile);
        addEntity(banner);
    }

    public void addEntity(PlayerVSPlayerButton entity) {
        super.addEntity(entity);
        entity.menu = this;
    }
}
