import java.awt.Color;
import java.awt.Point;
import java.io.File;

public class MenuScene extends Scene {
    /**
     * Constructor that calls Scene constructor and initializes the frame color, buttons and banner
     */
    public MenuScene() {
        super();
        frame.getContentPane().setBackground(Color.gray);
        initButtons();
        initBanner();
    }

    /**
     * Initializes the possible game mode buttons
     */
    public void initButtons() {
        PlayerVSPlayerButton startButton = new PlayerVSPlayerButton();
        startButton.setPos(new Point(130, 500));
        addEntity(startButton);
        PlayerVSComputerButton startCompButton = new PlayerVSComputerButton();
        startCompButton.setPos(new Point(130, 200));
        addEntity(startCompButton);       
    }

    /**
     * Initializes the banner image
     */
    public void initBanner() {
        Entity banner = new Entity();
        banner.setPos(new Point(280, 50));
        banner.setSize(new Point(300, 200));
        String bannerPath = System.getProperty("user.dir") + "/textures/banner.png";
        File bannerFile = new File(bannerPath);
        banner.loadTexture(bannerFile);
        addEntity(banner);
    }

    /**
     * calls Scene addEntity function and also sets the menu variable
     * @param entity thisis the button to add
     */
    public void addEntity(PlayerVSPlayerButton entity) {
        super.addEntity(entity);
        entity.menu = this;
    }

    /**
     * calls Scene addEntity function and also sets the menu variable
     * @param entity thisis the button to add
     */
    public void addEntity(PlayerVSComputerButton entity) {
        super.addEntity(entity);
        entity.menu = this;
    }
}
