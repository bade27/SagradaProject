package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.remoteInterface.Pair;

import java.awt.*;
import java.util.ArrayList;

public class Dadiera
{
    private ArrayList<Dice> listaDadi;
    private final Color [] listaColori = {null,Color.red,Color.blue,Color.magenta,Color.yellow,Color.green};
    private DiceBag bag;

    /**
     * Generate a new Dadiera item
     */
    public Dadiera ()
    {
        bag = new DiceBag();
        listaDadi = null;
    }

    /**
     * Generate a new set of dice randomly with dim of numGioc
     */
    public void mix (int numGioc)
    {
        listaDadi = bag.pickDices(numGioc*2 + 1);
        assert listaDadi != null;
    }

    /**
     * Delete passed die from dadiera
     * @param d die to delete
     */
    public void deleteDice (Dice d)
    {
        for (int i=0;i < listaDadi.size();i++)
            if (listaDadi.get(i).isEqual(d))
            {
                listaDadi.remove(i);
                return;
            }
    }

    /**
     * add dice x
     * @param x index of the dice that i must add
     */
    public void addDice (Dice x){
        listaDadi.add(x);
    }


    /**
     * Set die's value to n
     * @param n value
     * @param d die
     * @throws IllegalDiceException
     */
    public void setDiceValue(int n, Dice d) throws IllegalDiceException {
        if(n>0&&n<7) {
            for (int i = 0; i < listaDadi.size(); i++)
                if (d.equals(listaDadi.get(i))) {
                    listaDadi.get(i).setValue(n);
                    break;
                }
        }else throw new IllegalDiceException();
    }

    /**
     * Return die in i position
     * @param i position of occurred die
     * @return die selected
     * @throws IllegalDiceException die not present
     */
    public Dice getDice (int i) throws IllegalDiceException
    {
        try {
            return listaDadi.get(i);
        }
        catch (Exception ex) {
            throw new IllegalDiceException("Dice not init");
        }

    }

    public Pair[] toPairArray ()
    {
        Pair[] arr = new Pair[listaDadi.size()];
        for (int i = 0; i < listaDadi.size() ; i++)
            arr[i] = new Pair(listaDadi.get(i).getValue(),listaDadi.get(i).getColorEnum());
        return arr;
    }

    /**
     * return entire list of dice
     * @return
     */
    public ArrayList<Dice> getListaDadi() { return listaDadi; }

    /**
     *
     * @return dice inside item on string format
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
