package it.polimi.ingsw.remoteInterface;

import it.polimi.ingsw.server.ServerModelAdapter;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerRemoteInterface extends Remote
{
    //Connection Phase
    public void setClient (ClientRemoteInterface client) throws RemoteException;

    //Game Phase
    public String makeMove(Coordinates coord, Pair pair) throws RemoteException;
    public String askToolPermission(int nrTool) throws RemoteException;
    public String passTurn() throws RemoteException;


    //Tool nr. 1-6-7-10-11
    public String useTool (Pair p, String instruction) throws RemoteException;
    //Tool nr. 2-3
    public String useTool (Coordinates sartCoord,Coordinates endCoord) throws RemoteException;
    //Tool nr.4
    public String useTool (Pair p ,Coordinates sartCoord1,Coordinates endCoord1, Coordinates sartCoord2,Coordinates endCoord2) throws RemoteException;
    //Tool nr.5
    public String useTool (Pair dadiera,Pair trace,int nrRound) throws RemoteException;
    //Tool nr.8
    public String useTool () throws RemoteException;
    //Tool nr.9
    public String useTool (Pair p , Coordinates endCoord) throws RemoteException;

    //Disconnection message
    public void disconnection () throws  RemoteException;
}
