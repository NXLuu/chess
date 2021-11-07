/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.tcp.Message;

/**
 *
 * @author X550V
 */
public class Client {

    int id;
    int roomId = -1;
    String name = "NoName";
    String roomName = "Null";
    String timeSetting = "Null";
    String side = "Null";
    Socket soket;
    ObjectOutputStream sOutput;
    ObjectInputStream sInput;
    ClientListener listenThread;
    Client opponent;

    public Client(Socket gelenSoket, int id) {
        this.soket = gelenSoket;
        this.id = id;
        try {
            this.sOutput = new ObjectOutputStream(this.soket.getOutputStream());
            this.sInput = new ObjectInputStream(this.soket.getInputStream());
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.listenThread = new ClientListener(this);
    }

    public void Send(Message message) {
        try {
            this.sOutput.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ExitRoom() {
        roomName = "Null";
        timeSetting = "Null";
        side = "Null";
        roomId = -1;
        opponent.opponent = null;
        this.opponent = null;
    }
}
