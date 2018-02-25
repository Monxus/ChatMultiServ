/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

/**
 *
 * @author Ramon
 */
public class Server extends Cliente {

    private final String HOST;
    private final int PORT;

    /* CONSTRUCTOR ---------------------------------------------------------- */
    public Server(Socket s, ChatServer cs, int port, String host) {
        super(s, cs);
        this.HOST = host;
        this.PORT = port;
    }

    /* GETTERS Y SETTERS ---------------------------------------------------- */
    public String getHOST() {
        return HOST;
    }

    public int getPORT() {
        return PORT;
    }

    /* METODOS PUBLICOS ----------------------------------------------------- */
    //Comprara si el host y el port corresponden al servidor que entra.
    public boolean compareServer(String host, int port) {
        return ((this.PORT == port) && (this.HOST.equals(host)));
    }

    /* METODOS PRIVADOS ----------------------------------------------------- */
    @Override
    protected void takeMsg() {
        String line;
        boolean done = false;
        try {
            while (!done) {
                if ((line = in.readLine()) == null) {
                    done = true;
                } else {
                    this.chatServer.showMsgInServerConsole("Mensaje recibido de servidor: " + line);
                    if (line.equals("bye")) {
                        done = true;
                    } else if (line.startsWith("#")) {
                        this.chatServer.statsAddMsgServerStatsReceived();
                        this.chatServer.updateStats(line.substring(1, line.length()), this);
                    } else {
                        this.chatServer.statsAddMsgServerReceived();
                        this.chatServer.doBroadcast(line, this);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println(e);
        } finally {
            this.chatServer.serverDisconnected(this);
        }

    }

    

}
