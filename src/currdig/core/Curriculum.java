/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package currdig.core;

import blockchain.utils.Block;
import blockchain.utils.BlockChain;
import blockchain.utils.ObjectUtils;
import blockchain.utils.SecurityUtils;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author bmsff
 */
public class Curriculum implements Serializable {

    private static final long serialVersionUID = 1L;

    private BlockChain bc;
    private Map<PublicKey, List<Entry>> userEntries;
    public static int DIFFICULTY = 6;

    public Curriculum() {
        bc = new BlockChain();
        userEntries = new HashMap<>();
    }

    public void addEntry(PublicKey targetUserPubKey, Entry entry, byte[] signature) throws Exception {
        // Verify the signature
        if (!SecurityUtils.verifySign(entry.toString().getBytes(), signature, entry.getEntityPublicKey())) {
            throw new Exception("Invalid signature");
        }

        String entryData = ObjectUtils.convertObjectToBase64(entry);
        String targetUserPubKeyString = Base64.getEncoder().encodeToString(targetUserPubKey.getEncoded());
        bc.add(targetUserPubKeyString + "|" + entryData, DIFFICULTY);

        userEntries.computeIfAbsent(targetUserPubKey, k -> new ArrayList<>()).add(entry);
    }

    public List<Entry> getUserEntries(PublicKey userPublicKey) {
        return userEntries.getOrDefault(userPublicKey, new ArrayList<>());
    }

    public String toString() {
        StringBuilder txt = new StringBuilder();
        for (Block b : bc.getChain()) {
            String[] parts = b.getData().split("\\|", 2);
            PublicKey userPublicKey = decodePublicKey(parts[0]);
            Entry entry = (Entry) ObjectUtils.convertBase64ToObject(parts[1]);
            txt.append(b.getPreviousHash()).append(" ")
                    .append(userPublicKey.toString()).append(": ")
                    .append(entry.toString()).append(" ")
                    .append(b.getNonce()).append(" ")
                    .append(b.getCurrentHash())
                    .append("\n");
        }
        return txt.toString();
    }

    public void save(String fileName) throws IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(this);
        }
    }

    public static Curriculum load(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            return (Curriculum) in.readObject();
        }
    }

    public List<PublicKey> getUsers() {
        return new ArrayList<>(userEntries.keySet());
    }

    public List<Entry> getEntriesForEntity(PublicKey entityPublicKey) {
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

    public boolean isValid() {
        return bc.isValid();
    }

    private boolean verifySignature(PublicKey publicKey, String data, byte[] signature) throws Exception {
        Signature sig = Signature.getInstance("SHA256withRSA");
        sig.initVerify(publicKey);
        sig.update(data.getBytes());
        return sig.verify(signature);
    }

    private PublicKey decodePublicKey(String encodedKey) {
        try {
            byte[] publicBytes = Base64.getDecoder().decode(encodedKey);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Error decoding public key", e);
        }
    }
}
