/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import javax.swing.JTable;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

/**
 *
 * @author Ramon
 */
public class Estadisticas extends JTable {

    private int serversConnected;
    private int totalServersConnected;
    private int clientsConnected;
    private int totalClientsConnected;
    private int clientsMsg;
    private int totalClientsMsg;
    private int receiveServerMsg;
    private int sendServerMsg;
    private int receiveServerStatsMsg;
    private int sendServerStatsMsg;

    /* CONSTRUCTOR ---------------------------------------------------------- */
    public Estadisticas(Object[][] rowData, Object[] columnNames) {
        super(rowData, columnNames);
        this.serversConnected = 0;
        this.totalServersConnected = 0;
        this.clientsConnected = 0;
        this.totalClientsConnected = 0;
        this.clientsMsg = 0;
        this.totalClientsMsg = 0;
        this.receiveServerMsg = 0;
        this.sendServerMsg = 0;
        this.receiveServerStatsMsg = 0;
        this.sendServerStatsMsg = 0;
    }

    /* GETTERS Y SETTERS ---------------------------------------------------- */
    public int getClientsConnected() {
        return clientsConnected;
    }

    public int getTotalClientsConnected() {
        return totalClientsConnected;
    }

    public int getTotalServersConnected() {
        return totalServersConnected;
    }

    /* METODOS PUBLICOS ----------------------------------------------------- */
    //Añade un cliente a las estadisticas
    public void addClient() {
        this.clientsConnected++;
        TableModel tAux = this.getModel();
        tAux.setValueAt(this.clientsConnected, 2, 1);
        this.totalClientsConnected++;
        this.syncTotalClients(this.totalClientsConnected);
    }

    //Añade un servidor a las estadísticas
    public void addServer() {
        this.serversConnected++;
        TableModel tAux = this.getModel();
        tAux.setValueAt(this.serversConnected, 0, 1);
        this.totalServersConnected++;
        this.syncTotalServers(this.totalServersConnected);
    }

    //Añade mensajes recibidos por clientes
    public void addMsgClientReceived() {
        this.clientsMsg++;
        TableModel tAux = this.getModel();
        tAux.setValueAt(this.clientsMsg, 4, 1);
        this.addTotalMsg();
    }

    //Añade mensajes recibidos por servidor
    public void addMsgServerReceived() {
        this.receiveServerMsg++;
        TableModel tAux = this.getModel();
        tAux.setValueAt(this.receiveServerMsg, 6, 1);
        this.addTotalMsg();
    }

    //Añade mensajes enviados a servidor con estadisticas
    public void addMsgServerStatsSent() {
        this.sendServerStatsMsg++;
        TableModel tAux = this.getModel();
        tAux.setValueAt(this.sendServerStatsMsg, 9, 1);
    }

    //Añade mensajes enviados a servidor
    public void addMsgServerSent() {
        this.sendServerMsg++;
        TableModel tAux = this.getModel();
        tAux.setValueAt(this.sendServerMsg, 8, 1);
    }

    //Añade mensajes recibidos con estadísticas
    public void addMsgServerStatsReceived() {
        this.receiveServerStatsMsg++;
        TableModel tAux = this.getModel();
        tAux.setValueAt(this.receiveServerStatsMsg, 7, 1);
    }
    
    //Elimina un cliente de las estadisticas
    public void deleteClient() {
        this.clientsConnected--;
        TableModel tAux = this.getModel();
        tAux.setValueAt(this.clientsConnected, 2, 1);
        this.totalClientsConnected--;
        this.syncTotalClients(this.totalClientsConnected);
    }

    //Elimina un server de las estadísticas
    public void deleteServer() {
        this.serversConnected--;
        TableModel tAux = this.getModel();
        tAux.setValueAt(this.serversConnected, 0, 1);
        this.totalServersConnected--;
        this.syncTotalServers(this.totalServersConnected);
    }
    
    //Sincroniza el total de clientes al unir servidores
    public void syncTotalClients(int totalClients) {
        this.totalClientsConnected = totalClients;
        TableModel tAux = this.getModel();
        tAux.setValueAt(this.totalClientsConnected, 3, 1);
    }

    //Sncroniza el total de servidores al unir servidores
    public void syncTotalServers(int totalServers) {
        this.totalServersConnected = totalServers;
        TableModel tAux = this.getModel();
        tAux.setValueAt(this.totalServersConnected, 1, 1);
    }
    
    /* METODOS PRIVADOS ----------------------------------------------------- */
    //Añade mensajes al total
    private void addTotalMsg() {
        this.totalClientsMsg = this.clientsMsg + this.receiveServerMsg;
        TableModel tAux = this.getModel();
        tAux.setValueAt(this.totalClientsMsg, 5, 1);
    }

}
