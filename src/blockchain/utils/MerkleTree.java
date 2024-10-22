//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: 
//::                                                                         ::
//::     Antonio Manuel Rodrigues Manso                                      ::
//::                                                                         ::
//::     I N S T I T U T O    P O L I T E C N I C O   D E   T O M A R        ::
//::     Escola Superior de Tecnologia de Tomar                              ::
//::     e-mail: manso@ipt.pt                                                ::
//::     url   : http://orion.ipt.pt/~manso                                  ::
//::                                                                         ::
//::     This software was build with the purpose of investigate and         ::
//::     learning.                                                           ::
//::                                                                         ::
//::                                                               (c)2022   ::
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//////////////////////////////////////////////////////////////////////////////
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
 * Created on 13/09/2022, 11:42:56
 *
 * @author IPT - Ant0nio M@nso
 * @version 1.0
 */
public final class MerkleTree implements Serializable {

    // merkle tree hashs
    private List<List<String>> hashTree;
    // elements of tree
    List elements;

    /**
     * Builds a merkle tree with an array of data
     *
     * @param arrayOfData list of data
     */
    public MerkleTree(Object[] arrayOfData) {
        this(Arrays.asList(arrayOfData));

    }

    /**
     * Builds a merkle tree with an list of data
     *
     * @param listOfData list of data
     */
    public MerkleTree(List listOfData) {
        this(); //build lists
        //save data in elements
        elements.addAll(listOfData);
        //calculate list of hash of elements
        List<String> hashT = new ArrayList<>();
        for (Object elem : listOfData) {
            //convert T to String
            //hash the string
            hashT.add(getHashValue(elem.toString()));
        }
        //build merkle tree
        makeTree(hashT);
    }

    /**
     * Builds an empty merkle tree
     */
    public MerkleTree() {
        //build lists
        hashTree = new ArrayList<>();
        elements = new ArrayList<>();
    }

    /**
     * root of tree
     *
     * @return root of tree
     */
    public String getRoot() {
        //top o list
        return hashTree.get(0).get(0);
    }

    /**
     * builds a merkle tree
     *
     * @param hashList list of hashs
     */
    public void makeTree(List<String> hashList) {
        //add hashlist to the beginning of tree
        hashTree.add(0, hashList);                
        //top of tree -> terminate
        if (hashList.size() <= 1) {
            return; // top of the tree
        }
        //Fazer o próximo nível
        //new level
        List<String> newLevel = new ArrayList<>();
        //iterate list 2 by 2
        for (int i = 0; i < hashList.size(); i += 2) {
            //first element
            String data = hashList.get(i);
            //if have another element
            if (i + 1 < hashList.size()) {
                //concatenate element of right   
                data = data + hashList.get(i + 1);
            }
            //calculate hash of the elements concatenated
            String hash = getHashValue(data);
            //add hash to the new leval
            newLevel.add(hash);
        }
        //call the makeTree with new level
        makeTree(newLevel);
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
        int index = elements.indexOf(data);
        if (index < 0) { //element not found
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

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::::::::::::::::::::::::   V A L I D A T E    T R E E  ::::::::::::::::
    ///////////////////////////////////////////////////////////////////////////
    /**
     * verify the proof of an element
     *
     * @param data dara
     * @param proof list of proofs
     * @return true if the proof is valid
     */
    public static boolean isProofValid(Object data, List<String> proof) {

        if (proof.isEmpty()) {
            return false;
        }
        //hash of element
        String currentHash = getHashValue(data.toString());
        //index in the proof
        return isProofValid(currentHash, proof, 0);
    }

    public static boolean isProofValid(String currentHash, List<String> proof, int indexOfList) {
        //top of the tree
        if (indexOfList == proof.size()-1) {
            return currentHash.equals(proof.get(proof.size()-1));
        }
        
        //concatenate to the right
        String newHash = getHashValue(currentHash + proof.get(indexOfList));
        //verify next level
        if (isProofValid(newHash, proof, indexOfList+ 1)) {
            return true;
        }
        //concatenate to the left
        newHash = getHashValue(proof.get(indexOfList) + currentHash);
        //verify next level
        return isProofValid(newHash, proof, indexOfList + 1);

    }
    /**
     * verify if the merkle tree is valid
     *
     * @return valid merkle tree
     */
    public boolean isValid() {
        //verify the hash of elements  int the bottom of tree
        for (int i = 0; i < this.elements.size(); i++) {
            if (!getHashValue(this.elements.get(i).toString()).equals(hashTree.get(hashTree.size() - 1).get(i))) {
                return false;
            }
        }
        //verify the levels of the tree
        for (int level = 0; level < hashTree.size() - 1; level++) {
            // verify level            
            for (int index = 0; index < hashTree.get(level).size(); index++) {
                //left leaf
                String dataLeafs = hashTree.get(level + 1).get(index * 2);
                //if right leaf exists
                if (index * 2 + 1 < hashTree.get(level + 1).size()) {
                    //concatenate hashs
                    dataLeafs = dataLeafs + hashTree.get(level + 1).get(index * 2 + 1);
                }
                //calculate hash of leafs
                String hash = getHashValue(dataLeafs);
                //verify the hash leafs
                if (hashTree.get(level).get(index).equals(hash)) {
                    return false;
                }
            }
        }
        //all is ok
        return true;
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::::::::::::::::::::::::  T O   S T R I N G        ::::::::::::::::
    ///////////////////////////////////////////////////////////////////////////
    @Override
    public String toString() {
        return toTree();
    }

    public String toTree() {
        //calculate maxSize of elementos in chars
        int SIZE = 9; // minimum size
        for (Object elem : elements) {
            if (elem.toString().length() > SIZE) {
                SIZE = elem.toString().length();
            }
        }
        //Build TREE        
        StringBuilder txt = new StringBuilder();
        for (int i = 0; i < hashTree.size(); i++) {
            //spaces in the init of line
            int ini = (int) Math.pow(2, hashTree.size() - i - 1) - 1;
            //spaces in the meddle of elements
            int middle = (int) Math.pow(2, hashTree.size() - i) - 1;
            //put spaces in the beginning
            if (ini > 0) {
                txt.append(String.format("%" + ini * SIZE + "s", ""));
            }
            //put the line of elements
            for (String hash : hashTree.get(i)) {
                //element
                txt.append(centerString(hash, SIZE));
                //spaces
                txt.append(String.format("%" + middle * SIZE + "s", ""));
            }
            txt.append("\n");

        }
        //print the elements
        for (Object elem : elements) {
            txt.append(centerString(elem.toString(), SIZE));
            txt.append(String.format("%" + SIZE + "s", ""));
        }

        return txt.toString();
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::::::::::::::::::::::::   S A V E   /    L O A D      ::::::::::::::::
    ///////////////////////////////////////////////////////////////////////////
    public void saveToFile(String fileName) throws FileNotFoundException, IOException {
        try ( ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(fileName))) {
            out.writeObject(this);
        }
    }

    public static MerkleTree loadFromFile(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
        try ( ObjectInputStream in = new ObjectInputStream(new FileInputStream(fileName))) {
            return (MerkleTree) in.readObject();
        }
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //::::::          E N C A P S U L A M E N T O                      :::::::::
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::    
    ///////////////////////////////////////////////////////////////////////////
    public List<List<String>> getHashTree() {
        return hashTree;
    }

    /**
     * gets the elements in the merkle tree
     *
     * @return elements
     */
    public List getElements() {
        return elements;
    }
    
    /**
     * gets the elements in the merkle tree
     *
     * @return elements
     */
    public String getElementsString() {
        StringBuilder txt = new StringBuilder();
        for (Object obj : elements) {
            txt.append(obj.toString()+ "\n");
        }
        return txt.toString().trim();
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //::::::                                                           :::::::::
    //::::::                         U T I L S                         :::::::::
    //::::::                                                           :::::::::
    ///////////////////////////////////////////////////////////////////////////
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

   
    /**
     * Calculates the hash value of data using Arrays.hashCode(data)
     *
     * @param data data
     * @return hash value
     */
    public static String getHashValue(String data) {
        return intToHex(Math.abs(data.hashCode()));
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    private static final long serialVersionUID = 202209131142L;
    //:::::::::::::::::::::::::::  Copyright(c) M@nso  2022  :::::::::::::::::::
    ///////////////////////////////////////////////////////////////////////////

}
