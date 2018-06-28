package it.polimi.ingsw.model.tools;

//Tool nr.5 - 9

import it.polimi.ingsw.exceptions.IllegalStepException;
import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.RoundTrace;
import it.polimi.ingsw.model.Window;

public class MoveDadieraTool extends Tools {

    private Dadiera dadiera;

    public MoveDadieraTool(int id, String name) {

        this.price = 1;
        this.id =id;
        this.name=name;
    }

    @Override
    public void use() throws IllegalStepException
    {
        dadiera = adapter.getDadiera();
        switch (id)
        {
            case 5:
                exchangeDice();
                break;
            case 9:
                setDieNoRestriction();
                break;
            default:
                break;

        }

    }

    /**
     * Tool nr. 5
     */
    private void exchangeDice() throws IllegalStepException
    {
        RoundTrace roundTrace = adapter.getRoundTrace();
        if (d1 == null || d2 == null || pos_rt < 0 || pos_rt > 10 )
            throw new IllegalStepException("Riprova selezionando tutti i parametri del tool");
        try
        {
            dadiera.deleteDice(d1);
            roundTrace.addDice(pos_rt,d1);
            roundTrace.deleteDice(pos_rt,d2);
            dadiera.addDice(d2);
            setPrice();
        }catch (Exception e){
            throw new IllegalStepException();
        }

    }

    /**
     * tool nr. 9
     */
    private void setDieNoRestriction () throws IllegalStepException
    {
        Window window = adapter.getBoard();
        if(pos_iniz1 == null || d1 == null)
            throw new IllegalStepException("Riprova selezionando tutti i parametri del tool");

        try{
            window.addDice(pos_iniz1.getI(),pos_iniz1.getJ(),d1,3);
            dadiera.deleteDice(d1);
            adapter.setCanMove(false);
        }catch (Exception e){
            throw new IllegalStepException("Impossibile eseguire il tool coi parametri selezionati");
        }
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
