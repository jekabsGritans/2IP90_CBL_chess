import java.awt.MouseInfo;
import java.awt.Point;

import javax.swing.JFrame;

public final class InputManager {
    public static Point lastMousePos;
    public static Point deltaMousePos;
    public static JFrame currentFrame;

    public static void init(JFrame newFrame) {
        currentFrame = newFrame;
        if(currentFrame != null) {
            lastMousePos = currentFrame.getMousePosition();
        }
    }
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
