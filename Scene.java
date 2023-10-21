import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Point;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import engine.ChessMove;

class Scene {
  ArrayList<Entity> entities  = new ArrayList<Entity>();
  String name;
  JFrame frame;
  GameMain game;

  public Scene() {
    frame = new JFrame();
    frame.setSize(900, 900);
    frame.setLayout(null);
    frame.setTitle("Chess Game");
    frame.setLocationRelativeTo(null);
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
    boolean added = false;
    // Puts new entity into array based on zLayer
    for(int i = 0; i < entities.size(); i++) {
      // Also checks for current i for when i is 0
      // And makes sure it doesn't check for index out of range
      if(entities.get(i).zLayer > entity.zLayer || (i < entities.size()-1 && entities.get(i+1).zLayer > entity.zLayer)) {
        entities.add(i, entity);
        added = true;
        break;
      }
    }
    if(!added) {
      entities.add(entity);
    }
    frame.add(entity.graphic);
  }

  public Point MoveToPos(ChessMove move) {
    return null;
  }
}
