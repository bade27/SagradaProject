package Server;

import Exceptions.ModelException;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class ServerPlayer implements Runnable
{
    private ServerConnectionHandler com;
    private TokenTurn token;
    private ServerModelAdapter adapter;
    private String user;

    private ArrayList<String> possibleUsers;

    private String[] windowCard1,windowCard2,publicObjCard;
    private String privateObjCard;

    public ServerPlayer(TokenTurn tok, ServerModelAdapter adp, ArrayList ps)
    {
        adapter = adp;
        token = tok;
        possibleUsers = ps;
    }



    public synchronized void run ()
    {
        //Setup Phase
        synchronized (token)
        {
            try
            {
                //Wait until matchHandler signal start setup
                while (!token.getOnSetup())
                    token.wait();

                //Initialization of client
                initializeWindow();

                //End Setup phase comunication
                token.endSetup();
                token.notifyAll();
            }
            catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }

        //Game Phase
        while (true)//Da cambiare con la condizione di fine partita
        {
            synchronized (token)
            {
                try
                {
                    //Wait his turn
                    while (!token.isMyTurn(user))
                        token.wait();

                    //Simulazione del turno
                    System.out.println(">>>Turn of:" + user);
                    Thread.sleep(2000);

                    //End turn comunication
                    token.notifyAll();
                    token.wait();
                }
                catch (InterruptedException ex)
                {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                }

            }
        }
    }

    public boolean initializeComunication ()
    {
        String u;
        com = new ServerConnectionHandler();

        do{
            u = com.login();
        } while (!possibleUsers.contains(u));
        possibleUsers.remove(u);
        user = u;
        return true;
    }

    private void initializeWindow ()
    {
        String s1 = com.chooseWindow(windowCard1,windowCard2);
        try {
            adapter.initializeWindow(s1);
            System.out.println(">>>Window initialized: " + s1);
        }
        catch (ModelException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private void initializePrivateObjectives (String card)
    {
        //Comunicazione col client per la sua carta obbiettivo privato
    }

    private void initializePublicObjectives (String[] cards)
    {
        //Comunicazione col client per le carte obbiettivo pubblico
    }

    public void closeComunication ()
    {
        //com.close ();
    }


    public void setWindowCards (String c1[],String c2 [])
    {
        windowCard1 = c1;
        windowCard2 = c2;
    }


    public String getUser() {
        return user;
    }

    public void setPublicObjCard (String[] c)
    {
        publicObjCard = c;
    }

    public void setPrivateObjCard (String c)
    {
        privateObjCard = c;
    }

}
