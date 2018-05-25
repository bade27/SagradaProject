package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.remoteInterface.ClientRemoteInterface;
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


    //<editor-fold desc="Client Remote">
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

    //</editor-fold>

    @Override
    public void setClient(ClientRemoteInterface client) throws RemoteException
    {
        this.client = client;
        player.setCommunicator(this);
    }

    public void responseTurn (String s)
    {
        adapter.testMove(s);
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

    @Override
    public void updateGraphic(Pair[] dadiera) throws ClientOutOfReachException, RemoteException {

    }

    @Override
    public void updateGraphic(Pair[][] grid) throws ClientOutOfReachException, RemoteException {

    }

    @Override
    public boolean makeMove(Pair p, int i, int j) throws RemoteException {
        return false;
    }
}
