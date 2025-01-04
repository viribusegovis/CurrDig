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

/**
 * This class implements a Merkle Tree structure, which is a binary tree where
 * each leaf node is a hash of data, and non-leaf nodes are hashes of their
 * children. Merkle Trees are used in blockchain and cryptography to verify data
 * integrity.
 */
public final class MerkleTree implements Serializable {

    private List<List<String>> hashTree;  // List of hash levels (from leaves to root)
    private List<String> elements;        // Original data elements

    /**
     * Constructs a MerkleTree from an array of data.
     *
     * @param arrayOfData The data to be added to the Merkle tree.
     */
    public MerkleTree(Object[] arrayOfData) {
        this(Arrays.asList(arrayOfData));  // Convert array to list
    }

    /**
     * Constructs a MerkleTree from a list of data.
     *
     * @param listOfData The list of data to be added to the Merkle tree.
     */
    public MerkleTree(List<Object> listOfData) {
        this();
        for (Object obj : listOfData) {
            elements.add(obj.toString());
        }

        // Create a list of hashes for the elements
        List<String> hashT = new ArrayList<>();
        for (Object elem : listOfData) {
            hashT.add(getHashValue(elem.toString()));
        }
        makeTree(hashT);  // Build the Merkle tree from hashes
    }

    /**
     * Default constructor, initializes the Merkle tree with empty lists.
     */
    public MerkleTree() {
        hashTree = new ArrayList<>();
        elements = new ArrayList<>();
    }

    /**
     * Returns the root hash of the Merkle tree.
     *
     * @return The root hash.
     */
    public String getRoot() {
        return hashTree.get(0).get(0);  // The root hash is the first item of the first level
    }

    /**
     * Recursively builds the Merkle tree from the list of hash values.
     *
     * @param hashList A list of hashes (leaf nodes).
     */
    public void makeTree(List<String> hashList) {
        hashTree.add(0, hashList);  // Add the current level to the tree
        if (hashList.size() <= 1) {
            return;  // Base case: if only one hash remains, tree construction is done
        }

        List<String> newLevel = new ArrayList<>();
        for (int i = 0; i < hashList.size(); i += 2) {
            // Concatenate two adjacent hashes and hash them again to create the parent hash
            String data = hashList.get(i);
            if (i + 1 < hashList.size()) {
                data = data + hashList.get(i + 1);
            }
            String hash = getHashValue(data);  // Hash the concatenated data
            newLevel.add(hash);
        }
        makeTree(newLevel);  // Recursively build the next level
    }

    /**
     * calculate the proff of the element
     *
     * @param data element
     * @return list of proofs
     */
    public List<String> getProof(Object data) {
        //list of proofs
        List<String> proof = new ArrayList<>();

        //index of element
        int index = elements.indexOf(data.toString());
        if (index == -1) { //element not found
            System.out.println("Element not found");
            return proof; // empty proof
        }
        //calculate proof
        return getProof(index, hashTree.size() - 1, proof);
    }

    /**
     * calculate the proff of the element
     *
     * @param index index of element
     * @param level level of the tree
     * @param proof list of proofs
     * @return list of proofs
     */
    private List<String> getProof(int index, int level, List<String> proof) {

        if (level > 0) { // not the top
            if (index % 2 == 0) { // is even [ index or index+1]               
                //if have elements in the right
                if (index + 1 < hashTree.get(level).size()) {
                    //add element of the right
                    proof.add(hashTree.get(level).get(index + 1));
                } else {
                    //add the hash of element
                    proof.add(hashTree.get(level).get(index));
                }
            } else {// is odd [ index - 1 ]
                proof.add(hashTree.get(level).get(index - 1));
            }
            //calculate top level
            return getProof(index / 2, level - 1, proof);
        } else {
            //add root of tree
            proof.add(getRoot());
            return proof;
        }
    }

    /**
     * Verifies if a proof is valid for a given data element.
     *
     * @param data The data element to verify.
     * @param proof The proof to verify.
     * @return True if the proof is valid, false otherwise.
     */
    public static boolean isProofValid(Object data, List<String> proof) {
        if (proof.isEmpty()) {
            return false;  // Invalid proof if empty
        }
        String currentHash = getHashValue(data.toString());
        return isProofValid(currentHash, proof, 0);  // Start verification
    }

    /**
     * Helper method to recursively verify a proof.
     *
     * @param currentHash The current hash to verify.
     * @param proof The proof to verify.
     * @param indexOfList The current index in the proof list.
     * @return True if the proof is valid, false otherwise.
     */
    public static boolean isProofValid(String currentHash, List<String> proof, int indexOfList) {
        if (indexOfList == proof.size() - 1) {
            return currentHash.equals(proof.get(proof.size() - 1));  // Base case: compare final hash
        }
        // Combine current hash with the next proof element and hash again
        String newHash = getHashValue(currentHash + proof.get(indexOfList));
        if (isProofValid(newHash, proof, indexOfList + 1)) {
            return true;
        }
        // If the order was reversed, try the opposite combination
        newHash = getHashValue(proof.get(indexOfList) + currentHash);
        return isProofValid(newHash, proof, indexOfList + 1);
    }

    /**
     * Validates the integrity of the Merkle tree by comparing the computed hash
     * values with the stored hash values at each level.
     *
     * @return True if the tree is valid, false otherwise.
     */
    public boolean isValid() {
        // Check if the hashes in the leaf level match the hashes of the elements
        for (int i = 0; i < this.elements.size(); i++) {
            if (!getHashValue(this.elements.get(i).toString())
                    .equals(hashTree.get(hashTree.size() - 1).get(i))) {
                return false;
            }
        }
        // Validate internal nodes
        for (int level = 0; level < hashTree.size() - 1; level++) {
            for (int index = 0; index < hashTree.get(level).size(); index++) {
                String dataLeafs = hashTree.get(level + 1).get(index * 2);
                if (index * 2 + 1 < hashTree.get(level + 1).size()) {
                    dataLeafs = dataLeafs + hashTree.get(level + 1).get(index * 2 + 1);  // Combine child hashes
                }
                String hash = getHashValue(dataLeafs);
                if (!hashTree.get(level).get(index).equals(hash)) {
                    return false;  // If hash doesn't match, tree is invalid
                }
            }
        }
        return true;  // Tree is valid
    }

    @Override
    public String toString() {
        return toTree();  // Convert the tree to string representation
    }

    /**
     * Converts the Merkle tree to a formatted string for display.
     *
     * @return A string representation of the Merkle tree.
     */
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
                txt.append(String.format("%" + ini * SIZE + "s", ""));  // Indentation for each level
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
        return txt.toString();  // Return tree in string format
    }

    /**
     * Saves the current Merkle tree to a file.
     *
     * @param fileName The file name where the tree will be saved.
     * @throws FileNotFoundException If the file cannot be found.
     * @throws IOException If an I/O error occurs while writing the file.
     */
    public void saveToFile(String fileName) throws FileNotFoundException, IOException {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(this);  // Serialize and save the Merkle tree
        }
    }

    /**
     * Loads a Merkle tree from a file.
     *
     * @param fileName The file name from which the tree will be loaded.
     * @return The loaded Merkle tree.
     * @throws FileNotFoundException If the file cannot be found.
     * @throws IOException If an I/O error occurs while reading the file.
     * @throws ClassNotFoundException If the class of the loaded object cannot
     * be found.
     */
    public static MerkleTree loadFromFile(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            return (MerkleTree) in.readObject();  // Deserialize and return the loaded tree
        }
    }

    // Helper methods for hash and string manipulation
    /**
     * Returns the hash tree.
     *
     * @return The hash tree.
     */
    public List<List<String>> getHashTree() {
        return hashTree;
    }

    /**
     * Returns the list of elements.
     *
     * @return The list of elements.
     */
    public List<String> getElements() {
        return elements;
    }

    /**
     * Returns the elements as a string.
     *
     * @return The elements as a string.
     */
    public String getElementsString() {
        StringBuilder txt = new StringBuilder();
        for (Object obj : elements) {
            txt.append(obj.toString() + "\n");
        }
        return txt.toString().trim();
    }

    /**
     * Centers a string within a given length.
     *
     * @param text The text to center.
     * @param len The length of the string.
     * @return The centered string.
     */
    public static String centerString(String text, int len) {
        String out = String.format("%" + len + "s%s%" + len + "s", "", text, "");
        float mid = (out.length() / 2);
        float start = mid - (len / 2);
        float end = start + len;
        return out.substring((int) start, (int) end);
    }

    /**
     * Converts an integer to its hexadecimal representation.
     *
     * @param i The integer to convert.
     * @return The hexadecimal string.
     */
    public static String intToHex(int i) {
        return Integer.toString(i, 16).toUpperCase();
    }

    /**
     * Computes the hash value of a given string.
     *
     * @param data The string to hash.
     * @return The hexadecimal hash value.
     */
    public static String getHashValue(String data) {
        return intToHex(Math.abs(data.hashCode()));  // Simple hash function using hashCode
    }
}
