//::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: 
//::                                                                         ::
//::     Antonio Manuel Rodrigues Manso                                      ::
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
//::                                                               (c)2024   ::
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//////////////////////////////////////////////////////////////////////////////
package p2p;

import blockchain.utils.BlockChain;

/**
 * Interface to listen for events related to the Peer-to-Peer (P2P) network.
 * This interface defines methods for responding to network events such as
 * connections, transactions, mining activities, and blockchain updates.
 */
public interface P2Plistener {

    /**
     * Called when the network starts with a given message.
     *
     * @param message A message containing information about the start event.
     */
    public void onStart(String message);

    /**
     * Called when a node successfully connects to another node in the network.
     *
     * @param address The address of the node that was connected.
     */
    public void onConnect(String address);

    /**
     * Called when a node disconnects from the network.
     *
     * @param address The address of the node that was disconnected.
     */
    public void onDisconnect(String address);

    /**
     * Called when a new transaction is broadcasted to the network.
     *
     * @param transaction The transaction details.
     */
    public void onTransaction(String transaction);

    /**
     * Called when an exception occurs during network communication.
     *
     * @param e The exception that occurred.
     * @param title A title or description of the exception.
     */
    public void onException(Exception e, String title);

    /**
     * Called when a message is received in the network.
     *
     * @param title The title or subject of the message.
     * @param message The content of the message.
     */
    public void onMessage(String title, String message);

    /**
     * Called when the remote system starts with a given message.
     *
     * @param message A message containing information about the remote system's
     * start.
     */
    public void onStartRemote(String message);

    /**
     * Called when mining starts with a given message and number of zeros for
     * the hash.
     *
     * @param message A message describing the mining event.
     * @param zeros The number of leading zeros required in the mined hash.
     */
    public void onStartMining(String message, int zeros);

    /**
     * Called when mining stops with the resulting nonce.
     *
     * @param message A message describing the mining event.
     * @param nonce The nonce value found during the mining process.
     */
    public void onStopMining(String message, int nonce);

    /**
     * Called when a valid nonce is found during mining.
     *
     * @param message A message describing the nonce found.
     * @param nonce The nonce value that was found.
     */
    public void onNonceFound(String message, int nonce);

    /**
     * Called when the blockchain is updated.
     *
     * @param b The updated blockchain.
     */
    public void onBlockchainUpdate(BlockChain b);
}
