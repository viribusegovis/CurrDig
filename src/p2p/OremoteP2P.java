package p2p;

import blockchain.utils.Block;
import blockchain.utils.BlockChain;
import blockchain.utils.SecurityUtils;

import currdig.core.Entry;
import currdig.core.User;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import currdig.utils.RMI;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import blockchain.utils.Miner;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Represents a remote peer-to-peer object for managing transactions and
 * blockchain. This class listens for incoming connections, processes
 * transactions, mines blocks, and manages the blockchain in a distributed
 * manner. It implements the {@link IremoteP2P} interface to provide remote
 * communication capabilities.
 *
 * This class supports: - Listening for incoming network connections. - Managing
 * transactions and adding them to a buffer. - Mining blocks and adding them to
 * the blockchain. - Periodically checking and mining blocks.
 *
 * @throws RemoteException if there is an issue with remote communication.
 */
public class OremoteP2P extends UnicastRemoteObject implements IremoteP2P {

    private final String address;
    private final CopyOnWriteArrayList<IremoteP2P> network; // List of connected peers in the network
    private CopyOnWriteArraySet<Entry> transactionBuffer; // Buffer for pending transactions
    private final P2Plistener listener; // Listener to handle events like start and block announcements
    private Map<PublicKey, List<Entry>> userEntries; // Map of user entries by their public key

    private static final String BLOCHAIN_FILENAME = "currdig.obj"; // Filename to save/load blockchain

    // Concurrent mining object for distributed mining
    Miner myMiner;
    // Blockchain object prepared for concurrent access
    BlockChain myBlockchain;

    private final ScheduledExecutorService executorService; // Executor service for scheduled tasks

    /**
     * Constructor to initialize the peer-to-peer object with an address and
     * listener. This will start the object listening for incoming connections,
     * initialize mining, and periodically mine blocks if there are pending
     * transactions.
     *
     * @param address The address of the current peer in the network.
     * @param listener The listener to handle events during the operation.
     * @throws RemoteException if there is an issue with remote communication.
     */
    public OremoteP2P(String address, P2Plistener listener) throws RemoteException {
        super(RMI.getAdressPort(address));
        this.address = address;
        this.network = new CopyOnWriteArrayList<>();
        this.transactionBuffer = new CopyOnWriteArraySet<>();
        this.listener = listener;
        myMiner = new Miner(listener); // Initialize the mining object with the listener
        myBlockchain = new BlockChain(); // Initialize the blockchain
        userEntries = new HashMap<>(); // Initialize user entries map

        try {
            myBlockchain.load("currdig.obj"); // Load blockchain from file
        } catch (Exception e) {
            // Handle loading errors if any
        }

        listener.onStart("Object " + address + " listening"); // Notify listener that the object is listening
        System.out.println("Object " + address + " listening");

        // Start the health-check thread
        startHealthCheck();

        // Set up periodic block creation task every 30 seconds
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(this::checkAndMineBlock, 0, 30, TimeUnit.SECONDS);
    }

    /**
     * Periodically checks if there are pending transactions. If so, starts
     * mining a new block.
     */
    private void checkAndMineBlock() {
        try {
            // If there are pending transactions, we will mine
            if (!getTransactions().isEmpty()) {
                // Start mining the block
                mineBlock();
            } else {
                // No transactions to mine
                System.out.println("Checked and there are no transactions!");
            }
        } catch (Exception e) {
            // Handle any exceptions that may occur during the block mining check
        }
    }

    /**
     * Mines a new block if there are pending transactions. This involves
     * removing the transactions from the buffer, creating a block, and adding
     * it to the blockchain.
     *
     * @throws Exception if there are issues during the mining process.
     */
    private void mineBlock() throws Exception {
        // Get the transactions for the block
        CopyOnWriteArraySet<Entry> blockTransactions = getTransactions();

        // If no transactions, we cannot mine a block
        if (blockTransactions.isEmpty()) {
            System.out.println("No transactions to mine.");
            return;
        }

        // Remove the transactions from the local buffer
        removeTransactions(blockTransactions);

        // Notify other peers to remove the transactions as well
        for (IremoteP2P peer : network) {
            peer.removeTransactions(blockTransactions);
        }

        // Create the block with the transactions
        Block b = new Block(myBlockchain.getLastBlockHash(), blockTransactions);

        // Start mining the block with difficulty (number of leading zeros)
        int zeros = 4; // Difficulty level
        int nonce = mine(b.getMinerData(), zeros); // Block mining process

        System.out.println("Block mined, nonce found: " + nonce);

        // Update the nonce and add the block to the blockchain
        b.setNonce(nonce, zeros);
        addBlock(b);

        // Log block added to the blockchain
        System.out.println("Block added to blockchain by " + address);
    }

    /**
     * Implements the P2P network logic for managing node connections,
     * synchronization, and health checks in a peer-to-peer network. This class
     * provides functionality for adding nodes, checking network health, and
     * synchronizing blockchain data across nodes.
     *
     * @throws RemoteException if there is an issue with remote communication.
     */
    @Override
    public String getAddress() throws RemoteException {
        return address;
    }

    /**
     * Checks if a node with the given address is already part of the network.
     * This method iterates through the network and compares the addresses of
     * the nodes.
     *
     * @param address The address of the node to check.
     * @return True if the node is found in the network, false otherwise.
     */
    private boolean isInNetwork(String address) {
        for (int i = network.size() - 1; i >= 0; i--) {
            try {
                // If the node address matches, return true
                if (network.get(i).getAddress().equals(address)) {
                    return true;
                }
            } catch (RemoteException ex) {
                // If there's an exception (node is unreachable), remove it from the network
                network.remove(i);
            }
        }
        return false; // Node not found
    }

    /**
     * Adds a new node to the network. This method ensures that the node is not
     * already part of the network and synchronizes user data and blockchain
     * with the new node.
     *
     * @param node The node to add to the network.
     * @throws RemoteException if there is an issue with remote communication.
     */
    @Override
    public void addNode(IremoteP2P node) throws RemoteException {
        try {
            // If the node is already part of the network, don't add it
            if (isInNetwork(node.getAddress())) {
                System.out.println("Already have address: " + node.getAddress());
                return;
            }

            // Add the new node to the network
            network.add(node);
            listener.onConnect(node.getAddress());
            System.out.println("Added node: " + node.getAddress());

            // If the node isn't this one, propagate the new node to the other node
            if (!node.getAddress().equals(this.address)) {
                node.addNode(this);
            }

            // Synchronize user data between the nodes
            syncUserDataFolder(node);  // Synchronize user data when a new node joins

            // Propagate the new node to all connected peers in the network
            for (IremoteP2P peer : network) {
                peer.addNode(node);
            }

            // Print the entire network for monitoring
            System.out.println("P2P Network:");
            for (IremoteP2P peer : network) {
                System.out.println(peer.getAddress());
            }

            // Synchronize the blockchain with the new node
            synchronizeBlockchain();
        } catch (Exception ex) {
            // Log any exceptions that occur during the process
            Logger.getLogger(OremoteP2P.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Retrieves the list of nodes currently part of the network.
     *
     * @return A list of remote nodes in the network.
     * @throws RemoteException if there is an issue with remote communication.
     */
    @Override
    public List<IremoteP2P> getNetwork() throws RemoteException {
        return new ArrayList<>(network);
    }

    /**
     * Starts a health check thread that monitors the status of the nodes in the
     * network. The thread periodically checks each node for responsiveness and
     * removes unresponsive nodes from the network.
     */
    private void startHealthCheck() {
        new Thread(() -> {
            while (true) {
                try {
                    List<IremoteP2P> unresponsiveNodes = new ArrayList<>(); // List of unresponsive nodes
                    List<String> removedAddresses = new ArrayList<>(); // List of addresses of removed nodes

                    for (IremoteP2P node : network) {
                        try {
                            // Perform a lightweight RMI call to check if the node is responsive
                            node.getAddress(); // If this call fails, the node is unresponsive
                        } catch (RemoteException e) {
                            // Mark the node as unresponsive and record its address
                            unresponsiveNodes.add(node);
                            try {
                                removedAddresses.add(node.getAddress()); // Attempt to retrieve address only once
                            } catch (RemoteException ignored) {
                                removedAddresses.add("Unknown address (RemoteException)");
                            }
                        }
                    }

                    // Remove unresponsive nodes from the network
                    for (IremoteP2P unresponsiveNode : unresponsiveNodes) {
                        network.remove(unresponsiveNode);
                    }

                    // Notify about removed nodes
                    for (String un_address : removedAddresses) {
                        listener.onDisconnect("Removed unresponsive node: " + un_address);
                        System.out.println("Removed unresponsive node: " + un_address);
                    }

                    // Sleep before the next health check cycle
                    Thread.sleep(5000); // Perform health checks every 5 seconds
                } catch (InterruptedException e) {
                    // Handle any exceptions during health check process

                }
            }
        }).start(); // Start the health check thread
    }

    // ::::::::::::::::::::::::: USER MANAGEMENT :::::::::::::::::::::::::
    /**
     * Authenticates a user based on their username and password. This method
     * attempts to load the user's keys using the provided password.
     *
     * @param username The username of the user to authenticate.
     * @param password The password to authenticate the user.
     * @return True if authentication is successful, false otherwise.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public boolean authenticate(String username, String password) throws RemoteException {
        try {
            // Attempt to load the user's keys with the provided password
            User user = new User(username);
            user.load(password);
            return true; // Successful authentication
        } catch (Exception e) {
            // Log error and return false if authentication fails
            System.err.println("Authentication failed for user " + username + ": " + e.getMessage());
            return false; // Authentication failed
        }
    }

    /**
     * Retrieves a User object for the given username.
     *
     * @param username The username of the user to retrieve.
     * @return The User object corresponding to the provided username.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public User getUser(String username) throws RemoteException {
        // Create and return the User object for the given username
        User user = new User(username);
        return user;
    }

    /**
     * Adds a new user to the system. A new user object is created, and keys are
     * generated and saved. After adding the user, it notifies all other nodes
     * to sync the UsersData folder.
     *
     * @param username The username of the user to add.
     * @param password The password of the user to add.
     * @return True if the user is successfully added, false otherwise.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public boolean addUser(String username, String password) throws RemoteException {
        try {
            // Create a new user, generate keys, and save the user's data
            User newUser = new User(username);
            newUser.generateKeys();
            newUser.save(password);

            // After adding the user, notify all other nodes to sync UsersData from this node
            notifyNodesToSyncUsersData();

            return true; // User added successfully
        } catch (Exception ex) {
            // Log error and return false if user addition fails
            System.err.println("Error adding user: " + ex.getMessage());
            return false; // Error adding user
        }
    }

    /**
     * Checks whether a user already exists in the system by attempting to load
     * their data.
     *
     * @param username The username to check.
     * @return True if the user exists, false otherwise.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public boolean checkUsrExists(String username) throws RemoteException {
        // Define the path to check if the user exists
        String path = "UsersData/" + username;

        // Check if the user directory exists
        return Files.exists(Path.of(path)); // Return true if user exists, false otherwise
    }

    /**
     * Lists all the users currently in the system. The method scans the
     * "UsersData" folder, reads the public key for each user, and returns a
     * list of User objects.
     *
     * @return A list of all users in the system.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public List<User> listUsers() throws RemoteException {
        // Define the path to the UsersData folder
        String USER_DATA_PATH = "UsersData";

        List<User> users = new ArrayList<>();
        Path rootPath = Paths.get(USER_DATA_PATH);

        try (var paths = Files.walk(rootPath, 1)) {
            // Collect directories in the UsersData folder, excluding the root path
            List<Path> userDirectories = paths
                    .filter(path -> Files.isDirectory(path) && !path.equals(rootPath))
                    .collect(Collectors.toList());

            // Iterate through each directory and create a User object for each
            for (Path userDir : userDirectories) {
                String username = userDir.getFileName().toString();
                Path publicKeyPath = userDir.resolve("public.key");

                if (Files.exists(publicKeyPath)) {
                    byte[] publicKeyBytes = null;
                    try {
                        publicKeyBytes = Files.readAllBytes(publicKeyPath);
                    } catch (IOException ex) {
                        // Log error if reading the public key fails
                        Logger.getLogger(OremoteP2P.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    PublicKey pub = SecurityUtils.getPublicKey(publicKeyBytes);

                    // Add the user with their public key to the list
                    users.add(new User(username, pub));
                }
            }
        } catch (IOException ex) {
            // Handle IOExceptions during file operations
            Logger.getLogger(OremoteP2P.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            // Handle other exceptions
            Logger.getLogger(OremoteP2P.class.getName()).log(Level.SEVERE, null, ex);
        }

        return users; // Return the list of users
    }

    /**
     * Synchronizes the UsersData folder from the current node to a remote node.
     * The entire UsersData folder is copied to the remote node.
     *
     * @param node The remote node to sync the UsersData folder to.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public void syncUserDataFolder(IremoteP2P node) throws RemoteException {
        // Local path for the UsersData folder
        String localFolderPath = "UsersData";

        // Ensure the 'UsersData' folder exists on the remote node
        try {
            System.out.println("Syncing the entire UsersData folder from this node to the new node...");

            // Use the remote node's functionality to transfer files
            copyFolderToRemote(localFolderPath, node);  // Copy the entire UsersData folder to the new node

            System.out.println("Successfully synced the UsersData folder to the new node.");
        } catch (IOException e) {
            // Log error if syncing the folder fails
            System.err.println("Error syncing UsersData folder: " + e.getMessage());
        }
    }

    /**
     * Copies the entire folder from the local node to a remote node. It handles
     * both directories and files. If a directory is encountered, it is created
     * on the remote node. If a file is encountered, it is sent to the remote
     * node.
     *
     * @param sourceDirPath The local directory path to copy from.
     * @param node The remote node to copy the data to.
     * @throws IOException If an I/O error occurs while reading or writing
     * files.
     * @throws RemoteException If a remote communication error occurs.
     */
    private void copyFolderToRemote(String sourceDirPath, IremoteP2P node) throws IOException, RemoteException {
        Path sourcePath = Paths.get(sourceDirPath);
        String targetFolderPath = "UsersData";  // Remote path on the new node

        // Check if the source directory exists
        if (!Files.exists(sourcePath)) {
            System.err.println("Source directory does not exist: " + sourceDirPath);
            return;
        }

        // Retrieve the remote node's file handling capabilities via RMI
        try (var paths = Files.walk(sourcePath)) {
            paths.forEach(path -> {
                try {
                    Path relativePath = sourcePath.relativize(path);
                    String remotePath = targetFolderPath + "/" + relativePath.toString();

                    // If it's a directory, create it on the remote node
                    if (Files.isDirectory(path)) {
                        node.createRemoteDirectory(remotePath);  // Create the directory remotely
                        System.out.println("Created directory on remote: " + remotePath);
                    } else {
                        // If it's a file, send it to the remote node
                        byte[] fileBytes = Files.readAllBytes(path);  // Read the file's bytes
                        node.receiveFile(remotePath, fileBytes);  // Send the file to the remote node
                        System.out.println("Copied file to remote: " + remotePath);
                    }
                } catch (RemoteException e) {
                    System.err.println("Error copying " + path + " to remote node: " + e.getMessage());
                } catch (IOException ex) {
                    Logger.getLogger(OremoteP2P.class.getName()).log(Level.SEVERE, null, ex);
                }
            });
        } catch (IOException e) {
            System.err.println("Error walking through source directory: " + e.getMessage());
        }
    }

    /**
     * Creates a directory on the remote node at the specified path. If the
     * directory already exists, it is not created again.
     *
     * @param remotePath The path of the directory to create on the remote node.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public void createRemoteDirectory(String remotePath) throws RemoteException {
        try {
            Path targetDir = Paths.get(remotePath);

            // Check if the directory already exists; if not, create it
            if (!Files.exists(targetDir)) {
                Files.createDirectories(targetDir);
                System.out.println("Created directory on remote node: " + remotePath);
            }
        } catch (IOException e) {
            // Log the error and throw a RemoteException if directory creation fails
            System.err.println("Error creating directory on remote node: " + e.getMessage());
            throw new RemoteException("Failed to create directory: " + remotePath, e);
        }
    }

    /**
     * Receives a file from a remote node and stores it locally. The method
     * ensures that the necessary parent directories are created before writing
     * the file content.
     *
     * @param remotePath The path where the file should be stored on the remote
     * node.
     * @param fileData The content of the file to store on the remote node.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public void receiveFile(String remotePath, byte[] fileData) throws RemoteException {
        try {
            Path targetFile = Paths.get(remotePath);

            // Ensure parent directories exist before writing the file
            Files.createDirectories(targetFile.getParent());

            // Write the file content to the target location
            Files.write(targetFile, fileData);
            System.out.println("Received and stored file on remote node: " + remotePath);
        } catch (IOException e) {
            // Log the error and throw a RemoteException if file receiving fails
            System.err.println("Error receiving file on remote node: " + e.getMessage());
            throw new RemoteException("Failed to receive file: " + remotePath, e);
        }
    }

    /**
     * Notifies all nodes in the network to synchronize their UsersData folder
     * from this node. This method is called after a new user is added or when a
     * node joins the network.
     *
     * @throws RemoteException If a remote communication error occurs.
     */
    private void notifyNodesToSyncUsersData() throws RemoteException {
        for (IremoteP2P peer : network) {
            // Only notify peers that are not the current node itself
            if (!peer.getAddress().equals(this.address)) {
                peer.syncUserDataFromHost(this);  // Notify other nodes to sync from this node (the host)
            }
        }
    }

    /**
     * Synchronizes the UsersData folder from the host node (this node). This
     * method will call the syncUserDataFolder method on the host node to copy
     * the UsersData folder.
     *
     * @param hostNode The host node that has the up-to-date UsersData folder.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public void syncUserDataFromHost(IremoteP2P hostNode) throws RemoteException {
        // Sync the UsersData folder from the host node (Node 1)
        System.out.println("Syncing UsersData from host node: " + hostNode.getAddress());
        hostNode.syncUserDataFolder(this);  // Call the sync function on the host node to get the data
    }

    // ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // ::::::::::           T R A N S A C T I O N S           :::::::::::::
    // ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /**
     * Returns the current size of the transaction buffer.
     *
     * @return The number of transactions in the buffer.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public int getTransactionsSize() throws RemoteException {
        return transactionBuffer.size();
    }

    /**
     * Adds a new transaction to the local transaction buffer and propagates it
     * to other nodes in the network.
     *
     * @param targetUserPubKey The public key of the user the transaction is
     * intended for.
     * @param entry The transaction entry to add.
     * @param signature The signature verifying the transaction.
     * @return true if the transaction was successfully added, false if it is a
     * duplicate.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public boolean addTransaction(PublicKey targetUserPubKey, Entry entry, byte[] signature) throws RemoteException {
        try {
            // Verify the signature
            if (!SecurityUtils.verifySign(entry.toString().getBytes(), signature, entry.getEntityPublicKey())) {
                throw new RemoteException("Invalid signature");
            }
        } catch (Exception ex) {
            Logger.getLogger(OremoteP2P.class.getName()).log(Level.SEVERE, null, ex);
            throw new RemoteException("Error verifying signature", ex);
        }

        synchronized (transactionBuffer) {
            // Check for duplicate transactions
            for (Entry trans : transactionBuffer) {
                if (entry.getDescription().equals(trans.getDescription())) {
                    listener.onTransaction("Duplicate transaction: " + entry.getDescription());
                    return false; // Do not propagate duplicate transaction
                }
            }

            // Add the transaction to the local node
            transactionBuffer.add(entry);
        }

        // Propagate the transaction to the network nodes
        for (IremoteP2P iremoteP2P : network) {
            try {
                if (!iremoteP2P.getAddress().equals(this.address)) { // Avoid sending back to the originating node
                    iremoteP2P.addTransaction(targetUserPubKey, entry, signature);
                }
            } catch (RemoteException ex) {
                Logger.getLogger(OremoteP2P.class.getName()).log(Level.SEVERE, "Error propagating transaction", ex);
            }
        }

        System.out.println("Transaction successfully added: " + entry.getDescription());

        return true;
    }

    /**
     * Returns a copy of the transaction buffer, ensuring thread-safety with
     * CopyOnWriteArraySet.
     *
     * @return A thread-safe set of transactions.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public CopyOnWriteArraySet<Entry> getTransactions() throws RemoteException {
        return new CopyOnWriteArraySet<Entry>(transactionBuffer);
    }

    /**
     * Synchronizes the transactions with the provided node. If the remote node
     * has new transactions, it synchronizes with the current node and
     * propagates the changes to the rest of the network.
     *
     * @param node The remote node to synchronize with.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public void synchronizeTransactions(IremoteP2P node) throws RemoteException {
        // Store the size before synchronization
        int oldsize = transactionBuffer.size();
        listener.onMessage("Synchronizing transactions with node", node.getAddress());

        // Add all the transactions from the node (duplicates will be removed by the Set)
        this.transactionBuffer.addAll(node.getTransactions());
        int newSize = transactionBuffer.size();

        // If the size has increased, synchronize further
        if (oldsize < newSize) {
            listener.onMessage("sinchronizeTransactions", "different size");
            // Ask the node to synchronize with the current node's transactions
            node.synchronizeTransactions(this);
            listener.onTransaction(address);
            listener.onMessage("sinchronizeTransactions", "");

            // Request synchronization from other nodes in the network
            for (IremoteP2P iremoteP2P : network) {
                // If the size on the remote node is smaller, sync with the current node
                if (iremoteP2P.getTransactionsSize() < newSize) {
                    listener.onMessage("sinchronizeTransactions", "");
                    iremoteP2P.synchronizeTransactions(this);
                }
            }
        }
    }

    /**
     * Removes the specified transactions from the local transaction buffer and
     * propagates the removal to other nodes.
     *
     * @param myTransactions The transactions to remove from the local buffer.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public void removeTransactions(CopyOnWriteArraySet<Entry> myTransactions) throws RemoteException {
        // Log the state of the transaction buffer before removal
        System.out.println("Before removal: " + transactionBuffer);
        System.out.println("Transactions to remove: " + myTransactions);

        // Remove the transactions from the local buffer
        boolean removed = transactionBuffer.removeAll(myTransactions);
        listener.onTransaction("Attempting to remove " + myTransactions.size() + " transactions");

        if (removed) {
            System.out.println("Transactions removed from buffer: " + myTransactions);
        } else {
            System.out.println("No transactions were removed from buffer.");
        }

        // Propagate the removal to other nodes in the network
        for (IremoteP2P iremoteP2P : network) {
            // If the remote node shares common transactions, remove them
            if (iremoteP2P.getTransactions().retainAll(transactionBuffer)) {
                System.out.println("Removing transactions from remote node...");
                iremoteP2P.removeTransactions(myTransactions);
            }
        }

        // Log the state of the transaction buffer after removal
        System.out.println("After removal: " + transactionBuffer);
    }

    /**
     * Retrieves the list of entries associated with a specific entity,
     * identified by its public key.
     *
     * @param entityPublicKey The public key of the entity whose entries to
     * retrieve.
     * @return A list of entries for the specified entity.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public List<Entry> getEntriesForEntity(PublicKey entityPublicKey) throws RemoteException {
        List<Entry> entityEntries = new ArrayList<>();
        for (List<Entry> entries : userEntries.values()) {
            for (Entry entry : entries) {
                if (entry.getEntityPublicKey().equals(entityPublicKey)) {
                    entityEntries.add(entry);
                }
            }
        }
        return entityEntries;
    }

    /**
     * Retrieves all transactions from the blockchain, combining them from each
     * block in the chain.
     *
     * @return A list of all transactions in the blockchain.
     * @throws RemoteException If a remote communication error occurs.
     */
    public List<Entry> getAllTransactionsFromBC() throws RemoteException {
        List<Entry> allTransactions = new ArrayList<>();

        // Iterate over each block in the blockchain and add the transactions to the list
        for (Block block : myBlockchain.getChain()) {
            allTransactions.addAll(block.getBuffer());
        }

        return allTransactions;
    }

    // ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // ::::::::::::::::::      M I N E R    :::::::::::::::::::::::::::::::::::::
    // ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // //////////////////////////////////////////////////////////////////////////
    /**
     * Starts mining the given message by initiating the mining process locally
     * and propagating the mining request to other nodes in the network.
     *
     * @param msg The message to mine.
     * @param zeros The number of leading zeros required for the mining process.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public void startMining(String msg, int zeros) throws RemoteException {
        try {
            // Start mining locally with the provided message and difficulty (zeros)
            myMiner.startMining(msg, zeros);
            listener.onStartMining(msg, zeros);

            // Propagate the mining request to other nodes in the network
            for (IremoteP2P iremoteP2P : network) {
                // If the remote node is not already mining, start mining there as well
                if (!iremoteP2P.isMining()) {
                    listener.onStartMining(iremoteP2P.getAddress() + " mining", zeros);
                    iremoteP2P.startMining(msg, zeros);
                }
            }
        } catch (Exception ex) {
            // Log and propagate any exceptions encountered during mining
            listener.onException(ex, "startMining");
        }
    }

    /**
     * Stops mining and distributes the calculated nonce to other nodes in the
     * network.
     *
     * @param nonce The nonce to distribute to other nodes when stopping mining.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public void stopMining(int nonce) throws RemoteException {
        // Stop the local mining process and distribute the nonce
        myMiner.stopMining(nonce);

        // Propagate the stop mining request to other nodes
        for (IremoteP2P iremoteP2P : network) {
            // If the remote node is mining, stop the mining process there
            if (iremoteP2P.isMining()) {
                iremoteP2P.stopMining(nonce);
            }
        }
    }

    /**
     * Mines the given message and returns the nonce once it is calculated. This
     * method starts mining, waits for the nonce to be calculated, and then
     * returns the nonce.
     *
     * @param msg The message to mine.
     * @param zeros The number of leading zeros required for the mining process.
     * @return The calculated nonce, or -1 if an interruption occurs during
     * mining.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public int mine(String msg, int zeros) throws RemoteException {
        try {
            // Start mining the message with the specified difficulty (zeros)
            startMining(msg, zeros);

            // Wait until the nonce is calculated and return it
            return myMiner.waitToNonce();
        } catch (InterruptedException ex) {
            // Handle interruptions during mining
            listener.onException(ex, "Mine");
            return -1; // Return -1 in case of interruption
        }
    }

    /**
     * Checks if the current node is actively mining.
     *
     * @return true if the node is currently mining, false otherwise.
     * @throws RemoteException If a remote communication error occurs.
     */
    @Override
    public boolean isMining() throws RemoteException {
        return myMiner.isMining();
    }

    // ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // ::::::::::::::::::      B L O C K C H A I N    :::::::::::::::::::::::::::
    // ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // //////////////////////////////////////////////////////////////////////////
    /**
     * Adds a block to the local blockchain after validating it and checking if
     * it fits with the current blockchain. If the block is valid and fits, it
     * is added to the blockchain, saved to a file, and propagated to other
     * peers.
     *
     * @param b The block to be added.
     * @throws RemoteException If a remote communication error occurs during the
     * process.
     */
    @Override
    public void addBlock(Block b) throws RemoteException {
        try {
            // Validate the block's integrity and structure
            if (!b.isValid()) {
                throw new RemoteException("Invalid block");
            }

            // Check if the block's previous hash matches the last block in the current blockchain
            if (myBlockchain.getLastBlockHash().equals(b.getPreviousHash())) {
                // Add the valid block to the local blockchain
                myBlockchain.add(b);

                // Save the updated blockchain to the file
                myBlockchain.save(BLOCHAIN_FILENAME);
                listener.onBlockchainUpdate(myBlockchain);

                // Propagate the block to the network to update other peers' blockchains
                propagateBlock(b);
            } else {
                // If the block doesn't fit, synchronize the blockchain with the network
                System.out.println("Block does not fit, synchronizing...");
                synchronizeBlockchain();
            }

        } catch (Exception ex) {
            // Log any exceptions encountered while adding the block
            listener.onException(ex, "Add block " + b);
        }
    }

    /**
     * Propagates the block to all peers in the network to ensure consistency
     * across nodes. Each peer receives and processes the block.
     *
     * @param b The block to be propagated.
     * @throws RemoteException If a remote communication error occurs while
     * propagating the block.
     */
    private void propagateBlock(Block b) throws RemoteException {
        // Propagate the block to all peers in the network
        for (IremoteP2P peer : network) {
            try {
                // Ensure that the block is only added once per peer
                peer.addBlock(b);
            } catch (RemoteException e) {
                // Log any errors while propagating the block to a peer
                System.err.println("Error propagating block to " + peer.getAddress());
            }
        }
    }

    /**
     * Returns the size of the local blockchain.
     *
     * @return The size of the local blockchain.
     * @throws RemoteException If a remote communication error occurs while
     * retrieving the size.
     */
    @Override
    public int getBlockchainSize() throws RemoteException {
        return myBlockchain.getSize();
    }

    /**
     * Returns the hash of the last block in the blockchain.
     *
     * @return The hash of the last block in the blockchain.
     * @throws RemoteException If a remote communication error occurs while
     * retrieving the hash.
     */
    @Override
    public String getBlockchainLastHash() throws RemoteException {
        return myBlockchain.getLastBlockHash();
    }

    /**
     * Returns the entire blockchain object.
     *
     * @return The blockchain object.
     * @throws RemoteException If a remote communication error occurs while
     * retrieving the blockchain.
     */
    @Override
    public BlockChain getBlockchain() throws RemoteException {
        return myBlockchain;
    }

    /**
     * Synchronizes the local blockchain with the largest valid blockchain from
     * the network. If a peer has a longer valid blockchain, it is adopted by
     * the local node.
     *
     * @throws RemoteException If a remote communication error occurs during the
     * synchronization process.
     */
    @Override
    public void synchronizeBlockchain() throws RemoteException {
        // Check all nodes in the network to compare blockchain sizes
        for (IremoteP2P iremoteP2P : network) {
            // If a peer has a larger blockchain, try to synchronize
            if (iremoteP2P.getBlockchainSize() > myBlockchain.getSize()) {
                BlockChain remote = iremoteP2P.getBlockchain();

                // Only synchronize if the peer's blockchain is valid
                if (remote.isValid()) {
                    // Update the local blockchain with the peer's blockchain
                    myBlockchain = remote;

                    // The blockchain should be updated with only the missing blocks
                    listener.onBlockchainUpdate(myBlockchain);
                }
            }
        }
    }

}
