package it.polimi.ingsw.model.tools;

//Tool nr. 1-6-10

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.IllegalStepException;
import it.polimi.ingsw.model.Dice;

import java.util.Random;

public class SetValueTool extends Tools {

    private Dice remember;

    public SetValueTool(int id, String name) {  //qua avrò oltre a id un array con dentro i dati
        this.price = 1;                  // necessari a usare i metodi
        this.id = id;
        this.name=name;
    }

    @Override
    public void use() throws IllegalStepException {
        switch (id)
        {case 1:
            addSub();
            break;
         case 6:
             relaunchDice();
                break;
          case 10:
             turnDice();
                break;
            default:
                break;
        }
    }

    /**
     * increments or decrements the value of the selected dice
     * @throws IllegalStepException in case the move is invalid
     */
    private void addSub() throws IllegalStepException {
        if(instruction == null || d1 == null || dadiera == null)
            throw new IllegalStepException();

        int value = d1.getValue();
        int toSum;

        if(instruction.equals("inc")) {
            toSum = 1;
        } else {
            toSum = -1;
        }

        try {
            dadiera.setDiceValue(value + toSum, d1);
        } catch (IllegalDiceException ide) {
            throw new IllegalStepException();
        }

        setPrice();
    }

    /**
     * turn the die x
     *
     * @throws IllegalStepException
     */
    private void turnDice(/*Dice x, Dadiera s*/) throws IllegalStepException {
        if(d1 == null || dadiera == null)
            throw new IllegalStepException();

        int value = d1.getValue();
        try {
            dadiera.setDiceValue(7 - value, d1);
        } catch (IllegalDiceException ise) {
            throw new IllegalStepException();
        }
        setPrice();
    }

    /**
     * Tool nr. 6 function
     */
    private void relaunchDice(/*Dice x, Dadiera s*/) throws IllegalStepException {
        if(d1 == null || dadiera == null)
            throw new IllegalStepException();
        int v;
        try {
            v = new Random().nextInt(6) + 1;
            dadiera.setDiceValue(v, d1);
        } catch (IllegalDiceException ide) {
            throw new IllegalStepException();
        }
        remember = new Dice(v,d1.getColor());
        finished = false;
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
     * Return tool's price
     * @return *tool's price*
     */
    public int getPrice(){
        return price;
    }

    public int getId(){
        return id;
    }

    @Override
    public boolean canPlaceDie(Dice d) {
        if (id == 6)
        {
            if (remember == null)
                return false;
            if (remember.isEqual(d))
                return true;
            return false;
        }
        return true;
    }
}