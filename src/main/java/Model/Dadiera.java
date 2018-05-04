package Model;

import Exceptions.IllegalDiceException;

import java.awt.*;
import java.util.ArrayList;

public class Dadiera
{
    private ArrayList<Dice> listaDadi;
    private final Color [] listaColori = {null,Color.red,Color.blue,Color.magenta,Color.yellow,Color.green};
    private DiceBag bag;

    /**
     * Genera una nuova dadiera per il numero di fiocatori passati
     */
    public Dadiera ()
    {
        bag = new DiceBag();
        listaDadi = null;
    }

    /**
     * Genera un nuovo set di dadi casualmente
     */
    public void mix (int numGioc)
    {
        listaDadi = bag.pickDices(numGioc*2 + 1);
        assert listaDadi != null;
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

    public ArrayList<Dice> getListaDadi() {
        return listaDadi;
    }

    /**
     *
     * @return i dadi presenti sull'area di gioco in formato String
     */
    @Override
    public String toString() {
        String str =  "Dadiera vuota!";
        if(listaDadi != null) {
            str = "Dadiera{";
            for (int i = 0; i < listaDadi.size(); i++) {
                str += listaDadi.get(i).toString();
                if (i != (listaDadi.size() - 1))
                    str += ", ";
            }
            str += '}';
        }
        return str;
    }
}
