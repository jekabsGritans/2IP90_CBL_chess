import java.util.ArrayList;

import javax.swing.JFrame;

class Scene {
  ArrayList<Entity> entities  = new ArrayList<Entity>();
  String name;
  JFrame frame;

  public Scene() {
    frame = new JFrame();
    frame.setSize(900, 900);
    frame.setLayout(null);
  }

  public void init() {

  }

  public Scene(ArrayList<Entity> entities) {
    for(int i = 0; i < entities.size(); i++) {
      addEntity(entities.get(i));
    }
  }

  public void render() {
    for(int i = 0; i < entities.size(); i++) {
      entities.get(i).render();
    }
  }

  public void update() {
    for(int i = 0; i < entities.size(); i++) {
      entities.get(i).update();
    }
  }

  public void addEntity(Entity entity) {
    // Puts new entity into array based on zLayer
    for(int i = 0; i < entities.size(); i++) {
      // Also checks for current i for when i is 0
      // And makes sure it doesn't check for index out of range
      if(entities.get(i).zLayer > entity.zLayer || (i < entities.size()-1 && entities.get(i+1).zLayer > entity.zLayer)) {
        entities.add(i, entity);
      }
    }
  }
}
