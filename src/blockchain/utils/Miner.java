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
 * This class implements a Miner that works to find a valid nonce for a given
 * message. It runs multiple threads to perform the mining process in parallel.
 */
public class Miner {

    private P2Plistener listener;            // Listener for mining events
    private MinerThread[] threads;           // Array of miner threads
    private String message;                  // Message to be mined
    private AtomicInteger globalNonce;       // Shared nonce across threads

    /**
     * Constructor to initialize the Miner with a listener for mining events.
     *
     * @param listener The listener that will receive mining updates.
     */
    public Miner(P2Plistener listener) {
        this.listener = listener;
    }

    /**
     * Starts mining a message by finding a nonce that produces a hash with the
     * specified number of leading zeros.
     *
     * @param message The message to mine.
     * @param zeros The number of leading zeros required in the hash.
     * @throws Exception If an error occurs during the mining process.
     */
    public void startMining(String message, int zeros) throws Exception {
        if (isMining()) {
            return; // Exit if already mining
        }
        this.message = message;

        int numCores = 4; // Use 4 cores for mining
        threads = new MinerThread[numCores];
        globalNonce = new AtomicInteger();

        // Start mining threads
        for (int i = 0; i < numCores; i++) {
            threads[i] = new MinerThread(globalNonce, message, zeros);
            threads[i].start();
        }

        // Notify the listener that mining has started
        if (listener != null) {
            listener.onStartMining("Start Mining with " + numCores + " cores", zeros);
        }
    }

    /**
     * Stops the mining process and updates the global nonce.
     *
     * @param nonce The nonce to be set, greater than zero.
     */
    public void stopMining(int nonce) {
        globalNonce.set(nonce);
        if (listener != null) {
            listener.onStopMining("Stop Mining from " + Thread.currentThread().getName(), nonce);
        }

        // Interrupt all mining threads
        if (threads != null) {
            for (MinerThread thread : threads) {
                thread.interrupt();
            }
            threads = null;
        }
    }

    /**
     * Checks if the miner is currently mining.
     *
     * @return true if mining is in progress, false otherwise.
     */
    public boolean isMining() {
        return threads != null && globalNonce != null && globalNonce.get() <= 0;
    }

    /**
     * Gets the current nonce.
     *
     * @return The current nonce, or zero if mining hasn't finished.
     */
    public int getNonce() {
        return globalNonce.get();
    }

    /**
     * Gets the message that is being mined.
     *
     * @return The message being mined.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Converts the mining time in milliseconds to a formatted string.
     *
     * @param miningTime The mining time in milliseconds.
     * @return The formatted time string.
     */
    public static String getMiningTimeText(long miningTime) {
        return df.format(new Date(miningTime));
    }

    private static final SimpleDateFormat df = new SimpleDateFormat("mm:ss.SSSS");

    /**
     * Waits for the mining threads to finish and returns the nonce.
     *
     * @return The mined nonce.
     * @throws InterruptedException If the current thread is interrupted.
     */
    public int waitToNonce() throws InterruptedException {
        for (MinerThread thread : threads) {
            thread.join();
        }
        return globalNonce.get();
    }

    /**
     * Mines a message by finding a valid nonce with the required number of
     * leading zeros.
     *
     * @param message The message to mine.
     * @param zeros The number of leading zeros required.
     * @return The valid nonce.
     * @throws Exception If an error occurs during mining.
     */
    public int mine(String message, int zeros) throws Exception {
        startMining(message, zeros);
        return waitToNonce();
    }

    // :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    // :::::::::      I N T E G R I T Y       ::::::::::::::::::::::::::::::::::::::: 
    /////////////////////////////////////////////////////////////////////////////
    public static String hashAlgorithm = "SHA3-256";

    /**
     * Calculates the hash of a message concatenated with the nonce.
     *
     * @param data The message.
     * @param nonce The nonce.
     * @return The Base64 encoded hash of the message and nonce.
     */
    public static String getHash(String data, int nonce) {
        try {
            return getHash(data + nonce);
        } catch (Exception ex) {
            return ex.getMessage();
        }
    }

    /**
     * Calculates the hash of a message.
     *
     * @param data The message to hash.
     * @return The Base64 encoded hash of the message.
     * @throws Exception If an error occurs during hashing.
     */
    public static String getHash(String data) throws Exception {
        MessageDigest md = MessageDigest.getInstance(hashAlgorithm);
        return Base64.getEncoder().encodeToString(md.digest(data.getBytes()));
    }

    /**
     * A thread that performs mining operations, searching for a valid nonce.
     */
    private class MinerThread extends Thread {

        private final AtomicInteger sharedNonce;  // Shared nonce across threads
        private final String message;             // Message to mine
        private final int zeros;                  // Required leading zeros in hash
        private final MessageDigest hasher;       // Hashing algorithm used in the thread

        /**
         * Initializes a new mining thread.
         *
         * @param globalNonce Shared global nonce.
         * @param message The message to mine.
         * @param zeros The number of leading zeros required in the hash.
         * @throws NoSuchAlgorithmException If the hash algorithm is invalid.
         */
        public MinerThread(AtomicInteger globalNonce, String message, int zeros) throws NoSuchAlgorithmException {
            this.sharedNonce = globalNonce;
            this.message = message;
            this.zeros = zeros;
            this.hasher = MessageDigest.getInstance(hashAlgorithm);
        }

        @Override
        public void run() {
            try {
                // Notify listener that the thread is starting
                if (listener != null) {
                    listener.onStartMining("RUN " + Thread.currentThread().getName(), zeros);
                }

                // Create a prefix for the hash comparison (e.g., "0000" for 4 leading zeros)
                String prefix = String.format("%0" + zeros + "d", 0);

                // Keep searching for the correct nonce until found
                while (sharedNonce.get() <= 0) {
                    // Generate a random nonce and test it
                    int number = Math.abs(ThreadLocalRandom.current().nextInt());

                    if (listener != null && number % 368 == 0) {
                        listener.onException(new Exception(number + ""), "number");
                    }

                    // Check if the hash starts with the required number of zeros
                    if (getThreadHash(message, number).startsWith(prefix)) {
                        // Update the shared nonce and notify listeners
                        sharedNonce.set(number);

                        if (listener != null) {
                            listener.onException(new Exception(number + ""), "nonce");
                            listener.onNonceFound(Thread.currentThread().getName(), number);
                        }
                    }
                }

                // Notify listener that the thread has stopped
                if (listener != null) {
                    listener.onStopMining(Thread.currentThread().getName(), sharedNonce.get());
                }
            } catch (Exception ex) {
                // Handle errors during mining
                if (listener != null) {
                    listener.onStopMining("ERROR: " + ex.getMessage(), -1);
                }
            }
        }

        /**
         * Calculates the hash of a message concatenated with the nonce.
         *
         * @param message The message to hash.
         * @param nonce The nonce to include in the hash.
         * @return The Base64 encoded hash of the message and nonce.
         * @throws Exception If an error occurs during hashing.
         */
        public String getThreadHash(String message, int nonce) throws Exception {
            return Base64.getEncoder().encodeToString(hasher.digest((message + nonce).getBytes()));
        }
    }

    private static final long serialVersionUID = 202111021828L;
}
