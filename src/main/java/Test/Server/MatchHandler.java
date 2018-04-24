package Test.Server;
import Test.Model.ParserXML;
import Test.Exceptions.ParserXMLException;

import java.net.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Cose da aggiungere:
 *  -la funzione run() che gestisce la partita
 *  -l'oggetto comunicator che comunica col client da passare al serverPlayer
 *  -completare l'initiailizePlayers
 *  -completare la startGame
 */
public class MatchHandler implements Runnable
{
    private ServerPlayer[] player;

    private int nConn;
    private int currentPlayer;
    private TokenTurn tok;

    private final static int MAXGIOC =2;//Da modificare a 4

    public synchronized void run ()
    {
        acceptConnection();
        initiliazeWindowPlayers();
        waitInitialition();
        System.out.println(">>>Initialization ended");
    }

    /**
     * Iniziatalize connecition with server for each players
     */
    private void acceptConnection()
    {
        System.out.println(">>>Server Started");
        nConn = 0;
        currentPlayer = 0;
        player = new ServerPlayer[4];
        try
        {
            tok = new TokenTurn(MAXGIOC);
            //Per ogni giocatore initzializza la comunicazione attendendo che qualche client si connetta
            for (int i =0 ;i< MAXGIOC;i ++)
            {
                player[i] = new ServerPlayer (tok);
                player[i].initializeComunication();
                Thread t = new Thread(player[i]);
                t.start();
                nConn++;
            }
        }
        catch (Exception e)
        {
            System.out.println("Exception: "+e);
            e.printStackTrace();

        }
        System.out.println(">>>Number of client connected:" + nConn);
    }

    /**
     *  Initialization board game for each players
     */
    private boolean initiliazeWindowPlayers ()
    {
        int c1,c2;
        ArrayList<String []> cards;

        //Take all xml names of windows cards
        try{
            cards = ParserXML.readWindowsName("resources/vetrate/xml/windows_list.xml");
        }
        catch (ParserXMLException ex){
            System.out.println(ex.getMessage());
            return false;
        }

        for (int i = 0; i < nConn ; i++)
        {
            //Pick up 2 random cards
            if (cards.size() > 2) {
                c1 = (int) (Math.random() * (cards.size()));
                do {
                    c2 = (int) (Math.random() * (cards.size()));
                } while (c1 == c2);
            } else {
                c1 = 0;
                c2 = 1;
            }

            player[i].setWindowCards(cards.get(c1), cards.get(c2));

            //Remove picked cards from list
            if (c1 > c2) {
                cards.remove(c1);
                cards.remove(c2);
            } else {
                cards.remove(c2);
                cards.remove(c1);
            }
        }
        return true;
    }

    /**
     *  Initialization private objective card for each players
     */
    private boolean initalizePrivateObjectiveCards ()
    {
        int c;
        ArrayList<String> cards;

        //Take all xml names of private objective cards
        try{
            cards = ParserXML.readObjectiveNames("resources/carte/obbiettivi/obbiettiviPrivati/xml/privateObjectiveList.xml");
        }
        catch (ParserXMLException ex){
            System.out.println(ex.getMessage());
            return false;
        }

        try
        {
            //For each players initialize his own private objective randomly
            for (int i=0;i<nConn;i++)
            {
                c = (int)(Math.random()* (cards.size()));
                player[i].setPrivateObjCard(cards.get(c));
                cards.remove(c);
            }
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
        return true;
    }

    /**
     *  Initialization public objective cards for each players
     */
    private boolean initializePublicObjectiveCards ()
    {
        int c;
        ArrayList<String> cards;

        //Take all xml names of public objective cards
        try{
            cards = ParserXML.readObjectiveNames("resources/carte/obbiettivi/obbiettiviPubblici/xml/publicObjectiveList.xml");
        }
        catch (ParserXMLException ex){
            System.out.println(ex.getMessage());
            return false;
        }

        try
        {
            String [] objs = new String[3];
            //Select randomly 3 public objective cards from list
            for (int i = 0; i < 3; i++)
            {
                c = (int)(Math.random()* (cards.size()));
                objs[i]=cards.get(c);
                cards.remove(c);
            }
            //For each players initialize public objective already selected
            for (int i=0;i<nConn;i++)
                player[i].setPublicObjCard(objs);
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
            return false;
        }
        return true;
    }


    private void startGame ()
    {
        while (true)
        {
            synchronized (tok)
            {
                for (int i = 0 ; i < (nConn * 2); i++)
                {
                    tok.nextTurn();
                    tok.notifyAll();
                    try {
                        tok.wait();
                    }
                    catch (InterruptedException ex)
                    {
                        System.out.println(ex.getMessage());
                        ex.printStackTrace();
                    }

                    //Aggiornamento ai client della mossa appena fatta

                }
            }
        }
    }


    /**
     * Start client's setup comunication (Windows and Objectives)
     */
    private synchronized void waitInitialition ()
    {
        //Partenza della fase di inizializzazione
        tok.startSetup();

        synchronized (tok)
        {
            try
            {
                //Sveglia tutti i client in attesa di inizializzarsi
                tok.notifyAll();
                //Attende che tutti i client siano inizializzati
                for (int i = 0; i < nConn ; i++)
                    if (tok.getOnSetup())
                        tok.wait();
            }
            catch (InterruptedException ex)
            {
                System.out.println(ex.getMessage());
                ex.printStackTrace();
            }
        }

    }


    public static void main(String[] args)
    {
        (new Thread(new MatchHandler())).start();

    }
}




