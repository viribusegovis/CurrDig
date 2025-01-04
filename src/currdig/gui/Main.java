package currdig.gui;

import blockchain.utils.Block;
import blockchain.utils.BlockChain;
import blockchain.utils.Hash;
import blockchain.utils.MerkleTree;
import blockchain.utils.SecurityUtils;
import currdig.core.Entry;
import currdig.core.User;
import currdig.utils.Utils;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import p2p.IremoteP2P;

/**
 * The Main class represents the primary user interface for the application. It
 * provides features for interacting with the blockchain and managing users.
 */
public class Main extends javax.swing.JFrame {

    private IremoteP2P node;

    private PublicKey pubKey;
    private PrivateKey privKey;
    private Key simKey;
    private String username;

    private List<User> users;
    private Block selectedBlock;

    public static String fileCurrDig = "currdig.obj";

    /**
     * Creates a new Main form.
     *
     * @param pubKey The user's public key.
     * @param privKey The user's private key.
     * @param simKey The symmetric encryption key.
     * @param username The username of the logged-in user.
     * @param node The remote P2P node the user is connected to.
     */
    public Main(PublicKey pubKey, PrivateKey privKey, Key simKey, String username, IremoteP2P node) {
        this.pubKey = pubKey;
        this.privKey = privKey;
        this.simKey = simKey;
        this.username = username; // Set the username
        this.node = node;

        initComponents();
        initMyComponents();

        startNodeHealthCheck(); // Start health monitoring
        startMiningMonitor(); // Start the monitoring job
    }

    /**
     * Initializes custom components and listeners for the Main form.
     */
    private void initMyComponents() {
        try {
            // Load users from utility function
            users = Utils.loadUsers();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage());
        }

        // Display the public key in the text field
        if (this.pubKey != null) {
            String pub = Base64.getEncoder().encodeToString(this.pubKey.getEncoded());
            txtEntity.setText(pub);
        } else {
            // Optionally set txtEntity to empty or a default message
            txtEntity.setText("");
        }

        listUsers();// Populate the user list
        jList1.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        jList2.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add a selection listener to the user list
        listUsers.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                try {
                    listUsersValueChanged(evt);
                } catch (Exception ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        //Add ActionListener to search field
        jTextFieldNomePesquisar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performSearch();
            }
        });

        // Add a selection listener to the block list
        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            @Override
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                if (!evt.getValueIsAdjusting()) {
                    displayBlockList();
                }
            }
        });

        // Add a listener for tab changes
        jTabbedPane1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (jTabbedPane1.getSelectedIndex() == 1) {
                    try {
                        // Assuming History is the second tab
                        displayBlockList();
                    } catch (Exception ex) {
                        Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        });

    }

    /**
     * Start a thread to check the node's health.
     */
    private void startNodeHealthCheck() {
        new Thread(() -> {
            try {
                while (true) {
                    try {
                        // Perform a lightweight RMI call to check if the node is responsive
                        node.getAddress(); // If this call fails, the node is unresponsive
                    } catch (RemoteException e) {
                        // Notify the user
                        JOptionPane.showMessageDialog(this,
                                "Node is unavailable. Trying to find new server.",
                                "Node Unavailable",
                                JOptionPane.WARNING_MESSAGE);

                        // Redirect to Landing page
                        java.awt.EventQueue.invokeLater(() -> {
                            try {
                                new Landing().setVisible(true); // Show the Landing screen
                            } catch (NotBoundException | MalformedURLException ex) {
                                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            this.dispose(); // Close the current instance
                        });

                        break; // Exit the health check loop
                    }

                    // Sleep before the next health check
                    Thread.sleep(5000); // Check every 5 seconds
                }
            } catch (HeadlessException | InterruptedException e) {
            }
        }).start();
    }

    /**
     * Hashes a string using SHA-256 and encodes the result in Base64.
     *
     * @param keyString The string to hash.
     * @return The Base64-encoded hash of the input string, or null if an error
     * occurs.
     */
    private String hashKey(String keyString) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(keyString.getBytes());
            return Base64.getEncoder().encodeToString(hashedBytes); // Encode to Base64 for readability
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null; // Handle exceptions appropriately
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel7 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaDescricao = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtEntity = new javax.swing.JTextArea();
        jScrollPane6 = new javax.swing.JScrollPane();
        txtUser = new javax.swing.JTextArea();
        btnRefreshUserList = new javax.swing.JButton();
        btnBC = new javax.swing.JButton();
        btnActiveTrans = new javax.swing.JButton();
        btnAddTrans = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jTextFieldNomePesquisar = new javax.swing.JTextField();
        btnSearchUser = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        listUsers = new javax.swing.JList<>();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jPanel9 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        txtEntityPublicKey = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        txtTimestamp = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jScrollPane5 = new javax.swing.JScrollPane();
        txtareaDescription = new javax.swing.JTextArea();
        jLabel5 = new javax.swing.JLabel();
        txtTransactionHash = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        txtBlockHash = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        txtNonce = new javax.swing.JTextField();
        txtMerkleRoot = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        txtProof = new javax.swing.JTextField();
        btnVerifyProof = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Digital Curriculum Entity View");
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jTabbedPane1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jTextAreaDescricao.setColumns(20);
        jTextAreaDescricao.setLineWrap(true);
        jTextAreaDescricao.setRows(5);
        jTextAreaDescricao.setBorder(javax.swing.BorderFactory.createTitledBorder("Description"));
        jTextAreaDescricao.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jScrollPane2.setViewportView(jTextAreaDescricao);

        txtEntity.setEditable(false);
        txtEntity.setBackground(new java.awt.Color(255, 255, 255));
        txtEntity.setColumns(20);
        txtEntity.setLineWrap(true);
        txtEntity.setRows(5);
        txtEntity.setBorder(javax.swing.BorderFactory.createTitledBorder("Entity"));
        txtEntity.setFocusable(false);
        jScrollPane3.setViewportView(txtEntity);

        txtUser.setEditable(false);
        txtUser.setBackground(new java.awt.Color(255, 255, 255));
        txtUser.setColumns(20);
        txtUser.setLineWrap(true);
        txtUser.setRows(5);
        txtUser.setBorder(javax.swing.BorderFactory.createTitledBorder("User"));
        txtUser.setFocusable(false);
        jScrollPane6.setViewportView(txtUser);

        btnRefreshUserList.setText("Refresh List");
        btnRefreshUserList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshUserListActionPerformed(evt);
            }
        });

        btnBC.setText("Blockchain Explorer");
        btnBC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBCActionPerformed(evt);
            }
        });

        btnActiveTrans.setText("Active Transactions");
        btnActiveTrans.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnActiveTransActionPerformed(evt);
            }
        });

        btnAddTrans.setText("Add Transaction");
        btnAddTrans.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAddTransActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnBC, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                    .addComponent(jScrollPane3)
                    .addComponent(jScrollPane6)
                    .addComponent(btnActiveTrans, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAddTrans, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnRefreshUserList, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(83, 83, 83)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(68, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 129, Short.MAX_VALUE)
                .addComponent(btnAddTrans)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnActiveTrans)
                .addGap(18, 18, 18)
                .addComponent(btnRefreshUserList)
                .addGap(18, 18, 18)
                .addComponent(btnBC)
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(304, 304, 304)
                    .addComponent(jLabel3)
                    .addContainerGap(348, Short.MAX_VALUE)))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Search by name"));
        jPanel3.setToolTipText("");
        jPanel3.setName(""); // NOI18N
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));
        jPanel3.add(jTextFieldNomePesquisar);

        btnSearchUser.setText("Search");
        btnSearchUser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSearchUserActionPerformed(evt);
            }
        });
        jPanel3.add(btnSearchUser);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("User Listing"));

        listUsers.setBorder(null);
        listUsers.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        listUsers.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane4.setViewportView(listUsers);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane4)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 576, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.getAccessibleContext().setAccessibleName("User List");

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())))
        );

        jTabbedPane1.addTab("Submitions", jPanel7);

        jScrollPane1.setViewportView(jList1);

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Details"));

        jLabel1.setText("User's Public key");

        jLabel2.setText("Entity's Public Key");

        jLabel4.setText("Timestamp");

        txtareaDescription.setColumns(20);
        txtareaDescription.setRows(5);
        jScrollPane5.setViewportView(txtareaDescription);

        jLabel5.setText("Description");

        jLabel6.setText("Transaction Hash");

        jLabel7.setText("Block Hash");

        jLabel8.setText("Merkle Root");

        jLabel9.setText("Element Proof");

        jLabel10.setText("Nonce");

        btnVerifyProof.setText("Verify");
        btnVerifyProof.setEnabled(false);
        btnVerifyProof.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnVerifyProofActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(222, 222, 222))
                            .addComponent(jTextField1))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                .addGap(349, 349, 349))
                            .addComponent(txtEntityPublicKey)))
                    .addComponent(jLabel5)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtTimestamp)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(txtTransactionHash)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(236, 236, 236))
                                    .addComponent(txtBlockHash))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtMerkleRoot)
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(226, 226, 226)))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNonce)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(216, 216, 216))
                            .addComponent(jLabel10)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(txtProof)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnVerifyProof)))))
                .addContainerGap())
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtEntityPublicKey, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel6)
                        .addComponent(jLabel10)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtTimestamp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtTransactionHash, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtNonce, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7)
                    .addComponent(jLabel8)
                    .addComponent(jLabel9))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtBlockHash, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtMerkleRoot, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtProof, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnVerifyProof))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 172, Short.MAX_VALUE)
                .addContainerGap())
        );

        jScrollPane7.setViewportView(jList2);

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 366, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane7)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addComponent(jScrollPane7, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("History", jPanel8);

        getContentPane().add(jTabbedPane1);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Fetches and displays the list of users available on the node, excluding
     * the current user. This method uses a SwingWorker to perform the
     * background operation and updates the GUI on the Event Dispatch Thread.
     */
    private void listUsers() {
        // Use SwingWorker to load the users in a background thread
        SwingWorker<List<User>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<User> doInBackground() throws Exception {
                // Fetch the list of users from the node (background thread)
                return node.listUsers();
            }

            @Override
            protected void done() {
                try {
                    // Retrieve the list of users (this runs on the EDT)
                    List<User> fetchedUsers = get();

                    // Filter out the current user
                    fetchedUsers.removeIf(user -> user.getName().equals(username));

                    // Create a DefaultListModel<String> to hold the usernames
                    DefaultListModel<String> listModel = new DefaultListModel<>();

                    // Populate the listModel with usernames
                    for (User user : fetchedUsers) {
                        listModel.addElement(user.getName());
                    }

                    // Set the listModel to the listUsers JList
                    listUsers.setModel(listModel);

                    // Set selection mode to single selection
                    listUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                } catch (Exception ex) {
                    // Log any exceptions and show an error message
                    java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null, "Failed to load user list", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        // Execute the SwingWorker
        worker.execute();
    }

    /**
     * Handles the selection event when a user is selected from the list.
     * Displays the selected user's public key in the associated text area.
     *
     * @param evt ListSelectionEvent triggered when a selection occurs in the
     * listUsers JList.
     * @throws Exception if any decoding or selection error occurs.
     */
    private void listUsersValueChanged(javax.swing.event.ListSelectionEvent evt) throws Exception {
        if (!evt.getValueIsAdjusting()) { // Ensure the event is the final selection
            int selectedIndex = listUsers.getSelectedIndex();

            if (selectedIndex != -1) {

                // Retrieve the list of users (this runs on the EDT)
                List<User> fetchedUsers = users;

                // Filter out the current user
                fetchedUsers.removeIf(user -> user.getName().equals(username));

                // Get the selected user
                User selectedUser = fetchedUsers.get(selectedIndex);

                // Get the public key of the selected user
                PublicKey pubKey = selectedUser.getPub();

                // Encode the public key to Base64 string
                String pubKeyString = Base64.getEncoder().encodeToString(pubKey.getEncoded());

                // Update the txtEntity text area
                txtUser.setText(pubKeyString);
            }
        }
    }

    private List<Block> allBlocks;  // Store blocks globally to avoid issues

    /**
     * Retrieves and displays the list of blocks from the blockchain. Adds
     * listeners for block selection and initializes the transaction list.
     */
    private void displayBlockList() {
        try {
            // Get all blocks from the blockchain
            allBlocks = node.getBlockchain().getChain(); // This method returns the list of blocks

            // Check if the blockchain is empty
            if (allBlocks.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No blocks available in the blockchain.", "Error", JOptionPane.ERROR_MESSAGE);
                return; // Exit early if no blocks are available
            }

            // Create a DefaultListModel to hold the block strings
            DefaultListModel<String> blockListModel = new DefaultListModel<>();

            // Convert the blocks to strings and add them to the list model
            for (Block block : allBlocks) {
                blockListModel.addElement("Block Hash: " + block.getCurrentHash()); // Display block hash or any relevant info
            }

            // Set the list model for jList1 (the block list)
            jList1.setModel(blockListModel);

            // Add a listener to handle block selection
            jList1.addListSelectionListener(e -> {
                if (!e.getValueIsAdjusting()) {
                    int selectedIndex = jList1.getSelectedIndex();
                    if (selectedIndex != -1) {
                        Block selectedBlock = allBlocks.get(selectedIndex);
                        this.selectedBlock = selectedBlock;
                        displayBlockDetails(selectedBlock); // Display block details
                        displayTransactionsForBlock(selectedBlock); // Display transactions of selected block
                        resetTransactionList(); // Clear previous transaction selection in jList2
                    }
                }
            });
        } catch (RemoteException e) {
            // Handle any RemoteExceptions if blockchain access fails
            JOptionPane.showMessageDialog(this, "Error retrieving blocks: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Displays the details of the selected block in the associated text fields.
     *
     * @param selectedBlock The block selected by the user.
     */
    private void displayBlockDetails(Block selectedBlock) {
        // Display the hash of the selected block in the text field
        txtBlockHash.setText(selectedBlock.getCurrentHash());

        // Clear any previous transaction details to reset the UI for the selected block
        clearTransactionDetails();
    }

    /**
     * Displays the list of transactions contained in the selected block.
     * Populates the transaction list UI component and adds a listener for
     * transaction selection.
     *
     * @param selectedBlock The block whose transactions need to be displayed.
     */
    private void displayTransactionsForBlock(Block selectedBlock) {
        // Store transactions in a final list to ensure thread safety
        final List<Entry> selectedTransactions = selectedBlock.getBuffer();

        DefaultListModel<String> transactionListModel = new DefaultListModel<>();

        // Populate model with transactions
        for (Entry entry : selectedTransactions) {
            transactionListModel.addElement("Transaction: " + entry.getDescription() + " - "
                    + Base64.getEncoder().encodeToString(entry.getTargetUserPublicKey().getEncoded()).substring(0, 10) + "...");
        }

        // Set model for transaction list
        jList2.setModel(transactionListModel);

        // Remove any existing listeners to prevent duplicates
        for (javax.swing.event.ListSelectionListener listener : jList2.getListSelectionListeners()) {
            jList2.removeListSelectionListener(listener);
        }

        // Add new selection listener
        jList2.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedTransactionIndex = jList2.getSelectedIndex();
                if (selectedTransactionIndex >= 0 && selectedTransactionIndex < selectedTransactions.size()) {
                    Entry selectedEntry = selectedTransactions.get(selectedTransactionIndex);
                    displayTransactionDetailsForEntry(selectedEntry, selectedBlock);

                }
            }
        });
    }

    /**
     * Resets the transaction list and clears any previously displayed
     * transaction details. Ensures a clean state when switching between blocks
     * or transactions.
     */
    private void resetTransactionList() {
        // Clear selection in the transaction list UI component
        jList2.clearSelection();

        // Clear any transaction details displayed in the UI
        clearTransactionDetails();
    }

    /**
     * Displays the details of the selected transaction entry. This method
     * updates various text fields with the details of the selected entry from
     * the block.
     *
     * @param selectedEntry The transaction entry whose details need to be
     * displayed.
     * @param selectedBlock The block containing the selected transaction.
     */
    private void displayTransactionDetailsForEntry(Entry selectedEntry, Block selectedBlock) {
        // Display the Target User's Public Key in the corresponding text field
        jTextField1.setText(Base64.getEncoder().encodeToString(selectedEntry.getTargetUserPublicKey().getEncoded()));

        // Display the Entity's Public Key in the corresponding text field
        txtEntityPublicKey.setText(Base64.getEncoder().encodeToString(selectedEntry.getEntityPublicKey().getEncoded()));

        // Display the timestamp of the transaction
        txtTimestamp.setText(selectedEntry.getDateTime().toString());

        // Display the transaction description
        txtareaDescription.setText(selectedEntry.getDescription());

        // Display the transaction hash (calculated hash of the entry's string representation)
        txtTransactionHash.setText(Hash.getHash(selectedEntry.toString()));

        // Optionally, display the nonce associated with the block
        txtNonce.setText(String.valueOf(selectedBlock.getNonce()));

        // Check if Merkle Tree exists
        if (selectedBlock.getMerkleTree() != null) {
            // Get the Merkle root
            String root = selectedBlock.getMerkleTree().getRoot();
            txtMerkleRoot.setText(root);

            // Check if it's a single transaction block
            if (selectedBlock.getBuffer().size() == 1) {
                txtProof.setText(root);
            } else {
                // Get and format proof for multiple transaction blocks
                List<String> proof = selectedBlock.getMerkleTree().getProof(selectedEntry);
                StringBuilder proofText = new StringBuilder();
                for (String hash : proof) {
                    proofText.append(hash).append("\n");
                }
                txtProof.setText(proofText.toString());
            }
            btnVerifyProof.setEnabled(true);
        } else {
            // Handle unfinalized block
            txtMerkleRoot.setText("Block not finalized");
            txtProof.setText("Block not finalized");
        }
    }

    /**
     * Clears all the transaction details displayed in the text fields. This
     * method is used to reset the UI when changing the transaction or block
     * selection.
     */
    private void clearTransactionDetails() {
        // Clear all the text fields related to transaction details
        jTextField1.setText("");
        txtEntityPublicKey.setText("");
        txtTimestamp.setText("");
        txtareaDescription.setText("");
        txtTransactionHash.setText("");
        txtNonce.setText("");
        txtProof.setText("");
        txtMerkleRoot.setText("");
        btnVerifyProof.setEnabled(false);
    }

    /**
     * Action listener method for the search button. This method is triggered
     * when the user clicks the "Pesquisar" button and initiates a search
     * operation.
     *
     * @param evt The action event triggered by clicking the search button.
     */
    private void btnSearchUserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSearchUserActionPerformed
        performSearch();
    }//GEN-LAST:event_btnSearchUserActionPerformed

    /**
     * Action handler for the "Add Entry" button. This method is triggered when
     * the user clicks the "Add Entry" button. It disables the button, retrieves
     * input data, creates a transaction entry, signs it, and adds it to the
     * blockchain. The button is re-enabled after the operation is completed.
     *
     * @param evt The action event triggered by clicking the button.
     */
    private void btnAddTransActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAddTransActionPerformed
        // Disable the button to prevent multiple clicks during transaction processing
        btnAddTrans.setEnabled(false);

        // Start a new thread to handle adding the entry in the background
        new Thread(() -> {
            try {
                // Get the description from the text area (transaction description)
                String description = jTextAreaDescricao.getText();

                // Retrieve the target user's public key from the text area (Base64 encoded)
                String targetUserPubKeyString = txtUser.getText();
                PublicKey targetUserPubKey = SecurityUtils.getPublicKey(Base64.getDecoder().decode(targetUserPubKeyString));

                // Create a new Entry with the description, current user's public key, and the target user's public key
                Entry newEntry = new Entry(description, this.pubKey, targetUserPubKey); // Updated constructor for Entry

                // Sign the Entry using the current user's private key
                byte[] signature = SecurityUtils.sign(newEntry.toString().getBytes(), this.privKey);

                // Add the Entry to the blockchain (or curriculum system)
                if (!node.addTransaction(targetUserPubKey, newEntry, signature)) {
                    throw new Exception("Failed to add transaction");
                }

                // Clear the input fields after successful transaction
                jTextAreaDescricao.setText("");
                txtUser.setText("");
                listUsers.clearSelection();

                // Update the history list of transactions or entries
                updateHistoryList();

                // Show a success message
                JOptionPane.showMessageDialog(this, "Entry added successfully!");

                // Re-enable the button in the Event Dispatch Thread
                SwingUtilities.invokeLater(() -> {
                    btnAddTrans.setEnabled(true);
                });

            } catch (Exception ex) {
                // Handle exceptions (e.g., errors in signing, adding transactions)
                JOptionPane.showMessageDialog(this, "Error adding entry: " + ex.getMessage());

                // Ensure the button is re-enabled if an error occurs
                btnAddTrans.setEnabled(true);
            } finally {
                // Ensure the button is enabled in case of any error or successful completion
                btnAddTrans.setEnabled(true);
            }
        }).start(); // Start the operation in a new thread to avoid blocking the UI
    }//GEN-LAST:event_btnAddTransActionPerformed

    /**
     * Action handler for the button that fetches the transactions. This method
     * creates a SwingWorker to fetch transactions in a background thread and
     * displays them in a dialog upon completion.
     *
     * @param evt The action event triggered by clicking the button.
     */
    private void btnActiveTransActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnActiveTransActionPerformed
        // Create a SwingWorker to handle the transaction retrieval in a background thread
        SwingWorker<CopyOnWriteArraySet, Void> worker = new SwingWorker<>() {
            @Override
            protected CopyOnWriteArraySet doInBackground() throws RemoteException {
                return node.getTransactions();
            }

            @Override
            protected void done() {
                try {
                    CopyOnWriteArraySet transactions = get();

                    // Create formatted string for display
                    StringBuilder displayText = new StringBuilder();
                    displayText.append("Active Transactions:\n\n");

                    int count = 1;
                    for (Object trans : transactions) {
                        Entry entry = (Entry) trans;
                        displayText.append("Transaction #").append(count).append("\n");
                        displayText.append("Description: ").append(entry.getDescription()).append("\n");
                        displayText.append("Timestamp: ").append(entry.getDateTime()).append("\n");
                        displayText.append("Target User: ").append(
                                Base64.getEncoder().encodeToString(
                                        entry.getTargetUserPublicKey().getEncoded()).substring(0, 15)
                        ).append("...\n");
                        displayText.append("Entity: ").append(
                                Base64.getEncoder().encodeToString(
                                        entry.getEntityPublicKey().getEncoded()).substring(0, 15)
                        ).append("...\n");
                        displayText.append("----------------------------------------\n");
                        count++;
                    }

                    // Create and configure JTextArea
                    JTextArea textArea = new JTextArea(displayText.toString());
                    textArea.setEditable(false);
                    textArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
                    textArea.setMargin(new Insets(10, 10, 10, 10));

                    // Add scrolling capability
                    JScrollPane scrollPane = new JScrollPane(textArea);
                    scrollPane.setPreferredSize(new Dimension(500, 400));

                    // Show in dialog
                    JOptionPane.showMessageDialog(null, scrollPane,
                            "Active Transactions", JOptionPane.INFORMATION_MESSAGE);

                } catch (Exception ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                    JOptionPane.showMessageDialog(null,
                            "Failed to fetch transactions", "Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }//GEN-LAST:event_btnActiveTransActionPerformed

    /**
     * Action handler for the button that retrieves and displays the blockchain.
     * This method creates a SwingWorker to fetch the blockchain in a background
     * thread, and once the blockchain is retrieved, it opens the Blockchain
     * Explorer UI in a separate thread.
     *
     * @param evt The action event triggered by clicking the button.
     */
    private void btnBCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBCActionPerformed
        // Create a SwingWorker to handle the blockchain retrieval in a background thread
        SwingWorker<BlockChain, Void> worker = new SwingWorker<>() {
            @Override
            protected BlockChain doInBackground() throws Exception {
                // Retrieve the blockchain from the node (this runs in a background thread)
                return node.getBlockchain();
            }

            @Override
            protected void done() {
                try {
                    // Get the retrieved blockchain (this happens on the EDT)
                    BlockChain blockchain = node.getBlockchain();

                    // Open the Blockchain Explorer UI in a separate thread to avoid blocking the EDT
                    SwingUtilities.invokeLater(() -> {
                        try {
                            // Open BlockchainExplorer with the retrieved blockchain
                            new blockchain.utils.BlockchainExplorer(blockchain, node, false);
                        } catch (RemoteException ex) {
                            // Log the error if RemoteException occurs while opening the explorer
                            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    });
                } catch (RemoteException e) {
                    // Handle any errors that occur during blockchain retrieval
                    // Show an error dialog if blockchain loading fails
                    JOptionPane.showMessageDialog(null, "Failed to load blockchain", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        // Execute the SwingWorker to fetch the blockchain
        worker.execute();
    }//GEN-LAST:event_btnBCActionPerformed

    /**
     * Action handler for the button that lists all users. This method is
     * triggered when the user clicks the "List Users" button.
     *
     * @param evt The action event triggered by clicking the button.
     */
    private void btnRefreshUserListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshUserListActionPerformed
        listUsers();
    }//GEN-LAST:event_btnRefreshUserListActionPerformed

    private void btnVerifyProofActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnVerifyProofActionPerformed
        // Get selected transaction
        int selectedTransactionIndex = jList2.getSelectedIndex();

        if (selectedTransactionIndex < 0) {
            JOptionPane.showMessageDialog(this, "Please select a transaction");
            return;
        }

        Entry selectedEntry = selectedBlock.getBuffer().get(selectedTransactionIndex);
        boolean isValid = false;

        if (selectedBlock.getBuffer().size() == 1) {
            // For single transaction blocks, compare the hash directly
            String transactionHash = MerkleTree.getHashValue(selectedEntry.toString());
            String rootHash = selectedBlock.getMerkleTree().getRoot();
            isValid = transactionHash.equals(rootHash);
        } else {
            // For multiple transactions, verify using Merkle proof
            List<String> proof = selectedBlock.getMerkleTree().getProof(selectedEntry);
            isValid = MerkleTree.isProofValid(selectedEntry, proof);
        }

        String message = isValid
                ? "Transaction is valid and included in block"
                : "Transaction verification failed";

        JOptionPane.showMessageDialog(this, message);
    }//GEN-LAST:event_btnVerifyProofActionPerformed

    /*private void btnCreateBlockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCreateBlockActionPerformed
        // Disable the button to prevent multiple clicks during mining
        btnCreateBlock.setEnabled(false);
        isMining = true; // Mark mining as in progress

        // Start a new thread to handle block creation and mining
        new Thread(() -> {
            try {
                // Retrieve the list of transactions from the node
                CopyOnWriteArraySet<Entry> blockTransactions = node.getTransactions();

                // If there are no transactions, exit early
                if (blockTransactions.isEmpty()) {
                    return;
                }

                // Create a new block with the transactions and the last block's hash
                Block b = new Block(node.getBlockchainLastHash(), blockTransactions);

                // Remove the transactions from the node as they're now included in the block
                node.removeTransactions(blockTransactions);

                // Start the mining process to find a valid nonce for the block
                int zeros = 4; // Define the number of leading zeros required for the proof of work
                int nonce = node.mine(b.getMinerData(), zeros); // Start mining until the correct nonce is found

                // Log the found nonce
                System.out.println("Nonce found: " + nonce);
                isMining = false; // Mark mining as completed

                // Set the nonce in the block and add it to the blockchain
                b.setNonce(nonce, zeros);
                node.addBlock(b);
            } catch (IOException e) {
                // Handle errors related to saving the blockchain
                System.err.println("Failed to save blockchain: " + e.getMessage());
            } catch (Exception ex) {
                // Log any other exceptions that occur
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                // Ensure that the mining state is reset even if an exception occurred
                isMining = false; // Mark mining as complete
                // Re-enable the button once mining is done or failed
                SwingUtilities.invokeLater(() -> btnCreateBlock.setEnabled(true));
            }
        }).start(); // Start the mining process in a new thread
    }*/
    /**
     * A flag that indicates whether the mining process is currently running.
     * This variable is accessed and modified in a thread-safe manner using the
     * 'volatile' keyword.
     */
    private volatile boolean isMining = false;

    /**
     * Starts a timer that monitors the mining process every second. If mining
     * is not in progress, it enables the "Create Block" button. The monitoring
     * check is performed on the Event Dispatch Thread (EDT).
     */
    private void startMiningMonitor() {
        // Create a Timer that runs every second
        Timer miningMonitor = new Timer(1000, e -> {
            // Run this check every second
            SwingUtilities.invokeLater(() -> {
                // If mining is not in progress, enable the button to create a new block
                if (!isMining) {
                    //btnCreateBlock.setEnabled(true);
                }
            });
        });

        miningMonitor.start(); // Start the timer that periodically checks the mining status
    }

    /**
     * Updates the list of entries associated with the current entity. This
     * method retrieves the user's entries from the blockchain, formats them,
     * and displays them in a list. Each entry shows the description, issuer,
     * recipient, and timestamp information.
     *
     * @throws Exception If there is an error while fetching or processing the
     * entries.
     */
    private void updateHistoryList() throws Exception {
        // Fetch the entries associated with the current entity (user) from the blockchain
        List<Entry> userEntries = node.getEntriesForEntity(this.pubKey); // Entries issued by this entity

        // Create a DefaultListModel to hold the formatted strings for the list
        DefaultListModel<String> listModel = new DefaultListModel<>();

        // Iterate through each entry and format the details for display
        for (Entry entry : userEntries) {
            // Get the usernames of the target and entity based on their public keys
            String targetUserName = getUsernameByPublicKey(entry.getTargetUserPublicKey());
            String entityUserName = getUsernameByPublicKey(entry.getEntityPublicKey());

            // Format the entry details into a readable string
            String formattedString = String.format("%s - Issued By: %s - Issued To: %s - Date: %s",
                    entry.getDescription(),
                    entityUserName,
                    targetUserName,
                    entry.getDateTime().toString());

            // Add the formatted string to the list model
            listModel.addElement(formattedString);
        }

        // Set the list model to the UI component (JList) to display the entries
        jList1.setModel(listModel);
    }

    /**
     * Retrieves the username associated with a given public key. This method
     * checks the public key against the known users and returns their username.
     * If the public key is not found in the list of users, it returns a portion
     * of the key itself.
     *
     * @param publicKey The public key whose associated username is to be
     * fetched.
     * @return The username associated with the provided public key.
     * @throws Exception If there is an error while retrieving the username.
     */
    private String getUsernameByPublicKey(PublicKey publicKey) throws Exception {
        // If the public key matches the current user's public key, return the username
        if (this.pubKey.equals(publicKey)) {
            return username;
        }

        // Search through the list of users to find a match for the public key
        for (User user : users) {
            if (user.getPub().equals(publicKey)) {
                return user.getName();
            }
        }

        // If the public key is not found in the list, return a substring of the public key as a fallback
        return Base64.getEncoder().encodeToString(publicKey.getEncoded()).substring(0, 10) + "...";
    }

    /**
     * Performs a search for users based on the input name and updates the UI
     * with the results. It filters users by name, excluding the current user's
     * name, and displays the matching users in a list. If no matches are found,
     * a message is displayed to the user.
     *
     * @throws Exception If any errors occur while performing the search or
     * updating the UI.
     */
    private void performSearch() {
        try {
            // Get the search term from the text field
            String nome = jTextFieldNomePesquisar.getText();

            // Check if the search term is not empty
            if (!nome.isEmpty()) {
                // Load the list of users from the data source
                listUsers();

                // Create a new DefaultListModel to hold the filtered user names
                DefaultListModel<String> filteredModel = new DefaultListModel<>();

                // Iterate through the users and add those that match the search term
                for (User user : users) {
                    // Check if the user's name contains the search term (case insensitive)
                    if (user.getName().toLowerCase().contains(nome.toLowerCase())) {
                        // Exclude the current user from the search results
                        if (!this.username.equals(user.getName())) {
                            filteredModel.addElement(user.getName()); // Add matching user to the list
                        }
                    }
                }

                // Set the filtered list model to the JList
                listUsers.setModel(filteredModel);

                // Set the selection mode to single selection (only one user can be selected)
                listUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                // If there are matches, select the first item in the list
                if (!filteredModel.isEmpty()) {
                    listUsers.setSelectedIndex(0);
                } else {
                    // If no matches are found, show a message
                    JOptionPane.showMessageDialog(this, "No matching users found.");
                }

            } else {
                // If the search term is empty, load the full list of users
                listUsers();
            }
        } catch (HeadlessException ex) {
            // Handle any errors that may occur during the search or UI update
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

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
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Main(null, null, null, null, null).setVisible(true); // Initial call without keys or username (for testing)
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnActiveTrans;
    private javax.swing.JButton btnAddTrans;
    private javax.swing.JButton btnBC;
    private javax.swing.JButton btnRefreshUserList;
    private javax.swing.JButton btnSearchUser;
    private javax.swing.JButton btnVerifyProof;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<String> jList1;
    private javax.swing.JList<String> jList2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextArea jTextAreaDescricao;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JTextField jTextFieldNomePesquisar;
    private javax.swing.JList<String> listUsers;
    private javax.swing.JTextField txtBlockHash;
    private javax.swing.JTextArea txtEntity;
    private javax.swing.JTextField txtEntityPublicKey;
    private javax.swing.JTextField txtMerkleRoot;
    private javax.swing.JTextField txtNonce;
    private javax.swing.JTextField txtProof;
    private javax.swing.JTextField txtTimestamp;
    private javax.swing.JTextField txtTransactionHash;
    private javax.swing.JTextArea txtUser;
    private javax.swing.JTextArea txtareaDescription;
    // End of variables declaration//GEN-END:variables
}
