package it.polimi.ingsw.server;

import java.util.ArrayList;

public class TokenTurn
{
    private ArrayList<Player> players;

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

    public TokenTurn ()
    {
        currentTurn = 0;
        clockwise = true;
        onSetup = false;
        players = new ArrayList<>();
        initNumberOfPlayers = 0;
        fatalError = false;
    }

    /**
     * Request if player s has turn
     * @param s username of player
     * @return true if turn, false otherwise
     */
    public synchronized boolean isMyTurn (String s)
    {
        for (int i = 0; i < players.size() ; i++)
        {
            if (players.get(i).getName().equals(s))
                return  (players.get(i).getIdTurn() == currentTurn);
        }
        return false;
    }

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
     * es.
     * numPlayer = 4 , current turn = 0
     * current turn = 1 - 2 - 3 - 4 - 4 - 3 - 2 - 1 - 1 - 2 -......
      */
    public synchronized void nextTurn ()
    {
        endRound = false;
        if (clockwise)
        {
            if (currentTurn < players.size() )
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
                //Shift idTurn of players
                for (int i = 0; i < players.size() ; i++)
                {
                    int t = players.get(i).getIdTurn();
                    t--;
                    if (t < 1)
                        t = players.size();
                    players.get(i).setIdTurn(t);
                }
            }
        }
    }

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
        int inc,turnDel;
        for (int i = 0; i < players.size() ; i++)
            if (players.get(i).getName().equals(name))
            {
                turnDel = players.get(i).getIdTurn();
                players.remove(i);
                inc=0;
                for (int j = 0; j < players.size() ; j++)
                {
                    if (players.get(j).getIdTurn() > turnDel)
                    {
                        players.get(j).setIdTurn(turnDel + inc);
                        inc++;
                    }
                }
                return;
            }
    }

    public int getNumPlayers ()
    {
        return players.size();
    }

    public String toString ()
    {
        String r = "";
        for (int i = 0; i < players.size() ; i++)
            r = r + "Player: " + players.get(i).getName() + " IdTurn: " + players.get(i).getIdTurn() + "\n";

        return r;
    }

    public boolean isEndRound ()
    {
        return endRound;
    }

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

    public synchronized boolean isFatalError() {
        return fatalError;
    }

    public synchronized void notifyFatalError() {
        this.fatalError = true;
    }


    //////SETUP PHASE//////
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


}
