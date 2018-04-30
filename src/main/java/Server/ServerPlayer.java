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
        while (true)//Da cambiare con la condizione di fine partita
        {
            synchronized (token)
            {
                try
                {
                    /////Inizializzazione Partita

                    //Attende che il MatchHandler faccia partire la fase di inizializzazione
                    while (!token.getOnSetup())
                        token.wait();

                    //Inizializza il client
                    initializeWindow();

                    //Comunica che ha terminato la fase di inizializzazione
                    token.endSetup();
                    token.notifyAll();

                    /////Inizializzazione Partita


                    //Partita vera e propria

                    //Attende che è il prorpio turno di gioco
                    while (!token.isMyTurn(user))
                        token.wait();

                    //Comunica al client che è il suo turno e attende le mosse del client


                    token.notifyAll();
                }
                catch (InterruptedException ex)
                {
                    System.out.println(ex.getMessage());
                    ex.printStackTrace();
                }

            }
        }
    }

    public void initializeComunication ()
    {
        String u;
        com = new ServerConnectionHandler();

        do{
            u = com.login();
        } while (!possibleUsers.contains(u));
        possibleUsers.remove(u);
        user = u;
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
