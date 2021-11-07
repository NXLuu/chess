/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.tcp.Message;

/**
 *
 * @author X550V
 */
public class ClientListener extends Thread {

    Client TheClient;

    ClientListener(Client TheClient) {
        this.TheClient = TheClient;
    }

    @Override
    public void run() {
        while (TheClient.soket.isConnected()) {
            try {
                try {
                    Message msg = (Message) TheClient.sInput.readObject();

                    switch (msg.type) {
                        case JoinServer:
                            TheClient.name = msg.content.toString();
                            System.out.println("User " + TheClient.name + " has joined the server...");
                            break;
                        case CreateRoom:
                            ArrayList<String> informations = (ArrayList<String>) msg.content;
                            TheClient.roomName = informations.get(0);
                            TheClient.timeSetting = informations.get(1);
                            TheClient.side = informations.get(2);
                            TheClient.roomId = Server.IdRoom;
                            ++Server.IdRoom;
                            System.out.println(TheClient.name + " has create a room named : " + TheClient.roomName);
                            break;
                        case ReturnRoomsNames:
                            Message newMsg = new Message(Message.Message_Type.ReturnRoomsNames);
                            newMsg.content = Server.ReturnRooms();
                            System.out.println("111");
                            Server.Send(TheClient, newMsg);
                            System.out.println("User " + TheClient.name + " refreshing rooms...");
                            break;
                        case JoinRoom:
                            if (Server.JoinRoom(msg.content.toString()) == null) {
                                System.out.println("Room is full");
                            } else {
                                TheClient.opponent = Server.JoinRoom(msg.content.toString());
                                TheClient.opponent.opponent = TheClient;
                                TheClient.roomName = msg.content.toString();
                                TheClient.roomId = TheClient.opponent.roomId;

                                Message joinMsgToRoomOwner = new Message(Message.Message_Type.JoinRoom);
                                joinMsgToRoomOwner.content = TheClient.name;

                                Message joinMsgToOpponent = new Message(Message.Message_Type.JoinRoom);
                                ArrayList<String> informationsToOpponent = new ArrayList<>();
                                informationsToOpponent.add(TheClient.name);
                                if (TheClient.opponent.side.equals("white")) {
                                    TheClient.side = "black";
                                } else {
                                    TheClient.side = "white";
                                }
                                informationsToOpponent.add(TheClient.side);
                                informationsToOpponent.add(TheClient.opponent.timeSetting);
                                informationsToOpponent.add(TheClient.opponent.name);
                                joinMsgToOpponent.content = informationsToOpponent;

                                Server.Send(TheClient.opponent, joinMsgToRoomOwner);
                                Server.Send(TheClient, joinMsgToOpponent);

                                System.out.println("User " + TheClient.name + " is joining the room named " + TheClient.roomName);
                            }

                            break;
                        case MovePiece:
                            Server.Send(TheClient.opponent, msg);
                            System.out.println(TheClient.name + " has made a move...");
                            break;
                        case Attack:
                            Server.Send(TheClient.opponent, msg);
                            System.out.println(TheClient.name + " has made a attack...");
                            break;
                        case Upgrade:
                            Server.Send(TheClient.opponent, msg);
                            System.out.println(TheClient.name + " is upgrading his pawn");
                            break;
                        case CheckMate:
                            Server.Send(TheClient.opponent, msg);
                            break;
                        case Ready:
                            Server.Send(TheClient.opponent, msg);
                            break;
                        case Start:
                            Server.Send(TheClient.opponent, msg);
                            break;
                        case ExitRoom:
                            Server.Send(TheClient.opponent, msg);
                            TheClient.ExitRoom();
                            break;

                    }

                } catch (ClassNotFoundException ex) {
                    Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (IOException ex) {
                Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
                Server.Clients.remove(TheClient);

            }
        }
    }
}
