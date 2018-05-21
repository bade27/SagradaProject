package it.polimi.ingsw.model.tools;

import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.RoundTrace;

public class MoveDadieraTraceTool extends Tools {


    public MoveDadieraTraceTool() {
        this.price = 1;
    }

    /**
     * exchange one die in Dadiera and one die in RoundTrace
     * @param d_dad die in Dadiera
     * @param s Dadiera
     * @param d_round die in RoundTrace
     * @param pos_trace position in RoundTrace, in this position i have to extract the die
     * @param rt RoundTrace
     */
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
