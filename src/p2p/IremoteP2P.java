package p2p;

import currdig.core.User;
import java.nio.file.Path;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IremoteP2P extends Remote {

    //:::: N E T WO R K  :::::::::::
    public String getAdress() throws RemoteException;

    public void addNode(IremoteP2P node) throws RemoteException;

    public List<IremoteP2P> getNetwork() throws RemoteException;

    //::::::::::: T R A NS A C T IO N S  :::::::::::
    public void addTransaction(String data) throws RemoteException;

    public List<String> getTransactions() throws RemoteException;

    public void removeTransaction(String data) throws RemoteException;

    public void sinchronizeTransactions(IremoteP2P node) throws RemoteException;

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
}
