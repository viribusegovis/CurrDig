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

public class OremoteP2P extends UnicastRemoteObject implements IremoteP2P {

    private final String address;
    private final CopyOnWriteArrayList<IremoteP2P> network;
    private CopyOnWriteArraySet<Entry> transactionBuffer;
    private final P2Plistener listener;

    private static final String BLOCHAIN_FILENAME = "currdig.obj";

    //objeto mineiro concorrente e distribuido
    Miner myMiner;
    //objeto da blockchain preparada para cesso concorrente
    BlockChain myBlockchain;

    public OremoteP2P(String address, P2Plistener listener) throws RemoteException {
        super(RMI.getAdressPort(address));
        this.address = address;
        this.network = new CopyOnWriteArrayList<>();
        this.transactionBuffer = new CopyOnWriteArraySet<>();
        this.listener = listener;
        myMiner = new Miner(listener);
        myBlockchain = new BlockChain();

        try {
            myBlockchain.load("currdig.obj");
        } catch (Exception e) {
        }

        listener.onStart("Object " + address + " listening");
        System.out.println("Object " + address + " listening");
    }

    @Override
    public String getAddress() throws RemoteException {
        return address;
    }

    private boolean isInNetwork(String address) {
        for (int i = network.size() - 1; i >= 0; i--) {
            try {
                if (network.get(i).getAddress().equals(address)) {
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
            if (isInNetwork(node.getAddress())) {
                System.out.println("Already have address: " + node.getAddress());
                return;
            }

            // Add the new node to the network
            network.add(node);
            listener.onConect(node.getAddress());
            System.out.println("Added node: " + node.getAddress());

            if (!node.getAddress().equals(this.address)) {
                node.addNode(this);
            }

            // Sync user data between the nodes
            syncUserDataFolder(node);  // Synchronize user data when a new node joins

            // Propagate the new node to the network
            for (IremoteP2P peer : network) {
                peer.addNode(node);
            }

            System.out.println("P2P Network:");
            for (IremoteP2P peer : network) {
                System.out.println(peer.getAddress());
            }

            synchronizeBlockchain();
        } catch (Exception ex) {
            Logger.getLogger(OremoteP2P.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<IremoteP2P> getNetwork() throws RemoteException {
        return new ArrayList<>(network);
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
            if (!peer.getAddress().equals(this.address)) {
                peer.syncUserDataFromHost(this);  // Notify other nodes to sync from this node (the host)
            }
        }
    }

    @Override
    public void syncUserDataFromHost(IremoteP2P hostNode) throws RemoteException {
        // Sync the UsersData folder from the host node (Node 1)
        System.out.println("Syncing UsersData from host node: " + hostNode.getAddress());
        hostNode.syncUserDataFolder(this);  // Call the sync function on the host node to get the data
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //::::::::            T R A N S A C T I O N S       ::::::::::::::::::
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    @Override
    public int getTransactionsSize() throws RemoteException {
        return transactionBuffer.size();
    }

    @Override
    public boolean addTransaction(PublicKey targetUserPubKey, Entry entry, byte[] signature) throws RemoteException {
        try {
            // Verificar a assinatura
            if (!SecurityUtils.verifySign(entry.toString().getBytes(), signature, entry.getEntityPublicKey())) {
                throw new RemoteException("Assinatura inválida");
            }
        } catch (Exception ex) {
            Logger.getLogger(OremoteP2P.class.getName()).log(Level.SEVERE, null, ex);
            throw new RemoteException("Erro ao verificar a assinatura", ex);
        }

        synchronized (transactionBuffer) {
            // Verificar se a transação já existe
            for (Entry trans : transactionBuffer) {
                if (entry.getDescription().equals(trans.getDescription())) {
                    listener.onTransaction("Transação repetida: " + entry.getDescription());
                    return false; // Não propagar transação duplicada
                }
            }

            // Adicionar a transação ao nó local
            transactionBuffer.add(entry);
        }

        // Adicionar a transação aos nós da rede (propagação)
        for (IremoteP2P iremoteP2P : network) {
            try {
                if (!iremoteP2P.getAddress().equals(this.address)) { // Evitar enviar de volta ao nó originador
                    iremoteP2P.addTransaction(targetUserPubKey, entry, signature);
                }
            } catch (RemoteException ex) {
                Logger.getLogger(OremoteP2P.class.getName()).log(Level.SEVERE, "Erro ao propagar transação", ex);
            }
        }

        System.out.println("Transação adicionada com sucesso: " + entry.getDescription());

        return true;
    }

    @Override
    public CopyOnWriteArraySet<Entry> getTransactions() throws RemoteException {
        return new CopyOnWriteArraySet<Entry>(transactionBuffer);
    }

    @Override
    public void synchronizeTransactions(IremoteP2P node) throws RemoteException {
        //tamanho anterior
        int oldsize = transactionBuffer.size();
        listener.onMessage("sinchronizeTransactions", node.getAddress());
        // juntar as transacoes todas (SET elimina as repetidas)
        this.transactionBuffer.addAll(node.getTransactions());
        int newSize = transactionBuffer.size();
        //se o tamanho for incrementado
        if (oldsize < newSize) {
            listener.onMessage("sinchronizeTransactions", "tamanho diferente");
            //pedir ao no para sincronizar com as nossas
            node.synchronizeTransactions(this);
            listener.onTransaction(address);
            listener.onMessage("sinchronizeTransactions", "node.sinchronizeTransactions(this)");
            //pedir á rede para se sincronizar
            for (IremoteP2P iremoteP2P : network) {
                //se o tamanho for menor
                if (iremoteP2P.getTransactionsSize() < newSize) {
                    //cincronizar-se com o no actual
                    listener.onMessage("sinchronizeTransactions", " iremoteP2P.sinchronizeTransactions(this)");
                    iremoteP2P.synchronizeTransactions(this);
                }
            }
        }

    }

    @Override
    public void removeTransactions(CopyOnWriteArraySet<Entry> myTransactions) throws RemoteException {
        // Log before removal to check contents
        System.out.println("Before removal: " + transactionBuffer);
        System.out.println("Transactions to remove: " + myTransactions);

        // Remove the transactions from the current list
        boolean removed = transactionBuffer.removeAll(myTransactions);
        listener.onTransaction("Attempting to remove " + myTransactions.size() + " transactions");

        if (removed) {
            System.out.println("Transactions removed from buffer: " + myTransactions);
        } else {
            System.out.println("No transactions were removed from buffer.");
        }

        // Propagate the removal to other nodes
        for (IremoteP2P iremoteP2P : network) {
            // If there are common transactions in remote node transactions
            if (iremoteP2P.getTransactions().retainAll(transactionBuffer)) {
                System.out.println("Removing transactions from remote node...");
                iremoteP2P.removeTransactions(myTransactions);
            }
        }

        // Log the state of transactionBuffer after removal
        System.out.println("After removal: " + transactionBuffer);
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::::::::::::::      M I N E R   :::::::::::::::::::::::::::::::::::::::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //////////////////////////////////////////////////////////////////////////////
    @Override
    public void startMining(String msg, int zeros) throws RemoteException {
        try {
            //colocar a mineiro a minar
            myMiner.startMining(msg, zeros);
            listener.onStartMining(msg, zeros);
            //mandar minar a rede
            for (IremoteP2P iremoteP2P : network) {
                //se o nodo nao estiver a minar
                if (!iremoteP2P.isMining()) {
                    listener.onStartMining(iremoteP2P.getAddress() + " mining", zeros);
                    //iniciar a mineracao no nodo
                    iremoteP2P.startMining(msg, zeros);
                }
            }
        } catch (Exception ex) {
            listener.onException(ex, "startMining");
        }

    }

    @Override
    public void stopMining(int nonce) throws RemoteException {
        //parar o mineiro e distribuir o nonce
        myMiner.stopMining(nonce);
        //mandar parar a rede
        for (IremoteP2P iremoteP2P : network) {
            //se o nodo estiver a minar   
            if (iremoteP2P.isMining()) {
                //parar a mineração no nodo 
                iremoteP2P.stopMining(nonce);
            }
        }
    }

    @Override
    public int mine(String msg, int zeros) throws RemoteException {
        try {
            //começar a minar a mensagem
            startMining(msg, zeros);
            //esperar que o nonce seja calculado
            return myMiner.waitToNonce();
        } catch (InterruptedException ex) {
            listener.onException(ex, "Mine");
            return -1;
        }

    }

    @Override
    public boolean isMining() throws RemoteException {
        return myMiner.isMining();
    }

    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //::::::::::::::::: B L O C K C H A I N :::::::::::::::::::::::::::::::::::::::
    //:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //////////////////////////////////////////////////////////////////////////////
    @Override
    public void addBlock(Block b) throws RemoteException {
        try {
            //se não for válido
            if (!b.isValid()) {
                throw new RemoteException("invalid block");
            }
            //se encaixar adicionar o bloco
            if (myBlockchain.getLastBlockHash().equals(b.getPreviousHash())) {
                myBlockchain.add(b);
                //guardar a blockchain
                myBlockchain.save(BLOCHAIN_FILENAME);
                listener.onBlockchainUpdate(myBlockchain);
            }
            //propagar o bloco pela rede
            for (IremoteP2P iremoteP2P : network) {
                //se encaixar na blockcahin dos nodos remotos
                if (!iremoteP2P.getBlockchainLastHash().equals(b.getPreviousHash())
                        || //ou o tamanho da remota for menor
                        iremoteP2P.getBlockchainSize() < myBlockchain.getSize()) {
                    //adicionar o bloco ao nodo remoto
                    iremoteP2P.addBlock(b);
                }
            }
            //se não encaixou)
            if (!myBlockchain.getLastBlockHash().equals(b.getCurrentHash())) {
                //sincronizar a blockchain
                synchronizeBlockchain();
            }
        } catch (Exception ex) {
            listener.onException(ex, "Add bloco " + b);
        }
    }

    @Override
    public int getBlockchainSize() throws RemoteException {
        return myBlockchain.getSize();
    }

    @Override
    public String getBlockchainLastHash() throws RemoteException {
        return myBlockchain.getLastBlockHash();
    }

    @Override
    public BlockChain getBlockchain() throws RemoteException {
        return myBlockchain;
    }

    @Override
    public void synchronizeBlockchain() throws RemoteException {
        //para todos os nodos da rede
        for (IremoteP2P iremoteP2P : network) {
            //se a blockchain for maior
            if (iremoteP2P.getBlockchainSize() > myBlockchain.getSize()) {
                BlockChain remote = iremoteP2P.getBlockchain();
                //e a blockchain for válida
                if (remote.isValid()) {
                    //atualizar toda a blockchain
                    myBlockchain = remote;
                    //deveria sincronizar apenas os blocos que faltam
                    listener.onBlockchainUpdate(myBlockchain);
                }
            }
        }
    }
}
