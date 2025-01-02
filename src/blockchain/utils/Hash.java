package blockchain.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class used to Hash Strings
 */
public class Hash {

    /**
     * Converts a byte array to a hexadecimal string.
     *
     * @param bytes the byte array
     * @return hexadecimal string
     */
    public static String toHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString().toUpperCase();
    }

    /**
     * Generates a secure hash using SHA-256 for the given input.
     *
     * @param data the input string
     * @return the SHA-256 hash as a hexadecimal string
     */
    public static String getHash(String data) {
        try {
            // Use SHA-256 for secure hashing
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes());
            return toHexString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not found!", e);
        }
    }
}
