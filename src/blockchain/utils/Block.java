package blockchain.utils;

import currdig.core.Entry;
import java.io.Serializable;
import java.util.concurrent.CopyOnWriteArraySet;

public class Block implements Serializable {

    String previousHash;     // link to previous block
    int nonce;              // proof of work 
    String currentHash;     // Hash of block
    String merkleRoot;  // Merkle Tree

    public Block(String previousHash, CopyOnWriteArraySet<Entry> entries) {
        this.previousHash = previousHash;
        this.nonce = 0;
        this.currentHash = null;

        MerkleTree mkt = new MerkleTree(entries.toArray());
        this.merkleRoot = mkt.getRoot();
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public int getNonce() {
        return nonce;
    }

    public String calculateHash(String data) {
        if (merkleRoot == null) {
            return null;
        }
        //return Hash.getHash(nonce + previousHash + merkleTree.getRoot());

        return Hash.getHash(nonce + data);
    }

    public String getCurrentHash() {
        return currentHash;
    }

    public String toString() {
        if (merkleRoot == null) {
            return "Block not finalized - Transactions in buffer: ";
        }
        return String.format("[ %8s", previousHash) + " <- "
                + String.format("%-10s", merkleRoot)
                + String.format(" %7d ] = ", nonce)
                + String.format("%8s", currentHash);
    }

    public boolean isValid(String data) {
        if (merkleRoot == null || currentHash == null) {
            return false;
        }
        return currentHash.equals(calculateHash(data));
    }

    public String getMinerData() {
        return previousHash + merkleRoot;
    }

    public void setNonce(int nonce, int zeros) throws Exception {
        this.nonce = nonce;
        //calcular o hash
        this.currentHash = calculateHash();
        //calcular o prefixo
        String prefix = String.format("%0" + zeros + "d", 0);
        if (!currentHash.startsWith(prefix)) {
            throw new Exception(nonce + " not valid Hash=" + currentHash);
        }

    }

    public String calculateHash() {
        return Miner.getHash(getMinerData(), nonce);
    }

    public boolean isValid() {
        return currentHash.equals(calculateHash());
    }
}
