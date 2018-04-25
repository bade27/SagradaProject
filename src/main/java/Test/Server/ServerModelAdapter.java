package Test.Server;
import Test.Exceptions.IllegalDiceException;
import Test.Exceptions.ModelException;
import Test.Exceptions.ParserXMLException;
import Test.Model.Dadiera;
import Test.Model.Window;
import Test.Model.Dice;

public class ServerModelAdapter
{
    private Window board;
    private Dadiera dadiera;

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
    public void addDiceToBoard (int i,int j,Dice d) throws ModelException
    {
        try {
            board.addDice(i,j,d,0);
        }
        catch (IllegalDiceException ex) {
            throw new ModelException("Impossible to place dice: " + ex.getMessage());
        }
    }


}
