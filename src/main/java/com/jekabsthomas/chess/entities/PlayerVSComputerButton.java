package com.jekabsthomas.chess.entities;

import com.jekabsthomas.chess.scenes.MenuScene;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

/**
 * Button on the menu that loads the player vs bot scene.
 * @author Thomas de Bock
 */
public class PlayerVSComputerButton extends Entity implements MouseListener {
    public MenuScene menu;

    /**
     * calls entity constructor and sets the size, texture and initializes the mouseListener.
     */
    public PlayerVSComputerButton() {
        super();
        setSize(new Point(600, 315));
        String imgPath = System.getProperty("user.dir") + "/textures/playervscomputer.png";
        File imgFile = new File(imgPath);
        loadTexture(imgFile);
        initMouseEvents();
    }

    /**
     * binds the player vs bot scene when releasing the button.
     */
    public void mouseReleased(MouseEvent e) {
        menu.game.bindScene(menu.game.playerVsBotScene);
    }

    public void mouseEntered(MouseEvent e) {

    }

    public void mouseExited(MouseEvent e) {

    }
 
    public void mouseClicked(MouseEvent e) {

    }
    
    public void mousePressed(MouseEvent e) {

    }

    /**
     * Initializes the mouseListener.
     */
    public void initMouseEvents() { 
        graphic.addMouseListener(this);
    }
}
