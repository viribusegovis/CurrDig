package blockchain.utils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BlockChain implements Serializable {

    private static final long serialVersionUID = 202208221009L;
    private CopyOnWriteArrayList<Block> chain;

    public BlockChain() {
        chain = new CopyOnWriteArrayList<>();
    }

    public String getLastBlockHash() {
        if (chain.isEmpty()) {
            return String.format("%08d", 0);
        }
        return chain.get(chain.size() - 1).getCurrentHash();
    }

    public void add(Block newBlock) throws Exception {
        if (chain.contains(newBlock)) {
            throw new Exception("Duplicated Block");
        }

        /*
        //verify block
        if (!newBlock.isValid()) {
            throw new Exception("Invalid Block");
        }*/
        //verify link
        if (getLastBlockHash().compareTo(newBlock.previousHash) != 0) {
            throw new Exception("Previous hash not combine");
        }
        //add new block to the chain
        chain.add(newBlock);
    }

    public Block get(int index) {
        return chain.get(index);
    }

    public List<Block> getChain() {
        return chain;
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
        }
    }

    public boolean isValid() {

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
                .append(")\n");
        for (Block block : chain) {
            txt.append(block.toString()).append("\n");
        }
        return txt.toString();
    }

    public int getSize() {
        return chain.size();
    }
}
