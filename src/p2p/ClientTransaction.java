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

package p2p;

import java.net.InetAddress;
import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import currdig.utils.RMI;

/**
 * Created on 27/11/2024, 18:04:02 
 * @author manso - computer
 */
public class ClientTransaction {
    static int port = 10_012;
    static String name = "P2P";

            
    public static void main(String[] args) throws Exception {
       //create object  to listen in the remote port
            //node 10
        IremoteP2P node = (IremoteP2P) RMI.getRemote("//10.10.208.35:10010/P2P");
        
        node.addTransaction("mestrado do xxxx pelo IPT");
        
////        
       
        
        
    }

}
