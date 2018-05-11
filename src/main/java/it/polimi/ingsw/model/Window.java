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
    private boolean firstTurn;//Indica se il dado da inserire deve per forza stare sul bordo (primo inserimento)
    private String boardPath;

    /**
     * Genera una nuova window da file xml
     * @param path path del file xml per la generazione della window+
     */
    public Window (String path) throws ParserXMLException
    {
        boardPath = path;
        firstTurn = true;
        board = new Cell[rows][cols];
        board = ParserXML.readWindowFromPath(boardPath,board);
    }

    /**
     * Aggiunge alla window il Dice passato come parametro agli indici i e j swe rispetta i vincoli di gioco,altrimenti invoca un exception
     * @param i Riga in cui aggiungere il Dice
     * @param j Colonna in cui aggiungere il Dice
     * @param d Dice da Aggiungere
     * @param level livello di controllo da adottare nell'inseriemnto (-1: nessun controllo ; 0:tutti controlli)
     * @throws IllegalDiceException
     */
    public void addDice (int i , int j , Dice d, int level) throws IllegalDiceException
    {
        if (i < 0 || j < 0 || i > rows-1 || j > cols-1 )
            throw new IllegalDiceException("Position out of bound");

        if (level == -1)
        {
            board[i][j].setFrontDice(d);
            return;
        }
        //Controllo se il primo dado immesso sia inserito sul bordo
        if (firstTurn)
        {
            if ((i == 0 || i == rows - 1 || j == 0 || j == cols - 1))
            {
                //Controllo se il placement è compatibile con il FrontDice
                if (!board[i][j].setDice(d))
                    throw new IllegalDiceException("Die not placed on compatible cell");
                else
                    firstTurn = false;
            }
            else
                throw new IllegalDiceException("First die not placed on edge");
        }
        else
        {
            //Controllo se già presente un currentDice
            if (board[i][j].getFrontDice() != null)
                throw new IllegalDiceException("Die placed on another one");

            boolean near=false;
            boolean noSimilar = true;

            //Controllo su posizionamento di dadi con colore/valore adiacenti
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

            if (near && noSimilar)
            {
                if (!board[i][j].setDice(d))
                    throw new IllegalDiceException("Die not placed on compatible cell");
                return;
            }
            else
                throw new IllegalDiceException("Die not placed near a compatible one");
        }
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