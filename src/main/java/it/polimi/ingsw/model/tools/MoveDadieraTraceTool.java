package it.polimi.ingsw.model.tools;

import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.RoundTrace;

public class MoveDadieraTraceTool extends Tools {


    public MoveDadieraTraceTool() {
        this.price = 1;
    }

    public void exchangeDice(Dice d_dad, Dadiera s, Dice d_round, int pos_trace, RoundTrace rt){
        s.deleteDice(d_dad);
        rt.addDice(pos_trace,d_dad);
        rt.deleteDice(pos_trace,d_round);
        s.addDice(d_round);
        setPrice();
    }

    /**
     * if the price if one it change price=2
     */
    void setPrice(){
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
