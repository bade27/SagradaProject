package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.remoteInterface.*;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class ServerRmiHandler  extends UnicastRemoteObject implements ClientRemoteInterface,ServerRemoteInterface
{
    private ServerModelAdapter adapter;
    private ClientRemoteInterface client;
    private MatchHandler match;


    public ServerRmiHandler (MatchHandler m) throws RemoteException
    {
        match = m;
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
    public String updateRoundTrace(ArrayList<Pair>[] dice) throws RemoteException {
        return client.updateRoundTrace(dice);
    }

    @Override
    public void updateOpponents(String user,Pair[][] grid) throws ClientOutOfReachException, RemoteException {
        client.updateOpponents(user,grid);
    }
    //</editor-fold>


    //<editor-fold desc="From client to server">
    @Override
    public void setClient(ClientRemoteInterface client) throws RemoteException
    {
        this.client = client;
        adapter = match.setClient(this);
    }


    /**
     * allows the client to make a move
     * @param move the move sent by the client
     * @return a message saying weather the move is valid or not
     * @throws RemoteException if the server is unreachable
     */
    @Override
    public String makeMove(Move move) throws RemoteException {
        try {
            if (!adapter.CanMove())
                return "You have already moved on this turn";
            adapter.addDiceToBoard(move.getI(), move.getJ(), new Dice(move.getP().getValue(), move.getP().getColor()));

            match.updateClient();

        } catch (ModelException e) {
            return e.getMessage();
        }

        return "Move ok";
    }


    @Override
    public String askToolPermission(int nrTool) throws RemoteException {
        return adapter.toolRequest(nrTool);
    }

    @Override
    public String useTool(ToolMove toolMove) throws RemoteException {
        System.out.println(toolMove.toString());//temporaneo per il debug
        String ret = adapter.useTool(toolMove);
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
        return "Turn passed";
    }

    //</editor-fold>


    /**
     * Notify server the end of current turn
     */
    private void notifyServer ()
    {
        try{
            synchronized (adapter) {
                adapter.notifyAll();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
            //log.addLog("Fatal error on thread " + user  , e.getStackTrace());
            //token.notifyFatalError();
            Thread.currentThread().interrupt();
        }
    }
}
