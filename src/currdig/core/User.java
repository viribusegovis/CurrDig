package currdig.core;

import blockchain.utils.SecurityUtils;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.Key;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Base64;

/**
 * This class represents a user in the system. It includes methods for
 * generating, saving, and loading user keys, along with utility methods for key
 * conversion and file handling.
 */
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    private static final String UserPath = "UsersData/";

    private String name;
    private PublicKey pub;
    private PrivateKey priv;
    private Key sim; // Symmetric key for encryption

    /**
     * Constructor to create a user with a name.
     *
     * @param name The name of the user.
     */
    public User(String name) {
        this.name = name;
        this.pub = null;
        this.priv = null;
        this.sim = null;
    }

    /**
     * Constructor to create a user with a name and a given public key.
     *
     * @param username The username of the user.
     * @param publicKey The public key of the user.
     */
    public User(String username, PublicKey publicKey) {
        this.name = username;
        this.pub = publicKey;
    }

    /**
     * Generates a key pair (public and private keys) and a symmetric AES key.
     *
     * @throws Exception If an error occurs while generating keys.
     */
    public void generateKeys() throws Exception {
        this.sim = SecurityUtils.generateAESKey(256);
        KeyPair kp = SecurityUtils.generateECKeyPair(256);
        this.pub = kp.getPublic();
        this.priv = kp.getPrivate();
    }

    /**
     * Saves the user's data (keys and username) to the file system.
     *
     * @param password The password used for encryption.
     * @throws Exception If an error occurs during saving.
     */
    public void save(String password) throws Exception {
        String path = UserPath + "/" + name;

        // Check if the user directory already exists
        if (Files.exists(Path.of(path))) {
            throw new Exception("User with this username already exists.");
        }

        Files.createDirectories(Path.of(path));

        // Save the username to a file
        Files.write(Path.of(path + "/username.txt"), name.getBytes());

        // Encode and save the public key (no encryption needed)
        String pub64 = Base64.getEncoder().encodeToString(pub.getEncoded());
        Files.write(Path.of(path + "/public.key"), pub.getEncoded());

        // Encrypt and save the private key
        byte[] secret = SecurityUtils.encrypt(priv.getEncoded(), password);
        Files.write(Path.of(path + "/private.key"), secret);

        // Encrypt and save the symmetric key
        byte[] simData = SecurityUtils.encrypt(sim.getEncoded(), password);
        Files.write(Path.of(path + "/symmetric.key"), simData);
    }

    /**
     * Loads the user's data (keys and username) from the file system.
     *
     * @param password The password used for decryption.
     * @throws Exception If an error occurs during loading.
     */
    public void load(String password) throws Exception {
        String path = UserPath + "/" + this.name;

        // Check if the user directory exists
        if (!Files.exists(Path.of(path))) {
            throw new Exception("User with this username does not exist.");
        }

        try {
            // Load and decrypt the private key
            byte[] privData = Files.readAllBytes(Path.of(path + "/private.key"));
            privData = SecurityUtils.decrypt(privData, password);
            this.priv = SecurityUtils.getPrivateKey(privData);

            // Load and decrypt the symmetric key
            byte[] simData = Files.readAllBytes(Path.of(path + "/symmetric.key"));
            simData = SecurityUtils.decrypt(simData, password);
            this.sim = SecurityUtils.getAESKey(simData);

            // Load the public key (no decryption needed)
            byte[] pubData = Files.readAllBytes(Path.of(path + "/public.key"));
            this.pub = SecurityUtils.getPublicKey(pubData);
        } catch (Exception ex) {
            // Handle decryption failure, likely due to wrong password
            throw new Exception("Invalid password. Could not decrypt keys.");
        }
    }

    // Serialization-friendly getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PublicKey getPub() {
        return pub;
    }

    public void setPub(PublicKey pub) {
        this.pub = pub;
    }

    public PrivateKey getPriv() {
        return priv;
    }

    public void setPriv(PrivateKey priv) {
        this.priv = priv;
    }

    public Key getSim() {
        return sim;
    }

    public void setSim(Key sim) {
        this.sim = sim;
    }
}
