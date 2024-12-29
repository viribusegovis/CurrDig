package blockchain.utils;

import currdig.core.Entry;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;

public class Block implements Serializable {

    String previousHash;     // link to previous block
    int nonce;              // proof of work 
    String currentHash;     // Hash of block
    CopyOnWriteArraySet<Entry> transactions; // transações do bloco (devem ser guardadas em separado)
    String merkleRoot;  // Merkle Root
    MerkleTree merkleTree;  // Merkle Tree

    public Block(String previousHash, CopyOnWriteArraySet<Entry> entries) {
        this.previousHash = previousHash;
        this.nonce = 0;
        this.currentHash = null;
        this.transactions = entries;

        String[] transactions1 = this.transactions.stream()
                .map(Entry::toString)
                .toArray(String[]::new);
        this.merkleTree = new MerkleTree(transactions1);

        this.merkleRoot = this.merkleTree.getRoot();
    }

    public String getPreviousHash() {
        return previousHash;
    }

    public int getNonce() {
        return nonce;
    }

    public CopyOnWriteArraySet<Entry> transactions() {
        return transactions;
    }

    public String getTransactionsString() {
        StringBuilder txt = new StringBuilder();
        for (Entry transaction : transactions) {
            txt.append(transaction).append("\n");
        }
        return txt.toString();
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

    public MerkleTree getMerkleTree() {
        return merkleTree;
    }

    public List<Entry> getBuffer() {
        return new ArrayList<>(transactions);
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

    public String getMerkleRoot() {
        return merkleRoot;
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
