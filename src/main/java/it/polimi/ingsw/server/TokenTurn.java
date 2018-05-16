package it.polimi.ingsw.server;

import org.omg.CosNaming.NamingContextExtPackage.StringNameHelper;

import java.util.ArrayList;

public class TokenTurn
{
    private ArrayList<Player> players;

    //Tools using
    private ArrayList<Player> tempPlayers;
    private boolean toolInUsing;
    private boolean firstTurnTool;
    private boolean secondTurnTool;
    private int turnOfTool;


    //Game Phase
    private int currentTurn;
    private boolean clockwise;
    private boolean endRound;


    //Setup Phase
    private boolean onSetup;
    private int playerEndSetup;
    private int initNumberOfPlayers;

    //ControlMatch
    private boolean fatalError;
    private boolean justDeleting;

    public TokenTurn ()
    {
        currentTurn = 0;
        clockwise = true;
        onSetup = false;
        players = new ArrayList<>();
        initNumberOfPlayers = 0;
        fatalError = false;
        toolInUsing = false;
        tempPlayers = new ArrayList<>();
        justDeleting = false;
    }

    /**
     * Request if player s has turn
     * @param s username of player
     * @return true if turn, false otherwise
     */
    public synchronized boolean isMyTurn (String s)
    {
        if (toolInUsing)
        {
            for (int i = 0; i < tempPlayers.size() ; i++)
            {
                if (tempPlayers.get(i).getName().equals(s))
                    return  (tempPlayers.get(i).getIdTurn() == currentTurn);
            }
        }
        else
        {
            for (int i = 0; i < players.size() ; i++)
            {
                if (players.get(i).getName().equals(s))
                    return  (players.get(i).getIdTurn() == currentTurn);
            }

        }
        return false;
    }

    /**
     * es.
     * numPlayer = 4 , current turn = 0
     * current turn = 1 - 2 - 3 - 4 - 4 - 3 - 2 - 1 - 1 - 2 -......
      */
    public synchronized void nextTurn ()
    {
        if (!justDeleting)
        {
            if (!toolInUsing)
                normalTurn();
            else
                toolTurn();
        }
        else
            justDeleting = false;

    }

    /**
     * Increment turn when tool8 is not active
     */
    private void normalTurn ()
    {
        endRound = false;
        if (clockwise)
        {
            if (currentTurn < players.size())
                    currentTurn ++;
            else
                clockwise = false;
        }
        else
        {
            if (currentTurn > 1)
            {
                currentTurn --;
                if (currentTurn == 1)
                    endRound = true;
            }
            else
            {
                clockwise = true;
                leftShiftIdTurn();
            }
        }
    }

    /**
     * Increment turn when tooli is active
     */
    private void toolTurn ()
    {
        endRound = false;
        if (clockwise)
        {
            if (currentTurn < tempPlayers.size() )
                currentTurn ++;
            else
            {
                clockwise = false;
                if (tempPlayers.size() == 2)
                    endRound = true;
            }
        }
        else
        {
            if (currentTurn > 2)
            {
                currentTurn --;
                if (currentTurn == 2)
                    endRound = true;
            }
            else
            {
                clockwise = true;
                toolInUsing = false;
                currentTurn = 1;
                leftShiftIdTurn();
            }
        }
        if (firstTurnTool)
        {
            secondTurnTool = true;
            firstTurnTool = false;
            currentTurn --;
        }
        else if (secondTurnTool)
        {
            secondTurnTool();
            secondTurnTool = false;
        }
    }

    /**
     * Left shift idTurn of players
     */
    private void leftShiftIdTurn ()
    {
        for (int i = 0; i < players.size() ; i++)
        {
            int t = players.get(i).getIdTurn();
            t--;
            if (t < 1)
                t = players.size();
            players.get(i).setIdTurn(t);
        }
    }


    //<editor-fold desc="Initialization/Deleting Players">
    /**
     * Add a new player
     * @param name username of new added player
     */
    public synchronized void addPlayer (String name)
    {
        players.add(new Player(name,players.size() + 1));
    }

    /**
     * IMPORTANT!!!!  IT MUST BE CALLED WHEN IS THE TURN OF LEAVED PLAYER
     * Delete a player and change idPlayer of the others
     * @param name username of deleted player
     */
    public synchronized void deletePlayer (String name)
    {
        if (toolInUsing)
            delPlayer(name,tempPlayers);
        delPlayer(name,players);
    }

    private void delPlayer (String name,ArrayList<Player> t)
    {
        int inc,turnDel;
        for (int i = 0; i < t.size() ; i++)
            if (t.get(i).getName().equals(name))
            {
                if (currentTurn == t.size())
                {
                    currentTurn--;
                    clockwise = false;
                }
                if (currentTurn == 1)
                    clockwise = true;
                turnDel = t.get(i).getIdTurn();
                t.remove(i);
                inc=0;
                for (int j = 0; j < t.size() ; j++)
                {
                    if (t.get(j).getIdTurn() > turnDel)
                    {
                        t.get(j).setIdTurn(turnDel + inc);
                        inc++;
                    }
                }
                justDeleting = true;
                return;
            }
    }
    //</editor-fold>

    //<editor-fold desc="Tools function">
    /**
     * Request if player is in his second round
     * @param s username of player
     * @return true if turn, false otherwise
     */
    public boolean isMySecondRound (String s)
    {
        for (int i = 0; i < players.size() ; i++)
        {
            if (players.get(i).getName().equals(s))
                return (players.get(i).getIdTurn() == currentTurn && !clockwise);
        }
        return false;
    }

    /**
     * Function that must be called when the second turn on tool is run
     */
    private void secondTurnTool ()
    {
        for (int i = 0; i < tempPlayers.size() ; i++)
        {
            if (tempPlayers.get(i).getIdTurn() == turnOfTool)
                tempPlayers.get(i).setIdTurn(0);
            else if (tempPlayers.get(i).getIdTurn() < 0)
                tempPlayers.get(i).setIdTurn(tempPlayers.get(i).getIdTurn() + turnOfTool + 1);
        }
    }

    /**
     *  Used for changing turn sequence after tool number 8
     * @param s
     * @return
     */
    public synchronized boolean useToolNumber8 (String s)
    {
        if (!clockwise || !isMyTurn(s) || currentTurn == players.size())
            return false;
        if (!toolInUsing)
        {
            tempPlayers.clear();


            for (int i = 0 ; i < players.size() ; i++)
            {
                if (players.get(i).getIdTurn() >= currentTurn)
                {
                    Player a = new Player( players.get(i).getName(),players.get(i).getIdTurn());
                    tempPlayers.add(a);
                }
                else
                {
                    Player a = new Player( players.get(i).getName(),players.get(i).getIdTurn() - currentTurn);
                    tempPlayers.add(a);
                }
            }
        }
        else
            return false;

        turnOfTool = currentTurn;
        firstTurnTool = true;
        secondTurnTool = false;
        toolInUsing = true;
        return true;
    }

    //</editor-fold>

    //<editor-fold desc="Utilities">

    public int getNumPlayers ()
    {
        return players.size();
    }

    public boolean isEndRound ()
    {
        return endRound;
    }

    public String toString ()
    {
        String r = "";
        for (int i = 0; i < players.size() ; i++)
        {
            r = r + "Player: " + players.get(i).getName() + " IdTurn: " + players.get(i).getIdTurn();
            r = r + "\n";
        }
        r = r + "Turn: " + currentTurn + " End Round:" + endRound + "\n" ;
        return r;
    }

    public String toStringTemp ()
    {
        String r = "";
        for (int i = 0; i < tempPlayers.size() ; i++)
        {
            r = r + "TempPlayer: " + tempPlayers.get(i).getName() + " TempIdTurn: " + tempPlayers.get(i).getIdTurn();
            r = r + "\n";
        }
        r = r + "Turn: " + currentTurn + " End Round:" + endRound + "\n" ;
        return r;
    }
    //</editor-fold>

    //<editor-fold desc="Class Player">
    /**
     * Class that associates username to player turn
     */
    class Player
    {
        String name;
        int turn;

        Player (String n, int t)
        {
            name = n;
            turn = t;
        }

        void setIdTurn (int t) {
            turn = t;
        }

        public String getName() {
            return name;
        }

        public int getIdTurn() {
            return turn;
        }

    }
    //</editor-fold>

    //<editor-fold desc="Fatal Error">
    public synchronized boolean isFatalError() {
        return fatalError;
    }

    public synchronized void notifyFatalError() {
        this.fatalError = true;
    }
    //</editor-fold>

    //<editor-fold desc="Setup Phase">
    /**
     * Starts initialization phase
     */
    public synchronized void startSetup ()
    {
        onSetup = true;
        playerEndSetup = 0;
    }

    /**
     * Increase number of client who finished setup
     */
    public synchronized void endSetup ()
    {
        playerEndSetup ++ ;
        if (playerEndSetup == initNumberOfPlayers)
            onSetup = false;
    }

    /**
     * @return if setup phase is running
     */
    public synchronized boolean getOnSetup ()
    {
        return onSetup;
    }

    public void setInitNumberOfPlayers (int i)
    {
        initNumberOfPlayers = i;
    }
    //</editor-fold>

}
