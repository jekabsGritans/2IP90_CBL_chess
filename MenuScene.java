import java.awt.Color;
import java.awt.Point;

public class MenuScene extends Scene {
    public MenuScene() {
        super();
        frame.getContentPane().setBackground(Color.YELLOW);
        initButtons();
    }

    public void initButtons() {
        StartButton startButton = new StartButton();
        startButton.setPos(new Point(200, 200));
        startButton.setColor(Color.magenta);
        addEntity(startButton);
    }

    public void addEntity(StartButton entity) {
        super.addEntity(entity);
        entity.menu = this;
    }
}
