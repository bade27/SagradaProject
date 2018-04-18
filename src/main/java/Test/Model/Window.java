package Test.Model;

import Test.Exceptions.IllegalDiceException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.awt.*;
import java.io.File;

public class Window
{
    private int difficult;
    private int id;
    private Cell board [][];
    private String name;
    private final int rows = 4;
    private final int cols = 5;
    private boolean firstTurn;
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
    public Window (String path)
    {
        boardPath = path;
        firstTurn = true;
        board =new Cell [rows][cols];
        readXML(path);
    }

    /**
     * Aggiunge alla window il Dice passato come parametro agli indici i e j swe rispetta i vincoli di gioco,altrimenti invoca un exception
     * @param i Riga in cui aggiungere il Dice
     * @param j Colonna in cui aggiungere il Dice
     * @param d Dice da Aggiungere
     * @throws IllegalDiceException
     */
    public void addDice (int i , int j , Dice d) throws IllegalDiceException
    {
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


    public Cell[][] getGrid() {
        return this.board;
    }

    public Dice getDice (int i , int j)
    {
        return board[i][j].getCurrentDice();
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

    /**
     * Legge il file xml passato come parametro e inizializza la board di gioco
     * @param path path del file xml di inizializzazione
     */
    private void readXML (String path)
    {
        try
        {
            File file = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);

            //ora è un po' più efficiente anche qui (prendo solo i tag che mi servono, senza fare la lista di tutti
            int id = Integer.parseInt(document.getElementsByTagName("id").item(0).getTextContent());
            String name = document.getElementsByTagName("name").item(0).getTextContent();
            int difficult = Integer.parseInt(document.getElementsByTagName("difficulty").item(0).getTextContent());

            //sono due liste che contengono l'elenco dei tag del file (hanno stessa lunghezza)
            //in questo modo posso leggere in parallelo i valori delle celle
            NodeList values = document.getElementsByTagName("value");
            NodeList colors = document.getElementsByTagName("color");
            //NodeList imgs = document.getElementsByTagName("img_source");

            //il parametro k mi serve come indice delle due liste valori e colori
            for(int i = 0, k = 0; i < rows; i++)
            {
                for(int j = 0; j < cols; j++)
                {
                    int currentValue = Integer.parseInt(values.item(k).getTextContent());
                    String currentColor = colors.item(k).getTextContent();
                    if (currentColor.equals("dc"))
                        board[i][j] = new Cell(new Dice(currentValue));
                    else
                    {
                        if (currentColor.equals("rosso"))
                            board[i][j] = new Cell(new Dice(currentValue,Color.red));
                        if (currentColor.equals("verde"))
                            board[i][j] = new Cell(new Dice(currentValue,Color.green));
                        if (currentColor.equals("giallo"))
                            board[i][j] = new Cell(new Dice(currentValue,Color.yellow));
                        if (currentColor.equals("azzurro"))
                            board[i][j] = new Cell(new Dice(currentValue,Color.blue));
                        if (currentColor.equals("viola"))
                            board[i][j] = new Cell(new Dice(currentValue,Color.magenta));
                    }
                    k++;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
