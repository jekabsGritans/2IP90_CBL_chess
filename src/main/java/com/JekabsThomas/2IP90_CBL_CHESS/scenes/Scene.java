package scenes;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import core.GameMain;
import entities.Entity;

public class Scene implements MouseListener {
  public ArrayList<Entity> entities  = new ArrayList<Entity>();
  public String name;
  public JFrame frame;
  public JLayeredPane pane;
  public GameMain game;
  public boolean mouseDown = false;
  public boolean mouseReleased = false;
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
    frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    frame.setResizable(false);
    pane = new JLayeredPane();
    pane.setSize(900, 900);
    pane.setLayout(null);
    frame.add(pane);
    frame.addMouseListener(this);
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
  @Override
  public void mouseClicked(MouseEvent e) {

  }

  /**
   * Sets the MouseDown bool true, cant be done in input manager as the listener needs to be attached to the scene
   */
  @Override
  public void mousePressed(MouseEvent e) {
    mouseDown = true;
  }
  /**
   * Sets the MouseDown bool false, cant be done in input manager as the listener needs to be attached to the scene
   */
  @Override
  public void mouseReleased(MouseEvent e) {
    mouseDown = false;
    // Mouse released is used as a boolean that decides if anywhere on the screen the mouse is released, thus when
    // neccesary, it should be implemented in all corresponding components
    mouseReleased = true;
  }
  @Override
  public void mouseEntered(MouseEvent e) {
    // TODO Auto-generated method stub
  }
  @Override
  public void mouseExited(MouseEvent e) {
    // TODO Auto-generated method stub
  }
}
