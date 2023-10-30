package com.jekabsthomas.chess.entities;

import java.awt.Image;
import java.io.File;
import javax.swing.ImageIcon;

/**
 * Indicator of where the currently held piece can move to (legal moves).
 * @author Thomas de Bock
 */
public class IndicatorEntity extends Entity {
    public static Image img;
    
    /**
     * Constructor that calls entity constructor and also loads the appropriate texture.
     */
    public IndicatorEntity() {
        super();
        if (IndicatorEntity.img == null) {
            String imgPath = System.getProperty("user.dir") + "/textures/indicator.png";
            File imgFile = new File(imgPath);
            img = new ImageIcon(imgFile.getAbsolutePath()).getImage();
        }
        updateTextureImage(img);
    }
}
