package com.jekabsthomas.chess.entities;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Base class off of which all game entities are built.

 * @author Thomas de Bock
 * 
 */
public class Entity {
    private Point pos = new Point(150, 200);
    private Point size = new Point(100, 100);
    private Color color = Color.PINK;
    public BufferedImage texture;
    public Image textureImage;
    public JLabel textureComponent = new JLabel();
    public JPanel graphic;

    /**
     * Initializes the position, size and graphic.
     */
    public Entity() {
        graphic = new JPanel();
        graphic.setOpaque(false);
        updateTransform();
    }

    /**
     * function which can be overwritten where custom code needs to be run every tick.
     */
    public void update() {
        
    }

    /**
     * Sets color.

     * @param newColor color to set entity graphic to
     */
    public void setColor(Color newColor) {
        color = newColor;
        updateTransform();
    }

    /**
     * Sets position.

     * @param newPos position to set entity and graphic to
     */
    public void setPos(Point newPos) {
        pos = newPos;
        updateTransform();
    }

    /**
     * Gets entity position.
     */
    public Point getPos() {
        return pos;
    }

    /**
     * Sets size.

     * @param newSize size to set entity and graphic to
     */
    public void setSize(Point newSize) {
        size = newSize;
        updateTransform();
        updateTextureSize();
    }

    /**
     * Gets size.
     */
    public Point getSize() {
        return size;
    }

    /**
     * updates the graphic position, size and color.
     */
    public void updateTransform() {
        graphic.setBounds(pos.x, pos.y, size.x, size.y);
        if (color != Color.PINK) {
            graphic.setBackground(color);
        }
        graphic.repaint(pos.x, pos.y, size.x, size.y);
    }

    /**
     * Resizes texture to graphic size.
     */
    public void updateTextureSize() {
        if (textureImage != null) {
            Image scaled = textureImage.getScaledInstance(size.x, size.y, Image.SCALE_DEFAULT);
            ImageIcon newImage = new ImageIcon(scaled);
            textureComponent.setIcon(newImage);
        }
    }

    /**
     * Load texture from File object.

     * @param texFile file to load
     */
    public void loadTexture(File texFile) {
        updateTextureImage(new ImageIcon(texFile.getAbsolutePath()).getImage());
        updateTextureSize();
    }

    /**
     * Updates texture displayed on the graphic.

     * @param newImage image to set to
     */
    public void updateTextureImage(Image newImage) {
        textureImage = newImage;
        if (!graphic.isAncestorOf(textureComponent)) {
            graphic.add(textureComponent);
        }
        updateTextureSize();
    }
}
