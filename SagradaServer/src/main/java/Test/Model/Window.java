package Test.Model;

import Test.Exceptions.IllegalDiceException;
import Test.Exceptions.ParserXMLException;

public class Window
{
    private int difficult;
    private int id;
    private Cell board [][];
    private String name;
    public final int rows = 4;
    public final int cols = 5;
    private boolean firstTurn;//Indica se il dado da inserire deve per forza stare sul bordo (primo inserimento)
    private String boardPath;

    /*public Window ()
    {
        board =new Cell [rows][cols];
        difficult = 0;
        name = "";
        id = 0;
        for (int i =0;i < rows ; i++)
            for (int j = 0;j < cols;j++)
                board[i][j] = new Cell ();
        firstTurn = true;
    }*/

    /**
     * Genera una nuova window da file xml
     * @param path path del file xml per la generazione della window+
     */
    public Window (String path) throws ParserXMLException
    {
        boardPath = path;
        firstTurn = true;
        board = new Cell [rows][cols];
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
    public void addDice (int i , int j , Dice d,int level) throws IllegalDiceException
    {
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
                //Controllo se il backDice è compatibile con il FrontDice
                if (!board[i][j].setDice(d))
                    throw new IllegalDiceException("Dado posizionato su una casella non compatibile");
                else
                    firstTurn = false;
            }
            else
                throw new IllegalDiceException("Primo dado non posizionato sul bordo");
        }
        else
        {
            //Controllo se già presente un currentDice
            if (board[i][j].getFrontDice() != null)
                throw new IllegalDiceException("Dado posizionato sopra ad un altro");

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
                    throw new IllegalDiceException("Dado posizionato su una casella non compatibile");
                return;
            }
            else
                throw new IllegalDiceException("Dado posizionato in prossimità di nessun altro compatibile");
        }
    }


    public Dice getDice (int i , int j)
    {
        return board[i][j].getCurrentDice();
    }

    public Cell getCell (int i , int j)
    {
        return board[i][j];
    }

    public void initializeWindow (String path) throws ParserXMLException
    {
        board = ParserXML.readWindowFromPath(path,board);
    }

    public int getRows ()
    {
        return rows;
    }

    public int getCols ()
    {
        return cols;
    }


}
