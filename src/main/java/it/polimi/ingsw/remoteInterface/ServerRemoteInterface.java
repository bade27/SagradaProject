package it.polimi.ingsw.remoteInterface;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerRemoteInterface extends Remote
{
    public void setClient (ClientRemoteInterface client) throws RemoteException;

    //game
    public String makeMove(Move move) throws RemoteException;
    public boolean askToolPermission(int nrTool) throws RemoteException;
    public String useTool(ToolMove move) throws RemoteException;
    public String passTurn() throws RemoteException;
    public String updateTokens(int n) throws ClientOutOfReachException, RemoteException;
}
