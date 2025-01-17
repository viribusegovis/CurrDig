package p2p;

import blockchain.utils.BlockChain;
import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import currdig.utils.GuiUtils;
import currdig.utils.RMI;
import currdig.utils.Utils;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import javax.swing.JOptionPane;

/**
 * NodeP2PGui is a graphical user interface for interacting with the
 * Peer-to-Peer (P2P) network. It allows the user to start a server, discover
 * peers, connect to them, and handle mining activities.
 */
public class NodeP2PGui extends javax.swing.JFrame implements P2Plistener {

    OremoteP2P myremoteObject;
    String address;

    /**
     * Creates a new form NodeP2PGui. Initializes the components and disables
     * the buttons initially.
     */
    public NodeP2PGui() {
        initComponents();
        btnConnect.setEnabled(false);
        btnFind.setEnabled(false);
        btnManualCon.setEnabled(false);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnServer = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        btStartServer = new javax.swing.JButton();
        jPanel7 = new javax.swing.JPanel();
        txtServerListeningPort = new javax.swing.JTextField();
        txtServerListeningObjectName = new javax.swing.JTextField();
        imgServerRunning = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        txtServerLog = new javax.swing.JTextPane();
        pnNetwork = new javax.swing.JPanel();
        txtNodeAddress = new javax.swing.JTextField();
        btnConnect = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        txtNetwork = new javax.swing.JTextArea();
        jScrollPane4 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        btnFind = new javax.swing.JButton();
        btnManualCon = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 100, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Digital Curriculum Node");

        pnServer.setLayout(new java.awt.BorderLayout());

        jPanel3.setLayout(new java.awt.BorderLayout(10, 10));

        btStartServer.setText("Start");
        btStartServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btStartServerActionPerformed(evt);
            }
        });
        jPanel3.add(btStartServer, java.awt.BorderLayout.WEST);

        jPanel7.setLayout(new java.awt.GridLayout(2, 0));

        txtServerListeningPort.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtServerListeningPort.setText("10010");
        txtServerListeningPort.setBorder(javax.swing.BorderFactory.createTitledBorder("Port Number"));
        txtServerListeningPort.setPreferredSize(new java.awt.Dimension(200, 36));
        jPanel7.add(txtServerListeningPort);

        txtServerListeningObjectName.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        txtServerListeningObjectName.setText("remoteP2P");
        txtServerListeningObjectName.setBorder(javax.swing.BorderFactory.createTitledBorder("ObjectName"));
        jPanel7.add(txtServerListeningObjectName);

        jPanel3.add(jPanel7, java.awt.BorderLayout.CENTER);

        imgServerRunning.setEnabled(false);
        jPanel3.add(imgServerRunning, java.awt.BorderLayout.EAST);

        pnServer.add(jPanel3, java.awt.BorderLayout.PAGE_START);

        txtServerLog.setBorder(javax.swing.BorderFactory.createTitledBorder("Log Server"));
        txtServerLog.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        jScrollPane1.setViewportView(txtServerLog);

        pnServer.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jTabbedPane1.addTab("Server", pnServer);

        txtNodeAddress.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNodeAddressActionPerformed(evt);
            }
        });

        btnConnect.setText("Connect");
        btnConnect.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnConnectActionPerformed(evt);
            }
        });

        txtNetwork.setColumns(20);
        txtNetwork.setRows(5);
        txtNetwork.setEnabled(false);
        jScrollPane2.setViewportView(txtNetwork);

        jScrollPane4.setViewportView(jList1);

        btnFind.setText("Find Servers");
        btnFind.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFindActionPerformed(evt);
            }
        });

        btnManualCon.setText("Manual Connection");
        btnManualCon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnManualConActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnNetworkLayout = new javax.swing.GroupLayout(pnNetwork);
        pnNetwork.setLayout(pnNetworkLayout);
        pnNetworkLayout.setHorizontalGroup(
            pnNetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnNetworkLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnNetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane4, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
                    .addGroup(pnNetworkLayout.createSequentialGroup()
                        .addComponent(txtNodeAddress)
                        .addGap(18, 18, 18)
                        .addGroup(pnNetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnManualCon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(btnFind, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(pnNetworkLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(btnConnect, javax.swing.GroupLayout.PREFERRED_SIZE, 132, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );
        pnNetworkLayout.setVerticalGroup(
            pnNetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnNetworkLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(pnNetworkLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnManualCon)
                    .addComponent(txtNodeAddress, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFind)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnConnect)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(168, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("P2pNetwork", pnNetwork);

        jTextArea1.setEditable(false);
        jTextArea1.setColumns(20);
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jTextArea1.setText("Java Application consisting of a Digital Curriculum implemented using Blockchain protocols to maintain integrity and safety.\n\nMade by:\n23193 Vasco Alves\n22912 Bruno Freitas");
        jScrollPane3.setViewportView(jTextArea1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(45, Short.MAX_VALUE)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 388, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(42, 42, 42))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(55, 55, 55)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 228, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(230, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("About", jPanel2);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.PAGE_START);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * This method handles the manual connection to a node based on user input.
     * It attempts to connect to a manually entered node address via RMI.
     *
     * @param evt The event triggered by the manual connection button.
     */
    private void btnManualConActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnManualConActionPerformed
        // Get the selected server from the JList
        String address = txtNodeAddress.getText();

        try {
            IremoteP2P node = (IremoteP2P) RMI.getRemote(address);
            myremoteObject.addNode(node);
        } catch (Exception ex) {
            onException(ex, "connect");
            Logger.getLogger(NodeP2PGui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnManualConActionPerformed

    /**
     * This method discovers available P2P nodes in the network by broadcasting
     * discovery requests. It filters out the current node's address and updates
     * the UI with the list of available servers.
     *
     * @param evt The event triggered by the find peers button.
     */
    private void btnFindActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFindActionPerformed
        new Thread(() -> {
            try {
                // Discover servers
                List<String> availableServers = Utils.discoverServers();

                // Filter out servers that are equal to "address"
                availableServers.removeIf(server -> server.equals(address));

                // Close the "Searching" dialog or wait until the list is fetched
                if (availableServers.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "No P2P nodes found. Please try again.");
                    return; // Do not continue if no servers are found
                }

                // Convert the List<String> to an array of Strings
                String[] serversArray = availableServers.toArray(new String[0]);

                // Update the JList with filtered servers
                jList1.setListData(serversArray);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Unexpected error: " + ex.getMessage());
                ex.printStackTrace();
            }
        }).start();
    }//GEN-LAST:event_btnFindActionPerformed

    /**
     * This method connects to a selected P2P node from the discovered nodes
     * list. It establishes an RMI connection with the selected peer node.
     *
     * @param evt The event triggered by the connect button.
     */
    private void btnConnectActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnConnectActionPerformed
        // Get the selected server from the JList
        String selectedServer = jList1.getSelectedValue();

        // Check if a server is selected
        if (selectedServer == null) {
            JOptionPane.showMessageDialog(this, "Please select a server from the list.");
            return; // If no server is selected, exit the method
        }

        try {
            IremoteP2P node = (IremoteP2P) RMI.getRemote(selectedServer);
            myremoteObject.addNode(node);
        } catch (Exception ex) {
            onException(ex, "connect");
            Logger.getLogger(NodeP2PGui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btnConnectActionPerformed

    private void txtNodeAddressActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNodeAddressActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNodeAddressActionPerformed

    /**
     * This method starts the P2P server on the specified port. It binds the
     * server to a registry and starts listening for incoming discovery
     * requests.
     *
     * @param evt The event triggered by the start server button.
     */
    private void btStartServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btStartServerActionPerformed
        try {
            int port = Integer.parseInt(txtServerListeningPort.getText());
            String name = txtServerListeningObjectName.getText();
            //local adress of server
            String host = InetAddress.getLocalHost().getHostAddress();
            //create registry to object
            LocateRegistry.createRegistry(port);
            //create adress of remote object
            address = String.format("//%s:%d/%s", host, port, name);
            myremoteObject = new OremoteP2P(address, this);
            //link adress to object
            Naming.rebind(address, myremoteObject);

            btnConnect.setEnabled(true);
            btnFind.setEnabled(true);
            btnManualCon.setEnabled(true);

            new Thread(() -> {
                int initialPort = 12345; // Start with port 12345
                DatagramSocket socket = null;

                try {
                    // Attempt to bind to the initial or an alternative port
                    while (socket == null) {
                        try {
                            socket = new DatagramSocket(initialPort);
                            socket.setBroadcast(true);
                            System.out.println("Peer Discovery Server running on port " + initialPort);
                        } catch (SocketException e) {
                            System.out.println("Port " + initialPort + " is in use. Trying next port...");
                            initialPort++; // Increment port and try again
                        }
                    }

                    while (true) {
                        byte[] buffer = new byte[256];
                        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                        // Wait for incoming discovery requests
                        socket.receive(packet);

                        // Send a response back to the requesting peer with the server's address
                        String message = "P2P Node: " + address;
                        DatagramPacket responsePacket = new DatagramPacket(message.getBytes(), message.length(), packet.getAddress(), packet.getPort());
                        socket.send(responsePacket);
                        System.out.println("Responding with: " + message);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (socket != null && !socket.isClosed()) {
                        socket.close();
                    }
                }
            }).start();

        } catch (Exception ex) {
            onException(ex, "Starting server");
            Logger.getLogger(NodeP2PGui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_btStartServerActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NodeP2PGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NodeP2PGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NodeP2PGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NodeP2PGui.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new NodeP2PGui().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btStartServer;
    private javax.swing.JButton btnConnect;
    private javax.swing.JButton btnFind;
    private javax.swing.JButton btnManualCon;
    private javax.swing.JLabel imgServerRunning;
    private javax.swing.JList<String> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JPanel pnNetwork;
    private javax.swing.JPanel pnServer;
    private javax.swing.JTextArea txtNetwork;
    private javax.swing.JTextField txtNodeAddress;
    private javax.swing.JTextField txtServerListeningObjectName;
    private javax.swing.JTextField txtServerListeningPort;
    private javax.swing.JTextPane txtServerLog;
    // End of variables declaration//GEN-END:variables

    @Override
    public void onStart(String message) {
        imgServerRunning.setEnabled(true);
        btStartServer.setEnabled(false);
        GuiUtils.addText(txtServerLog, "Start server", message);
    }

    @Override
    public void onException(Exception e, String title) {

    }

    @Override
    public void onConnect(String address) {
        // Update the network display when a new connection is made
        updateNetwork();
    }

    @Override
    public void onDisconnect(String address) {
        // Update the network display when a node disconnects
        updateNetwork();
    }

    /**
     * Updates the displayed list of connected network nodes.
     */
    private void updateNetwork() {
        try {
            List<IremoteP2P> net = myremoteObject.getNetwork();
            StringBuilder txt = new StringBuilder();
            for (IremoteP2P iremoteP2P : net) {
                txt.append(iremoteP2P.getAddress()).append("\n");
            }
            txtNetwork.setText(txt.toString());
        } catch (RemoteException ex) {
            onException(ex, "On connect/disconnect");
            Logger.getLogger(NodeP2PGui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void onTransaction(String transaction) {

    }

    @Override
    public void onMessage(String title, String message) {
        System.out.println("message");
    }

    @Override
    public void onStartRemote(String message) {
        GuiUtils.addText(txtServerLog, "Start server", message);
    }

    @Override
    public void onStartMining(String message, int zeros) {
        System.out.println("Mining started: " + message);
    }

    @Override
    public void onStopMining(String message, int nonce) {
        System.out.println("Mining stopped: " + message + " Nonce: " + nonce);
    }

    @Override
    public void onBlockchainUpdate(BlockChain b) {
        System.out.println("Blockchain updated");
    }

    @Override
    public void onNonceFound(String message, int nonce) {
        System.out.println("Nonce found: " + nonce + " by node: " + message);

        try {
            myremoteObject.stopMining(nonce);
        } catch (RemoteException ex) {
            Logger.getLogger(NodeP2PGui.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
