package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.remoteInterface.Coordinates;
import it.polimi.ingsw.remoteInterface.Pair;
import it.polimi.ingsw.utilities.ParserXML;

public class Window
{
    private int difficult;
    private Cell board [][];
    public final int rows = 4;
    public final int cols = 5;
    private boolean firstTurn;//Flag that tell if we are in firs turn of game
    private String boardPath;

    /**
     * Create a new Window from XML file
     * @param path file xml path
     */
    public Window (String path) throws ParserXMLException
    {
        boardPath = path;
        firstTurn = true;
        board = new Cell[rows][cols];
        board = ParserXML.readWindowFromPath(boardPath,board);
        difficult = ParserXML.readBoardDifficult(boardPath);
    }

    //<editor-fold desc="Add dice to board">
    /**
     * Adds to window indicated die in indicated position, if window rejects insertion this method throw an exception
     * @param i Row index to add die
     * @param j Column index to add die
     * @param d Die to add
     * @param level control level of insertion (-1: no control ; 0:all control ; 1:control to Placement value ;
     *                                          2:control to Placement color ; 3:no control to near die)
     */
    public void addDice (int i , int j , Dice d, int level) throws IllegalDiceException
    {
        if (checkPositionBorder(i,j))
            throw new IllegalDiceException("Dado posizionato fuori dai bordi");

        //Check if no controls are expected
        if (level == -1)
        {
            board[i][j].setFrontDice(d);
            return;
        }

        //Check if die position is on board
        if (firstTurn)
        {
            if ((i == 0 || i == rows - 1 || j == 0 || j == cols - 1))
            {
                if (!setDie(i,j,d,level))
                    throw new IllegalDiceException("Dado posizionato su una cella inompatibile");
                else
                    firstTurn = false;
            }
            else
                throw new IllegalDiceException("Dado non posizionato sul bordo");
        }
        else
        {
            //Control if die position is occupied
            if (board[i][j].getFrontDice() != null)
                throw new IllegalDiceException("Dado posizionato su una cella occupata");

            boolean near=false; //true if there is die near my position
            boolean noSimilar = true; //true if there are not dice similar on cardinal position

            //Check near and similar condition
            for (int ind=0; ind<3 ; ind++)
            {
                for (int jnd=0; jnd<3; jnd++)
                {
                    if (!(i-1+ind < 0 || i-1+ind > rows - 1 || j-1+jnd < 0 || j-1+jnd > cols - 1))
                        if (board[i-1+ind][j-1+jnd].getFrontDice() != null)
                        {
                            near = true;
                            if (board[i-1+ind][j-1+jnd].getFrontDice().isSimilar(d) && (((i-1+ind) == i || (j-1+jnd) == j)))
                                noSimilar = false;
                        }
                }
            }

            if (noSimilar && level == 3) {
                if (!setDie(i, j, d, 0))
                    throw new IllegalDiceException("Dado posizionato su una cella inompatibile");
            }

            else if (near && noSimilar) {
                    if (!setDie(i, j, d, level))
                        throw new IllegalDiceException("Dado posizionato su una cella inompatibile");
            }

            else
                throw new IllegalDiceException("Dado non posizionato vicino ad uno compatibile");
        }
    }

    /**
     * Check if die position parameters are correct
     */
    private boolean checkPositionBorder (int i, int j)
    {
        return  (i < 0 || j < 0 || i > rows-1 || j > cols-1 );
    }

    /**
     * Set die with opportune control
     * @return true die set, false otherwise
     */
    private boolean setDie (int i , int j ,Dice d , int control)
    {
        if (control == 0)
            return board[i][j].setDice(d);
        else if (control == 1)
            return board[i][j].setDiceByValue(d);
        else if (control == 2)
            return board[i][j].setDiceByColor(d);
        else if (control == 3)
            return board[i][j].setDice(d);
        return false;
    }
    //</editor-fold>

    //<editor-fold desc="Move dice in board">
    /**
     * Set cel[i][j] at null
     * @param i ascissa
     * @param j ordinata
     */
    public void emptyCell(int i, int j) {
        board[i][j].setFrontDice(null);
    }

    /**
     *Move one dice from pos_in to pos_fin
     * @param pos_in start position
     * @param pos_end final position
     * @param level level of movement
     * @throws IllegalDiceException
     */
    public void moveDice(Coordinates pos_in, Coordinates pos_end, int level) throws IllegalDiceException {
        Cell c1=board[pos_in.getI()][pos_in.getJ()];
        Dice dice=c1.getFrontDice();
        if(dice!=null) {
            emptyCell(pos_in.getI(), pos_in.getJ());
            try {
                addDice(pos_end.getI(),pos_end.getJ(),dice,level);
            }catch(Exception ex) {
                addDice(pos_in.getI(),pos_in.getJ(),dice,-1);
                throw new IllegalDiceException();
            }
        } else {
            throw new IllegalDiceException();
        }

    }
    //</editor-fold>

    //<editor-fold desc="Utilities">
    public Cell getCell (int i , int j)
    {
        return board[i][j];
    }

    public int getRows ()
    {
        return rows;
    }

    public int getCols ()
    {
        return cols;
    }

    public Cell[][] getGrid ()
    {
        return board;
    }

    @Override
    public String toString ()
    {
        String ret = "";
        for (int i = 0 ; i < rows ; i++)
        {
            for (int j = 0; j < cols; j++)
            {
                if (board[i][j].getFrontDice() == null)
                    ret = ret + " p:" + board[i][j].getPlacement().getValue() + "-" + board[i][j].getPlacement().getColor() + "\t|\t";
                else
                    ret = ret + " D:" + board[i][j].getFrontDice().getValue() + "-" + board[i][j].getFrontDice().getColor() + "\t|\t";
            }

            ret = ret + "\n";
        }
        return ret;
    }

    public Pair[][] getPairMatrix ()
    {
        Pair[][] mat = new Pair[rows][cols];
        for (int i = 0; i < rows ; i++)
            for (int j = 0; j<cols ; j++)
                mat[i][j] = new Pair(board[i][j].getDiceValue(),board[i][j].getDiceColor());
        return mat;
    }

    public int getDifficult() {
        return difficult;
    }


    /**
     * Count and return number of blank cell for count of points
     * @return number of blank cells
     */
    public int getNumberBlankCell ()
    {
        int cont = 0;
        for (int i = 0 ; i < rows ; i++)
            for (int j = 0; j < cols; j++)
                if (board[i][j].getFrontDice() ==  null)
                    cont++;
        return cont;
    }
    //</editor-fold>
}
