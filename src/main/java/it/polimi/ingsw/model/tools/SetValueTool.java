package it.polimi.ingsw.model.tools;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.IllegalStepException;

import java.util.Random;

public class SetValueTool extends Tools {

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
             turnDice();
                break;
          case 10:
             relaunchDice();
                break;
            default:
                break;
        }
    }

    /**
     * The dice value increase (i=0) or decrease (i=1)by one
     *

     * @throws IllegalStepException
     * @throws IllegalDiceException
     */


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

        /*if (index==0) {
            if(value<6) {
                dadiera.setDiceValue(value + 1, d1);
                setPrice();
            } else throw new IllegalStepException();

        }    else if (index==1) {
            if (value>1) {
                dadiera.setDiceValue(value - 1, d1);
                setPrice();
            } else throw new IllegalStepException();
        } else throw new IllegalStepException();*/
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
     * relaunch the die

     * @throws IllegalDiceException
     */
    private void relaunchDice(/*Dice x, Dadiera s*/) throws IllegalStepException {
        if(d1 == null || dadiera == null)
            throw new IllegalStepException();

        try {
            dadiera.setDiceValue(new Random().nextInt(6) + 1, d1);
        } catch (IllegalDiceException ide) {
            throw new IllegalStepException();
        }
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
}