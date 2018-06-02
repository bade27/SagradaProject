package it.polimi.ingsw.model.tools;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.*;
import java.util.Random;

public class SetValueTool extends Tools {

    public SetValueTool(String type, String name) {  //qua avrò oltre a type un array con dentro i dati
        this.price = 1;                  // necessari a usare i metodi
        this.type = type;
        this.name=name;
    }


    @Override
    public void use() throws IllegalStepException, IllegalDiceException {
        switch (type)
        {case "tool1":
            addSub();
            break;
         case "tool6":
             turnDice();
                break;
          case "tool10":
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



    private void addSub() throws IllegalStepException, IllegalDiceException {
        int value = d1.getValue();
        if (index==0) {
            if(value<6) {
                dadiera.setDiceValue(value + 1, d1);
                setPrice();
            } else throw new IllegalStepException();

        }    else if (index==1) {
            if (value>1) {
                dadiera.setDiceValue(value - 1, d1);
                setPrice();
            } else throw new IllegalStepException();
        } else throw new IllegalStepException();
    }

    /**
     * turn the die x
     *
     * @throws IllegalDiceException
     */
    private void turnDice(/*Dice x, Dadiera s*/) throws IllegalDiceException {
        int value = d1.getValue();
            dadiera.setDiceValue(7 - value,d1);
            setPrice();
    }

    /**
     * relaunch the die

     * @throws IllegalDiceException
     */
    private void relaunchDice(/*Dice x, Dadiera s*/) throws IllegalDiceException {
            dadiera.setDiceValue(new Random().nextInt(6) + 1, d1);
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
}