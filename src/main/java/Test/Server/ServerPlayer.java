package Test.Server;

import Test.Exceptions.ModelException;

public class ServerPlayer implements Runnable
{
    private ServerConnectionHandler com;
    private int idTurn;
    private TokenTurn token;
    private ServerModelAdapter adapter;

    private String[] windowCard1,windowCard2,publicObjCard;
    private String privateObjCard;

    public ServerPlayer(TokenTurn tok,ServerModelAdapter adp)
    {
        adapter = adp;
        idTurn = -1;
        token = tok;
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
        //String s1 = windowCard1[0];
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

}
