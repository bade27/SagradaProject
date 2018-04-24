package Test.Server;

public class TokenTurn
{
    private int currentTurn;
    private int numPlayer;
    private boolean clockwise;
    private boolean onSetup;
    private int playerEndSetup;

    public TokenTurn (int n)
    {
        numPlayer = n;
        currentTurn = 0;
        clockwise = true;
        onSetup = false;
    }

    public synchronized boolean isMyTurn (int t)
    {
        if (t == currentTurn)
            return true;
        return false;
    }

    /**
     * es.
     * numPlayer = 4 , current turn = 0
     * current turn = 1 - 2 - 3 - 4 - 4 - 3 - 2 - 1 - 1 - 2 -......
      */
    public synchronized void nextTurn ()
    {
        if (clockwise)
        {
            if (currentTurn < numPlayer )
                currentTurn ++;
            else
                clockwise = false;
        }
        else
        {
            if (currentTurn > 1)
                currentTurn --;
            else
                clockwise = true;
        }
    }

    public synchronized void startSetup ()
    {
        onSetup = true;
        playerEndSetup = 0;
    }

    public synchronized void endSetup ()
    {
        playerEndSetup ++ ;
        if (playerEndSetup == numPlayer)
            onSetup = false;
    }

    public synchronized boolean getOnSetup ()
    {
        return onSetup;
    }

}
