package Test.Client;

import Test.Exceptions.IllegalDiceException;
import Test.Model.Dice;
import Test.Model.Dadiera;
import Test.Model.Window;

import java.net.Socket;

public class ClientPlayer implements Runnable
{
    private Window board;
    private Dadiera dadiera;
    private Socket server;//Da cambiare con oggetto comunicator
    private int idTurn;


    public void ClientPlayer (Socket ser)
    {
        server = ser;
    }


    public void run()
    {
        //Inizializza la com con il server
        //Inizializza la window w e dadiera
    }


    public void addDiceToBoard(int x,int y,Dice d) throws IllegalDiceException
    {
        board.addDice(x,y,d);
    }

    public void deleteDiceFromDadiera (Dice d) throws IllegalDiceException
    {
        dadiera.deleteDice(d);
    }


    public Window getWindow()
    {
        return board;
    }

    public Dadiera getDadiera()
    {
        return dadiera;
    }
}
