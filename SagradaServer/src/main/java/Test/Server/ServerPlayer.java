package Test.Server;

import Test.Model.Dadiera;
import Test.Model.Window;


/**
 * Cose da aggiungere:
 *  -Gestione del comunicator nel costruttore
 *  -nella funzione setIdTurn comunicare il cambio al server
 *  -l'intera funzione initializePlayer per comunicare al client l'inizializzazione del player (board,dadiera ecc...)
 *  -la partita vera e propria nella funzione run()
 */

public class ServerPlayer implements Runnable
{
    //private Comunicator com;
    private int idTurn;
    private TokenTurn token;
    ServerModelAdapter adapter;
    //private boolean onComunication;


    public ServerPlayer(TokenTurn tok)
    {
        //com = new Comunicator ();
        adapter = new ServerModelAdapter();
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
        //com.initialize ();
    }

    public void initializeWindow (String[] c1,String[] c2)
    {
        //Comunicazione col client per la sua scelta della window
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
}
