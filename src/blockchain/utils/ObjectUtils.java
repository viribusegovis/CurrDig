package blockchain.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

/**
 * Utility class for converting objects to Base64 encoded strings and vice
 * versa. This class provides methods to serialize objects into Base64 strings
 * and deserialize Base64 strings back into objects.
 */
public class ObjectUtils {

    /**
     * Converts a Base64 encoded string into a Java object.
     *
     * @param base64String The Base64 encoded string representing the object.
     * @return The deserialized object, or null if an error occurs during the
     * process.
     */
    public static Object convertBase64ToObject(String base64String) {
        try {
            // Decode the Base64 string into bytes
            byte[] data = Base64.getDecoder().decode(base64String);

            // Deserialize the byte array back into an object
            ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
            ObjectInputStream objectStream = new ObjectInputStream(byteStream);
            Object object = objectStream.readObject();
            objectStream.close();
            return object;
        } catch (Exception e) {
            // Handle exceptions such as invalid Base64 or failed deserialization
            e.printStackTrace();
        }
        return null; // Return null if an exception occurs
    }

    /**
     * Converts a Java object into a Base64 encoded string.
     *
     * @param object The object to be serialized.
     * @return The Base64 encoded string representing the serialized object.
     * @throws Exception If an error occurs during the serialization process.
     */
    public static String convertObjectToBase64(Serializable object) throws Exception {
        // Serialize the object into a byte array
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
        objectStream.writeObject(object);
        objectStream.close();

        // Convert the byte array into a Base64 encoded string
        return Base64.getEncoder().encodeToString(byteStream.toByteArray());
    }
}
