package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.NotEnoughDiceException;
import it.polimi.ingsw.remoteInterface.Pair;

import java.util.ArrayList;

public class Dadiera
{
    private ArrayList<Dice> diceList;
    private DiceBag bag;

    /**
     * Generate a new Dadiera item
     */
    public Dadiera ()
    {
        bag = new DiceBag();
        diceList = null;
    }

    /**
     * Generate a new set of dice randomly with dim of numGioc
     */
    public synchronized void mix (int numGioc) throws NotEnoughDiceException {
        diceList = bag.pickDices(numGioc*2 + 1);
        assert diceList != null;
    }

    /**
     * Delete passed die from dadiera
     * @param d die to delete
     */
    public synchronized void deleteDice (Dice d)
    {
        for (int i = 0; i < diceList.size(); i++)
            if (diceList.get(i).isEqual(d))
            {
                diceList.remove(i);
                return;
            }
    }

    /**
     * add dice x
     * @param x index of the dice that i must add
     */
    public synchronized void addDice (Dice x){
        diceList.add(x);
    }


    /**
     * Set die's value to n
     * @param n value
     * @param d die
     * @throws IllegalDiceException
     */
    public synchronized void setDiceValue(int n, Dice d) throws IllegalDiceException {
        if(n>0&&n<7) {
            for (int i = 0; i < diceList.size(); i++)
                if (d.isEqual(diceList.get(i))) {
                    diceList.get(i).setValue(n);
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
    public synchronized Dice getDice (int i) throws IllegalDiceException
    {
        try {
            return diceList.get(i);
        }
        catch (Exception ex) {
            throw new IllegalDiceException("Dice not init");
        }

    }

    public DiceBag getBag() {
        return bag;
    }

    public synchronized Pair[] toPairArray ()
    {
        Pair[] arr = new Pair[diceList.size()];
        for (int i = 0; i < diceList.size() ; i++)
            arr[i] = new Pair(diceList.get(i).getValue(), diceList.get(i).getColor());
        return arr;
    }

    /**
     * return entire list of dice
     * @return
     */
    public synchronized ArrayList<Dice> getDiceList() { return diceList; }

    /**
     *
     * @return dice inside item on string format
     */
    @Override
    public String toString() {
        String str =  "Dadiera vuota!";
        if(diceList != null) {
            str = "Dadiera{";
            for (int i = 0; i < diceList.size(); i++) {
                str += diceList.get(i).toString();
                if (i != (diceList.size() - 1))
                    str += ", ";
            }
            str += '}';
        }
        return str;
    }
}
