/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chatserver;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Ramon
 */
public class GUI extends JFrame implements ActionListener {

    private final ChatServer cs;

    private JTextField myPort;
    private JTextField portB;
    private JTextField hostB;
    private JTextField portC;
    private JTextField hostC;

    private JLabel labelMyServer;
    private JLabel labelServerB;
    private JLabel labelServerC;

    private JButton butStartMyServer;
    private JButton butStartServerB;
    private JButton butStartServerC;

    private JTextArea serverConsole;
    private JTextArea clientConsole;
    private Estadisticas stadistics;

    /* CONSTRUCTOR ---------------------------------------------------------- */
    public GUI(ChatServer cs) {
        this.cs = cs;
        this.crearVentana();
    }

    /* GETTERS Y SETTERS ---------------------------------------------------- */
    public Estadisticas getStadistics() {
        return stadistics;
    }

    /* METODOS PUBLICOS ----------------------------------------------------- */
    // Configura el aspecto visual cuando se cierra un server
    public void closeServer(String host, int port) {
        boolean checkSameServer = this.checkEqualsServer(hostB.getText(), Integer.parseInt(portB.getText()), hostC, portC);
        if (this.checkEqualsServer(host, port, this.hostB, this.portB)) {
            boolean connectedAux = checkSameServer && this.labelServerB.getForeground() == Color.GREEN;
            this.setServerTextfield(host, port, hostB, portB, labelServerB, butStartServerB, connectedAux);
        } else if (this.checkEqualsServer(host, port, this.hostC, this.portC)) {
            boolean connectedAux = checkSameServer && this.labelServerC.getForeground() == Color.GREEN;
            this.setServerTextfield(host, port, hostC, portC, labelServerC, butStartServerC, connectedAux);
        }
    }

    //Muestra el error por no poder conectarse a un servidor que ya está lleno
    public void errorOverloadServer(String host, int port) {
        JOptionPane.showMessageDialog(this, "El servidor al que intentas conectarte no admite más conexiones", "Error", JOptionPane.ERROR_MESSAGE);
        this.closeServer(host, port);
    }

    //Muetra el error de tener el puerto ya en uso
    public void errorPortInUse() {
        this.showMsgServer("ERROR: Este puerto ya está siendo utilizado");
        JOptionPane.showMessageDialog(this, "El puerto ya está en uso, por favor, elige uno diferente", "Error", JOptionPane.ERROR_MESSAGE);
        this.myPort.setEditable(true);
        this.butStartMyServer.setEnabled(true);
    }

    //Cuando un servidor se conecta lo añade a los textfields
    public void serverConnected(String host, int port) {
        if (this.checkEqualsServer(host, port, this.hostB, this.portB)) {
            this.setServerTextfield(host, port, hostB, portB, labelServerB, butStartServerB, true);
        } else if (this.checkEqualsServer(host, port, this.hostC, this.portC)) {
            this.setServerTextfield(host, port, hostC, portC, labelServerC, butStartServerC, true);
        } else if (this.butStartServerB.isEnabled()) {
            this.setServerTextfield(host, port, hostB, portB, labelServerB, butStartServerB, true);
        } else {
            this.setServerTextfield(host, port, hostC, portC, labelServerC, butStartServerC, true);
        }
    }

    //Muestra mensaje en consola cliente
    public void showMsgClient(String msg) {
        this.clientConsole.append(msg + "\n");
    }

    //Muestra mensaje en consola servidor
    public void showMsgServer(String msg) {
        this.serverConsole.append(msg + "\n");
    }

    /* METODOS PRIVADOS ----------------------------------------------------- */
    //Añade los componentes con la info de los servidores
    private JPanel addInfoServerComponents() {
        JPanel p = new JPanel();

        p.setLayout(new GridLayout(4, 4, 10, 10));
        p.add(new JPanel());

        JLabel labelHost = new JLabel("HOST");
        labelHost.setFont(new Font("Arial Black", 0, 14));
        labelHost.setHorizontalAlignment(JLabel.CENTER);
        labelHost.setForeground(Color.BLUE);
        p.add(labelHost);
        JLabel labelPort = new JLabel("PORT");
        labelPort.setFont(new Font("Arial Black", 0, 14));
        labelPort.setHorizontalAlignment(JLabel.CENTER);
        labelPort.setForeground(Color.BLUE);
        p.add(labelPort);
        p.add(new JPanel());

        labelMyServer = new JLabel("My Server");
        labelMyServer.setFont(new Font("Arial Black", 0, 14));
        labelMyServer.setHorizontalAlignment(JLabel.CENTER);
        labelMyServer.setForeground(Color.red);
        p.add(labelMyServer);
        p.add(new JPanel());
        this.myPort = new JTextField();
        p.add(this.myPort);

        this.butStartMyServer = new JButton("Start");
        this.butStartMyServer.addActionListener(this);
        p.add(this.butStartMyServer);

        labelServerB = new JLabel("Server B");
        labelServerB.setFont(new Font("Arial Black", 0, 14));
        labelServerB.setHorizontalAlignment(JLabel.CENTER);
        labelServerB.setForeground(Color.red);
        p.add(labelServerB);
        this.hostB = new JTextField();
        this.hostB.setEditable(false);
        p.add(this.hostB);
        this.portB = new JTextField();
        this.portB.setEditable(false);
        p.add(this.portB);

        this.butStartServerB = new JButton("Start");
        this.butStartServerB.addActionListener(this);
        this.butStartServerB.setEnabled(false);
        p.add(this.butStartServerB);

        labelServerC = new JLabel("Server C");
        labelServerC.setFont(new Font("Arial Black", 0, 14));
        labelServerC.setHorizontalAlignment(JLabel.CENTER);
        labelServerC.setForeground(Color.red);
        p.add(labelServerC);
        this.hostC = new JTextField();
        this.hostC.setEditable(false);
        p.add(this.hostC);
        this.portC = new JTextField();
        this.portC.setEditable(false);
        p.add(this.portC);

        this.butStartServerC = new JButton("Start");
        this.butStartServerC.addActionListener(this);
        this.butStartServerC.setEnabled(false);
        p.add(this.butStartServerC);

        return p;
    }

    //Añade el textarea que hace de consola
    private JPanel addConsoles() {
        JPanel p = new JPanel();
        p.setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();

        JLabel label = new JLabel("Client Console");
        label.setFont(new Font("Arial Black", 0, 14));

        c.gridx = 0;
        c.gridy = 0;

        p.add(label, c);

        this.clientConsole = new JTextArea(15, 50);
        this.clientConsole.setLineWrap(true);
        this.clientConsole.setWrapStyleWord(true);
        this.clientConsole.setEditable(false);

        JScrollPane scrollPane = new JScrollPane(this.clientConsole, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        c.gridy = 1;
        p.add(scrollPane, c);

        JLabel label2 = new JLabel("Server Console");
        label2.setFont(new Font("Arial Black", 0, 14));

        c.gridy = 2;
        p.add(label2, c);

        this.serverConsole = new JTextArea(15, 50);
        this.serverConsole.setLineWrap(true);
        this.serverConsole.setWrapStyleWord(true);
        this.serverConsole.setEditable(false);

        JScrollPane scrollPane2 = new JScrollPane(this.serverConsole, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        c.gridy = 3;
        p.add(scrollPane2, c);

        return p;
    }

    //Añade la tabla con las estadísticas
    private JPanel addStadistics() {
        JPanel p = new JPanel();
        p.setLayout(new BorderLayout(0, 10));
        JLabel label = new JLabel("STADISTICS");
        label.setFont(new Font("Arial Black", 0, 14));
        label.setHorizontalAlignment(JLabel.CENTER);
        p.add(label, BorderLayout.NORTH);

        String[] columnHeaders = {"Descripción", "Valor"};
        Object[][] dataRows = {
            {"Servidores conectados a este servidor", "0"}, {"Servidores totales del chat", "0"},
            {"Clientes conectados a este servidor", "0"}, {"Clientes totales del chat", "0"},
            {"Mensajes de clientes en este servidor", "0"}, {"Mensajes totales de clientes en el servidor", "0"},
            {"Mensajes recibidos por servidores (mensajes chat)", "0"}, {"Mensajes recibidos por servidores (estadisticas)", "0"},
            {"Mensajes enviados a servidores (mensajes chat)", "0"}, {"Mensajes enviados a servidores (estadisticas)", "0"}};

        this.stadistics = new Estadisticas(dataRows, columnHeaders);
        this.stadistics.getColumnModel().getColumn(1).setMaxWidth(45);
        this.stadistics.getColumnModel().getColumn(0).setMinWidth(300);
        this.stadistics.setRowHeight(35);

        p.add(this.stadistics, BorderLayout.CENTER);

        return p;
    }

    //Añade los componentes a la ventana
    private void addUIComponentes(Container panel) {
        panel.add(this.addInfoServerComponents(), BorderLayout.NORTH);
        panel.add(this.addConsoles(), BorderLayout.CENTER);
        panel.add(this.addStadistics(), BorderLayout.EAST);
    }

    //Comprueba si hay dos serviores iguales en los textfield
    private boolean checkEqualsServer(String host, int port, JTextField tfHost, JTextField tfPort) {
        String hostAux = tfHost.getText();
        int portAux = (tfPort.getText().equals("")) ? -1 : Integer.parseInt(tfPort.getText());
        return (host.equals(hostAux)) && port == portAux;
    }

    //Crea la interfaz del server
    private void crearVentana() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setTitle("2do DAM - Server");
        this.setLayout(new BorderLayout());

        this.addUIComponentes(getContentPane());
        this.pack();
        this.setResizable(false);
        this.setVisible(true);
    }

    //Muestra el error de tener el mismo servidor ya conectado
    private void errorServerAlreadyConnected(JLabel lblServer, JTextField host, JTextField port, JButton butServer) {
        JOptionPane.showMessageDialog(this, "Ya tienes una conexión abierta con este servidor", "Error", JOptionPane.ERROR_MESSAGE);
        lblServer.setForeground(Color.red);
        host.setEditable(true);
        port.setEditable(true);
        butServer.setEnabled(true);
    }

    // Activa/Descativa los campos segúnel estado del servidor
    private void setServerTextfield(String host, int port, JTextField tfHost, JTextField tfPort, JLabel lblServer, JButton btnServer, boolean connected) {
        btnServer.setEnabled(!connected);

        tfPort.setEditable(!connected);
        tfHost.setEditable(!connected);
        tfHost.setText(host);
        tfPort.setText(String.valueOf(port));
        Color fg = (connected) ? Color.green : Color.red;
        lblServer.setForeground(fg);
    }

    //Inicia mi servidor
    private void startMyServer() {
        this.butStartMyServer.setEnabled(false);
        this.butStartServerB.setEnabled(true);
        this.butStartServerC.setEnabled(true);

        this.portB.setEditable(true);
        this.hostB.setEditable(true);
        this.portC.setEditable(true);
        this.hostC.setEditable(true);

        this.myPort.setEditable(false);
        this.labelMyServer.setForeground(Color.GREEN);

        this.cs.startServer(Integer.parseInt(this.myPort.getText()));
    }

    //Inicia el servidor B
    private void startServerB() {
        this.butStartServerB.setEnabled(false);

        this.portB.setEditable(false);
        this.hostB.setEditable(false);

        String hostAux = (this.hostB.getText().equals("localhost")) ? "127.0.0.1" : this.hostB.getText();
        this.hostB.setText(hostAux);

        this.labelServerB.setForeground(Color.ORANGE);

        if (this.checkEqualsServer(hostB.getText(), Integer.parseInt(portB.getText()), hostC, portC)) {
            this.errorServerAlreadyConnected(this.labelServerB, this.hostB, this.portB, this.butStartServerB);
        } else {
            this.cs.startOutServer(hostAux, Integer.parseInt(this.portB.getText()), Integer.parseInt(this.myPort.getText()));
        }
    }

    //Inicia el servidor C
    private void startServerC() {
        this.butStartServerC.setEnabled(false);

        this.portC.setEditable(false);
        this.hostC.setEditable(false);

        String hostAux = (this.hostC.getText().equals("localhost")) ? "127.0.0.1" : this.hostC.getText();
        this.hostC.setText(hostAux);

        this.labelServerC.setForeground(Color.ORANGE);

        if (this.checkEqualsServer(hostC.getText(), Integer.parseInt(portC.getText()), hostB, portB)) {
            this.errorServerAlreadyConnected(this.labelServerC, this.hostC, this.portC, this.butStartServerC);
        } else {
            this.cs.startOutServer(hostAux, Integer.parseInt(this.portC.getText()), Integer.parseInt(this.myPort.getText()));
        }
    }

    /* LISTENER ------------------------------------------------------------- */
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton bAux = (JButton) e.getSource();
        if (bAux == this.butStartMyServer) {
            if (this.myPort.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Introduce un valor en el puerto", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                this.startMyServer();
            }

        }
        if (bAux == this.butStartServerB) {
            if (this.portB.getText().equals("") || this.hostB.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Introduce valores para el puerto y el host", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                this.startServerB();
            }

        }
        if (bAux == this.butStartServerC) {
            if (this.portC.getText().equals("") || this.hostC.getText().equals("")) {
                JOptionPane.showMessageDialog(this, "Introduce valores para el puerto y el host", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                this.startServerC();
            }

        }

    }

}
