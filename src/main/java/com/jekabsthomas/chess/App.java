package com.jekabsthomas.chess;

import com.jekabsthomas.chess.core.GameMain;

/**
 * Class that runs the GameMain, only exists as easy entry point for maven.

 * @author Thomas de Bock
 */
public class App {
  public static void main(String[] args) {
    (new GameMain()).startGame();
  }
}
