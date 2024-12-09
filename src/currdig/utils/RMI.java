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
//::                                                               (c)2018   ::
//:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
//////////////////////////////////////////////////////////////////////////////
package currdig.utils;

import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Created on 24/nov/2018, 16:44:06
 *
 * @author zulu - computer
 */
public class RMI {

    /**
     * gets the RMI name of an remote object in the server
     *
     * @param port listen port
     * @param objectName name of object
     * @return remote RMI adress
     */
    public static String getRemoteName(int port, String objectName) throws UnknownHostException {
        //get adress of the localhost        
        return getRemoteName(InetAddress.getLocalHost().getHostAddress(), port, objectName);
    }

    /**
     * gets the RMI name of an remote object
     *
     * @param host name of remote host
     * @param port listen port
     * @param objectName name of object
     * @return remote RMI adress
     */
    public static String getRemoteName(String host, int port, String objectName) {
        //RMI format of names
        return String.format("//%s:%d/%s", host, port, objectName);
    }

    /**
     * Makes a remote object available on the server
     *
     * @param remote remote object
     * @param port port to receive calls
     * @param objectName name of the object
     * @throws java.rmi.RemoteException
     * @throws java.net.UnknownHostException
     * @throws java.net.MalformedURLException
     */
    public static void startRemoteObject(Remote remote, int port, String objectName)
            throws RemoteException, UnknownHostException, MalformedURLException {
        startRemoteObject(remote, getRemoteName(port, objectName));

    }

    /**
     * Makes a remote object available on the server
     *
     * @param remote remote object
     * @param address Adress of remote object
     * @throws java.rmi.RemoteException
     * @throws java.net.UnknownHostException
     * @throws java.net.MalformedURLException
     */
    public static void startRemoteObject(Remote remote, String address)
            throws RemoteException, UnknownHostException, MalformedURLException {
        //extract port from adress
        String port = address.substring(address.indexOf(":") + 1, address.lastIndexOf("/"));
        //create port registry
        LocateRegistry.createRegistry(Integer.parseInt(port));
        //Rebind remote to the adress
        Naming.rebind(address, remote);
        System.out.println("remote Object " + address + " avaiable.");
    }

    /**
     * gests a server name of a RMI adress
     *
     * @param address //server:port/object
     * @return server
     */
    public static String getAdressServer(String address) {
        return address.substring(address.indexOf("//")+3 , address.lastIndexOf(":"));
    }
    
        /**
     * gests a object name of a RMI adress
     *
     * @param address //server:port/object
     * @return object
     */
    public static String getAdressObjectName(String address) {
        return address.substring(address.lastIndexOf("/")+1, address.length());
    }
    
    /**
     * gests a port of a RMI adress
     *
     * @param address //server:port/object
     * @return port
     */
    public static int getAdressPort(String address) {
        return Integer.parseInt(address.substring(address.indexOf(":") + 1, address.lastIndexOf("/")));
    }


    /**
     * Gets a remote object
     *
     * @param host name of the host
     * @param port number of listen port
     * @param objectName name of the object
     * @return remote object
     * @throws java.rmi.NotBoundException
     * @throws java.net.MalformedURLException
     * @throws java.rmi.RemoteException
     */
    public static Remote getRemote(String host, int port, String objectName)
            throws NotBoundException, MalformedURLException, RemoteException {
        //gets remote refefence
        return Naming.lookup(getRemoteName(host, port, objectName));
    }
    
     /**
     * Gets a remote object
     *
     * @param host name of the host
     * @param port number of listen port
     * @param objectName name of the object
     * @return remote object
     * @throws java.rmi.NotBoundException
     * @throws java.net.MalformedURLException
     * @throws java.rmi.RemoteException
     */
    public static Remote getRemote(String address)
            throws NotBoundException, MalformedURLException, RemoteException {
        //gets remote refefence
        return Naming.lookup(address);
    }

    /**
     * Removes a remote object from server
     *
     * @param remote object
     * @param port port to receive calls
     * @param objectName name of the object
     * @throws java.rmi.RemoteException
     * @throws java.net.UnknownHostException
     */
    public static void stopRemoteObject(Remote remote, int port, String objectName)
            throws RemoteException, UnknownHostException {
        //address of the remote object 
        String address = getRemoteName(port, objectName);
        //remove object
        UnicastRemoteObject.unexportObject(remote, true);
        System.out.println("remote Object :" + address + " NOT avaiable ");
    }

    //::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
    private static final long serialVersionUID = 201512152207L;
    //:::::::::::::::::::::::::::  Copyright(c) M@nso  2018  :::::::::::::::::::
    ///////////////////////////////////////////////////////////////////////////
}
