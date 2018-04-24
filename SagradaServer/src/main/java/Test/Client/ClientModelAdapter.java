package Test.Client;

import Test.Exceptions.IllegalDiceException;
import Test.Exceptions.ModelException;
import Test.Exceptions.ParserXMLException;
import Test.Model.Dice;
import Test.Model.Dadiera;
import Test.Model.Window;

public class ClientModelAdapter
{
    private Window board;
    private Dadiera dadiera;
    private int idTurn;


    public ClientModelAdapter ()
    {
        board = null;
        dadiera = null;
        idTurn = -1;
    }

    public void initializeWindow (String path) throws ModelException
    {
        try {
            board.initializeWindow(path);
        }
        catch (ParserXMLException ex) {
            throw new ModelException (ex.getMessage());
        }
    }

    public void addDiceToBoard(int x,int y,Dice d) throws ModelException
    {
        try {
            board.addDice(x,y,d,-1);
        }
        catch (IllegalDiceException ex) {
            throw new ModelException (ex.getMessage());
        }
    }

    public void deleteDiceFromDadiera (Dice d) throws ModelException
    {
        try {
            dadiera.deleteDice(d);
        }
        catch (IllegalDiceException ex) {
            throw new ModelException (ex.getMessage());
        }
    }

    public Window getWindow() {
        return board;
    }

    public Dadiera getDadiera() {
        return dadiera;
    }
}
