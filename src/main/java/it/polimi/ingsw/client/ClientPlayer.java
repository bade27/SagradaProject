package it.polimi.ingsw.client;

import it.polimi.ingsw.remoteInterface.ClientRemoteInterface;
import it.polimi.ingsw.remoteInterface.ServerRemoteInterface;
import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.exceptions.ModelException;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class ClientPlayer extends UnicastRemoteObject implements ClientRemoteInterface
{
    private Graphic graph;
    ClientModelAdapter adp;

    int typeOfCOnnection; //1 rmi , 0 Socket

    private ServerRemoteInterface server;


    public ClientPlayer (int t,Graphic g) throws RemoteException
    {
        typeOfCOnnection = t;
        this.graph = g;
        adp = new ClientModelAdapter(graph);

        try
        {
            //if connection is socket, creates socket connect
            if (typeOfCOnnection == 0)
                server = new ClientSocketHandler(this);

            //if connection is RMI, creates RMI lookup of stub
            else if (typeOfCOnnection == 1)
            {
                int port = 7000;
                String address = "127.0.0.1";
                //String address = "192.168.1.3";
                String remote = "rmi://" + address + ":" + port;
                System.out.println("RMI connection to host " + address + " port " + port +  "...");
                String[] e = Naming.list(remote);

                /*for (int i = 0; i < e.length ; i++)
                    System.out.println(e[i]);*/

                String s = Collections.max(new ArrayList<>(Arrays.asList(e)));

                server=(ServerRemoteInterface) Naming.lookup(s);
                server.setClient(this);
            }

        }
        catch (ClientOutOfReachException e){
            System.out.println("Impossible to connect to Host with socket");
            //e.printStackTrace();
            return;
        }
        catch (RemoteException e){
            System.out.println("Impossible to connect to Host with RMI");
            return;
        }
        catch (Exception e){
            System.out.println("Impossible to connect to Host");
            return;
        }
        System.out.println("Client connected");
    }

    //<editor-fold desc="Setup Phase">
    /**
     * Request to user a username
     * @return username inserted
     */
    public String login() throws ClientOutOfReachException
    {
        System.out.println("Insert Username: ");
        Scanner cli = new Scanner(System.in);
        return cli.nextLine();
    }

    /**
     * Choosing window method
     */
    public String chooseWindow(ArrayList<String[]> list)
    {
        String choice = "";
        try {
            choice = chooseWindow(list.get(0),list.get(1));
        }
        catch (ClientOutOfReachException ex){
            return "";
        }
        return choice;
    }

    /**
     * Real Choosing window method
     */
    public String chooseWindow(String[] s1, String[] s2)  throws ClientOutOfReachException
    {
        try
        {
            adp.initializeWindow(s1[0]);
        }
        catch (ModelException ex){
            return "";
        }
        return s1[0];
    }

    @Override
    public void sendCards(String[]... s) throws ClientOutOfReachException, RuntimeException {

    }

    //</editor-fold>

    //<editor-fold desc="Utilities: Ping">
    /**
     * @return if we are alive
     */
    public boolean ping ()
    {
        return true;
    }

    public void sendMessage (String s)
    {
        System.out.println(s);
    }
    //</editor-fold>
}
