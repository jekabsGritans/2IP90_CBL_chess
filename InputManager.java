import java.awt.MouseInfo;
import java.awt.Point;

public final class InputManager {
    public static Point lastMousePos;
    public static Point deltaMousePos;
    public static void init() {
        lastMousePos = MouseInfo.getPointerInfo().getLocation();
    }
    public static void update() {
        deltaMousePos = new Point(MouseInfo.getPointerInfo().getLocation().x-lastMousePos.x, MouseInfo.getPointerInfo().getLocation().y-lastMousePos.y);
        lastMousePos = MouseInfo.getPointerInfo().getLocation();
    }
}
