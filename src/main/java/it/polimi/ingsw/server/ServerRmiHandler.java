package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.remoteInterface.*;
import it.polimi.ingsw.utilities.Wrapper;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ServerRmiHandler  extends UnicastRemoteObject implements ClientRemoteInterface,
        ServerRemoteInterface, ServerCommunicator
{
    private ServerModelAdapter adapter;
    private ClientRemoteInterface client;
    private MainServerApplication main;
    private MatchHandler match;


    public ServerRmiHandler (MainServerApplication m) throws RemoteException
    {
        main = m;
    }


    //<editor-fold desc="From server to client">
    @Override
    public String login() throws ClientOutOfReachException, RemoteException {
        return client.login();
    }

    @Override
    public String chooseWindow(String[] s1, String[] s2) throws ClientOutOfReachException, RemoteException {
        return client.chooseWindow(s1,s2);
    }

    @Override
    public boolean sendCards(String[]... s) throws ClientOutOfReachException, RemoteException {
        return client.sendCards(s);
    }

    @Override
    public boolean ping() throws RemoteException {
        return client.ping();
    }

    @Override
    public boolean sendMessage(String s) throws ClientOutOfReachException, RemoteException {
        return client.sendMessage(s);
    }

    @Override
    public boolean closeCommunication(String cause) throws ClientOutOfReachException, RemoteException {
        return client.closeCommunication(cause);
    }

    @Override
    public String doTurn() throws ClientOutOfReachException, RemoteException {
        return client.doTurn();
    }

    @Override
    public String updateGraphic(Pair[] dadiera) throws ClientOutOfReachException, RemoteException {
        return client.updateGraphic(dadiera);
    }

    @Override
    public String updateGraphic(Pair[][] grid) throws ClientOutOfReachException, RemoteException {
        return client.updateGraphic(grid);
    }

    @Override
    public String updateTokens(int n) throws ClientOutOfReachException, RemoteException {
        return client.updateTokens(n);
    }

    @Override
    public String updateRoundTrace(ArrayList<Pair>[] dice) throws RemoteException,ClientOutOfReachException {
        return client.updateRoundTrace(dice);
    }

    @Override
    public void sendResults(String[] u, int[] p) throws RemoteException,ClientOutOfReachException {
        client.sendResults(u,p);
    }

    @Override
    public String updateOpponents(String user,Pair[][] grid,boolean active) throws ClientOutOfReachException, RemoteException {
        return client.updateOpponents(user,grid,active);
    }
    //</editor-fold>


    //<editor-fold desc="From client to server">
    @Override
    public void setClient(ClientRemoteInterface client) throws RemoteException
    {
        this.client = client;
        main.setClient(this);
    }


    /**
     * allows the client to make a move
     * @param coor position whre to place die
     * @param p couple color and value of die to place
     * @returna message saying weather the move is valid or not
     * @throws RemoteException if the server is unreachable
     */
    @Override
    public String makeMove(Coordinates coor, Pair p) throws RemoteException {
        try {
            if (!adapter.CanMove())
                return "Hai già mosso in questo turno";
            adapter.addDiceToBoard(coor.getI(), coor.getJ(), new Dice(p.getValue(), p.getColor()));

            match.updateClient();

        } catch (ModelException e) {
            return e.getMessage();
        }

        return "Mossa applicata correttamente";
    }


    @Override
    public String askToolPermission(int nrTool) throws RemoteException {
        return adapter.toolRequest(nrTool);
    }

    /**
     * Overload about tool function
     * @param p pair of value and color
     * @param instruction increment (inc) or decrement (dec) the die
     * @return result of tool
     */
    @Override
    public String useTool(Pair p, String instruction) throws RemoteException
    {
        String ret = adapter.useTool(new Wrapper(p), new Wrapper(instruction));
        match.updateClient();
        return ret;
    }

    /**
     * Overload about tool function
     * @param startCoord start coordinates
     * @param endCoord end coordinates
     * @return result of tool
     */
    @Override
    public String useTool(Coordinates startCoord, Coordinates endCoord) throws RemoteException
    {
        String ret = adapter.useTool(new Wrapper(startCoord), new Wrapper(endCoord));
        match.updateClient();
        return ret;
    }

    /**
     * Overload about tool function
     * @param startCoord1 start coordinates one
     * @param endCoord1 end coordinates one
     * ......
     * @return result of tool
     */
    @Override
    public String useTool(Pair p ,Coordinates startCoord1, Coordinates endCoord1, Coordinates startCoord2, Coordinates endCoord2) throws RemoteException
    {
        String ret = adapter.useTool(new Wrapper(p),new Wrapper(startCoord1),new Wrapper(endCoord1) , new Wrapper(startCoord2) , new Wrapper(endCoord2));
        match.updateClient();
        return ret;
    }

    @Override
    public String useTool(Pair dadieraP, Pair traceP, int nrRound) throws RemoteException {
        String ret = adapter.useTool(new Wrapper(dadieraP),new Wrapper(traceP),new Wrapper(nrRound));
        match.updateClient();
        return ret;
    }

    @Override
    public String useTool() throws RemoteException {
        String ret = adapter.useTool();
        match.updateClient();
        return ret;
    }

    @Override
    public String useTool(Pair p, Coordinates endCoord) throws RemoteException {
        String ret = adapter.useTool(new Wrapper(p),new Wrapper(endCoord));
        match.updateClient();
        return ret;
    }

    /**
     * if the client doesn't want to make a move, he can pass the turn using this method
     * @return a string saying the turn is passed
     * @throws RemoteException if the server is unreachable
     */
    @Override
    public String passTurn() throws RemoteException {
        notifyServer();
        return "Turno concluso";
    }

    //</editor-fold>


    //<editor-fold desc="Utilities">
    /**
     * Notifies server the end of current turn
     */
    private void notifyServer ()
    {
        try{

            adapter.setTurnDone(true);

        }catch (Exception e){
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void reconnect() throws RemoteException {
        client.reconnect();
    }

    @Override
    public void disconnection() throws RemoteException {
        adapter.notifyClientExited();
    }

    @Override
    public void setAdapter(ServerModelAdapter sma) {
        this.adapter = sma;
    }

    @Override
    public void setMatchHandler(MatchHandler match) {

        this.match = match;
    }

    @Override
    public String getName() throws RemoteException {
        return client.getName();
    }
    //</editor-fold>


    @Override
    public void close() {
        //unused
    }
}
