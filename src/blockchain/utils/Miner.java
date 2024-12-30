//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: 
//::                                                                         ::
//::     Antonio Manuel Rodrigues Manso                                      ::
//::                                                                         ::
//::     Biosystems & Integrative Sciences Institute                         ::
//::     Faculty of Sciences University of Lisboa                            ::
//::     http://www.fc.ul.pt/en/unidade/bioisi                               ::
//::                                                                         ::
//::                                                                         ::
//::     I N S T I T U T O    P O L I T E C N I C O   D E   T O M A R        ::
//::     Escola Superior de Tecnologia de Tomar                              ::
//::     e-mail: manso@ipt.pt                                                ::
//::     url   : http://orion.ipt.pt/~manso                                  ::
//::                                                                         ::
//::     This software was build with the purpose of investigate and         ::
//::     learning.                                                           ::
//::                                                                         ::
//::                                                               (c)2021   ::
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//////////////////////////////////////////////////////////////////////////////
package blockchain.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import p2p.P2Plistener;

/**
 * Created on 02/11/2021, 18:28:06 Updated on 07/12/2021
 *
 * @author IPT - computer
 */
public class Miner {

    //atributos 
    P2Plistener listener;            // Listener dos mineiros
    private MinerThread[] threads;      // Threads de calculo de hashs
    private String message;             //  Mensagem a ser minada 
    private AtomicInteger globalNonce;  // Nonce que valida a mensagem

    public Miner(P2Plistener listener) {
        this.listener = listener;
    }

    /**
     * inicia a mineração de uma mensagem
     *
     * @param message mensagem
     * @param zeros número de zeros do hash
     * @throws Exception
     */
    public void startMining(String message, int zeros) throws Exception {
        //está a minar
        if (isMining()) {
            return; // Sair
        }
        this.message = message;
        //configurar os atributos    
        int numCores = 4;
        //int numCores = Runtime.getRuntime().availableProcessors();
        threads = new MinerThread[numCores];
        //inicializar o globalNonce
        globalNonce = new AtomicInteger();

        //executar as threads
        for (int i = 0; i < numCores; i++) {
            threads[i] = new MinerThread(globalNonce, message, zeros);
            threads[i].start();
        }
        //notificar o listener
        if (listener != null) {
            listener.onStartMining("Start Mining " + numCores + " cores", zeros);
        }

    }

    /**
     * Terminar a mineração
     *
     * @param nonce numero maior que zero
     */
    public void stopMining(int nonce) {
        //atualizar o nonce
        globalNonce.set(nonce);
        if (listener != null) {
            listener.onStopMining("Stop Mining" + Thread.currentThread().getName(), nonce);
        }
        //abortar as threads
        if (threads != null) {
            for (MinerThread thread : threads) {
                thread.interrupt();
            }
            threads = null;
        }

    }

    /**
     * Verificar se está a minerar
     *
     * @return está a minerar
     */
    public boolean isMining() {
        return threads != null && globalNonce != null && globalNonce.get() <= 0;
    }

    /**
     * Devolve o resultado da mineração ou zero
     *
     * @return nonce
     */
    public int getNonce() {
        return globalNonce.get();
    }

    /**
     * mensagem
     *
     * @return message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Tempo de minagem
     *
     * @param miningTime
     * @return message
     */
    public static String getMiningTimeText(long miningTime) {
        return df.format(new Date(miningTime));
    }
    private static final SimpleDateFormat df = new SimpleDateFormat("mm:ss.SSSS");

    /**
     * Devolve o resultado da mineração ou zero
     *
     * @return nonce
     * @throws java.lang.InterruptedException
     */
    public int waitToNonce() throws InterruptedException {
        for (MinerThread thread : threads) {
            thread.join();
        }
        return globalNonce.get();
    }

    /**
     * Calcula o valor do nonce da mensagem
     *
     * @param message mensagem
     * @param zeros número de zeros
     * @return
     * @throws Exception
     */
    public int mine(String message, int zeros) throws Exception {
        startMining(message, zeros);
        return waitToNonce();
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    //:::::::::      I N T E G R I T Y         :::::::::::::::::::::::::::::::::    
    ///////////////////////////////////////////////////////////////////////////
    public static String hashAlgorithm = "SHA3-256";

    /**
     * calcula a hash da mensagem com o nonce em Base64
     *
     * @param data dados
     * @param nonce nonce
     * @return hash(mensagem + nonce)
     */
    public static String getHash(String data, int nonce) {
        try {
            return getHash(data + nonce);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    /**
     * calcula a hash da mensagem em Base64
     *
     * @param data mensagem
     * @return Base64(hash(data))
     * @throws Exception
     */
    public static String getHash(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
        return Base64.getEncoder().encodeToString(md.digest(data.getBytes()));
    }

    private class MinerThread extends Thread {

        //atributos da thread        
        private final AtomicInteger sharedNonce;  // referência para o global nonce
        private final String message;             // mensagem do bloco
        private final int zeros;                  // número de zeros
        private final MessageDigest hasher;       // calculador de  hashs da thread

        /**
         * Thread para minar uma mensagem
         *
         * @param globalNonce objeto partilhado com o nonce global
         * @param ticket objeto partilhado do sistema de tickets
         * @param message mensagem a minar
         * @param zeros número de zeros do hash
         * @param listener listenar do mineiro
         * @throws NoSuchAlgorithmException
         */
        public MinerThread(AtomicInteger globalNonce, String message, int zeros) throws NoSuchAlgorithmException {
            this.sharedNonce = globalNonce;
            this.message = message;
            this.zeros = zeros;

            //criar um objeto para a thread calcular hashs
            this.hasher = MessageDigest.getInstance(hashAlgorithm);
        }

        @Override
        public void run() {
            try {
                //notificar o listener
                if (listener != null) {
                    listener.onStartMining("RUN " + Thread.currentThread().getName(), zeros);
                }
                //Zeros no inicio do hash
                String prefix = String.format("%0" + zeros + "d", 0);
                //enquanto não for encontrado o nonce ( nonce <= 0 )
                while (sharedNonce.get() <= 0) {
                    //gerar uma numero e testá-lo
                    int number = Math.abs(ThreadLocalRandom.current().nextInt());
                    if (listener != null && number % 368 == 0) {
                        listener.onException(new Exception(number + ""), "number");
                    }

                    //verificar se o hash esta correto
                    if (getThreadHash(message, number).startsWith(prefix)) {
                        //atualizar o nonce e terminar as threads
                        sharedNonce.set(number);
                        //notifificar os listeners
                        if (listener != null) {
                            listener.onException(new Exception(number + ""), "nonce");
                            listener.onNonceFound(Thread.currentThread().getName(), number);
                        }
                    }
                }
                //notificar os listeners que a thread terminou
                if (listener != null) {
                    //nome da thread e o nonce

                    listener.onStopMining(Thread.currentThread().getName(), sharedNonce.get());
                }
            } catch (Exception ex) {
                //alguma coisa deu errado  
                //notificar os listeners a cada 9973 numeros
                if (listener != null) {
                    listener.onStopMining("ERROR " + ex.getMessage(), -1);
                }
            }
        }

        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        /**
         * calcula a hash da mensagem com o nonce em Base64
         *
         * @param message mensagem
         * @param nonce nonce
         * @return hash(mensagem + nonce)
         * @throws Exception
         */
        public String getThreadHash(String message, int nonce) throws Exception {
            return Base64.getEncoder().encodeToString(hasher.digest((message + nonce).getBytes()));
        }
        //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
        private static final long serialVersionUID = 202111021828L;
        //:::::::::::::::::::::::::::  Copyright(c) M@nso  2021  :::::::::::::::::::
        ///////////////////////////////////////////////////////////////////////////
    }

}
