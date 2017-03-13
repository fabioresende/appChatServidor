/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.projeto.app.service;

import com.projeto.app.bean.ChatMessage;
import com.projeto.app.bean.ChatMessage.Action;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.Marshaller;

/**
 *
 * @author fabri_000
 */
public class ServidorService {

    private ServerSocket serverSocket;
    private Socket socket;
    private Map<String, ObjectOutputStream> mapOnlines = new HashMap<String, ObjectOutputStream>();

    public ServidorService() {
        try {
            this.serverSocket = new ServerSocket(5555);

            while (true) {
                socket = serverSocket.accept();
                new Thread(new ListenerSocket(socket)).start();
            }
        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private class ListenerSocket implements Runnable {

        private ObjectOutputStream output;
        private ObjectInputStream input;

        public ListenerSocket(Socket socket) {
            try {
                this.output = new ObjectOutputStream(socket.getOutputStream());
                this.input = new ObjectInputStream(socket.getInputStream());
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        @Override
        public void run() {
            ChatMessage message = null;

            try {
                while ((message = (ChatMessage) input.readObject()) != null) {
                    Action action = message.getAction();

                    switch (action) {
                        case CONNECT:
                            boolean isConnect = connect(message, output);
                            if (isConnect) {
                                mapOnlines.put(message.getName(), output);
                            }
                            break;
                        case DISCONNECT:
                            break;
                        case SEND_ONE:
                            break;
                        case SEND_ALL:
                            break;
                        case USERS_ONLINE:
                            break;
                        default:
                            break;
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    private boolean connect(ChatMessage message, ObjectOutputStream output) {
        if (this.mapOnlines.size() != 0) {
            message.setText("Yes");
            sendOne(message, output);
            return true;
        }
        
        for (Map.Entry<String,ObjectOutputStream> kv : mapOnlines.entrySet()) {
            if (kv.getKey().equals(message.getName())) {
                message.setText("NO");
                sendOne(message, output);
                return true;
            }
            else {
                message.setText("Yes");
                sendOne(message, output);
                return false;
            }
        }
        return false;
    }

    private void sendOne(ChatMessage message, ObjectOutputStream output) {
        try {
            output.writeObject(message);
        } catch (IOException ex) {
            Logger.getLogger(ServidorService.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
