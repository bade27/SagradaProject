package Server;

import Exceptions.ClientOutOfReachException;
import Exceptions.ModelException;
import Utilities.LogFile;

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
                boolean b = initializeWindow();
                //DA TESTAREEE!!!!!
                if (!b)
                {
                    //Notify token that client is dead
                    token.deletePlayer(user);
                    token.notifyAll();
                    return;
                }

                //End Setup phase comunication
                token.endSetup();
                token.notifyAll();
            }
            catch (InterruptedException ex) {
                System.out.println(ex.getMessage());
                LogFile.addLog(ex.getStackTrace().toString());
                return;
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
                    return;
                }

            }
        }
    }

    public boolean initializeComunication ()
    {
        String u;
        try{
            com = new ServerConnectionHandler();

            do{
                u = com.login();
            } while (!possibleUsers.contains(u));
            possibleUsers.remove(u);
            user = u;
            return true;
        }
        catch (ClientOutOfReachException e) {
            LogFile.addLog(e.getMessage() , e.getStackTrace());
            return false;
        }
    }

    private boolean initializeWindow ()
    {
        String s1 ="";
        try {
            s1 = com.chooseWindow(windowCard1, windowCard2);
        }
        catch (ClientOutOfReachException e) {
            LogFile.addLog("(User:" + user + ")" + e.getMessage() , e.getStackTrace());
            return false;
        }

        try {
            adapter.initializeWindow(s1);
            LogFile.addLog("User: " + user + " Window initialized: " + s1);
        }
        catch (ModelException ex) {
            LogFile.addLog(ex.getMessage());
            return false;
        }
        return true;
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
