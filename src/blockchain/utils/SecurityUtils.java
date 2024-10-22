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
//::                                                               (c)2020   ::
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//////////////////////////////////////////////////////////////////////////////
package blockchain.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidAlgorithmParameterException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.IllegalFormatException;
import java.util.Random;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * Algoritmos de segurança
 *
 * @author ZULU
 */
public class SecurityUtils {

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::::::       K E Y S        :::::::::::::::::::::::::::::::::    
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Gera uma chave de criptogradia simetrica TrippleDes
     *
     * @param keySize tamanho da chave 128, 192 ou 256 bits
     * @return chave cahve simétrica gerada
     * @throws Exception muito improvável de ocurrer
     */
    public static Key generateAESKey(int keySize) throws Exception {
        return generateAESKey(keySize, "SunJCE");
    }

    /**
     * Gera uma chave de criptogradia simetrica TrippleDes
     *
     * @param keySize tamanho da chave 128, 192 ou 256 bits
     * @param provider name of provider
     * @return chave cahve simétrica gerada
     * @throws Exception muito improvável de ocurrer
     */
    public static Key generateAESKey(int keySize, String provider) throws Exception {
        // gerador de chaves
        KeyGenerator keyGen = KeyGenerator.getInstance("AES", provider);
        //tamanho da chave
        keyGen.init(keySize);
        //gerar a chave
        Key key = keyGen.generateKey();
        return key;
    }

    /**
     * Gera um par de chave para elliptic curves
     *
     * @param size tamanho da chave ( 224 , 256 , 384 , 521 ) bits
     * @return par de chaves EC
     * @throws Exception
     */
    public static KeyPair generateECKeyPair(int size) throws Exception {
        String secCurve;
        //tamanho da chave
        switch (size) {
            case 224:
                secCurve = "secp224r1";
                break;
            case 256:
                secCurve = "secp256r1";
                break;
            case 384:
                secCurve = "secp384r1";
                break;
            case 521:
                secCurve = "secp521r1";
                break;
            default: //caso o tamanho dado nao seja permitido
                throw new Exception("Só são permitidos os seguintes tamanhos: 224, 256, 384 e 521");
        }
        // gerador de chaves Eliptic curve
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("EC");

        // gerador de chaves Eliptic Curves Criptografy
        ECGenParameterSpec generationParam = new ECGenParameterSpec(secCurve);
        keyGen.initialize(generationParam, new SecureRandom());
        //devolve o par de chaves gerado
        return keyGen.generateKeyPair();
    }

    /**
     * Gera um par de chave para o algoritmo RSA
     *
     * @param size tamanho da chave minimo 512
     * @return par de chaves RSA
     * @throws Exception
     */
    public static KeyPair generateRSAKeyPair(int size) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        //tamanho da chave
        keyGen.initialize(size);
        //devolve o par de chaves gerado
        return keyGen.generateKeyPair();
    }

    /**
     * Gera um par de chave para o algoritmo RSA
     *
     * @param size tamanho da chave minimo 512
     * @param provider provider
     * @return par de chaves RSA
     * @throws Exception
     */
    public static KeyPair generateRSAKeyPair(int size, String provider) throws Exception {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA", provider);
        //tamanho da chave
        keyGen.initialize(size);
        //devolve o par de chaves gerado
        return keyGen.generateKeyPair();
    }

    /**
     * Transforma um array de bytes representante de uma chave publica em chave
     * publica
     *
     * @param pubData array de bytes representante da chave
     * @return a chave publica em forma de chave
     * @throws Exception Caso ocorra algum erro:
     * <code>NoSuchAlgorithmException</code>,<code>InvalidKeySpecException</code>,<code>NullPointerException</code>
     * e <code>NoSuchProviderException</code>
     */
    public static PublicKey getPublicKey(byte[] pubData) throws Exception {
        //especificação do encoding da chave publica X509
        X509EncodedKeySpec pubSpec = new X509EncodedKeySpec(pubData);
        KeyFactory keyFactory;
        //objecto para grerar a chave RSA        
        try {//test RSA
            keyFactory = KeyFactory.getInstance("RSA");
            //Gerar a chave pública
            return keyFactory.generatePublic(pubSpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NullPointerException ex) {
            try {//test EC
                keyFactory = KeyFactory.getInstance("EC");
                //Gerar a chave pública
                return keyFactory.generatePublic(pubSpec);
            } catch (Exception ex2) {
                throw new InvalidAlgorithmParameterException();
            }
        }
    }

    /**
     * Transforma um array de bytes representante de uma chave privada em chave
     * privada
     *
     * @param privData array de bytes representante da chave
     * @return a chave privada em forma de chave
     * @throws Exception Caso ocorra algum erro:
     * <code>NoSuchAlgorithmException</code>,<code>InvalidKeySpecException</code>,<code>NullPointerException</code>
     * e <code>NoSuchProviderException</code>
     */
    public static PrivateKey getPrivateKey(byte[] privData) throws Exception {
        //especificações da chave privada PKCS8
        PKCS8EncodedKeySpec privSpec = new PKCS8EncodedKeySpec(privData);
        KeyFactory keyFactory;
        //objecto para grerar a chave RSA        
        try {//test RSA
            keyFactory = KeyFactory.getInstance("RSA");
            //Gerar a chave privada
            return keyFactory.generatePrivate(privSpec);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NullPointerException ex) {
            try {//test EC
                keyFactory = KeyFactory.getInstance("EC");
                //Gerar a chave privada
                return keyFactory.generatePrivate(privSpec);
            } catch (Exception ex2) {
                throw new InvalidAlgorithmParameterException();
            }
        }
    }

    /**
     * Gera uma chave AES
     *
     * @param key chave em array de bytes
     * @return chave chave carregada através da base64
     */
    public static Key getAESKey(byte[] key) {
        return new SecretKeySpec(key, "AES");
    }

    /**
     * nomraliza o nome do ficheiro de chaves
     *
     * @param key chave para guardar
     * @param file nome do ficheiro
     * @return nome do ficheiro com a extensão correta
     * @throws IllegalFormatException
     */
    private static String normalizeKeyFileName(Key key, String file) {
        //remove extension
        if (file.contains(".")) {
            file = file.substring(0, file.lastIndexOf('.'));
        }
        //put extension
        if (key instanceof PublicKey) {
            return file + "." + PUBLIC_KEY_EXTENSION_FILE;
        } else if (key instanceof PrivateKey) {
            return file + "." + PRIVATE_KEY_EXTENSION_FILE;
        } else {
            return file + "." + KEY_EXTENSION_FILE;
        }
    }

    /**
     * Guarda uma chave num ficheiro
     *
     * @param key chave a ser armazenada
     * @param file nome do ficheiro
     * @throws IOException caso não haja permissão para aceder ou escrever ao
     * ficheiro indicado
     */
    public static void saveKey(KeyPair key, String file) throws IOException {
        saveKey(key.getPublic(), file);
        saveKey(key.getPrivate(), file);
    }

    /**
     * Guarda uma chave num ficheiro
     *
     * @param key chave a ser armazenada
     * @param file nome do ficheiro
     * @throws IOException caso não haja permissão para aceder ou escrever ao
     * ficheiro indicado
     */
    public static void saveKey(Key key, String file) throws IOException {
        Files.write(Paths.get(normalizeKeyFileName(key, file)), key.getEncoded());
    }

    public static PrivateKey loadPrivateKey(String file) throws IOException {
        //remove extension
        if (file.contains(".")) {
            file = file.substring(0, file.lastIndexOf('.'));
        }
        return (PrivateKey) loadKey(file + "." + PRIVATE_KEY_EXTENSION_FILE);
    }

    public static PublicKey loadPublicKey(String file) throws IOException {
        //remove extension
        if (file.contains(".")) {
            file = file.substring(0, file.lastIndexOf('.'));
        }
        return (PublicKey) loadKey(file + "." + PUBLIC_KEY_EXTENSION_FILE);
    }

    public static SecretKey loadAESKey(String file) throws IOException {
        //remove extension
        if (file.contains(".")) {
            file = file.substring(0, file.lastIndexOf('.'));
        }
        return (SecretKey) loadKey(file + "." + KEY_EXTENSION_FILE);
    }

    /**
     * Carrega uma chave de um ficheiro
     *
     * @param file nome do ficheiro
     * @return a chave que estava armazenada no ficheiro
     * @throws IOException caso não haja permissão para aceder ou ler ao
     * ficheiro indicado
     */
    public static Key loadKey(String file) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(file));
        try {
            return getPublicKey(encoded);
        } catch (Exception e) {
            try {
                return getPrivateKey(encoded);
            } catch (Exception ex) {
                return getAESKey(encoded);
            }
        }
    }
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::   
    //:::::::::::::     E N C R Y P T    /   D E C R Y P T   :::::::::::::::::::
    ///////////////////////////////////////////////////////////////////////////

    /**
     * Encripta um array de dados
     *
     * @param data dados a serem encriptados
     * @param key chave a ser utilizada na encriptação
     * @return os dados encriptados em um array de dados
     * @throws Exception Caso ocorra algum erro, como por exemplo o algoritmo
     * não existir
     */
    public static byte[] encrypt(byte[] data, Key key) throws Exception {

        //criar um objecto de cifragem da chave
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        //configurar o objecto para cifrar
        cipher.init(Cipher.ENCRYPT_MODE, key);
        //cifrar os dados
        return cipher.doFinal(data);
    }

    /**
     * Desencripta um array de dados
     *
     * @param data dados a serem desencriptados
     * @param key chave a ser utilizada na desencriptação
     * @return os dados encriptados em um array de dados
     *
     */
    public static byte[] decrypt(byte[] data, Key key) throws Exception {
        //criar um objecto de cifragem da chave
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        //configurar o objecto para cifrar
        cipher.init(Cipher.DECRYPT_MODE, key);
        //decifrar os dados
        return cipher.doFinal(data);
    }
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::::::::        PASSWORD BASED ENCRYPTATION                 :::::::::::
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

    /**
     * Cria um objecto de cifragem com password
     *
     * @param mode Cipher.DECRYPT_MODE ou Cipher.ENCRYPT_MODE
     * @param password password de da cifra
     * @return Objecto de cifragem
     * @throws Exception
     */
    public static Cipher createCipherPBE(int mode, String password) throws Exception {
        //Criar a chave da cifra
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        //sal da chave - deve ser aleatório e diferente para cada mensagem
        byte[] salt = password.getBytes();
        //inicializar o gerador aleatorio
        Random rnd = new Random(new BigInteger(salt).longValue());
        rnd.nextBytes(salt); // gerar sal aleatorio
        //fazer iteracoes com a password
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 256);
        //Gerar uma chave secreta
        SecretKey key = factory.generateSecret(spec);
        //Gerar uma chave AES
        SecretKeySpec secretKey = new SecretKeySpec(key.getEncoded(), "AES");
        //criar Objecto de cifragem
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        //Vetor de inicializacao
        byte[] iv = new byte[16];
        rnd.nextBytes(iv); // aleatorizar o vetor
        IvParameterSpec ivspec = new IvParameterSpec(iv);
        //inicializar o objeto de cifragem
        cipher.init(mode, secretKey, ivspec);
        //retornar o Objecto de cifragem
        return cipher;
    }

    public static void main(String[] args) throws Exception {
        byte[] secret = encrypt("Ola".getBytes(), "pass");
        byte[] plain = decrypt(secret, "pass");
        System.out.println(new String(plain));
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::::::::               ENCRYPT /  DECRYPT                   :::::::::::
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /**
     * encripta dados usando uma password de texto
     *
     * @param data dados para encriptar
     * @param password password de encriptação
     * @return dados encriptados
     * @throws Exception
     */
    public static byte[] encrypt(byte[] data, String password) throws Exception {
        //criar um objecto de cifragem da chave
        Cipher cipher = createCipherPBE(Cipher.ENCRYPT_MODE, password);
        //cifrar os dados
        return cipher.doFinal(data);
    }

    /**
     * desencripta dados usando uma password de texto
     *
     * @param data dados para desencriptar
     * @param password password de desencriptação
     * @return dados desencriptados
     * @throws Exception
     */
    public static byte[] decrypt(byte[] data, String password) throws Exception {
        //criar um objecto de cifragem da chave
        Cipher cipher = createCipherPBE(Cipher.DECRYPT_MODE, password);
        //cifrar os dados
        return cipher.doFinal(data);
    }

    /**
     * Escreve a mensagem message encriptada com uma chave simetrica numa stream
     *
     * @param message mensagem a encriptar
     * @param out stream de saida
     * @param key chave de encriptacao
     * @throws Exception
     */
    public static void writeCrypt(byte[] message, OutputStream out, Key key) throws Exception {
        //criar a cifra
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        //iniciar para encriptar
        cipher.init(Cipher.ENCRYPT_MODE, key);
        //criar uma stream cifrada com a cifra
        CipherOutputStream cos = new CipherOutputStream(out, cipher);
        //escrever a mensagem
        cos.write(message);
        //fechar a stream
        cos.close();
    }

    /**
     * Le a mensagem cifrada com a chave key de uma stream
     *
     * @param in stream de entrada
     * @param key chave de cifragem
     * @return bytes do ficheiro
     * @throws Exception
     */
    public static byte[] readCrypt(InputStream in, Key key) throws Exception {
        //objecto para decifrar
        Cipher cipher = Cipher.getInstance(key.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, key);
        //abertura de uma stream cifrada
        CipherInputStream cis = new CipherInputStream(in, cipher);
        //ler os bytes da stream
        return cis.readAllBytes();
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::::::      I N T E G R I T Y         :::::::::::::::::::::::::::::::::    
    ///////////////////////////////////////////////////////////////////////////
    public static byte[] calculateHash(byte[] data, String algorithm)
            throws Exception {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.update(data);
        return md.digest();
    }

    public static boolean verifyHash(byte[] data, byte[] hash, String algorithm) throws Exception {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        md.update(data);
        byte[] trueHash = md.digest();
        return Arrays.equals(trueHash, hash);
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::::::       S I G N A T U R E        :::::::::::::::::::::::::::::::::    
    ///////////////////////////////////////////////////////////////////////////
    /**
     * Assina os dados passados com a chave privada passada
     *
     * @param data dados a serem utilizados para a assinatura
     * @param key chave que irá assinar os dados
     * @return A assinatura em um array de bytes
     * @throws Exception Caso ocorra algum erro, como por exemplo o algoritmo
     * não existir
     */
    public static byte[] sign(byte[] data, PrivateKey key) throws Exception {
        Signature sign;
        //verifica qual o algoritmo a ser utilizado
        switch (key.getAlgorithm()) {
            case "RSA":
                sign = Signature.getInstance("SHA256withRSA");
                break;
            case "EC":
                sign = Signature.getInstance("SHA256withECDSA");
                break;
            default: //caso o algoritmo pedido não exista
                throw new InvalidAlgorithmParameterException();
        }
        //inicializa a assinatura com a chave
        sign.initSign(key);
        //assina os dados
        sign.update(data);
        //devolve a assinatura
        return sign.sign();
    }

    /**
     * Verifica se assinatura é valida
     *
     * @param data dados assinados a serem validados com a assinatura
     * @param signature assinatura a ser validado
     * @param key cahve publica que faz par com a chave privada que foi
     * utilizada na assinatura
     * @return se a assinatura é valida
     * @throws Exception Caso ocorra algum erro, como por exemplo o algoritmo
     * não existir
     */
    public static boolean verifySign(byte[] data, byte[] signature, PublicKey key) throws Exception {
        Signature sign;
        //verifica qual o algoritmo a ser utilizado
        switch (key.getAlgorithm()) {
            case "RSA":
                sign = Signature.getInstance("SHA256withRSA");
                break;
            case "EC":
                sign = Signature.getInstance("SHA256withECDSA");
                break;
            default: //caso o algoritmo pedido não exista
                throw new InvalidAlgorithmParameterException();
        }
        //inicializa a validação da assinatura com a chave
        sign.initVerify(key);
        //verifica se assinatura é valida para os dados dados e para assinatura dada
        sign.update(data);
        return sign.verify(signature);
    }

        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::::::::        ZIP /  UNZIP                                :::::::::::
    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    /**
     * Comprime um array de bytes utilizando o algoritmo GZIP
     *
     * @param data dados originais
     * @return dados comprimidos
     * @throws IOException
     */
    public static byte[] zip(byte[] data) throws IOException {
        //array de bytes em memória
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        // adaptador GZIP para comprimir bytes
        GZIPOutputStream zout = new GZIPOutputStream(bout);
        //escrever os dados no GZIP
        zout.write(data, 0, data.length);
        //terminar a escrita de dados
        zout.finish();
        //devolver os dados comprimidos
        return bout.toByteArray();
    }

    /**
     * Expande um array de dados comprimidos pelo algoritmo GZIP
     *
     * @param data dados comprimidos
     * @return dados originais
     * @throws IOException
     */
    public static byte[] unzip(byte[] data) throws IOException {
        //Stream com Array de bytes em memória
        ByteArrayInputStream bin = new ByteArrayInputStream(data);
        //Adaptador GZIP para descomprimir a stream
        GZIPInputStream zin = new GZIPInputStream(bin);
        //ler os bytes
        return zin.readAllBytes();
    }

    /**
     * Extensão utilizada para armazenar chaves privadas em ficheiros
     */
    public static String KEY_EXTENSION_FILE = "key";
    /**
     * Extensão utilizada para armazenar chaves privadas em ficheiros
     */
    public static String PRIVATE_KEY_EXTENSION_FILE = "privkey";
    /**
     * Extensão utilizada para armazenar chaves publicas em ficheiros
     */
    public static String PUBLIC_KEY_EXTENSION_FILE = "pubkey";

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    private static final long serialVersionUID = 202208301028L;
    //:::::::::::::::::::::::::::  Copyright(c) M@nso  2022  :::::::::::::::::::
    ///////////////////////////////////////////////////////////////////////////
}
