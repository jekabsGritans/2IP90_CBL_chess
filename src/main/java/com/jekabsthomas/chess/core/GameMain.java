package com.jekabsthomas.chess.core;

import com.jekabsthomas.chess.engine.ChessPiece;
import com.jekabsthomas.chess.scenes.ChessScene;
import com.jekabsthomas.chess.scenes.MenuScene;
import com.jekabsthomas.chess.scenes.Scene;
import java.util.ArrayList;

/**
 * Controls loop of the game, loading and updating scenes.

 * @author Thomas de Bock
 * 
 */
public class GameMain {
    public ArrayList<Scene> scenes = new ArrayList<Scene>();
    public Scene menuScene;
    public ChessScene playerVsPlayerScene;
    public ChessScene playerVsBotScene;
    Scene currentScene;


    /**
     * Main game loop, initializes the scenes and calls the update function on every entity.
     */
    public void startGame() {
        initScenes();
        InputManager.init(currentScene.frame);
        while (true) {
            updateLoop();
            InputManager.update();
        }
    }

    /**
     * Calls the update function on the current scene, 
     * which then also calls update on all the scene's entities.
     */
    public void updateLoop() {
        currentScene.update();
    }

    /**
     * Initializes the scenes, and binds the menu scene.
     */
    public void initScenes() {
        menuScene = new MenuScene();
        playerVsPlayerScene = new ChessScene(false);
        playerVsBotScene = new ChessScene(true);
        menuScene.game = this;
        playerVsPlayerScene.game = this;
        playerVsBotScene.game = this;

        bindScene(menuScene);
    }

    /**
     * Resets the player chess scene without reloading all its entites and binds the menu scene.
     */
    public void endPlayerChessGame() {
        playerVsPlayerScene.blackBanner.graphic.setVisible(false);
        playerVsPlayerScene.stalemateBanner.graphic.setVisible(false);
        playerVsPlayerScene.whiteBanner.graphic.setVisible(false);

        playerVsPlayerScene.initGame();
        playerVsPlayerScene.updateBoardPieces(playerVsPlayerScene.chessGame.getBoard());
        playerVsPlayerScene.turnColor = ChessPiece.White;
        for (int i = 0; i < playerVsPlayerScene.moveIndicators.size(); i++) {
            playerVsPlayerScene.moveIndicators.get(i).graphic.setVisible(false);
        }
        bindScene(menuScene);
    }

    /**
     * Resets the bot chess scene without reloading all its entites and binds the menu scene.
     */
    public void endBotChessGame() {
        playerVsBotScene.blackBanner.graphic.setVisible(false);
        playerVsBotScene.stalemateBanner.graphic.setVisible(false);
        playerVsBotScene.whiteBanner.graphic.setVisible(false);

        playerVsBotScene.initGame();
        playerVsBotScene.updateBoardPieces(playerVsBotScene.chessGame.getBoard());
        playerVsBotScene.turnColor = ChessPiece.White;
        for (int i = 0; i < playerVsBotScene.moveIndicators.size(); i++) {
            playerVsBotScene.moveIndicators.get(i).graphic.setVisible(false);
        }
        bindScene(menuScene);
    }

    /**
     * Binds a scene to the current scene.

     * @param newScene the new scene to bind
     * 
     */
    public void bindScene(Scene newScene) {
        if (currentScene != null) {
            currentScene.frame.setVisible(false);
        }

        newScene.frame.setVisible(true);
        currentScene = newScene;
        InputManager.init(currentScene.frame);
    }
}
