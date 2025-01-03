package p2p;

import blockchain.utils.Block;
import blockchain.utils.BlockChain;
import currdig.core.Entry;
import currdig.core.User;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Remote interface for a Peer-to-Peer (P2P) network with functionalities for
 * transaction management, user authentication, mining, and blockchain
 * synchronization.
 */
public interface IremoteP2P extends Remote {

    // ::::::::: N E T W O R K :::::::::::
    /**
     * Returns the address of this node in the P2P network.
     *
     * @return A String representing the address of the node.
     * @throws RemoteException If a remote communication error occurs.
     */
    public String getAddress() throws RemoteException;

    /**
     * Adds a new node to the network.
     *
     * @param node The node to add to the network.
     * @throws RemoteException If a remote communication error occurs.
     */
    public void addNode(IremoteP2P node) throws RemoteException;

    /**
     * Retrieves the list of nodes currently in the network.
     *
     * @return A list of all nodes in the network.
     * @throws RemoteException If a remote communication error occurs.
     */
    public List<IremoteP2P> getNetwork() throws RemoteException;

    // ::::::::: T R A N S A C T I O N S :::::::::::
    /**
     * Returns the size of the transaction pool.
     *
     * @return The number of transactions in the pool.
     * @throws RemoteException If a remote communication error occurs.
     */
    public int getTransactionsSize() throws RemoteException;

    /**
     * Adds a new transaction to the network.
     *
     * @param targetUserPubKey The public key of the target user for the
     * transaction.
     * @param entry The entry that is part of the transaction.
     * @param signature The signature for the transaction.
     * @return True if the transaction was successfully added, false otherwise.
     * @throws RemoteException If a remote communication error occurs.
     */
    public boolean addTransaction(PublicKey targetUserPubKey, Entry entry, byte[] signature) throws RemoteException;

    /**
     * Retrieves the set of transactions from this node.
     *
     * @return A thread-safe set of entries representing the transactions.
     * @throws RemoteException If a remote communication error occurs.
     */
    public CopyOnWriteArraySet<Entry> getTransactions() throws RemoteException;

    /**
     * Removes the specified transactions from this node.
     *
     * @param myTransactions The transactions to remove.
     * @throws RemoteException If a remote communication error occurs.
     */
    public void removeTransactions(CopyOnWriteArraySet<Entry> myTransactions) throws RemoteException;

    /**
     * Synchronizes transactions with another node.
     *
     * @param node The node to synchronize with.
     * @throws RemoteException If a remote communication error occurs.
     */
    public void synchronizeTransactions(IremoteP2P node) throws RemoteException;

    /**
     * Retrieves all entries for a given entity (e.g., a user).
     *
     * @param entityPublicKey The public key of the entity to retrieve entries
     * for.
     * @return A list of entries related to the specified entity.
     * @throws RemoteException If a remote communication error occurs.
     */
    public List<Entry> getEntriesForEntity(PublicKey entityPublicKey) throws RemoteException;

    /**
     * Retrieves all transactions from the blockchain.
     *
     * @return A list of all transactions in the blockchain.
     * @throws RemoteException If a remote communication error occurs.
     */
    public List<Entry> getAllTransactionsFromBC() throws RemoteException;

    // ::::::::: U S E R S :::::::::::
    /**
     * Authenticates a user on the local node only based on their username and
     * password.
     *
     * @param username The username of the user to authenticate.
     * @param password The password to authenticate the user.
     * @return True if the credentials are valid on this node, false otherwise.
     * @throws java.rmi.RemoteException
     */
    public boolean authenticateLocal(String username, String password) throws RemoteException;

    /**
     * Authenticates a user using consensus across the network.
     *
     * @param username The username of the user to authenticate.
     * @param password The password to authenticate the user.
     * @return True if the consensus threshold is met, false otherwise.
     * @throws RemoteException If a remote communication error occurs.
     */
    public boolean authenticateWithConsensus(String username, String password) throws RemoteException;

    /**
     * Retrieves a user object for the given username.
     *
     * @param username The username of the user.
     * @return The User object corresponding to the username.
     * @throws RemoteException If a remote communication error occurs.
     */
    User getUser(String username) throws RemoteException;

    /**
     * Adds a new user with the given username and password.
     *
     * @param username The username for the new user.
     * @param password The password for the new user.
     * @return True if the user was successfully added, false otherwise.
     * @throws RemoteException If a remote communication error occurs.
     */
    boolean addUser(String username, String password) throws RemoteException;

    /**
     * Checks if a user already exists in the system.
     *
     * @param username The username to check.
     * @return True if the user exists, false otherwise.
     * @throws RemoteException If a remote communication error occurs.
     */
    public boolean checkUsrExists(String username) throws RemoteException;

    /**
     * Lists all users in the system.
     *
     * @return A list of all users.
     * @throws RemoteException If a remote communication error occurs.
     */
    public List<User> listUsers() throws RemoteException;

    /**
     * Synchronizes the user data folder with another node.
     *
     * @param node The node to synchronize with.
     * @throws RemoteException If a remote communication error occurs.
     */
    public void syncUserDataFolder(IremoteP2P node) throws RemoteException;

    /**
     * Creates a remote directory on the node.
     *
     * @param remotePath The path of the directory to create.
     * @throws RemoteException If a remote communication error occurs.
     */
    void createRemoteDirectory(String remotePath) throws RemoteException;

    /**
     * Receives a file from a remote node.
     *
     * @param remotePath The path where the file will be stored.
     * @param fileData The byte array containing the file data.
     * @throws RemoteException If a remote communication error occurs.
     */
    void receiveFile(String remotePath, byte[] fileData) throws RemoteException;

    /**
     * Synchronizes user data from a host node.
     *
     * @param hostNode The host node to synchronize data from.
     * @throws RemoteException If a remote communication error occurs.
     */
    public void syncUserDataFromHost(IremoteP2P hostNode) throws RemoteException;

    // ::::::::: M I N E R :::::::::::
    /**
     * Starts mining with the given message and number of leading zeros
     * required.
     *
     * @param msg The message to mine.
     * @param zeros The number of leading zeros in the hash.
     * @throws RemoteException If a remote communication error occurs.
     */
    public void startMining(String msg, int zeros) throws RemoteException;

    /**
     * Stops the mining process with the given nonce.
     *
     * @param nonce The nonce that was used during mining.
     * @throws RemoteException If a remote communication error occurs.
     */
    public void stopMining(int nonce) throws RemoteException;

    /**
     * Checks if mining is currently active.
     *
     * @return True if mining is active, false otherwise.
     * @throws RemoteException If a remote communication error occurs.
     */
    public boolean isMining() throws RemoteException;

    /**
     * Mines a block with the given message and number of leading zeros.
     *
     * @param msg The message to mine.
     * @param zeros The number of leading zeros in the hash.
     * @return The nonce used for mining.
     * @throws RemoteException If a remote communication error occurs.
     */
    public int mine(String msg, int zeros) throws RemoteException;

    // ::::::::: B L O C K C H A I N :::::::::::
    /**
     * Adds a new block to the blockchain.
     *
     * @param b The block to add.
     * @throws RemoteException If a remote communication error occurs.
     */
    public void addBlock(Block b) throws RemoteException;

    /**
     * Retrieves the size of the blockchain.
     *
     * @return The size of the blockchain.
     * @throws RemoteException If a remote communication error occurs.
     */
    public int getBlockchainSize() throws RemoteException;

    /**
     * Retrieves the last hash of the blockchain.
     *
     * @return The hash of the last block.
     * @throws RemoteException If a remote communication error occurs.
     */
    public String getBlockchainLastHash() throws RemoteException;

    /**
     * Retrieves the entire blockchain.
     *
     * @return The blockchain object.
     * @throws RemoteException If a remote communication error occurs.
     */
    public BlockChain getBlockchain() throws RemoteException;

    /**
     * Synchronizes the blockchain with another node.
     *
     * @throws RemoteException If a remote communication error occurs.
     */
    public void synchronizeBlockchain() throws RemoteException;
}
