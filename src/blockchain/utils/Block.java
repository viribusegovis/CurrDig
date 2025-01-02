package blockchain.utils;

import currdig.core.Entry;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * The Block class represents a single block in the blockchain. It contains
 * information such as the previous block hash, a list of transactions, the
 * Merkle root, and proof of work data.
 */
public class Block implements Serializable {

    // Link to the previous block
    String previousHash;

    // Nonce used in proof of work
    int nonce;

    // Current block hash
    String currentHash;

    // Set of transactions within the block
    CopyOnWriteArraySet<Entry> transactions;

    // Merkle root of the block's transactions
    String merkleRoot;

    // Merkle tree used to calculate the Merkle root
    MerkleTree merkleTree;

    /**
     * Constructor for the Block. Initializes a block with the given previous
     * hash and transactions.
     *
     * @param previousHash The hash of the previous block
     * @param entries The transactions for this block
     */
    public Block(String previousHash, CopyOnWriteArraySet<Entry> entries) {
        this.previousHash = previousHash;
        this.nonce = 0;
        this.currentHash = null;
        this.transactions = entries;

        // Create a Merkle tree from the transactions
        String[] transactions1 = this.transactions.stream()
                .map(Entry::toString)
                .toArray(String[]::new);
        this.merkleTree = new MerkleTree(transactions1);

        // Calculate the Merkle root from the Merkle tree
        this.merkleRoot = this.merkleTree.getRoot();
    }

    /**
     * Returns the previous block's hash.
     *
     * @return The hash of the previous block
     */
    public String getPreviousHash() {
        return previousHash;
    }

    /**
     * Returns the nonce for proof of work.
     *
     * @return The nonce of the block
     */
    public int getNonce() {
        return nonce;
    }

    /**
     * Returns a set of transactions within the block.
     *
     * @return A set of transactions
     */
    public CopyOnWriteArraySet<Entry> transactions() {
        return transactions;
    }

    /**
     * Returns a string representation of the transactions in the block.
     *
     * @return A string containing all the transactions in the block
     */
    public String getTransactionsString() {
        StringBuilder txt = new StringBuilder();
        for (Entry transaction : transactions) {
            txt.append(transaction).append("\n");
        }
        return txt.toString();
    }

    /**
     * Calculates the hash of the block. The hash is based on the nonce and the
     * given data.
     *
     * @param data The data to include in the hash
     * @return The calculated hash
     */
    public String calculateHash(String data) {
        if (merkleRoot == null) {
            return null;
        }
        // Return hash based on the nonce and the Merkle root
        return Hash.getHash(nonce + data);
    }

    /**
     * Returns the current hash of the block.
     *
     * @return The current hash of the block
     */
    public String getCurrentHash() {
        return currentHash;
    }

    /**
     * Returns the Merkle tree of the block.
     *
     * @return The Merkle tree used to calculate the Merkle root
     */
    public MerkleTree getMerkleTree() {
        return merkleTree;
    }

    /**
     * Returns the list of transactions in the block as a buffer.
     *
     * @return A list containing the transactions
     */
    public List<Entry> getBuffer() {
        return new ArrayList<>(transactions);
    }

    /**
     * Returns a string representation of the block.
     *
     * @return A string representing the block's details
     */
    public String toString() {
        if (merkleRoot == null) {
            return "Block not finalized - Transactions in buffer: ";
        }
        return String.format("[ %8s", previousHash) + " <- "
                + String.format("%-10s", merkleRoot)
                + String.format(" %7d ] = ", nonce)
                + String.format("%8s", currentHash);
    }

    /**
     * Checks if the block is valid by comparing the current hash with the
     * calculated hash.
     *
     * @param data The data to use in the hash calculation
     * @return true if the block's hash matches the calculated hash, otherwise
     * false
     */
    public boolean isValid(String data) {
        if (merkleRoot == null || currentHash == null) {
            return false;
        }
        return currentHash.equals(calculateHash(data));
    }

    /**
     * Returns the data used for mining the block (previous hash + Merkle root).
     *
     * @return The data used for mining
     */
    public String getMinerData() {
        return previousHash + merkleRoot;
    }

    /**
     * Returns the Merkle root of the block.
     *
     * @return The Merkle root
     */
    public String getMerkleRoot() {
        return merkleRoot;
    }

    /**
     * Sets the nonce for the block and calculates the hash. Also checks if the
     * current hash meets the required prefix.
     *
     * @param nonce The nonce to set
     * @param zeros The number of leading zeros required in the hash
     * @throws Exception if the calculated hash does not meet the required
     * prefix
     */
    public void setNonce(int nonce, int zeros) throws Exception {
        this.nonce = nonce;
        // Calculate the hash with the new nonce
        this.currentHash = calculateHash();
        // Check if the current hash meets the prefix requirement
        String prefix = String.format("%0" + zeros + "d", 0);
        if (!currentHash.startsWith(prefix)) {
            throw new Exception(nonce + " not valid Hash=" + currentHash);
        }
    }

    /**
     * Calculates the hash based on miner data and the nonce.
     *
     * @return The calculated hash
     */
    public String calculateHash() {
        return Miner.getHash(getMinerData(), nonce);
    }

    /**
     * Checks if the block's current hash is valid.
     *
     * @return true if the block's current hash is valid, otherwise false
     */
    public boolean isValid() {
        return currentHash.equals(calculateHash());
    }
}
