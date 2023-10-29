package com.jekabsthomas.chess.entities;

import com.jekabsthomas.chess.scenes.ChessScene;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Button that shows the result of the game and goes back to the menu.

 * @author Thomas de Bock
 * 
 */
public class EndingEntity extends Entity implements MouseListener {
  public ChessScene scene;


  /**
   * Constructor that calls entity constructor and also initializes mouseListener.
   */

  public EndingEntity() {
    super();
    initMouseEvents();
  }

  /**
   * Overwritten mouseListener function, loads menu scene on release.
   */
  @Override
  public void mouseReleased(MouseEvent e) {
    if (!scene.withBot) {
      scene.game.endPlayerChessGame();
      return;
    }
    scene.game.endBotChessGame();
  }

  @Override
  public void mouseEntered(MouseEvent e) {

  }

  @Override
  public void mouseExited(MouseEvent e) {
    
  }
 
  @Override
  public void mouseClicked(MouseEvent e) {

  }
  
  @Override
  public void mousePressed(MouseEvent e) {

  }

  /**
   * Initializes mouseListener.
   */
  public void initMouseEvents() { 
    graphic.addMouseListener(this);
  }
}