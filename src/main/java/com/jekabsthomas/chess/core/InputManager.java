package com.jekabsthomas.chess.core;
import java.awt.Point;
import javax.swing.JFrame;

public final class InputManager {
    public static Point lastMousePos;
    public static Point deltaMousePos;
    public static JFrame currentFrame;

    /**
     * initializes frame and last mouse position, used for calculating the deltaMousePos
     * @param newFrame is the frame that the inputManager will get the relative mouse position to on update
     */
    public static void init(JFrame newFrame) {
        currentFrame = newFrame;
        if(currentFrame != null) {
            lastMousePos = currentFrame.getMousePosition();
        }
    }
    /**
     * update function that calculates and sets the static deltaMousePos
     */
    public static void update() {
        if(currentFrame == null) {
            return;
        }
        Point mousePos = currentFrame.getMousePosition();
        if(mousePos != null) {
            if(lastMousePos == null) {
                lastMousePos = currentFrame.getMousePosition();
            }
            deltaMousePos = new Point(mousePos.x-lastMousePos.x, mousePos.y-lastMousePos.y);
            lastMousePos = mousePos;
        }
    }
}
