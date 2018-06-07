package it.polimi.ingsw.model.tools;

//Tool nr.5 - 9 - 11

import it.polimi.ingsw.exceptions.IllegalStepException;
import it.polimi.ingsw.model.Dice;

public class MoveDadieraTraceTool extends Tools {


    public MoveDadieraTraceTool(int id,String name) {

        this.price = 1;
        this.id =id;
        this.name=name;
    }

    @Override
    public void use() throws IllegalStepException
    {
        switch (id)
        {   //case 2 e case 3 si chiamano la stessa funzione, che si comporta in modo diverso a seconda del id
            case 5:
                exchangeDice();
                break;
            /*case 9:
                setDieNoRestriction();
                break;*/
            default:
                break;

        }

    }

    /**
     * Tool nr. 5
     */
    public void exchangeDice() throws IllegalStepException
    {
        try
        {
            dadiera.deleteDice(d1);
            rt.addDice(pos_rt,d1);
            rt.deleteDice(pos_rt,d2);
            dadiera.addDice(d2);
            setPrice();
        }catch (Exception e){
            throw new IllegalStepException();
        }

    }

    //Manca il setCanMove(false)
    /*public void setDieNoRestriction () throws IllegalStepException
    {
        if(window == null || pos_fin1 == null || d1 == null)
            throw new IllegalStepException();

        try{
            window.addDice(pos_fin1[0],pos_fin1[1],d1,3);
            dadiera.deleteDice(d1);
            //adapter.setCanMove(false)
        }catch (Exception e){
            throw new IllegalStepException();
        }
    }*/





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
