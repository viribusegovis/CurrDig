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
//::                                                               (c)2024   ::
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//////////////////////////////////////////////////////////////////////////////
package blockchain.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;

/**
 * Created on 09/10/2024, 18:35:40
 *
 * @author manso - computer
 */
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
