package it.polimi.ingsw.remoteInterface;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ClientRemoteInterface extends Remote
{
    //Utilities
    public boolean ping () throws RemoteException;
    public boolean sendMessage (String s) throws ClientOutOfReachException,RemoteException;
    public boolean closeCommunication (String cause) throws ClientOutOfReachException, RemoteException;

    //Setup
    public String login() throws ClientOutOfReachException, RemoteException;
    public String chooseWindow(String[] s1, String[] s2)  throws ClientOutOfReachException, RemoteException;
    public boolean sendCards(String[]... s) throws ClientOutOfReachException, RemoteException;

    //Game
    public String doTurn () throws ClientOutOfReachException,RemoteException;

    //Game updates
    public String updateGraphic(Pair[] dadiera) throws ClientOutOfReachException,RemoteException;
    public String updateGraphic(Pair[][] grid) throws ClientOutOfReachException,RemoteException;
    public String updateOpponents(String user,Pair[][] grid,boolean active) throws ClientOutOfReachException,RemoteException;
    public String updateTokens(int n) throws ClientOutOfReachException, RemoteException;
    public String updateRoundTrace(ArrayList<Pair>[] dice) throws RemoteException,ClientOutOfReachException;

    //End Game
    public void sendResults (String[] u,int [] p) throws RemoteException,ClientOutOfReachException;

    //Reconnection
    public void reconnect() throws RemoteException;
    public String getName() throws RemoteException;
}
