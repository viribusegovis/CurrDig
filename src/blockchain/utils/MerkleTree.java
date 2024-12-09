package blockchain.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class MerkleTree implements Serializable {

    private List<List<String>> hashTree;
    private List<String> elements;

    public MerkleTree(Object[] arrayOfData) {
        this(Arrays.asList(arrayOfData));
    }

    public MerkleTree(List<Object> listOfData) {
        this();

        for (Object obj : listOfData) {
            elements.add(obj.toString());
        }
        List<String> hashT = new ArrayList<>();
        for (Object elem : listOfData) {
            hashT.add(getHashValue(elem.toString()));
        }
        makeTree(hashT);
    }

    public MerkleTree() {
        hashTree = new ArrayList<>();
        elements = new ArrayList<>();
    }

    public String getRoot() {
        return hashTree.get(0).get(0);
    }

    public void makeTree(List<String> hashList) {
        hashTree.add(0, hashList);
        if (hashList.size() <= 1) {
            return;
        }

        List<String> newLevel = new ArrayList<>();
        for (int i = 0; i < hashList.size(); i += 2) {
            String data = hashList.get(i);
            if (i + 1 < hashList.size()) {
                data = data + hashList.get(i + 1);
            }
            String hash = getHashValue(data);
            newLevel.add(hash);
        }
        makeTree(newLevel);
    }

    public List<String> getProof(Object data) {
        List<String> proof = new ArrayList<>();
        String targetHash = getHashValue(data.toString());

        int index = -1;
        List<String> bottomLevel = hashTree.get(hashTree.size() - 1);
        for (int i = 0; i < bottomLevel.size(); i++) {
            if (bottomLevel.get(i).equals(targetHash)) {
                index = i;
                break;
            }
        }

        if (index < 0) {
            return proof;
        }

        int currentIndex = index;
        for (int level = hashTree.size() - 1; level > 0; level--) {
            List<String> currentLevel = hashTree.get(level);
            if (currentIndex % 2 == 0) {
                if (currentIndex + 1 < currentLevel.size()) {
                    proof.add(currentLevel.get(currentIndex + 1));
                }
            } else {
                proof.add(currentLevel.get(currentIndex - 1));
            }
            currentIndex /= 2;
        }
        return proof;
    }

    public static boolean isProofValid(Object data, List<String> proof) {
        if (proof.isEmpty()) {
            return false;
        }
        String currentHash = getHashValue(data.toString());
        return isProofValid(currentHash, proof, 0);
    }

    public static boolean isProofValid(String currentHash, List<String> proof, int indexOfList) {
        if (indexOfList == proof.size() - 1) {
            return currentHash.equals(proof.get(proof.size() - 1));
        }
        String newHash = getHashValue(currentHash + proof.get(indexOfList));
        if (isProofValid(newHash, proof, indexOfList + 1)) {
            return true;
        }
        newHash = getHashValue(proof.get(indexOfList) + currentHash);
        return isProofValid(newHash, proof, indexOfList + 1);
    }

    public boolean isValid() {
        for (int i = 0; i < this.elements.size(); i++) {
            if (!getHashValue(this.elements.get(i).toString())
                    .equals(hashTree.get(hashTree.size() - 1).get(i))) {
                return false;
            }
        }
        for (int level = 0; level < hashTree.size() - 1; level++) {
            for (int index = 0; index < hashTree.get(level).size(); index++) {
                String dataLeafs = hashTree.get(level + 1).get(index * 2);
                if (index * 2 + 1 < hashTree.get(level + 1).size()) {
                    dataLeafs = dataLeafs + hashTree.get(level + 1).get(index * 2 + 1);
                }
                String hash = getHashValue(dataLeafs);
                if (!hashTree.get(level).get(index).equals(hash)) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        return toTree();
    }

    public String toTree() {
        int SIZE = 9;
        for (Object elem : elements) {
            if (elem.toString().length() > SIZE) {
                SIZE = elem.toString().length();
            }
        }

        StringBuilder txt = new StringBuilder();
        for (int i = 0; i < hashTree.size(); i++) {
            int ini = (int) Math.pow(2, hashTree.size() - i - 1) - 1;
            int middle = (int) Math.pow(2, hashTree.size() - i) - 1;
            if (ini > 0) {
                txt.append(String.format("%" + ini * SIZE + "s", ""));
            }
            for (String hash : hashTree.get(i)) {
                txt.append(centerString(hash, SIZE));
                txt.append(String.format("%" + middle * SIZE + "s", ""));
            }
            txt.append("\n");
        }
        for (Object elem : elements) {
            txt.append(centerString(elem.toString(), SIZE));
            txt.append(String.format("%" + SIZE + "s", ""));
        }
        return txt.toString();
    }

    public void saveToFile(String fileName) throws FileNotFoundException, IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(this);
        }
    }

    public static MerkleTree loadFromFile(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            return (MerkleTree) in.readObject();
        }
    }

    public List<List<String>> getHashTree() {
        return hashTree;
    }

    public List<String> getElements() {
        return elements;
    }

    public String getElementsString() {
        StringBuilder txt = new StringBuilder();
        for (Object obj : elements) {
            txt.append(obj.toString() + "\n");
        }
        return txt.toString().trim();
    }

    public static String centerString(String text, int len) {
        String out = String.format("%" + len + "s%s%" + len + "s", "", text, "");
        float mid = (out.length() / 2);
        float start = mid - (len / 2);
        float end = start + len;
        return out.substring((int) start, (int) end);
    }

    public static String intToHex(int i) {
        return Integer.toString(i, 16).toUpperCase();
    }

    public static String getHashValue(String data) {
        return intToHex(Math.abs(data.hashCode()));
    }
}
