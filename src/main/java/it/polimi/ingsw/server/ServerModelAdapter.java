package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.Window;
import it.polimi.ingsw.model.objectives.Private.PrivateObjective;
import it.polimi.ingsw.model.objectives.Public.PublicObjective;
import it.polimi.ingsw.model.tools.Tools;
import it.polimi.ingsw.remoteInterface.Pair;

public class ServerModelAdapter
{
    private Window board;
    private Dadiera dadiera;
    private PrivateObjective myPrivateObject;
    private PublicObjective[] publicObjectives;
    private Tools[] tools;
    private String user;

    public ServerModelAdapter (Dadiera d)
    {
        board = null;
        dadiera = d;
    }

    /**
     * Inizializza l'oggetto window andando a prendere il design dal file xml passato
     * @param path percorso dell'immagine
     */
    public void initializeWindow (String path) throws ModelException
    {
        try {
            board = new Window(path);
        }
        catch (ParserXMLException ex) {
            throw new ModelException("Impossible to read XML: " + ex.getMessage());
        }
    }

    /**
     * Aggiunge se possibile un dado alla board
     * @param i riga del piazzamento
     * @param j colonna del piazzamento
     * @param d dado da piazzare
     */
    public void addDiceToBoard (int i, int j, Dice d) throws ModelException
    {
        try {
            board.addDice(i,j,d,0);
            //System.out.println(board.toString());
        }
        catch (IllegalDiceException ex) {
            throw new ModelException("Impossible to place dice: " + ex.getMessage());
        }
        dadiera.deleteDice(d);
    }

    public void setPublicObjectives(String path) throws ModelException
    {
        //publicObjectives[0] = ObjectivesFactory.getPublicObjective(path);
    }

    public void setUser (String s)
    {
        user = s;
    }


    public Pair[] getDadieraPair ()
    {
        return dadiera.toPairArray();
    }

    public Pair[][] getWindowPair ()
    {
        return board.getPairMatrix();
    }

    public void setDadiera(Dadiera dadiera) {
        this.dadiera = dadiera;
    }
}
