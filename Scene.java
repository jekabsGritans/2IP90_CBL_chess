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

  /**
   * Scene constructor, initializes the frame and pane
   */
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
  /**
   * Initializes the scene, and adds the specified entities
   * @param entities
   */
  public Scene(ArrayList<Entity> entities) {
    this();
    for(int i = 0; i < entities.size(); i++) {
      addEntity(entities.get(i));
    }
  }
  /**
   * Calls update on all the scene's entities
   */
  public void update() {
    for(int i = 0; i < entities.size(); i++) {
      entities.get(i).update();
    }
  }

  /**
   * Adds an entity to the entities and adds the graphic to the layered pane
   * @param entity this is the entity to add
   */
  public void addEntity(Entity entity) {
    entities.add(entity);
    pane.add(entity.graphic);
  }
  /**
   * Adds an entity to the entities and adds the graphic to the layered pane at the specified layer
   * @param entity this is the entity to add
   * @param layer layer to add entity to the layeredPane
   */
  public void addEntity(Entity entity, int layer) {
    entities.add(entity);
    pane.add(entity.graphic, layer);
  }

  /**
   * Removes an entity from the entities and removes the graphic from the layered pane
   * @param entity this is the entity to remove
   */
  public void removeEntity(Entity entity) {
    entities.remove(entity);
    pane.remove(entity.graphic);
  }
}
