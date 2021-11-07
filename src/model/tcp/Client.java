/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model.tcp;


import controller.ServerListener;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author X550V
 */
public class Client {

    public static Socket socket;
    public static ObjectInputStream sInput;
    public static ObjectOutputStream sOutput;
    public static ServerListener listenMe;
    public static ArrayList<String> rooms;

    public static void Start(String ip, int port, String userName) {
        try {
            Client.socket = new Socket(ip, port);
            Client.Display("Servera bağlandı");
            Client.sInput = new ObjectInputStream(Client.socket.getInputStream());
            Client.sOutput = new ObjectOutputStream(Client.socket.getOutputStream());
            Client.listenMe = new ServerListener();
            Client.listenMe.start();

            Message msg = new Message(Message.Message_Type.JoinServer);
            msg.content = userName;
            Client.Send(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void Stop() {
        try {
            if (Client.socket != null) {
                Client.listenMe.stop();
                Client.socket.close();
                Client.sOutput.flush();
                Client.sOutput.close();
                Client.sInput.close();
            }
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void Display(String msg) {

        System.out.println(msg);

    }

    //mesaj gönderme fonksiyonu
    public static void Send(Message msg) {
        try {
            Client.sOutput.writeObject(msg);
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
