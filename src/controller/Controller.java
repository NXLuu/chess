/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.tcp.Client;
import view.GameClient;
import view.GameFrame;
import view.Menu;

/**
 *
 * @author nxulu
 */
public class Controller {

    public static Menu menu;
    public static GameClient gameClient;
    public static GameFrame gameFrame;

    public static void run() {
        menu = new Menu();
        menu.setVisible(true);
    }

    public static boolean checkLogin(String userName, String password) {
        return true;
    }

    public static void startGameClient(String userName) {

        gameClient = new GameClient(userName);
        gameClient.setVisible(true);
    }

    public static void startGameFrame(String userName,boolean iswhite, boolean isRoomOwner) {
        gameFrame = new GameFrame(iswhite, gameClient);
        gameFrame.setMyName(userName);
        gameFrame.setVisible(true);
        gameFrame.isRoomOwner = isRoomOwner;
        gameClient.setGameBoard(gameFrame);
    }
    
    public static void closeGameFrame() {
        gameFrame.dispose();
        gameClient.setGameBoard(null);
        gameClient.isRoomOwner = false;
    }

    public static void connect(String userName) {
        Client.Start("localhost", 8124, userName);
        menu.setVisible(false);
        startGameClient(userName);

    }

    public static void main(String[] args) {
        Controller.run();
    }
}
