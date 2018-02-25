/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ramon
 */
public class Cliente implements Runnable {
    
    protected final Socket sock;
    protected BufferedReader in;
    protected PrintWriter out;
    
    protected final ChatServer chatServer;
    private String name;

    /* CONSTRUCTOR ---------------------------------------------------------- */
    public Cliente(Socket s, ChatServer cs) {
        this.sock = s;
        this.chatServer = cs;
    }

    /* GETTERS Y SETTERS ---------------------------------------------------- */
    public void setName(String name) {
        this.name = name;
    }

    /* METODOS PUBLICOS ----------------------------------------------------- */
    @Override
    public void run() {
        try {
            this.in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            this.out = new PrintWriter(sock.getOutputStream(), true);
            takeMsg();
            if (this.sock != null) {
                this.sock.close();
            }            
        } catch (IOException ex) {
            Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //Envia el mensaje al cliente
    public void sendMsg(String s) {
        try {
            out.println(s);
            this.chatServer.showMsgInClientConsole("Mensaje enviado: " + s);
        } catch (Exception e) {
            this.chatServer.showMsgInClientConsole("Error al enviar mensaje: " + s);
            this.chatServer.showMsgInClientConsole("Reintentando enviar");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Cliente.class.getName()).log(Level.SEVERE, null, ex);
            }
            this.sendMsg(s);
        }
    }

    /* METODOS PRIVADOS ----------------------------------------------------- */
    //Coge el mensaje del cliente y lo distribuye entre los clientes y el otro servidor
    protected void takeMsg() {
        String line;
        boolean done = false;
        try {
            while (!done) {
                if ((line = in.readLine()) == null) {
                    done = true;
                } else {
                    if (line.equals("bye")) {
                        done = true;
                    } else {
                        this.chatServer.statsAddMsgClientReceived();
                        this.chatServer.doBroadcast(line, this);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            this.chatServer.deleteClient(this, this.name);
        }
    }
    
}
