/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Ramon
 */
public class ChatServer {

    private ServerMultithread sm;
    private OutServer os;
    private ArrayList<Cliente> clientes;
    private Server[] servers;

    private GUI gui;
    private Estadisticas estadisticas;


    /* CONSTRUCTOR ---------------------------------------------------------- */
    public ChatServer() {
        this.clientes = new ArrayList();
        this.servers = new Server[2];
        this.gui = new GUI(this);
        this.estadisticas = this.gui.getStadistics();

    }

    /* GETTERS Y SETTERS ---------------------------------------------------- */
 /* METODOS PUBLICOS ----------------------------------------------------- */
    public static void main(String[] args) {
        ChatServer cs = new ChatServer();
    }

    //Crea y añade un cliente a la arraylist
    public void addClient(Socket s, String name) {
        Cliente c = new Cliente(s, this);
        c.setName(name);
        Thread t = new Thread(c);
        t.start();
        this.clientes.add(c);
        this.estadisticas.addClient();
        this.gui.showMsgClient("Cliente " + name + " conectado.");
        
        this.doBroadcastClient("#cliSer" + this.estadisticas.getClientsConnected(), null);
        this.doBroadcast("#cliTot" + this.estadisticas.getTotalClientsConnected(), null);

    }

    //Crea y añade un server si no estaba añadido
    private void addServer(Socket s, String host, int port) {
        for (int i = 0; i < this.servers.length; i++) {
            if (this.servers[i] == null && !this.checkIfServerExists(host, port)) {
                this.showMsgInServerConsole("Servidor " + host + ":" + port + " conectado.");
                Server server = new Server(s, this, port, host);
                this.servers[i] = server;
                this.estadisticas.addServer();
                Thread t = new Thread(server);
                t.start();
                
                this.syncServers(server);
                
            }
        }
    }

    //Reenvia el mensaje a todos los clientes (excepto al que lo envió)
    public void doBroadcast(String msg, Cliente c) {
        this.doBroadcastClient(msg, c);
        this.doBroadcastServer(msg, c);

    }

    private void doBroadcastServer(String msg, Cliente c) {       
        for (int i = 0; i < this.servers.length; i++) {
            if (!(this.servers[i] == null || this.servers[i].equals(c))) {
                this.servers[i].sendMsg(msg);
                this.statsAddMsgServerSent(msg);
                this.gui.showMsgServer("Enviando mensaje a servidores: "+msg);
            }
        }
    }
    
    private void doBroadcastClient(String msg, Cliente c){
        this.gui.showMsgClient("Enviando mensaje a clientes: "+msg);
        for (Cliente cliente : this.clientes) {
            if (cliente != c) {
                cliente.sendMsg(msg);
                System.out.println(msg);
            }
        }
    }

    //Elimina un cliente de la arraylist
    public void deleteClient(Cliente c, String name) {
        this.clientes.remove(c);
        this.estadisticas.deleteClient();
        this.gui.showMsgClient("Cliente " + name + " desconecado");
        
        this.doBroadcastClient("#cliSer" + this.estadisticas.getClientsConnected(), null);
        this.doBroadcast("#cliTot" + this.estadisticas.getTotalClientsConnected(), null);
    }

    //Crea y empeza el thread del server multithreading
    public void startServer(int port) {
        sm = new ServerMultithread(port, this);
        this.gui.showMsgServer("Iniciando servidor en el puerto " + port);
        sm.start();
        
        this.estadisticas.syncTotalServers(1);
    }

    //Crea y empieza el thread del Out Server
    public void startOutServer(String hostB, int portB, int portA) {
        os = new OutServer(hostB, portB, this, portA);
        os.start();
    }

    // Envia mensaje para que se muestre en consola de servidores
    public void showMsgInServerConsole(String msg) {
        this.gui.showMsgServer(msg);
    }

    // Envia mensaje para que se muestre en consola de clientes
    public void showMsgInClientConsole(String msg) {
        this.gui.showMsgClient(msg);
    }

    // Comprueba si un servidor existe a partir de un host y un puerto
    private boolean checkIfServerExists(String host, int port) {
        for (int i = 0; i < this.servers.length; i++) {
            if (this.servers[i] != null && this.servers[i].compareServer(host, port)) {
                return true;
            }
        }
        return false;
    }

    // Comprueba si se puede añadir un servidor
    public boolean checkIfCouldAddServer(String host, int port) {
        if (this.servers[0] == null || this.servers[1] == null) {
            return !this.checkIfServerExists(host, port);
        }
        return false;
    }

    // Desconecta un servidor
    public void serverDisconnected(Server s) {
        this.gui.closeServer(s.getHOST(), s.getPORT());
        this.gui.showMsgServer("Servidor " + s.getHOST() + ":" + s.getPORT() + " desconectado");
        for (int i = 0; i < this.servers.length; i++) {
            if (this.servers[i] != null && this.servers[i].equals(s)) {
                this.servers[i] = null;
                this.estadisticas.deleteServer();
            }
        }
    }

    // Conecta un servidor
    public void serverConnected(Socket s, String host, int port) {
        this.gui.serverConnected(host, port);
        this.addServer(s, host, port);
    }

    public void errorPortInUse() {
        this.gui.errorPortInUse();
    }

    public void errorOverloadServer(String host, int port) {
        this.gui.errorOverloadServer(host, port);
    }

    public void statsAddMsgClientReceived() {
        this.estadisticas.addMsgClientReceived();
    }

    public void statsAddMsgServerReceived() {
            this.estadisticas.addMsgServerReceived();
    }

    private void statsAddMsgServerSent(String msg) {
        if (msg.startsWith("#")) {
            this.estadisticas.addMsgServerStatsSent();
        } else {
            this.estadisticas.addMsgServerSent();
        }

    }

    public void statsAddMsgServerStatsReceived() {
        this.estadisticas.addMsgServerStatsReceived();
    }

    public void updateStats(String command, Server s) {

        if (command.startsWith("cliTot")) {
            this.estadisticas.syncTotalClients(Integer.parseInt(command.substring(6, command.length())));
            this.doBroadcast("#cliTot" + this.estadisticas.getTotalClientsConnected(), s);
        }else if (command.startsWith("serTot")) {
            this.estadisticas.syncTotalServers(Integer.parseInt(command.substring(6, command.length())));
        }else if (command.startsWith("synSer")) {
            this.estadisticas.syncTotalServers(this.estadisticas.getTotalServersConnected()+Integer.parseInt(command.substring(6, command.length()))-2);
            this.doBroadcastServer("#serTot" + this.estadisticas.getTotalServersConnected(), s);
        }else if (command.startsWith("synCli")) {
            this.estadisticas.syncTotalClients(this.estadisticas.getTotalClientsConnected()+Integer.parseInt(command.substring(6, command.length())));
            this.doBroadcast("#cliTot" + this.estadisticas.getTotalClientsConnected(), s);
        }
    }

    private void syncServers(Server s) {
        int servAux = this.estadisticas.getTotalServersConnected();
        int cliAux = this.estadisticas.getTotalClientsConnected();

        for (int i = 0; i < this.servers.length; i++) {
            if (this.servers[i]==null || (this.servers[i] != null && !this.servers[i].equals(s))) {
                this.doBroadcastServer("#synSer" + servAux, this.servers[i]);
                this.doBroadcastServer("#synCli" + cliAux, this.servers[i]);
            }
        }
    }

}
