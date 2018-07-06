package it.polimi.ingsw.remoteInterface;

import it.polimi.ingsw.server.ServerModelAdapter;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServerRemoteInterface extends Remote
{
    //Connection Phase

    /**
     * Set interface on server (used in rmi to communicate to server client's stub)
     * @param client client interface
     */
    public void setClient (ClientRemoteInterface client) throws RemoteException;

    //Game Phase

    /**
     * Communicate to server user's move
     * @param coord coordinates about place
     * @param pair die placed
     * @return Result of move
     */
    public String makeMove(Coordinates coord, Pair pair) throws RemoteException;

    /**
     * Ask to server if client's can use tool passed
     * @param nrTool number of tool that client's want to use
     * @return result of asking
     */
    public String askToolPermission(int nrTool) throws RemoteException;

    /**
     * Communicate to server that user want to pass turn
     * @return server response
     */
    public String passTurn() throws RemoteException;


    //Many overloads about using tool, because every tool use different resources
    public String useTool (Pair p, String instruction) throws RemoteException;
    public String useTool (Coordinates sartCoord,Coordinates endCoord) throws RemoteException;
    public String useTool (Pair p ,Coordinates sartCoord1,Coordinates endCoord1, Coordinates sartCoord2,Coordinates endCoord2) throws RemoteException;
    public String useTool (Pair dadiera,Pair trace,int nrRound) throws RemoteException;
    public String useTool () throws RemoteException;
    public String useTool (Pair p , Coordinates endCoord) throws RemoteException;

    /**
     * Communicate to server that client has closed window
     */
    public void disconnection () throws  RemoteException;
}
