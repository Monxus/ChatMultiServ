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
import java.net.ServerSocket;
import java.net.Socket;

/**
 *
 * @author Ramon
 */
public class ServerMultithread extends Thread {

    private final int PORT;
    private Socket clientSock;

    private final ChatServer chatServer;

    /* CONSTRUCTOR ---------------------------------------------------------- */
    public ServerMultithread(int p, ChatServer cs) {
        this.PORT = p;
        this.chatServer = cs;
    }

    /* METODOS PUBLICOS ----------------------------------------------------- */
    @Override
    public void run() {
        try {
            ServerSocket serverSock = new ServerSocket(PORT);
            while (true) {
                this.chatServer.showMsgInClientConsole("Waiting for a client...");
                clientSock = serverSock.accept();

                this.chatServer.showMsgInClientConsole("Client ("+ clientSock +") connected");

                BufferedReader in = new BufferedReader(
                        new InputStreamReader(clientSock.getInputStream()));
                PrintWriter out = new PrintWriter(clientSock.getOutputStream(), true);

                contactClient(in, out);
            }
        } catch (IOException e) {
            System.out.println(e);
            
            this.chatServer.errorPortInUse();
        }
    }

    /* METODOS PRIVADOS ----------------------------------------------------- */
    //Si entra en contacto con un cliente, diferencia si es un cliente o un servidor
    private void contactClient(BufferedReader in, PrintWriter out) {
        String line;

        try {
            if ((line = in.readLine()) == null) {
                this.chatServer.showMsgInClientConsole("Error inesperado al conectar cliente");
            } else {

                if (line.contains("&")) {
                    this.chatServer.showMsgInClientConsole("Mensaje recibido: "+line);
                    this.chatServer.showMsgInClientConsole("Conectando cliente");
                    out.println("Connected");
                    this.chatServer.addClient(this.clientSock,line.substring(1, line.length()));
                } else if (line.contains("$")) {
                    this.chatServer.showMsgInClientConsole("Mensaje recibido: "+line);
                    String hostAux = this.clientSock.getInetAddress().getHostAddress();
                    int portAux = Integer.parseInt(line.substring(1, line.length()));
                    if (this.chatServer.checkIfCouldAddServer(hostAux, portAux)) {
                        out.println("ok");
                        this.chatServer.showMsgInClientConsole("Conectando servidor "+hostAux+":"+portAux);                        
                        this.chatServer.serverConnected(this.clientSock, hostAux, portAux);
                    }else{
                        out.println("er");
                        this.chatServer.showMsgInClientConsole("El servidor "+hostAux+":"+portAux+" no ha podido conectarse");
                    }

                }
            }
        } catch (IOException e) {
            System.out.println(e);
        }
    }

}
