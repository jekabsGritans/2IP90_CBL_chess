package entities;
import java.awt.Point;
import java.awt.event.MouseListener;
import java.io.File;

import scenes.MenuScene;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class PlayerVSPlayerButton extends Entity implements MouseListener {
    public MenuScene menu;

    /**
     * calls entity constructor and sets the size, texture and initializes the mouseListener
     */
    public PlayerVSPlayerButton() {
        super();
        setSize(new Point(600, 315));
        String imgPath = System.getProperty("user.dir") + "/textures/playervsplayer.png";
        File imgFile = new File(imgPath);
        loadTexture(imgFile);
        initMouseEvents();
    }

    /**
     * binds the player vs player scene when releasing the button
     */
    public void mouseReleased(MouseEvent e) {
        menu.game.bindScene(menu.game.playerVSPlayerScene);
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
     * Initializes the mouseListener
     */
    public void initMouseEvents() { 
        graphic.addMouseListener(this);
    }
}
