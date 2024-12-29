/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package currdig.gui;

import blockchain.utils.Block;
import blockchain.utils.BlockChain;
import blockchain.utils.Hash;
import blockchain.utils.SecurityUtils;
import currdig.core.Entry;
import currdig.core.User;
import currdig.utils.Utils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.rmi.RemoteException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import p2p.IremoteP2P;

/**
 *
 * @author bmsff
 */
public class Main extends javax.swing.JFrame {

    private IremoteP2P node;

    private PublicKey pubKey;
    private PrivateKey privKey;
    private Key simKey;
    private String username;

    private List<User> users;

    public static String fileCurrDig = "currdig.obj";

    /**
     * Creates new form Main
     *
     * @param pubKey
     * @param privKey
     * @param simKey
     * @param username
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

    private void initMyComponents() {
        try {
            users = Utils.loadUsers();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error loading users: " + ex.getMessage());
        }

        if (this.pubKey != null) {
            String pub = Base64.getEncoder().encodeToString(this.pubKey.getEncoded());
            txtEntity.setText(pub);
        } else {
            // Optionally set txtEntity to empty or a default message
            txtEntity.setText("");
        }
        listUsers();

        listUsers.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
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

        jList1.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                if (!evt.getValueIsAdjusting()) {
                    displayBlockList();
                }
            }
        });

        // Add a listener for tab changes
        jTabbedPane1.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {
                if (jTabbedPane1.getSelectedIndex() == 1) {
                    try {
                        // Assuming History is the second tab
                        updateHistoryList();
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
                        // Notify the user and close the application
                        JOptionPane.showMessageDialog(this, "Node is unavailable. Closing application.");
                        System.out.println("Node is unresponsive. Application will exit.");
                        System.exit(1); // Exit the application
                    }

                    // Sleep before the next health check
                    Thread.sleep(5000); // Check every 5 seconds
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

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
        jPanel5 = new javax.swing.JPanel();
        jButtonAdicionar = new javax.swing.JButton();
        jButtonCriarBloco = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTextAreaDescricao = new javax.swing.JTextArea();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtEntity = new javax.swing.JTextArea();
        jScrollPane6 = new javax.swing.JScrollPane();
        txtUser = new javax.swing.JTextArea();
        jSeparator2 = new javax.swing.JSeparator();
        jButtonListar = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jTextFieldNomePesquisar = new javax.swing.JTextField();
        jButtonPesquisarCurr = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        listUsers = new javax.swing.JList<>();
        jButton3 = new javax.swing.JButton();
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
        jButton5 = new javax.swing.JButton();
        jScrollPane7 = new javax.swing.JScrollPane();
        jList2 = new javax.swing.JList<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        getContentPane().setLayout(new javax.swing.BoxLayout(getContentPane(), javax.swing.BoxLayout.LINE_AXIS));

        jTabbedPane1.setFont(new java.awt.Font("Segoe UI", 0, 18)); // NOI18N

        jPanel1.setBackground(new java.awt.Color(255, 255, 255));
        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jPanel5.setBackground(new java.awt.Color(255, 255, 255));
        jPanel5.setLayout(new java.awt.BorderLayout());

        jButtonAdicionar.setText("Adicionar");
        jButtonAdicionar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonAdicionarActionPerformed(evt);
            }
        });
        jPanel5.add(jButtonAdicionar, java.awt.BorderLayout.CENTER);

        jButtonCriarBloco.setText("Criar Bloco");
        jButtonCriarBloco.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCriarBlocoActionPerformed(evt);
            }
        });
        jPanel5.add(jButtonCriarBloco, java.awt.BorderLayout.PAGE_END);

        jTextAreaDescricao.setColumns(20);
        jTextAreaDescricao.setLineWrap(true);
        jTextAreaDescricao.setRows(5);
        jTextAreaDescricao.setBorder(javax.swing.BorderFactory.createTitledBorder("Descrição"));
        jTextAreaDescricao.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        jScrollPane2.setViewportView(jTextAreaDescricao);

        txtEntity.setEditable(false);
        txtEntity.setBackground(new java.awt.Color(255, 255, 255));
        txtEntity.setColumns(20);
        txtEntity.setLineWrap(true);
        txtEntity.setRows(5);
        txtEntity.setBorder(javax.swing.BorderFactory.createTitledBorder("Entidade"));
        txtEntity.setFocusable(false);
        jScrollPane3.setViewportView(txtEntity);

        txtUser.setEditable(false);
        txtUser.setBackground(new java.awt.Color(255, 255, 255));
        txtUser.setColumns(20);
        txtUser.setLineWrap(true);
        txtUser.setRows(5);
        txtUser.setBorder(javax.swing.BorderFactory.createTitledBorder("Utilizador"));
        txtUser.setFocusable(false);
        jScrollPane6.setViewportView(txtUser);

        jButtonListar.setText("Atualizar Lista");
        jButtonListar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonListarActionPerformed(evt);
            }
        });

        jButton1.setText("mostrar bc");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("transacoes ativas");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton4.setText("unlock");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane2)
                            .addComponent(jScrollPane3)
                            .addComponent(jScrollPane6)
                            .addComponent(jButtonListar, javax.swing.GroupLayout.DEFAULT_SIZE, 312, Short.MAX_VALUE)
                            .addComponent(jSeparator2)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(60, 60, 60)
                        .addComponent(jButton2)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addComponent(jButton1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton4)
                .addGap(29, 29, 29))
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(83, 83, 83)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(68, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 47, Short.MAX_VALUE)
                        .addComponent(jButton1)
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButton4)
                        .addGap(31, 31, 31)))
                .addComponent(jButton2)
                .addGap(56, 56, 56)
                .addComponent(jButtonListar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 3, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(304, 304, 304)
                    .addComponent(jLabel3)
                    .addContainerGap(79, Short.MAX_VALUE)))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("Pesquisa por nome"));
        jPanel3.setToolTipText("");
        jPanel3.setName(""); // NOI18N
        jPanel3.setLayout(new javax.swing.BoxLayout(jPanel3, javax.swing.BoxLayout.LINE_AXIS));
        jPanel3.add(jTextFieldNomePesquisar);

        jButtonPesquisarCurr.setText("Pesquisar");
        jButtonPesquisarCurr.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonPesquisarCurrActionPerformed(evt);
            }
        });
        jPanel3.add(jButtonPesquisarCurr);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Listagem"));

        listUsers.setBorder(null);
        listUsers.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        listUsers.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "teste", "teste" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
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
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jButton3.setText("mostrar blockchain bloco");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(58, 58, 58)
                .addComponent(jButton3)
                .addContainerGap(700, Short.MAX_VALUE))
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addContainerGap()))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addContainerGap(524, Short.MAX_VALUE)
                .addComponent(jButton3)
                .addGap(109, 109, 109))
            .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel7Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addContainerGap()))
        );

        jTabbedPane1.addTab("Submitions", jPanel7);

        jList1.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
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
                            .addComponent(jLabel1)
                            .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 440, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel2)
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtEntityPublicKey)))
                    .addComponent(jLabel5)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtTimestamp, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel4))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel9Layout.createSequentialGroup()
                                        .addComponent(jLabel6)
                                        .addGap(0, 0, Short.MAX_VALUE))
                                    .addComponent(txtTransactionHash)))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel9Layout.createSequentialGroup()
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel7)
                                    .addComponent(txtBlockHash, javax.swing.GroupLayout.PREFERRED_SIZE, 295, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtMerkleRoot, javax.swing.GroupLayout.PREFERRED_SIZE, 290, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel8))))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtNonce, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel9)
                            .addComponent(jLabel10)
                            .addComponent(txtProof, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 291, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
                    .addComponent(txtProof, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 127, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jButton5.setText("jButton5");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jList2.setModel(new javax.swing.AbstractListModel<String>() {
            String[] strings = { "Item 1", "Item 2", "Item 3", "Item 4", "Item 5" };
            public int getSize() { return strings.length; }
            public String getElementAt(int i) { return strings[i]; }
        });
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
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 333, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton5)))
                .addContainerGap())
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE)
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGap(122, 122, 122)
                        .addComponent(jButton5))
                    .addComponent(jScrollPane7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTabbedPane1.addTab("History", jPanel8);

        getContentPane().add(jTabbedPane1);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonListarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonListarActionPerformed
        listUsers();
    }//GEN-LAST:event_jButtonListarActionPerformed

    private void listUsers() {
        try {
            // Load users from Utils
            users = node.listUsers();

            // Create a DefaultListModel<String> to hold the usernames
            DefaultListModel<String> listModel = new DefaultListModel<>();

            for (User user : users) {
                if (user.getName().equals(this.username)) {
                    users.remove(user);
                    break; // Stop searching once a match is found
                }
            }

            // Loop through the users and add the usernames to the listModel
            for (User user : users) {
                listModel.addElement(user.getName());
            }

            // Set the listModel to the listUsers JList
            listUsers.setModel(listModel);

            // Set selection mode to single selection
            listUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(Main.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
    }

    private void listUsersValueChanged(javax.swing.event.ListSelectionEvent evt) throws Exception {
        if (!evt.getValueIsAdjusting()) { // Ensure the event is the final selection
            int selectedIndex = listUsers.getSelectedIndex();

            if (selectedIndex != -1) {
                // Get the selected user

                User selectedUser = users.get(selectedIndex);

                // Get the public key of the selected user
                PublicKey pubKey = selectedUser.getPub();

                // Encode the public key to Base64 string
                String pubKeyString = Base64.getEncoder().encodeToString(pubKey.getEncoded());

                // Update the txtEntity text area
                txtUser.setText(pubKeyString);
            }
        }
    }

    // Helper method to decode public key from Base64 string
    private PublicKey decodePublicKey(String encodedKey) throws Exception {
        byte[] publicBytes = Base64.getDecoder().decode(encodedKey);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePublic(keySpec);
    }

    private List<Block> allBlocks;  // Store blocks globally to avoid issues

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

    private void displayBlockDetails(Block selectedBlock) {
        // Display Block Hash in the TextBox
        txtBlockHash.setText(selectedBlock.getCurrentHash()); // Make sure this updates the Block Hash field

        // Clear any previous transaction details
        clearTransactionDetails();
    }

    private void displayTransactionsForBlock(Block selectedBlock) {
        List<Entry> selectedTransactions = selectedBlock.getBuffer();  // Get transactions from the selected block

        // Create a DefaultListModel for jList2 (the transaction list)
        DefaultListModel<String> transactionListModel = new DefaultListModel<>();
        for (Entry entry : selectedTransactions) {
            transactionListModel.addElement("Transaction: " + entry.getDescription() + " - "
                    + Base64.getEncoder().encodeToString(entry.getTargetUserPublicKey().getEncoded()).substring(0, 10) + "...");
        }

        // Set the model for jList2
        jList2.setModel(transactionListModel);

        // Add a listener to handle transaction selection from jList2
        jList2.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedTransactionIndex = jList2.getSelectedIndex();
                if (selectedTransactionIndex != -1) {
                    Entry selectedEntry = selectedTransactions.get(selectedTransactionIndex);
                    displayTransactionDetailsForEntry(selectedEntry, selectedBlock); // Show transaction details
                }
            }
        });
    }

    private void resetTransactionList() {
        // Reset jList2 to remove any previous selections
        jList2.clearSelection();  // Deselect any previously selected item in jList2
        clearTransactionDetails(); // Clear any transaction details in the text fields
    }

    private void displayTransactionDetailsForEntry(Entry selectedEntry, Block selectedBlock) {
        // Display transaction details in text fields

        // Update User's Public Key field (Target User's Public Key)
        jTextField1.setText(Base64.getEncoder().encodeToString(selectedEntry.getTargetUserPublicKey().getEncoded()));

        // Update Entity's Public Key field (Entity Public Key)
        txtEntityPublicKey.setText(Base64.getEncoder().encodeToString(selectedEntry.getEntityPublicKey().getEncoded()));

        // Update Timestamp field (Assume timestamp is available in Entry)
        txtTimestamp.setText(selectedEntry.getDateTime().toString());

        // Update Description field (Assume description is available in Entry)
        txtareaDescription.setText(selectedEntry.getDescription());

        // Update Transaction Hash (hash of the entry)
        txtTransactionHash.setText(Hash.getHash(selectedEntry.toString()));

        // Optionally, add details for Merkle Tree and Proof
        txtNonce.setText(String.valueOf(selectedBlock.getNonce()));

        // Check if Merkle Tree is available for the selected block
        if (selectedBlock.getMerkleTree() != null) {
            List<String> proof = selectedBlock.getMerkleTree().getProof(selectedEntry);

            // Format proof for display
            StringBuilder proofText = new StringBuilder();
            for (String hash : proof) {
                proofText.append(hash).append("\n");
            }

            txtProof.setText(proofText.toString());
            txtMerkleRoot.setText(selectedBlock.getMerkleTree().getRoot());
        } else {
            txtMerkleRoot.setText("Block not finalized");
            txtProof.setText("Block not finalized");
        }
    }

// Helper method to clear transaction details in text fields
    private void clearTransactionDetails() {
        jTextField1.setText("");
        txtEntityPublicKey.setText("");
        txtTimestamp.setText("");
        txtareaDescription.setText("");
        txtTransactionHash.setText("");
        txtNonce.setText("");
        txtProof.setText("");
        txtMerkleRoot.setText("");
    }


    private void jButtonAdicionarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonAdicionarActionPerformed
        jButtonAdicionar.setEnabled(false);

        new Thread(() -> {
            try {
                // Get the description from the text area
                String description = jTextAreaDescricao.getText();

                // Get the target user's public key from the txtUser text area
                String targetUserPubKeyString = txtUser.getText();
                PublicKey targetUserPubKey = SecurityUtils.getPublicKey(Base64.getDecoder().decode(targetUserPubKeyString));

                // Create the Entry with the target user's public key
                Entry newEntry = new Entry(description, this.pubKey, targetUserPubKey); // Updated constructor

                // Sign the Entry
                byte[] signature = SecurityUtils.sign(newEntry.toString().getBytes(), this.privKey);

                // Add the Entry to the Curriculum (blockchain)
                //curriculum.addEntry(targetUserPubKey, newEntry, signature);
                if (!node.addTransaction(targetUserPubKey, newEntry, signature)) {
                    throw new Exception();
                }

                // Clear the input fields
                jTextAreaDescricao.setText("");
                txtUser.setText("");
                //curriculum.save(fileCurrDig);
                updateHistoryList();
                JOptionPane.showMessageDialog(this, "Entry added successfully!");

                SwingUtilities.invokeLater(() -> {
                    //enable button
                    jButtonAdicionar.setEnabled(true);
                });

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding entry: " + ex.getMessage());
                jButtonAdicionar.setEnabled(true);
            } finally {
                jButtonAdicionar.setEnabled(true);
            }
        }).start();
    }//GEN-LAST:event_jButtonAdicionarActionPerformed

    private void jButtonPesquisarCurrActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonPesquisarCurrActionPerformed
        performSearch();
    }//GEN-LAST:event_jButtonPesquisarCurrActionPerformed

    private volatile boolean isMining = false;

    private void jButtonCriarBlocoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCriarBlocoActionPerformed
        // Disable the button to prevent multiple clicks
        jButtonCriarBloco.setEnabled(false);
        isMining = true; // Mark mining as in progress

        new Thread(() -> {
            try {
                // Make a block
                CopyOnWriteArraySet<Entry> blockTransactions = node.getTransactions();
                if (blockTransactions.isEmpty()) {
                    return;
                }
                Block b = new Block(node.getBlockchainLastHash(), blockTransactions);

                // Remove the transactions
                node.removeTransactions(blockTransactions);

                // Start mining the block
                int zeros = 4;
                int nonce = node.mine(b.getMinerData(), zeros); // Blocks until mining completes

                System.out.println("Nonce found: " + nonce);
                isMining = false;

                // Update the nonce and add the block
                b.setNonce(nonce, zeros);
                node.addBlock(b);
            } catch (IOException e) {
                System.err.println("Failed to save blockchain: " + e.getMessage());
            } catch (Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                isMining = false; // Mark mining as complete
            }
        }).start();
    }//GEN-LAST:event_jButtonCriarBlocoActionPerformed

    private void startMiningMonitor() {
        Timer miningMonitor = new Timer(1000, e -> {
            // Run this check every second
            SwingUtilities.invokeLater(() -> {
                if (!isMining) {
                    jButtonCriarBloco.setEnabled(true);
                }
            });
        });
        miningMonitor.start(); // Start the timer
    }


    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        try {
            JOptionPane.showMessageDialog(null, node.getBlockchainSize(), "Notification", JOptionPane.INFORMATION_MESSAGE);
        } catch (RemoteException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        try {
            JOptionPane.showMessageDialog(null, node.getTransactions(), "Notification", JOptionPane.INFORMATION_MESSAGE);
        } catch (RemoteException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        try {
            // Get the entire blockchain from the node
            BlockChain blockchain = node.getBlockchain();

            // Create a string to display all blocks
            StringBuilder blockchainInfo = new StringBuilder("Blockchain Information:\n");

            // Assuming BlockChain has a method to iterate over blocks
            for (Block block : blockchain.getChain()) { // You might need to replace getBlocks() with the appropriate method
                blockchainInfo.append(block.toString()).append("\n"); // Append block info
            }

            // Display the blockchain information in a message dialog
            JOptionPane.showMessageDialog(null, blockchainInfo.toString(), "Blockchain Notification", JOptionPane.INFORMATION_MESSAGE);

        } catch (RemoteException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        jButtonCriarBloco.setEnabled(true);
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        displayBlockList();
    }//GEN-LAST:event_jButton5ActionPerformed

    private void updateHistoryList() throws Exception {
        List<Entry> userEntries = node.getEntriesForEntity(this.pubKey); // Entries issued by this entity

        DefaultListModel<String> listModel = new DefaultListModel<>();
        for (Entry entry : userEntries) {
            String targetUserName = getUsernameByPublicKey(entry.getTargetUserPublicKey());
            String entityUserName = getUsernameByPublicKey(entry.getEntityPublicKey());
            String formattedString = String.format("%s - Issued By: %s - Issued To: %s - Date: %s",
                    entry.getDescription(),
                    entityUserName,
                    targetUserName,
                    entry.getDateTime().toString());
            listModel.addElement(formattedString);
        }
        jList1.setModel(listModel);
    }

    private String getUsernameByPublicKey(PublicKey publicKey) throws Exception {
        if (this.pubKey.equals(publicKey)) {
            return username;
        }
        for (User user : users) {
            if (user.getPub().equals(publicKey)) {
                return user.getName();
            }
        }
        // If the user is not found, return a part of the public key
        return Base64.getEncoder().encodeToString(publicKey.getEncoded()).substring(0, 10) + "...";
    }

    private void performSearch() {
        try {
            String nome = jTextFieldNomePesquisar.getText();

            if (!nome.isEmpty()) {
                // Load users
                listUsers();

                // Filter users matching the search
                DefaultListModel<String> filteredModel = new DefaultListModel<>();
                for (User user : users) {
                    if (user.getName().toLowerCase().contains(nome.toLowerCase())) {
                        if (!this.username.equals(user.getName())) {
                            filteredModel.addElement(user.getName());
                        }
                    }
                }

                // Set the filtered model to the listUsers JList
                listUsers.setModel(filteredModel);

                // Set selection mode to single selection
                listUsers.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

                // Select the first item if there are matches
                if (!filteredModel.isEmpty()) {
                    listUsers.setSelectedIndex(0);
                } else {
                    JOptionPane.showMessageDialog(this, "No matching users found.");
                }

            } else {
                listUsers();
            }
        } catch (Exception ex) {
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
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JButton jButtonAdicionar;
    private javax.swing.JButton jButtonCriarBloco;
    private javax.swing.JButton jButtonListar;
    private javax.swing.JButton jButtonPesquisarCurr;
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
    private javax.swing.JPanel jPanel5;
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
    private javax.swing.JSeparator jSeparator2;
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
