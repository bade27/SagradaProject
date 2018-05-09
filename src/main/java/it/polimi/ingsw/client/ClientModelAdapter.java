package it.polimi.ingsw.client;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.Window;

public class ClientModelAdapter
{
    private Window board;
    private Dadiera dadiera;
    private int idTurn;
    private Graphic graph;


    public ClientModelAdapter (Graphic g)
    {
        graph = g;
        board = null;
        dadiera = null;
        idTurn = -1;
    }

    public void initializeWindow (String path) throws ModelException
    {
        try {
            board = new Window(path);
            graph.initGraphic(this);
        }
        catch (ParserXMLException ex) {
            throw new ModelException(ex.getMessage());
        }
        catch (Exception ex){
            System.out.println(ex.getMessage());
        }
    }

    public void addDiceToBoard(int x, int y, Dice d) throws ModelException
    {
        try {
            board.addDice(x,y,d,-1);
        }
        catch (IllegalDiceException ex) {
            throw new ModelException(ex.getMessage());
        }
    }


    public Window getWindow() {
        return board;
    }

    public Dadiera getDadiera() {
        return dadiera;
    }
}
