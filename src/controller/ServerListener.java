/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.tcp.Client;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Pieces.Piece;
import model.tcp.Client;
import model.tcp.Message;
import server.Server;
import view.GameClient;
import view.GameFrame;
import view.GamePanel;

/**
 *
 * @author X550V
 */
public class ServerListener extends Thread {

    @Override
    public void run() {
        while (Client.socket.isConnected()) {
            try {
                Message received = (Message) (Client.sInput.readObject());
                GameClient gameClient = Controller.gameClient;
                GameFrame gameFrame = gameClient.getGameBoard();
                GamePanel gamePanel = null;
                if (gameFrame != null) {
                    gamePanel = gameFrame.getGamePanel();
                }

                switch (received.type) {
                    case ReturnRoomsNames:
                        Client.rooms = (ArrayList<String>) received.content;
                        Client.rooms.forEach((r) -> {
                            gameClient.getLm1().addElement(r);
                        });
                        gameClient.RefreshRooms();
                        System.out.println("Reached rooms list from server...");
                        break;
                    case JoinRoom:
                        if (gameClient.isRoomOwner) {
                            gameFrame.OpponentJoinedTheRoom(received.content.toString());
                            System.out.println("User " + received.content.toString() + " has joined the room...");
                        } else {
                            gameClient.JoinRoom((ArrayList<String>) received.content);
                            System.out.println("You have joined the room");
                        }
//                        gameFrame.StartTimer();
                        break;
                    case MovePiece:
                        ArrayList readMoveInf = (ArrayList) received.content;
                        gamePanel.getGame().opponentMove((int) readMoveInf.get(0), (int) readMoveInf.get(1), (int) readMoveInf.get(2), (int) readMoveInf.get(3), (int) readMoveInf.get(4));
                        System.out.println("Opponent has made a move " + readMoveInf.get(0) + " is moving to " + (63 - (int) readMoveInf.get(1)));
                        break;
                    case CheckMate:
                        gameFrame.enemyLoss(received);
                        GameFrame.showFinishDiaglog(true);
                    case Ready:
                        gameFrame.enemyReady();
                        break;
                    case Start:
                        gameFrame.startGame();
                        break;
                    case ExitRoom:
                        gameFrame.enemyLoss(received);
                        gameFrame.enemyExitRoom();
                        gameClient.isRoomOwner = true;

                        break;

                }

            } catch (IOException | ClassNotFoundException ex) {

                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                break;
            }
        }
    }
}
