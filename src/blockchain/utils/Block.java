package blockchain.utils;

import currdig.core.Entry;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Block implements Serializable {

    String previousHash;     // link to previous block
    int nonce;              // proof of work 
    String currentHash;     // Hash of block
    MerkleTree merkleTree;  // Merkle Tree
    List<Entry> buffer;     // Buffer for transactions

    public Block(String previousHash) {
        this.previousHash = previousHash;
        this.nonce = 0;
        this.buffer = new ArrayList<>();
        this.merkleTree = null;
        this.currentHash = null;
    }

    public void addTransaction(Entry transaction) {
        buffer.add(transaction);
    }

    public void createBlock(String data, int nonce) {
        this.nonce = nonce;
        // Convert buffer entries to String array for Merkle tree
        String[] transactions = buffer.stream()
                .map(Entry::toString)
                .toArray(String[]::new);
        this.merkleTree = new MerkleTree(transactions);
        this.currentHash = calculateHash(data);

    }

    public String getPreviousHash() {
        return previousHash;
    }

    public int getNonce() {
        return nonce;
    }

    public MerkleTree getMerkleTree() {
        return merkleTree;
    }

    public List<Entry> getBuffer() {
        return new ArrayList<>(buffer);
    }

    public String calculateHash(String data) {
        if (merkleTree == null) {
            return null;
        }
        //return Hash.getHash(nonce + previousHash + merkleTree.getRoot());

        return Hash.getHash(nonce + data);
    }

    public String getCurrentHash() {
        return currentHash;
    }

    public String toString() {
        if (merkleTree == null) {
            return "Block not finalized - Transactions in buffer: " + buffer.size();
        }
        return String.format("[ %8s", previousHash) + " <- "
                + String.format("%-10s", merkleTree.getRoot())
                + String.format(" %7d ] = ", nonce)
                + String.format("%8s", currentHash);
    }

    public boolean isValid(String data) {
        if (merkleTree == null || currentHash == null) {
            return false;
        }
        return currentHash.equals(calculateHash(data));
    }
}
