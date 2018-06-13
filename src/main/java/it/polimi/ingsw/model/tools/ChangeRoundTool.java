package it.polimi.ingsw.model.tools;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.IllegalStepException;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.server.TokenTurn;

public class ChangeRoundTool extends Tools
{
    public ChangeRoundTool(int id, String name)
    {
        this.price = 1;
        this.id =id;
        this.name=name;
    }


    @Override
    public void use() throws IllegalStepException, IllegalDiceException
    {
        TokenTurn token = adapter.getToken();
        String user = adapter.getUser();
        if (!token.useToolNumber8(user))
            throw new IllegalStepException("tool utilizzabile durante il primo giro del turno");
    }

    @Override
    void setPrice(){
        if(price==1)
            price++;
    }

    @Override
    public int getPrice() {
        return price;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public boolean canPlaceDie(Dice d) {
        return true;
    }
}
