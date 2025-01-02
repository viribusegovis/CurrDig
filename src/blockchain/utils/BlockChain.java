package blockchain.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The BlockChain class represents a collection of blocks that form the
 * blockchain. It allows for the addition of blocks, validation of the chain,
 * and loading/saving the blockchain to a file.
 */
public class BlockChain implements Serializable {

    private static final long serialVersionUID = 202208221009L;

    // List of blocks in the blockchain
    private CopyOnWriteArrayList<Block> chain;

    /**
     * Constructor that initializes an empty blockchain.
     */
    public BlockChain() {
        chain = new CopyOnWriteArrayList<>();
    }

    /**
     * Returns the hash of the last block in the chain. If the blockchain is
     * empty, it returns a default value.
     *
     * @return The hash of the last block or a default value if the chain is
     * empty
     */
    public String getLastBlockHash() {
        if (chain.isEmpty()) {
            return String.format("%08d", 0);
        }
        return chain.get(chain.size() - 1).getCurrentHash();
    }

    /**
     * Adds a new block to the blockchain after verifying that it is valid. The
     * block is checked for duplication and that it links to the previous block.
     *
     * @param newBlock The block to add to the blockchain
     * @throws Exception if the block is invalid or if the chain is not properly
     * linked
     */
    public void add(Block newBlock) throws Exception {
        // Check if the block is already in the chain
        if (chain.contains(newBlock)) {
            throw new Exception("Duplicated Block");
        }

        // Verify that the block links to the previous one
        if (getLastBlockHash().compareTo(newBlock.previousHash) != 0) {
            throw new Exception("Previous hash not combine");
        }

        // Add the new block to the chain
        chain.add(newBlock);
    }

    /**
     * Retrieves a block from the blockchain at the specified index.
     *
     * @param index The index of the block to retrieve
     * @return The block at the specified index
     */
    public Block get(int index) {
        return chain.get(index);
    }

    /**
     * Returns the entire blockchain as a list of blocks.
     *
     * @return The list of blocks in the blockchain
     */
    public List<Block> getChain() {
        return chain;
    }

    /**
     * Saves the blockchain to a file.
     *
     * @param fileName The name of the file to save the blockchain to
     * @throws Exception if an error occurs during file writing
     */
    public void save(String fileName) throws Exception {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(this);
        }
    }

    /**
     * Loads a blockchain from a file.
     *
     * @param fileName The name of the file to load the blockchain from
     * @throws Exception if an error occurs during file reading or if the file
     * is corrupted
     */
    public void load(String fileName) throws Exception {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            BlockChain loaded = (BlockChain) in.readObject();
            this.chain = loaded.chain;
        }
    }

    /**
     * Verifies the integrity of the blockchain by checking that each block
     * properly links to the previous one.
     *
     * @return true if the blockchain is valid, false otherwise
     */
    public boolean isValid() {
        // Loop through each block in the chain and verify that each block is correctly linked
        for (int i = 1; i < chain.size(); i++) {
            String prevHash = chain.get(i).getPreviousHash();
            String actualPrevHash = chain.get(i - 1).getCurrentHash();
            if (!prevHash.equals(actualPrevHash)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns a string representation of the blockchain, including the size of
     * the chain and details of each block.
     *
     * @return A string representation of the blockchain
     */
    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        txt.append("Blockchain size = ").append(chain.size())
                .append(")\n");
        for (Block block : chain) {
            txt.append(block.toString()).append("\n");
        }
        return txt.toString();
    }

    /**
     * Returns the number of blocks in the blockchain.
     *
     * @return The size of the blockchain
     */
    public int getSize() {
        return chain.size();
    }
}
