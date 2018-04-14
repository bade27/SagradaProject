package Test;

import Test.Model.Window;

public class Partita
{
    private int nGioc;
    private Player [] player;
    private Dadiera dadiera;

    /**
     * Inizializza una partita di n giocatori
     */
    public Partita (int n)
    {
        nGioc = n;
        player = new Player[nGioc];
        dadiera = new Dadiera(n*2 + 1);
        for (int i = 0;i< nGioc;i++)
        {
            Window w = new Window("kaleidoscopic_dream.xml");
            player[i] = new Player (w,dadiera);
        }
    }

    public void startGame ()
    {
        for (int i =0;i<nGioc;i++)
        {
            Graphic p = new Graphic(player[i]);
        }
    }
}
