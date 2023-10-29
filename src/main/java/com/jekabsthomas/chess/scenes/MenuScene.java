package com.jekabsthomas.chess.scenes;

import com.jekabsthomas.chess.entities.Entity;
import com.jekabsthomas.chess.entities.PlayerVsComputerButton;
import com.jekabsthomas.chess.entities.PlayerVsPlayerButton;
import java.awt.Color;
import java.awt.Point;
import java.io.File;

/**
 * Scene for the menu.

 * @author Thomas de Bock
 */
public class MenuScene extends Scene {

    /**
     * Constructor that calls Scene constructor and initializes the frame color, buttons and banner.
     */
    public MenuScene() {
        super();
        frame.getContentPane().setBackground(Color.gray);
        initButtons();
        initBanner();
    }

    /**
     * Initializes the possible game mode buttons.
     */
    public void initButtons() {
        PlayerVsPlayerButton startButton = new PlayerVsPlayerButton();
        startButton.setPos(new Point(130, 500));
        addEntity(startButton);
        PlayerVsComputerButton startCompButton = new PlayerVsComputerButton();
        startCompButton.setPos(new Point(130, 200));
        addEntity(startCompButton);         
    }

    /**
     * Initializes the banner image.
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
     * calls Scene addEntity function and also sets the menu variable.

     * @param entity thisis the button to add
     */
    public void addEntity(PlayerVsPlayerButton entity) {
        super.addEntity(entity);
        entity.menu = this;
    }

    /**
     * calls Scene addEntity function and also sets the menu variable.

     * @param entity thisis the button to add
     */
    public void addEntity(PlayerVsComputerButton entity) {
        super.addEntity(entity);
        entity.menu = this;
    }
}
