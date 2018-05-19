package it.polimi.ingsw.model.tools;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;
import java.util.Random;

public class SetValueTool extends Tools {


    public SetValueTool() {
        this.price = 1;
    }


    /**
     * The dice value increase (i=0) or decrease (i=1)by one
     *
     * @param i index
     * @param x die
     * @param s dadiera that contains x
     * @throws IllegalStepException
     * @throws IllegalDiceException
     */

    public void addSub(Dice x, Dadiera s, int i) throws IllegalStepException, IllegalDiceException {
        int value = x.getValue();
        if (i==0) {
            if(value<6) {
                s.setDiceValue(value + 1, x);
                setPrice();
            } else throw new IllegalStepException();

        }    else if (i==1) {
            if (value>1) {
                s.setDiceValue(value - 1, x);
                setPrice();
            } else throw new IllegalStepException();
        } else throw new IllegalStepException();
    }

    /**
     * turn the die x
     *
     * @param x die that it turn
     * @param s dadiera that contains x
     * @throws IllegalDiceException
     */
    public void turnDice(Dice x, Dadiera s) throws IllegalDiceException {
        int value = x.getValue();
            s.setDiceValue(7 - value, x);
            setPrice();
    }

    /**
     * relaunch the die
     * @param x die
     * @param s dadiera that contains x
     * @throws IllegalDiceException
     */
    public void relaunchDice(Dice x, Dadiera s) throws IllegalDiceException {
            s.setDiceValue(new Random().nextInt(6) + 1, x);
            setPrice();
    }

    /**
     * if the price if one it change price=2
     */
    protected void setPrice(){
        if(price==1)
            price++;
    }

    /**
     * Ritorna il costo del tool
     * @return *il prezzo del tool*
     */
    public int getPrice(){
        return price;
    }
}