package it.polimi.ingsw.remoteInterface;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface ClientRemoteInterface extends Remote
{
    //Utilities
    /**
     * Ping methods for testing if client is alive
     * @return true if client respond, false otherwise
     */
    public boolean ping () throws RemoteException;

    /**
     * Send a message to client
     * @param s message
     * @return true if client respond, false otherwise
     */
    public boolean sendMessage (String s) throws ClientOutOfReachException,RemoteException;

    /**
     * Close communication to client from server
     * @param cause cause of closing
     * @return true if client respond, false otherwise
     */
    public boolean closeCommunication (String cause) throws ClientOutOfReachException, RemoteException;


    //Setup

    /**
     * Ask to client username
     * @return client's username
     */
    public String login() throws ClientOutOfReachException, RemoteException;

    /**
     * Ask to client which pattern card he want to use
     * @param s1 card number one
     * @param s2 card number two
     * @return pattern card selected
     */
    public String chooseWindow(String[] s1, String[] s2)  throws ClientOutOfReachException, RemoteException;

    /**
     * Send to client all information about his tools, public and private objectives
     * @param s card
     * @return true if client respond, false otherwise
     */
    public boolean sendCards(String[]... s) throws ClientOutOfReachException, RemoteException;

    //Game

    /**
     * Communicate to client about his turn
     * @return Client response
     */
    public String doTurn () throws ClientOutOfReachException,RemoteException;

    //Update
    public String updateGraphic(Pair[] dadiera) throws ClientOutOfReachException,RemoteException;
    public String updateGraphic(Pair[][] grid) throws ClientOutOfReachException,RemoteException;
    public String updateOpponents(String user,Pair[][] grid,boolean active) throws ClientOutOfReachException,RemoteException;
    public String updateTokens(int n) throws ClientOutOfReachException, RemoteException;
    public String updateRoundTrace(ArrayList<Pair>[] dice) throws RemoteException,ClientOutOfReachException;

    //End Game

    /**
     * Send result of match at the end of the match
     * @param u list of players
     * @param p list of points
     */
    public void sendResults (String[] u,int [] p) throws RemoteException,ClientOutOfReachException;

    //Reconnection
    public void reconnect() throws RemoteException;
    public String getName() throws RemoteException;
}
