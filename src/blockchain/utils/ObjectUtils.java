package blockchain.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

public class ObjectUtils {

    public static Object convertBase64ToObject(String base64String) {
        try {
            // Decodifica a string Base64 em bytes
            byte[] data = Base64.getDecoder().decode(base64String);

            // Desserializa os bytes de volta para um objeto
            ByteArrayInputStream byteStream = new ByteArrayInputStream(data);
            ObjectInputStream objectStream = new ObjectInputStream(byteStream);
            Object object = objectStream.readObject();
            objectStream.close();
            return object;
        } catch (Exception e) {
        }
        return null;

    }

    public static String convertObjectToBase64(Serializable object) throws Exception {
        // Serializa o objeto em bytes
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(byteStream);
        objectStream.writeObject(object);
        objectStream.close();

        // Converte os bytes em Base64
        return Base64.getEncoder().encodeToString(byteStream.toByteArray());
    }

}
