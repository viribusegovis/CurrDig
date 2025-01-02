package blockchain.utils;

import currdig.core.Entry;
import currdig.core.User;
import javax.swing.*;
import java.awt.*;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import p2p.IremoteP2P;

/**
 * The BlockchainExplorer class provides a graphical user interface to explore
 * the blockchain. It allows users to view details of each block and search for
 * transactions related to specific users.
 */
public class BlockchainExplorer extends JFrame {

    private IremoteP2P node;
    private JList<String> blockList;
    private JEditorPane blockDetails;
    private BlockChain blockchain;
    private JPanel topPanel;
    private JScrollPane blockListScroll;
    private JComboBox<String> userComboBox;
    private JButton searchButton;

    // DateTimeFormatter to format timestamps
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss - dd/MM/yyyy");

    /**
     * Constructor for initializing the BlockchainExplorer GUI.
     *
     * @param blockchain The blockchain to display and explore
     * @param node The P2P node for accessing user data
     * @param closeAppOnExit Whether to close the application on exit or just
     * dispose the window
     * @throws RemoteException If there is an issue with the remote node
     */
    public BlockchainExplorer(BlockChain blockchain, IremoteP2P node, boolean closeAppOnExit) throws RemoteException {
        this.blockchain = blockchain;
        setTitle("Blockchain Explorer");
        setLayout(new BorderLayout());

        this.node = node;

        // Check if blockchain is loaded
        System.out.println("Blockchain Size: " + blockchain.getSize());

        // Set up the top panel for title and search bar
        topPanel = new JPanel();
        topPanel.setBackground(new Color(33, 37, 41)); // Dark background for the top panel
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        JLabel titleLabel = new JLabel("Blockchain Explorer");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        topPanel.add(titleLabel);

        // Populate user combo box with usernames
        List<String> usernames = getUsernamesList();
        userComboBox = new JComboBox<>(usernames.toArray(new String[0]));
        userComboBox.setEditable(true); // Allow typing

        searchButton = new JButton("Search");
        searchButton.setFont(new Font("Arial", Font.PLAIN, 14));
        searchButton.addActionListener(e -> {
            try {
                searchTransactionsByUserKey();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        });

        topPanel.add(userComboBox);
        topPanel.add(searchButton);

        // Set up Block list
        blockList = new JList<>(getBlockListData());
        blockList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        blockList.setFont(new Font("Arial", Font.PLAIN, 14));
        blockList.setBackground(new Color(240, 240, 240)); // Light background for block list
        blockList.setSelectionBackground(new Color(0, 123, 255)); // Highlight selection with blue
        blockList.setSelectionForeground(Color.WHITE); // Change text color of selected item
        blockList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (isSelected) {
                    label.setBackground(new Color(0, 123, 255));
                    label.setForeground(Color.WHITE);
                } else {
                    label.setBackground(new Color(240, 240, 240));
                    label.setForeground(Color.BLACK);
                }
                return label;
            }
        });
        blockList.addListSelectionListener(e -> showBlockDetails(blockList.getSelectedIndex()));
        blockListScroll = new JScrollPane(blockList);
        blockListScroll.setPreferredSize(new Dimension(250, 500));

        // Set up block details display area (use JEditorPane)
        blockDetails = new JEditorPane();
        blockDetails.setContentType("text/html");  // Set content type to HTML
        blockDetails.setFont(new Font("Monospaced", Font.PLAIN, 14));
        blockDetails.setEditable(false);
        blockDetails.setBackground(new Color(245, 245, 245)); // Lighter background for details
        blockDetails.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        blockDetails.setCaretColor(Color.BLACK);

        JScrollPane blockDetailsScroll = new JScrollPane(blockDetails);
        blockDetailsScroll.setBorder(BorderFactory.createTitledBorder("Block Details"));

        // Layout setup
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(20, 20));
        contentPanel.add(blockListScroll, BorderLayout.WEST);
        contentPanel.add(blockDetailsScroll, BorderLayout.CENTER);

        // Add components to the frame
        add(topPanel, BorderLayout.NORTH);
        add(contentPanel, BorderLayout.CENTER);

        setSize(900, 600);
        setLocationRelativeTo(null); // Center the window

        // Set the close operation to dispose the window, not exit the whole app
        if (closeAppOnExit) {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        } else {
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        }

        setVisible(true);
    }

    /**
     * Get the list of usernames from the P2P node.
     *
     * @return A list of usernames
     * @throws RemoteException If there is an issue accessing the remote node
     */
    private List<String> getUsernamesList() throws RemoteException {
        List<String> usernames = new ArrayList<>();
        for (User user : node.listUsers()) {
            usernames.add(user.getName());
        }
        return usernames;
    }

    /**
     * Returns the block data for the block list display.
     *
     * @return An array of block data as strings
     */
    private String[] getBlockListData() {
        List<Block> blocks = blockchain.getChain();
        String[] blockData = new String[blocks.size()];
        if (blocks.isEmpty()) {
            System.out.println("No blocks available.");
            return new String[]{"No blocks available"};
        }
        for (int i = 0; i < blocks.size(); i++) {
            blockData[i] = "Block " + (i + 1) + " - Hash: " + blocks.get(i).getCurrentHash().substring(0, 10) + "...";
        }
        return blockData;
    }

    /**
     * Display the details of the selected block in the block list.
     *
     * @param index The index of the selected block
     */
    private void showBlockDetails(int index) {
        if (index >= 0 && index < blockchain.getSize()) {
            Block block = blockchain.get(index);
            StringBuilder details = new StringBuilder();

            details.append("<html><b>Block #").append(index + 1).append("</b><br>");
            details.append("<b>Previous Block:</b> ").append(block.getPreviousHash()).append("<br>");
            details.append("<b>Nonce:</b> ").append(block.getNonce()).append("<br>");
            details.append("<b>Current Block:</b> ").append(block.getCurrentHash()).append("<br>");
            details.append("<b>Merkle Root:</b> ").append(block.getMerkleRoot()).append("<br><br>");
            details.append("<b>Transactions:</b><br>");

            for (Entry entry : block.transactions()) {
                try {
                    // Get the recipient (target) username
                    String recipientUsername = getUsernameByPublicKey(entry.getTargetUserPublicKey());

                    String senderUsername = getUsernameByPublicKey(entry.getEntityPublicKey());

                    String timestamp = entry.getDateTime().format(formatter);

                    details.append("<br><b>Timestamp:</b> ").append(timestamp).append("<br>");
                    details.append("<b>Sender Username:</b> ").append(senderUsername).append("<br>");
                    details.append("<b>Recipient Username:</b> ").append(recipientUsername).append("<br>");
                    details.append("<b>Description:</b> ").append(entry.getDescription()).append("<br>");

                } catch (Exception e) {
                    e.printStackTrace();
                    details.append("<b>Error loading user details</b><br>");
                }
            }

            details.append("</html>");
            blockDetails.setText(details.toString());
        }
    }

    /**
     * Search for transactions by username.
     */
    private void searchTransactionsByUserKey() throws Exception {
        String searchUsername = (String) userComboBox.getSelectedItem();  // Get selected username

        if (searchUsername == null || searchUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select or type a username to search for.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Get the public key corresponding to the entered username
        PublicKey searchKey = null;
        for (User user : node.listUsers()) {
            if (user.getName().equalsIgnoreCase(searchUsername)) {
                searchKey = user.getPub();
                break;
            }
        }

        if (searchKey == null) {
            JOptionPane.showMessageDialog(this, "User not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Search the blockchain for transactions where the target public key matches the searched key
        StringBuilder results = new StringBuilder("<html><b>Search Results for Username: " + searchUsername + "</b><br>");
        boolean found = false;

        for (Block block : blockchain.getChain()) {
            for (Entry entry : block.transactions()) {
                if (entry.getTargetUserPublicKey().equals(searchKey)) {
                    found = true;

                    String timestamp = entry.getDateTime().format(formatter);

                    // Get the usernames for entity (sender) and target (recipient)
                    String entityUsername = getUsernameByPublicKey(entry.getEntityPublicKey());
                    String targetUsername = getUsernameByPublicKey(entry.getTargetUserPublicKey());

                    // Append transaction details
                    results.append("<br><b>Timestamp:</b> ").append(timestamp).append("<br>");
                    results.append("<b>Sender Username:</b> ").append(entityUsername).append("<br>");
                    results.append("<b>Recipient Username:</b> ").append(targetUsername).append("<br>");
                    results.append("<b>Description:</b> ").append(entry.getDescription()).append("<br>");
                }
            }
        }

        if (!found) {
            results.append("No transactions found for the provided username.");
        }

        results.append("</html>");

        // Display search results in blockDetails
        blockDetails.setText(results.toString());
    }

    /**
     * Get the username corresponding to the given public key.
     *
     * @param publicKey The public key to look up
     * @return The username or a truncated version of the public key if not
     * found
     * @throws Exception If there is an issue retrieving the username
     */
    private String getUsernameByPublicKey(PublicKey publicKey) throws Exception {
        for (User user : node.listUsers()) {
            if (user.getPub().equals(publicKey)) {
                return user.getName();
            }
        }
        // If the user is not found, return a part of the public key (20 chars)
        return Base64.getEncoder().encodeToString(publicKey.getEncoded()).substring(0, 20) + "...";
    }
}
