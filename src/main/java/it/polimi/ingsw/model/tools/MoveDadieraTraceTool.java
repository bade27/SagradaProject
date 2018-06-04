package it.polimi.ingsw.model.tools;

//Tool nr.5

import it.polimi.ingsw.model.Dice;

public class MoveDadieraTraceTool extends Tools {


    public MoveDadieraTraceTool(int id,String name) {

        this.price = 1;
        this.id =id;
        this.name=name;
    }

    @Override
    public void use() {
        exchangeDice();
    }

    /**
     * exchange one die in Dadiera and one die in RoundTrace

     */
    public void exchangeDice(){
        dadiera.deleteDice(d1);              //d1=dado dadiera
        rt.addDice(pos_rt,d1);               //pos_fin=posizione tracciato round
        rt.deleteDice(pos_rt,d2);            //d2=dado tracciato round
        dadiera.addDice(d2);
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

    public int getId(){
        return id;
    }

    @Override
    public boolean canPlaceDie(Dice d) {
        return true;
    }

}
