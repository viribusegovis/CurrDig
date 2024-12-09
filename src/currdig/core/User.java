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

public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private static String UserPath = "UsersData/";

    private String name;

    private PublicKey pub;
    private PrivateKey priv;
    private Key sim;

    public User(String name) {
        this.name = name;
        this.pub = null;
        this.priv = null;
        this.sim = null;
    }

    public User(String username, PublicKey publicKey) {
        this.name = username;
        this.pub = publicKey;
    }

    public void generateKeys() throws Exception {
        this.sim = SecurityUtils.generateAESKey(256);
        KeyPair kp = SecurityUtils.generateECKeyPair(256);
        this.pub = kp.getPublic();
        this.priv = kp.getPrivate();
    }

    public void save(String password) throws Exception {
        // Check if the user directory already exists
        String path = UserPath + "/" + name;
        if (Files.exists(Path.of(path))) {
            throw new Exception("User with this username already exists.");
        }

        Files.createDirectories(Path.of(path));

        // Save the public key as the unique identifier (optional, you may store it elsewhere)
        String pub64 = Base64.getEncoder().encodeToString(pub.getEncoded());
        Files.write(Path.of(path + "/username.txt"), name.getBytes()); // Save username

        // Encrypt and save the private key
        byte[] secret = SecurityUtils.encrypt(priv.getEncoded(), password);
        Files.write(Path.of(path + "/private.key"), secret);

        // Encrypt and save the symmetric key
        byte[] simData = SecurityUtils.encrypt(sim.getEncoded(), password);
        Files.write(Path.of(path + "/symmetric.key"), simData);

        // Save the public key
        Files.write(Path.of(path + "/public.key"), pub.getEncoded());
    }

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
            this.priv = SecurityUtils.getPrivateKey(privData);  // Convert to PrivateKey

            // Load and decrypt the symmetric key
            byte[] simData = Files.readAllBytes(Path.of(path + "/symmetric.key"));
            simData = SecurityUtils.decrypt(simData, password);
            this.sim = SecurityUtils.getAESKey(simData);  // Convert to SecretKey

            // Load the public key (no decryption needed)
            byte[] pubData = Files.readAllBytes(Path.of(path + "/public.key"));
            this.pub = SecurityUtils.getPublicKey(pubData);  // Convert to PublicKey
        } catch (Exception ex) {
            // Handle decryption failure, likely due to wrong password
            throw new Exception("Invalid password. Could not decrypt keys.");
        }
    }

    // Serialization-friendly getters
    public String getName() {
        return name;
    }

    // Utility methods for converting Base64 strings to Key objects
    public PublicKey getPub() throws Exception {
        return pub;
    }

    public PrivateKey getPriv() throws Exception {
        return priv;
    }

    public Key getSim() throws Exception {
        return sim;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setPub(PublicKey pub) {
        this.pub = pub;
    }

    public void setPriv(PrivateKey priv) {
        this.priv = priv;
    }

    public void setSim(Key sim) {
        this.sim = sim;
    }
}
