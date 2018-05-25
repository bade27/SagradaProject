package it.polimi.ingsw.remoteInterface;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerRemoteInterface extends Remote
{
    public void setClient (ClientRemoteInterface client) throws RemoteException;
    public void responseTurn (String s) throws RemoteException;

    //game
    public boolean makeMove(Pair p, int i, int j) throws RemoteException;
}
