package blockchain.utils;

import currdig.core.Entry;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class BlockChain implements Serializable {
    private static final long serialVersionUID = 202208221009L;
    private ArrayList<Block> chain;
    private Block currentBlock;
    private List<Entry> transactionBuffer;

    public BlockChain() {
        chain = new ArrayList<>();
        transactionBuffer = new ArrayList<>();
        currentBlock = new Block(String.format("%08d", 0));
    }

    public String getLastBlockHash() {
        if (chain.isEmpty()) {
            return String.format("%08d", 0);
        }
        return chain.get(chain.size() - 1).getCurrentHash();
    }

    public void addTransaction(Entry transaction) {
        transactionBuffer.add(transaction);
    }

    public void createBlock(int difficulty) {
        if (transactionBuffer.isEmpty()) {
            System.out.println("Buffer Empty");
            return;
        }

        // Get mining target
        String prevHash = getLastBlockHash();
        
        // Create new block with buffered transactions
        Block block = new Block(prevHash);
        
        // Add all buffered transactions to the block
        for (Entry transaction : transactionBuffer) {
            block.addTransaction(transaction);
        }
        
        // Mine the block
        int nonce = Miner.getNonce(prevHash + transactionBuffer.toString(), difficulty);
        
        // Finalize the block with the found nonce
        block.createBlock(prevHash + transactionBuffer.toString(), nonce);
        
        // Add to chain and clear buffer
        chain.add(block);
        System.out.println(block);
        transactionBuffer.clear();
    }

    public Block get(int index) {
        return chain.get(index);
    }

    
    public List<Block> getChain() {
        return chain;
    }

    public List<Entry> getPendingTransactions() {
        return new ArrayList<>(transactionBuffer);
    }

    public void save(String fileName) throws Exception {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(this);
        }
    }

    public void load(String fileName) throws Exception {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            BlockChain loaded = (BlockChain) in.readObject();
            this.chain = loaded.chain;
            this.transactionBuffer = loaded.transactionBuffer;
        }
    }

    public boolean isValid() {
        for (Block block : chain) {
            if (!block.isValid(getLastBlockHash() + transactionBuffer.toString())) {
                return false;
            }
        }
        
        for (int i = 1; i < chain.size(); i++) {
            String prevHash = chain.get(i).getPreviousHash();
            String actualPrevHash = chain.get(i - 1).getCurrentHash();
            if (!prevHash.equals(actualPrevHash)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder();
        txt.append("Blockchain size = ").append(chain.size())
           .append(" (Pending transactions: ").append(transactionBuffer.size())
           .append(")\n");
        for (Block block : chain) {
            txt.append(block.toString()).append("\n");
        }
        return txt.toString();
    }
}