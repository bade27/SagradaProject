package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.remoteInterface.ClientRemoteInterface;
import it.polimi.ingsw.remoteInterface.Move;
import it.polimi.ingsw.remoteInterface.Pair;
import it.polimi.ingsw.remoteInterface.ServerRemoteInterface;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ServerRmiHandler  extends UnicastRemoteObject implements ClientRemoteInterface,ServerRemoteInterface
{
    private ServerModelAdapter adapter;
    private ClientRemoteInterface client;
    private ServerPlayer player;

    public ServerRmiHandler (ServerModelAdapter adp,ServerPlayer pl) throws RemoteException
    {
        adapter = adp;
        player = pl;
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
    //</editor-fold>



    //<editor-fold desc="From client to server">
    @Override
    public void setClient(ClientRemoteInterface client) throws RemoteException
    {
        this.client = client;
        player.setCommunicator(this);
    }

    @Override
    public void responseTurn (String s)
    {
        adapter.testMove(s);
        notifyServer();
    }


    @Override
    public boolean makeMove(Move move) throws RemoteException {
        /*adapter.testMove("moved die" + move.getP().getValue() + ", " + move.getP().getColor().toString()
                + " in position " + move.getI() + " ," + move.getJ());*/
        try {
            adapter.addDiceToBoard(move.getI(),move.getJ(),new Dice(move.getP().getValue(),move.getP().getColor()));
        }
        catch (ModelException e) {
            notifyServer();
            return false;
        }
        notifyServer();
        return true;
    }
    //</editor-fold>


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
