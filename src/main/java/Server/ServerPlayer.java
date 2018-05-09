package Server;

import Exceptions.ClientOutOfReachException;
import Exceptions.ModelException;
import Utilities.LogFile;

import java.util.ArrayList;

public class ServerPlayer extends Thread
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
        //////SETUP PHASE//////
        try
        {
            synchronized (token) {
                //Wait until matchHandler signal start setup
                while (!token.getOnSetup())
                    token.wait();
            }

            //Initialization of client
            try {
                login();
                token.addPlayer(user);
                initializeWindow();
            }
            catch (ClientOutOfReachException|ModelException ex) {
                //Notify token that client is dead
                token.deletePlayer(user);
                token.endSetup();
                synchronized (token) {
                    token.notifyAll();
                }
                //If client fail initialization, he will not return on game
                return;
            }

            //End Setup phase comunication
            token.endSetup();
            synchronized (token) {
                token.notifyAll();
            }
        }
        catch (InterruptedException ex) {
            System.out.println(ex.getMessage());
            LogFile.addLog(ex.getStackTrace().toString());
            token.notifyFatalError();
            return;
        }

        //////GAME PHASE//////
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
                    LogFile.addLog("Turn of:" + user);
                    System.out.println(">>>Turn of:" + user);
                    Thread.sleep(2000);

                    //End turn comunication
                    token.notifyAll();
                    token.wait();
                }
                catch (InterruptedException ex)
                {
                    System.out.println(ex.getMessage());
                    LogFile.addLog(ex.getStackTrace().toString());
                    token.notifyFatalError();
                    return;
                }

            }
        }
    }

    /**
     * Wait until client's connection
     * @return true if connection goes well, false otherwise
     */
    public boolean initializeComunication ()
    {
        try{
            com = new ServerConnectionHandler();
            return true;
        }
        catch (ClientOutOfReachException e) {
            LogFile.addLog(e.getMessage() , e.getStackTrace());
            return false;
        }
    }

    /**
     * Initialize username through login phase
     * @throws ClientOutOfReachException Client is out of reach
     */
    private void login () throws ClientOutOfReachException
    {
        String u;
        try{
            do{
                u = com.login();
            } while (!possibleUsers.contains(u));
            possibleUsers.remove(u);
            user = u;
            LogFile.addLog("User: " + user + " Added");
        }
        catch (ClientOutOfReachException e) {
            LogFile.addLog(e.getMessage() , e.getStackTrace());
            throw new ClientOutOfReachException();
        }
    }

    /**
     * Initialize client's window board
     * @throws ClientOutOfReachException Client is out of reach
     * @throws ModelException Impossible to set window
     */
    private void initializeWindow () throws ClientOutOfReachException,ModelException
    {
        String s1 ="";
        try {
            s1 = com.chooseWindow(windowCard1, windowCard2);
        }
        catch (ClientOutOfReachException e) {
            LogFile.addLog("(User:" + user + ")" + e.getMessage() , e.getStackTrace());
            throw new ClientOutOfReachException();
        }

        try {
            adapter.initializeWindow(s1);
            LogFile.addLog("User: " + user + " Window initialized: " + s1);
        }
        catch (ModelException ex) {
            LogFile.addLog(ex.getMessage());
            throw new ModelException();
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

    public void setPublicObjCard (String[] c)
    {
        publicObjCard = c;
    }

    public void setPrivateObjCard (String c)
    {
        privateObjCard = c;
    }

}
