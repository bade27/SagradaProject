package Test.Server;

import Test.Exceptions.ModelException;

/**
 * Cose da aggiungere:
 *  -Gestione del comunicator nel costruttore
 *  -nella funzione setIdTurn comunicare il cambio al server
 *  -l'intera funzione initializePlayer per comunicare al client l'inizializzazione del player (board,dadiera ecc...)
 *  -la partita vera e propria nella funzione run()
 */

public class ServerPlayer implements Runnable
{
    private ServerConnectionHandler com;
    private int idTurn;
    private TokenTurn token;
    private ServerModelAdapter adapter;
    private Boolean onSetup;

    private String[] windowCard1,windowCard2,publicObjCard;
    private String privateObjCard;

    public ServerPlayer(TokenTurn tok)
    {
        adapter = new ServerModelAdapter();
        idTurn = -1;
        token = tok;
        onSetup = false;
    }

    public synchronized void run ()
    {
        while (true)//Da cambiare con la condizione di fine partita
        {
            synchronized (token)
            {
                try
                {
                    //Inizializzazione Partita
                    while (!onSetup)
                        token.wait();

                    //Inizializza il client
                    initializeWindow();


                    onSetup = false;
                    token.notifyAll();


                    //Partita vera e propria

                    //Attende che è il prorpio turno di gioco
                    while (!token.isMyTurn(idTurn))
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
        com = new ServerConnectionHandler ();
    }

    private void initializeWindow ()
    {
        String s1 = com.chooseWindow(windowCard1,windowCard2);
        try {
            adapter.initializeWindow("resources/vetrate/xml/" + s1);
            System.out.println(">>>Window initialized");
        }
        catch (ModelException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void initializePrivateObjectives (String card)
    {
        //Comunicazione col client per la sua carta obbiettivo privato
    }

    public void initializePublicObjectives (String[] cards)
    {
        //Comunicazione col client per le carte obbiettivo pubblico
    }

    public void closeComunication ()
    {
        //com.close ();
    }

    public void setIdTurn (int id)
    {
        idTurn = id;
        //Comunica col client il cambio di idTurn
    }


    public void setWindowCards (String c1[],String c2 [])
    {
        windowCard1 = c1;
        windowCard2 = c2;
    }

    public void setPublicObjCard (String[] c)
    {
        publicObjCard = c;
    }

    public void setPrivateObjCard (String c)
    {
        privateObjCard = c;
    }

    public synchronized void setOnSetup ()
    {
        onSetup = true;
    }

    public synchronized boolean getOnSetup ()
    {
        return onSetup;
    }
}
