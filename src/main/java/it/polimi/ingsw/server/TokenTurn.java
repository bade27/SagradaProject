package it.polimi.ingsw.server;

import java.util.ArrayList;
import java.util.Arrays;

public class TokenTurn
{
    //List of effective players
    private ArrayList<Player> players;
    private ArrayList<Player> inWaitConnection;

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
    private boolean onGame;


    //Setup Phase
    private boolean onSetup;
    private int playerEndSetup;
    private int initNumberOfPlayers;

    //ControlMatch
    private boolean fatalError;
    private boolean justDeleting;

    //Object to synchronize
    private final Object synchronator = new Object();

    //End game
    private boolean endGame;

    public TokenTurn()
    {
        endGame = false;
        currentTurn = 0;
        clockwise = true;
        onSetup = false;
        players = new ArrayList<>();
        inWaitConnection = new ArrayList<>();
        initNumberOfPlayers = 0;
        fatalError = false;
        toolInUsing = false;
        tempPlayers = new ArrayList<>();
        justDeleting = false;
        onGame = false;
    }

    /**
     * Request if player s has turn
     *
     * @param s username of player
     * @return true if turn, false otherwise
     */
    public synchronized boolean isMyTurn(String s)
    {
        if (justDeleting)
            return false;
        if (toolInUsing)
        {
            for (int i = 0; i < tempPlayers.size(); i++)
            {
                if (tempPlayers.get(i).getName().equals(s))
                    return (tempPlayers.get(i).getIdTurn() == currentTurn);
            }
        } else
        {
            for (int i = 0; i < players.size(); i++)
            {
                if (players.get(i).getName().equals(s))
                    return (players.get(i).getIdTurn() == currentTurn);
            }

        }
        return false;
    }

    /**
     * Increment turn
     * numPlayer = 4 , current turn = 0
     * current turn = 1 - 2 - 3 - 4 - 4 - 3 - 2 - 1 - 1 - 2 -......
     */
    public synchronized void nextTurn()
    {
        if (!justDeleting)
        {
            if (!toolInUsing)
                normalTurn();
            else
                toolTurn();
        } else
            justDeleting = false;

    }

    /**
     * Increment in normal condition
     */
    private void normalTurn()
    {
        endRound = false;
        if (clockwise)
        {
            if (currentTurn < players.size())
                currentTurn++;
            else
                clockwise = false;
        } else
        {
            if (currentTurn > 1)
            {
                currentTurn--;
                if (currentTurn == 1)
                    closeRound();
            } else
            {
                clockwise = true;
                leftShiftIdTurn();
            }
        }
    }

    /**
     * Increment turn when tool is active
     */
    private void toolTurn()
    {
        endRound = false;
        if (clockwise)
        {
            if (currentTurn < tempPlayers.size())
                currentTurn++;
            else
            {
                clockwise = false;
                if (tempPlayers.size() == 2)
                    closeRound();
            }
        } else
        {
            if (currentTurn > 2)
            {
                currentTurn--;
                if (currentTurn == 2)
                   closeRound();
            } else
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
            currentTurn--;
        } else if (secondTurnTool)
        {
            secondTurnTool();
            secondTurnTool = false;
        }
    }

    /**
     * Left shift idTurn of players
     */
    private void leftShiftIdTurn()
    {
        for (int i = 0; i < players.size(); i++)
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
     *
     * @param name username of new added player
     */
    public synchronized void addPlayer(String name)
    {
        if (!onGame)
            players.add(new Player(name, players.size() + 1));
        else
            inWaitConnection.add(new Player(name, players.size() + 1 + inWaitConnection.size()));
    }

    /**
     * IMPORTANT!!!!  IT MUST BE CALLED WHEN IS THE TURN OF LEAVED PLAYER
     * Delete a player and change idPlayer of the others
     *
     * @param name username of deleted player
     */
    public synchronized void deletePlayer(String name)
    {
        if (toolInUsing)
            delPlayer(name, tempPlayers);
        delPlayer(name, players);
    }

    /**
     * Delete player from list of active players and adjust the correct sequence of turn
     *
     * @param name
     * @param players
     */
    private void delPlayer(String name, ArrayList<Player> players)
    {
        int inc, turnDel;
        for (int i = 0; i < players.size(); i++)
        {
            if (players.get(i).getName().equals(name))
            {
                if (!clockwise)
                {
                    if (currentTurn == 1)
                        clockwise = true;
                    else
                        currentTurn--;
                }

                if (currentTurn == players.size())
                {
                    currentTurn--;
                    clockwise = false;
                }

                if (currentTurn == 1 && !clockwise)
                    closeRound();

                turnDel = players.get(i).getIdTurn();
                players.remove(i);
                inc = 0;
                for (int j = 0; j < players.size(); j++)
                {
                    if (players.get(j).getIdTurn() > turnDel)
                    {
                        players.get(j).setIdTurn(turnDel + inc);
                        inc++;
                    }
                }
                justDeleting = true;
                return;
            }
        }

    }
    //</editor-fold>

    //<editor-fold desc="Tools function">

    /**
     * Request if player is in his second round
     *
     * @param s username of player
     * @return true if turn, false otherwise
     */
    public boolean isMySecondRound(String s)
    {
        for (int i = 0; i < players.size(); i++)
        {
            if (!toolInUsing)
            {
                if (players.get(i).getName().equals(s))
                    return (players.get(i).getIdTurn() == currentTurn && !clockwise);
            } else
            {
                if (tempPlayers.get(i).getName().equals(s))
                    return (tempPlayers.get(i).getIdTurn() == currentTurn && !clockwise);
            }

        }
        return false;
    }

    /**
     * Function that must be called when the second turn on tool is run
     */
    private void secondTurnTool()
    {
        for (int i = 0; i < tempPlayers.size(); i++)
        {
            if (tempPlayers.get(i).getIdTurn() == turnOfTool)
                tempPlayers.get(i).setIdTurn(0);
            else if (tempPlayers.get(i).getIdTurn() < 0)
                tempPlayers.get(i).setIdTurn(tempPlayers.get(i).getIdTurn() + turnOfTool + 1);
        }
    }

    /**
     * Used for changing turn sequence after tool number 8
     *
     * @param s user who required to use tool
     * @return if user can use tool 8
     */
    public synchronized boolean useToolNumber8(String s)
    {
        if (!clockwise || !isMyTurn(s) || currentTurn == players.size())
            return false;
        if (!toolInUsing)
        {
            tempPlayers.clear();


            for (int i = 0; i < players.size(); i++)
            {
                if (players.get(i).getIdTurn() >= currentTurn)
                {
                    Player a = new Player(players.get(i).getName(), players.get(i).getIdTurn());
                    tempPlayers.add(a);
                } else
                {
                    Player a = new Player(players.get(i).getName(), players.get(i).getIdTurn() - currentTurn);
                    tempPlayers.add(a);
                }
            }
        } else
            return false;

        turnOfTool = currentTurn;
        firstTurnTool = true;
        secondTurnTool = false;
        toolInUsing = true;
        return true;
    }

    //</editor-fold>

    //<editor-fold desc="Utilities">

    private void closeRound ()
    {
        endRound = true;
        for (int i = 0 ; i < inWaitConnection.size() ; i++) {
            players.add(inWaitConnection.get(i));
        }
        inWaitConnection.clear();
    }

    public int getNumPlayers()
    {
        return players.size();
    }

    public boolean isEndRound()
    {
        return endRound;
    }

    public String toString()
    {
        String r = "";
        for (int i = 0; i < players.size(); i++)
        {
            r = r + "Player: " + players.get(i).getName() + " IdTurn: " + players.get(i).getIdTurn();
            r = r + "\n";
        }
        r = r + "Turn: " + currentTurn + " End Round:" + endRound + "\n";
        return r;
    }

    public String toStringTemp()
    {
        String r = "";
        for (int i = 0; i < tempPlayers.size(); i++)
        {
            r = r + "TempPlayer: " + tempPlayers.get(i).getName() + " TempIdTurn: " + tempPlayers.get(i).getIdTurn();
            r = r + "\n";
        }
        r = r + "Turn: " + currentTurn + " End Round:" + endRound + "\n";
        return r;
    }

    public Object getSynchronator()
    {
        return synchronator;
    }

    public synchronized boolean isEndGame()
    {
        return endGame;
    }

    public void setEndGame()
    {
        this.endGame = true;
    }

    public void setOnGame(boolean onGame) {
        this.onGame = onGame;
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

        Player(String n, int t)
        {
            name = n;
            turn = t;
        }

        void setIdTurn(int t)
        {
            turn = t;
        }

        public String getName()
        {
            return name;
        }

        public int getIdTurn()
        {
            return turn;
        }

    }
    //</editor-fold>

    //<editor-fold desc="Fatal Error">
    public synchronized boolean isFatalError()
    {
        return fatalError;
    }

    public synchronized void notifyFatalError()
    {
        this.fatalError = true;
    }
    //</editor-fold>

    //<editor-fold desc="Setup Phase">

    /**
     * Starts initialization phase
     */
    public synchronized void startSetup()
    {
        onSetup = true;
        playerEndSetup = 0;
    }

    public synchronized void stopSetup() {
        onSetup = false;
    }

    /**
     * Increase number of client who finished setup
     */
    public synchronized void endSetup()
    {
        playerEndSetup++;
        if (playerEndSetup == initNumberOfPlayers)
            onSetup = false;
    }

    /**
     * @return if setup phase is running
     */
    public synchronized boolean getOnSetup()
    {
        return onSetup;
    }

    public void setInitNumberOfPlayers(int i)
    {
        initNumberOfPlayers = i;
    }
    //</editor-fold>


    public synchronized void setJustDeleting(boolean justDeleting) {
        this.justDeleting = justDeleting;
    }

    public synchronized ArrayList<String> getPlayers() {
        String[] names = players.stream().map(p -> p.getName()).toArray(String[]::new);
        return new ArrayList<>(Arrays.asList(names));
    }
}
