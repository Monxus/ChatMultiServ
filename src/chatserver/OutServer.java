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
public class OutServer extends Thread {

    private final int MY_PORT;
    private final int PORT; // server details
    private final String HOST;
    private final ChatServer chatServer;
    private Socket sock;
    private boolean tryConect;

    /* CONSTRUCTOR ---------------------------------------------------------- */
    public OutServer(String h, int p, ChatServer cs, int mp) {
        PORT = p;
        HOST = h;
        MY_PORT = mp;
        chatServer = cs;
        tryConect = true;
    }

    /* METODOS PUBLICOS ----------------------------------------------------- */
    @Override
    public void run() {
        try {

            while (this.chatServer.checkIfCouldAddServer(HOST, PORT) && tryConect) {
                makeContact();
                Thread.sleep(2000);
            }

        } catch (InterruptedException ex) {
            Logger.getLogger(OutServer.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /* METODOS PRIVADOS ----------------------------------------------------- */
    //Intenta establecer contacto con el otro servidor
    private void makeContact() {

        try {
            this.chatServer.showMsgInServerConsole("Connecting to Server " + HOST + ":" + PORT);
            sock = new Socket(HOST, PORT);
            PrintWriter out = new PrintWriter(sock.getOutputStream(), true);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(sock.getInputStream()));
            sendInfo(in, out);
        } catch (IOException e) {
            this.chatServer.showMsgInServerConsole("Error de conexión con el servidor " + HOST + ":" + PORT);
            System.out.println(e);
            this.chatServer.showMsgInServerConsole("Volviendo a conectar en breves");
        }

    }

    //Si consigue establecer contacto, le informa de que es un servidor y de su dirección
    private void sendInfo(BufferedReader in, PrintWriter out) {
        String line;
        try {
            String info = "$" + String.valueOf(MY_PORT);
            out.println(info);
            this.chatServer.showMsgInServerConsole("Mensaje enviado a " + HOST + ":" + PORT + ": " + info);
            if ((line = in.readLine()) == null) {
                this.chatServer.showMsgInServerConsole("Error inesperado al conectar sevidor");
            } else {
                this.chatServer.showMsgInServerConsole("Mensaje recibido de " + HOST + ":" + PORT + ": " + line);
                if (line.contains("ok")) {
                    if (this.chatServer.checkIfCouldAddServer(HOST, PORT)) {
                        this.chatServer.serverConnected(sock, HOST, PORT);
                    }
                } else if (line.contains("er")) {
                    this.chatServer.errorOverloadServer(HOST, PORT);
                    tryConect = false;
                }
            }

        } catch (Exception ex) {
            System.out.println("Problem sending info \n");
            System.out.println(ex);
        }
    }

}
