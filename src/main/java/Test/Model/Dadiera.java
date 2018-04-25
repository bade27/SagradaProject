package Test.Model;

import Test.Exceptions.IllegalDiceException;
import Test.Model.Dice;

import java.awt.*;
import java.util.ArrayList;

public class Dadiera
{
    private ArrayList<Dice> listaDadi;
    private final Color [] listaColori = {null,Color.red,Color.blue,Color.magenta,Color.yellow,Color.green};
    private DiceBag bag;
    private int numGioc;

    /**
     * Genera una nuova dadiera per il numero di fiocatori passati
     * @param n numero di giocatori
     */
    public Dadiera (int n)
    {
        numGioc = n;
        bag = new DiceBag();
        listaDadi = null;
    }

    /**
     * Genera un nuovo set di dadi casualmente
     */
    public void mix ()
    {
        listaDadi = bag.pickDices(numGioc*2 + 1);
    }

    public void deleteDice (Dice d)
    {
        for (int i=0;i < listaDadi.size();i++)
            if (listaDadi.get(i).isEqual(d))
            {
                listaDadi.remove(i);
                return;
            }
    }

    public Dice getDice (int i) throws IllegalDiceException
    {
        try {
            return listaDadi.get(i);
        }
        catch (Exception ex) {
            throw new IllegalDiceException("Dice not init");
        }

    }

}
