/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author X550V
 */
public class NewClientListener extends Thread {

    @Override
    public void run() {
        while (!Server.serverSocket.isClosed()) {
            try {  
                Socket clientSocket = Server.serverSocket.accept();
                
                System.out.println("Client conntected");
                
                Client nclient = new Client(clientSocket, Server.IdClient);

                Server.IdClient++;
                Server.Clients.add(nclient);
                
                nclient.listenThread.start();

            } catch (IOException ex) {
                Logger.getLogger(NewClientListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
