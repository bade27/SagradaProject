package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.utilities.ParserXML;

import java.awt.*;

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
    }

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
            throw new IllegalDiceException("Position out of bound");

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
                    throw new IllegalDiceException("Die not placed on compatible cell");
                else
                    firstTurn = false;
            }
            else
                throw new IllegalDiceException("First die not placed on edge");
        }
        else
        {
            //Control if die position is occupied
            if (board[i][j].getFrontDice() != null)
                throw new IllegalDiceException("Die placed on another one");

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
                    throw new IllegalDiceException("Die not placed on compatible cell");
            }

            else if (near && noSimilar) {
                    if (!setDie(i, j, d, level))
                        throw new IllegalDiceException("Die not placed on compatible cell");
            }

            else
                throw new IllegalDiceException("Die not placed near a compatible one");
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
                    ret = ret + " p:" + board[i][j].getPlacement().getValue() + "-" + getColor(board[i][j].getPlacement()) + "|";
                else
                    ret = ret + " D:" + board[i][j].getFrontDice().getValue() + "-" + getColor(board[i][j].getFrontDice()) + "|";
            }

            ret = ret + "\n";
        }
        return ret;
    }

    private String getColor (Placement p)
    {
        if (p.getColor() == Color.RED)
            return "Red    ";
        if (p.getColor() == Color.BLUE)
            return "Blue   ";
        if (p.getColor() == Color.YELLOW)
            return "Yellow ";
        if (p.getColor() == Color.MAGENTA)
            return "Violet ";
        if (p.getColor() == Color.GREEN)
            return "Green  ";
        return "n/d    ";
    }

    private String getColor (Dice p)
    {
        if (p.getColor() == Color.RED)
            return "Red    ";
        if (p.getColor() == Color.BLUE)
            return "Blue   ";
        if (p.getColor() == Color.YELLOW)
            return "Yellow ";
        if (p.getColor() == Color.MAGENTA)
            return "Violet ";
        if (p.getColor() == Color.GREEN)
            return "Green  ";
        return "n/d    ";
    }

}
