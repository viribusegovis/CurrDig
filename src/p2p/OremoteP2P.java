package p2p;

import blockchain.utils.SecurityUtils;
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
import java.nio.file.StandardCopyOption;
import java.security.Key;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class OremoteP2P extends UnicastRemoteObject implements IremoteP2P {

    private final String address;
    private final CopyOnWriteArrayList<IremoteP2P> network;
    private final CopyOnWriteArraySet<String> transactions;
    private final P2Plistener listener;

    public OremoteP2P(String address, P2Plistener listener) throws RemoteException {
        super(RMI.getAdressPort(address));
        this.address = address;
        this.network = new CopyOnWriteArrayList<>();
        this.transactions = new CopyOnWriteArraySet<>();
        this.listener = listener;

        listener.onStart("Object " + address + " listening");
        System.out.println("Object " + address + " listening");
    }

    @Override
    public String getAdress() throws RemoteException {
        return address;
    }

    private boolean isInNetwork(String address) {
        for (int i = network.size() - 1; i >= 0; i--) {
            try {
                if (network.get(i).getAdress().equals(address)) {
                    return true;
                }
            } catch (RemoteException ex) {
                network.remove(i);
            }
        }
        return false;
    }

    @Override
    public void addNode(IremoteP2P node) throws RemoteException {
        try {
            if (isInNetwork(node.getAdress())) {
                System.out.println("Already have address: " + node.getAdress());
                return;
            }

            // Add the new node to the network
            network.add(node);
            listener.onConect(node.getAdress());
            System.out.println("Added node: " + node.getAdress());
            node.addNode(this);

            // Sync user data between the nodes
            syncUserDataFolder(node);  // Synchronize user data when a new node joins

            // Propagate the new node to the network
            for (IremoteP2P peer : network) {
                peer.addNode(node);
            }

            System.out.println("P2P Network:");
            for (IremoteP2P peer : network) {
                System.out.println(peer.getAdress());
            }
        } catch (Exception ex) {
            Logger.getLogger(OremoteP2P.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<IremoteP2P> getNetwork() throws RemoteException {
        return new ArrayList<>(network);
    }

    @Override
    public void addTransaction(String data) throws RemoteException {
        if (transactions.contains(data)) {
            listener.onTransaction("Duplicate transaction: " + data);
            System.out.println("Duplicate transaction: " + data);
            return;
        }
        transactions.add(data);
        for (IremoteP2P peer : network) {
            peer.addTransaction(data);
        }
    }

    @Override
    public List<String> getTransactions() throws RemoteException {
        return new ArrayList<>(transactions);
    }

    @Override
    public void removeTransaction(String data) throws RemoteException {
        if (!transactions.contains(data)) {
            System.out.println("Transaction does not exist: " + data);
            return;
        }
        transactions.remove(data);
        for (IremoteP2P peer : network) {
            peer.removeTransaction(data);
        }
    }

    @Override
    public void sinchronizeTransactions(IremoteP2P node) throws RemoteException {
        this.transactions.addAll(node.getTransactions());
        listener.onTransaction(address);
    }

    // ::::::::::::::::::::::::: USER MANAGEMENT ::::::::::::::::::::::::::
    @Override
    public boolean authenticate(String username, String password) throws RemoteException {
        try {
            // Attempt to load the user's keys with the provided password
            User user = new User(username);
            user.load(password);
            return true; // Successful authentication
        } catch (Exception e) {
            System.err.println("Authentication failed for user " + username + ": " + e.getMessage());
            return false; // Authentication failed
        }
    }

    @Override
    public User getUser(String username) throws RemoteException {
        User user = new User(username);
        return user;
    }

    @Override
    public boolean addUser(String username, String password) throws RemoteException {
        try {
            // Create a new user and generate keys
            User newUser = new User(username);
            newUser.generateKeys();
            newUser.save(password);

            // After adding the user, notify all other nodes to sync UsersData from this node
            notifyNodesToSyncUsersData();

            return true;
        } catch (Exception ex) {
            System.err.println("Error adding user: " + ex.getMessage());
            return false;
        }
    }

    @Override
    public boolean checkUsrExists(String username) throws RemoteException {
        // Check if the user already exists by attempting to load it
        String path = "UsersData/" + username;

        if (Files.exists(Path.of(path))) {
            return true;
        }

        return false;
    }

    @Override
    public List<User> listUsers() throws RemoteException {
        String USER_DATA_PATH = "UsersData";

        List<User> users = new ArrayList<>();
        Path rootPath = Paths.get(USER_DATA_PATH);

        try (var paths = Files.walk(rootPath, 1)) {
            List<Path> userDirectories = paths
                    .filter(path -> Files.isDirectory(path) && !path.equals(rootPath))
                    .collect(Collectors.toList());

            for (Path userDir : userDirectories) {
                String username = userDir.getFileName().toString();
                Path publicKeyPath = userDir.resolve("public.key");

                if (Files.exists(publicKeyPath)) {
                    byte[] publicKeyBytes = null;
                    try {
                        publicKeyBytes = Files.readAllBytes(publicKeyPath);
                    } catch (IOException ex) {
                        Logger.getLogger(OremoteP2P.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    PublicKey pub = SecurityUtils.getPublicKey(publicKeyBytes);

                    users.add(new User(username, pub));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(OremoteP2P.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(OremoteP2P.class.getName()).log(Level.SEVERE, null, ex);
        }

        return users;
    }

    @Override
    public void syncUserDataFolder(IremoteP2P node) throws RemoteException {
        String localFolderPath = "UsersData";  // Local path (on the current node)

        // Ensure the 'UsersData' folder exists on the remote node
        try {
            System.out.println("Syncing the entire UsersData folder from this node to the new node...");

            // Now using the remote node's functionality to transfer files
            copyFolderToRemote(localFolderPath, node);  // Copy the entire UsersData folder to the new node

            System.out.println("Successfully synced the UsersData folder to the new node.");
        } catch (IOException e) {
            System.err.println("Error syncing UsersData folder: " + e.getMessage());
        }
    }

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

    // Method to create a directory on the remote node
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
            System.err.println("Error creating directory on remote node: " + e.getMessage());
            throw new RemoteException("Failed to create directory: " + remotePath, e);
        }
    }

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
            System.err.println("Error receiving file on remote node: " + e.getMessage());
            throw new RemoteException("Failed to receive file: " + remotePath, e);
        }
    }

    private void notifyNodesToSyncUsersData() throws RemoteException {
        for (IremoteP2P peer : network) {
            // Only notify peers that are not the current node itself
            if (!peer.getAdress().equals(this.address)) {
                peer.syncUserDataFromHost(this);  // Notify other nodes to sync from this node (the host)
            }
        }
    }

    @Override
    public void syncUserDataFromHost(IremoteP2P hostNode) throws RemoteException {
        // Sync the UsersData folder from the host node (Node 1)
        System.out.println("Syncing UsersData from host node: " + hostNode.getAdress());
        hostNode.syncUserDataFolder(this);  // Call the sync function on the host node to get the data
    }

}
