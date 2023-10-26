import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

class Scene {
  ArrayList<Entity> entities  = new ArrayList<Entity>();
  String name;
  JFrame frame;
  JLayeredPane pane;
  GameMain game;
  public static int maxEntities = 1000;

  public Scene() {
    frame = new JFrame();
    frame.setSize(900, 950);
    frame.setLayout(null);
    frame.setTitle("Chess Game");
    frame.setLocationRelativeTo(null);
    pane = new JLayeredPane();
    pane.setSize(900, 900);
    pane.setLayout(null);
    frame.add(pane);
  }

  public Scene(ArrayList<Entity> entities) {
    this();
    for(int i = 0; i < entities.size(); i++) {
      addEntity(entities.get(i));
    }
  }

  public void render() {

  }

  public void update() {
    for(int i = 0; i < entities.size(); i++) {
      entities.get(i).update();
    }
  }

  public void addEntity(Entity entity) {
    entities.add(entity);
    pane.add(entity.graphic);
  }
  public void addEntity(Entity entity, int layer) {
    entities.add(entity);
    pane.add(entity.graphic, layer);
  }

  public void removeEntity(Entity entity) {
    entities.remove(entity);
    pane.remove(entity.graphic);
  }
}
