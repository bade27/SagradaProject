package it.polimi.ingsw.remoteInterface;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientRemoteInterface extends Remote
{
    public String login() throws ClientOutOfReachException, RemoteException;
    public boolean ping () throws RemoteException;
    public String chooseWindow(String[] s1, String[] s2)  throws ClientOutOfReachException, RemoteException;
    public boolean sendCards(String[]... s) throws ClientOutOfReachException, RemoteException;
    public boolean sendMessage (String s) throws ClientOutOfReachException,RemoteException;

    public boolean closeComunication (String cause) throws  RemoteException,ClientOutOfReachException;

    //public boolean sendPrivateObjective(String privObj);
}
