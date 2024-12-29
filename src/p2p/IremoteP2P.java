package p2p;

import blockchain.utils.Block;
import blockchain.utils.BlockChain;
import currdig.core.Entry;
import currdig.core.User;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

public interface IremoteP2P extends Remote {

    //:::: N E T WO R K  :::::::::::
    public String getAddress() throws RemoteException;

    public void addNode(IremoteP2P node) throws RemoteException;

    public List<IremoteP2P> getNetwork() throws RemoteException;

    //::::::::::: T R A N S A C T I O N S  :::::::::::
    public int getTransactionsSize() throws RemoteException;

    public boolean addTransaction(PublicKey targetUserPubKey, Entry entry, byte[] signature) throws RemoteException;

    public CopyOnWriteArraySet<Entry> getTransactions() throws RemoteException;

    public void removeTransactions(CopyOnWriteArraySet<Entry> myTransactions) throws RemoteException;

    public void synchronizeTransactions(IremoteP2P node) throws RemoteException;

    public List<Entry> getEntriesForEntity(PublicKey entityPublicKey) throws RemoteException;

    public List<Entry> getAllTransactionsFromBC() throws RemoteException;

    //::::::::::: TESTE USERS  :::::::::::
    // Add authentication-related methods
    boolean authenticate(String username, String password) throws RemoteException;

    User getUser(String username) throws RemoteException;

    boolean addUser(String username, String password) throws RemoteException;

    public boolean checkUsrExists(String username) throws RemoteException;

    public List<User> listUsers() throws RemoteException;

    public void syncUserDataFolder(IremoteP2P node) throws RemoteException;

    void createRemoteDirectory(String remotePath) throws RemoteException;

    void receiveFile(String remotePath, byte[] fileData) throws RemoteException;

    public void syncUserDataFromHost(IremoteP2P hostNode) throws RemoteException;

    //::::::::::::::::: M I N E R :::::::::::::::::::::::::::::::::::::::::::
    public void startMining(String msg, int zeros) throws RemoteException;

    public void stopMining(int nonce) throws RemoteException;

    public boolean isMining() throws RemoteException;

    public int mine(String msg, int zeros) throws RemoteException;

    //::::::::::::::::: B L O C K C H A I N :::::::::::::::::::::::::::::::::::::::::::
    public void addBlock(Block b) throws RemoteException;

    public int getBlockchainSize() throws RemoteException;

    public String getBlockchainLastHash() throws RemoteException;

    public BlockChain getBlockchain() throws RemoteException;

    public void synchronizeBlockchain() throws RemoteException;
}
