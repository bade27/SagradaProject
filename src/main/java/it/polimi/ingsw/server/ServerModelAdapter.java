package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.Window;
import it.polimi.ingsw.model.objectives.ObjectivesFactory;
import it.polimi.ingsw.model.objectives.Private.PrivateObjective;
import it.polimi.ingsw.model.objectives.Public.PublicObjective;
import it.polimi.ingsw.model.tools.Tools;
import it.polimi.ingsw.model.tools.ToolsFactory;
import it.polimi.ingsw.remoteInterface.Pair;

public class ServerModelAdapter
{
    private static final int numPublicObj = 3;
    private static final int numTools = 3;

    private Window board;
    private Dadiera dadiera;
    private PrivateObjective myPrivateObject;
    private PublicObjective[] publicObjectives;
    private Tools[] tools;
    private String user;
    private boolean canMove;
    private int marker;

    private Tools toolInUse;

    public ServerModelAdapter (Dadiera d)
    {
        board = null;
        dadiera = d;
        publicObjectives = new PublicObjective[numPublicObj];
        tools = new Tools[numTools];
    }

    public void useTool() {


    }

    public boolean toolRequest (int nrTool)
    {
        for (int i = 0; i < tools.length ; i++)
            if (tools[i].getType() == nrTool)
                if (tools[i].getPrice() < marker) {
                    toolInUse = tools[i];
                    return true;
                }
        return false;
    }

    /**
     * Add die passed , if possible, to board in specific position
     * @param i placement's row
     * @param j placement's column
     * @param d die to place
     */
    public void addDiceToBoard (int i, int j, Dice d) throws ModelException
    {
        try {
            board.addDice(i,j,d,0);
        }
        catch (IllegalDiceException ex) {
            throw new ModelException("Impossible to place dice: " + ex.getMessage());
        }
        canMove = false;
        dadiera.deleteDice(d);
    }


    //<editor-fold desc="Setup Phase">
    /**
     * Initialize window with path passed
     * @param path path of windows' pattern
     */
    public void initializeWindow (String path) throws ModelException
    {
        try {
            board = new Window(path);
            marker = board.getDifficult();
        }
        catch (ParserXMLException ex) {
            throw new ModelException("Impossible to read XML: " + ex.getMessage());
        }
    }

    /**
     * Initialize Public objectives with path passed
     * @param path path of objectives' pattern
     */
    public void initializePublicObjectives(String[] path) throws ModelException
    {
        try
        {
            for (int i = 0 ; i < numPublicObj ; i++)
                publicObjectives[i] = ObjectivesFactory.getPublicObjective(path[i]);

        }catch (Exception ex){
            throw new ModelException("Impossible to create public objectives");
        }
    }

    /**
     * Initialize tool cards with path passed
     * @param names names of tools' pattern
     */
    public void initializeToolCards(String[] names) throws ModelException
    {
        try
        {
            /*for (int i = 0 ; i < numPublicObj ; i++)
                tools[i] = ToolsFactory.getTools(names[i]);*/
            tools[0] = ToolsFactory.getTools("Pinza Sgrossatrice");

        }catch (Exception ex){
            throw new ModelException("Impossible to create public objectives");
        }
    }

    public void setUser (String s)
    {
        user = s;
    }
    //</editor-fold>

    //<editor-fold desc="Utilities">
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

    public boolean CanMove() {
        return canMove;
    }

    public void setCanMove() {
        this.canMove = true;
        toolInUse = null;
    }
    //</editor-fold>

}
