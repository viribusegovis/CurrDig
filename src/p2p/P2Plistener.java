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
 * Created on 27/11/2024, 19:46:43
 *
 * @author manso - computer
 */
public interface P2Plistener {

    public void onStart(String message);

    public void onConnect(String address);

    public void onDisconnect(String address);

    public void onTransaction(String transaction);

    public void onException(Exception e, String title);

    public void onMessage(String title, String message);

    public void onStartRemote(String message);

    public void onStartMining(String message, int zeros);

    public void onStopMining(String message, int nonce);

    public void onNonceFound(String message, int nonce);

    public void onBlockchainUpdate(BlockChain b);

}
