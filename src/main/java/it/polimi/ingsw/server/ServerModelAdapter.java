package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.IllegalStepException;
import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.RoundTrace;
import it.polimi.ingsw.model.Window;
import it.polimi.ingsw.model.objectives.ObjectivesFactory;
import it.polimi.ingsw.model.objectives.Private.PrivateObjective;
import it.polimi.ingsw.model.objectives.Public.PublicObjective;
import it.polimi.ingsw.model.tools.Tools;
import it.polimi.ingsw.remoteInterface.Pair;
import it.polimi.ingsw.utilities.LogFile;
import it.polimi.ingsw.utilities.Wrapper;

import java.util.ArrayList;

public class ServerModelAdapter
{
    private static final int numPublicObj = 3;
    private static final int numTools = 3;

    private Window board;
    private Dadiera dadiera;
    private RoundTrace roundTrace;
    private PrivateObjective myPrivateObject;
    private PublicObjective[] publicObjectives;
    private Tools[] tools;
    private TokenTurn token;
    private String user;

    private boolean canMove;
    private int marker;
    private Tools toolInUse;


    public ServerModelAdapter (Dadiera d, RoundTrace trace, TokenTurn tok)
    {
        board = null;
        dadiera = d;
        roundTrace = trace;
        publicObjectives = new PublicObjective[numPublicObj];
        tools = new Tools[numTools];
        toolInUse = null;
        token = tok;
    }

    /**
     * executes the effect of the requested tool
     * @param w array of possible parameters
     * @return weather the execution of the tool has been successful or not
     */
    public String useTool(Wrapper... w)
    {
        String replyToClient = "";
        //Checks if any tool permission was requested
        if (toolInUse == null)
        {
            LogFile.addLog("User: " + user + "\t Tool not permission asked");
            return "Not using tool permission asked";
        }

        //sets all the necessary parameters inside the tool in use
        for(Wrapper wrapper : w)
            wrapper.myFunction();
        new Wrapper(this).myFunction();
        int current_price = toolInUse.getPrice();

        //Using of tool
        try {
            toolInUse.use();
        } catch (IllegalDiceException | IllegalStepException e) {
            LogFile.addLog("User: " + user + "\t Tool Action failed: " + e.getMessage());
            return "Tool Action failed: " + e.getMessage();
        }
        //If performed decrees client's own marker
        //first branch guarantees tool 11's right use
        if(toolInUse.getId() == 11 && !toolInUse.isDiceComplete()) {
            replyToClient = toolInUse.getColor();
        } else {
            marker = marker - current_price;
            replyToClient = "Tool used correctly";
        }
        Tools.setAllToNull();
        return replyToClient;

    }

    /**
     * Ask if tool passed, with his id, is usable
     * @param nrTool id tool request
     * @return Message to client about right asking of tool
     */
    public String toolRequest (int nrTool)
    {
        LogFile.addLog("User: " + user + "\t Tool request nr." + nrTool);
        //Check if there is a tool already in use
        if (toolInUse != null)
            if (!toolInUse.isToolFinished())
            {
                LogFile.addLog("User: " + user + "\t Tool request nr." + nrTool);
                return "Tool permission rejected: Another Tool in use";
            }

        //Check if tool is callable
        for (int i = 0; i < tools.length ; i++)
            if (tools[i].getId() == nrTool)
                //Check if client has enough marker
                if (tools[i].getPrice() <= marker) {
                    toolInUse = tools[i];
                    LogFile.addLog("User: " + user + "\t Tool permission accepted");
                    Tools.setAllToNull();
                    return "Tool permission accepted";
                }
        LogFile.addLog("User: " + user + "\t Tool permission rejected: Not enough marker");
        return "Tool permission rejected: Not enough marker";
    }

    /**
     * Add die passed , if possible, to board in specific position
     * @param i placement's row
     * @param j placement's column
     * @param d die to place
     */
    public void addDiceToBoard (int i, int j, Dice d) throws ModelException
    {
        LogFile.addLog("User: " + user + "\t Placement Die move: row:" + i + " col:" + j + " " + d.toString());
        //Check if there is a tool in use that force client to place a specific die
        if (toolInUse != null)
            if (!toolInUse.canPlaceDie(d))
            {
                LogFile.addLog("User: " + user + "\t Impossible to place die, wrong die selected");
                throw new ModelException("Impossible to place die, wrong die selected ");
            }

        //Try to put die on board
        try {
            board.addDice(i,j,d,0);
            LogFile.addLog("User: " + user + "\t Placement die correct ");
            if (toolInUse != null)
                toolInUse.setToolFinished(true); //Always set tool in use on finish mode
        }
        catch (IllegalDiceException ex) {
            LogFile.addLog("User: " + user + "\t Impossible to place die: " + ex.getMessage());
            throw new ModelException("Impossible to place die: " + ex.getMessage());
        }
        canMove = false;
        //After adding die, it will be delete from Dadiera
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
     * Initialize privte objectives with path passed
     * @param path path of objectives' pattern
     */
    public void initializePrivateObjectives(String path) throws ModelException
    {
        try
        {
            myPrivateObject = ObjectivesFactory.getPrivateObjective(path);
        }catch (Exception ex){
            throw new ModelException("Impossible to create private objectives");
        }
    }

    /**
     * Initialize tool cards with path passed
     * @param toolCards names of tools' pattern
     */
    public void initializeToolCards(Tools[] toolCards) throws ModelException
    {
        tools = toolCards;
        /*try
        {
            for (int i = 0 ; i < numTools ; i++)
                tools[i] = ToolsFactory.getTools(names[i]);

        }catch (Exception ex){
            throw new ModelException("Impossible to create public objectives");
        }*/
    }

    public void setUser (String s)
    {
        user = s;
    }
    //</editor-fold>

    //<editor-fold desc="End Game Phase">
    /**
     * Calculate total points of player and return that
     * @return total points made by player on matth
     */
    public int calculatePoints ()
    {
        int publicPoints=0,privatePoints=0;
        for (int i = 0; i < publicObjectives.length ; i++)
            publicPoints = publicPoints + publicObjectives[i].getScore(board);
        privatePoints = myPrivateObject.getScore(board);
        return privatePoints + publicPoints;
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

    public ArrayList<Pair>[] getRoundTracePair() {
        return roundTrace.getPair();
    }

    public int getMarker() {
        return marker;
    }

    public boolean CanMove() {
        return canMove;
    }

    public void setCanMove(boolean set) {
        this.canMove =set;
        if(canMove)
            toolInUse = null;
    }

    public Dadiera getDadiera() {
        return dadiera;
    }

    public Window getBoard() {
        return board;
    }

    public RoundTrace getRoundTrace() {
        return roundTrace;
    }

    public TokenTurn getToken() {
        return token;
    }

    public String getUser() {
        return user;
    }

    //</editor-fold>

}
