package it.polimi.ingsw.model.tools;

//Tool nr. 1-6-10

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.IllegalStepException;
import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.server.TokenTurn;

import java.util.Random;

public class SetValueTool extends Tools {

    private Dice remember;
    private Dadiera dadiera;
    private TokenTurn token;

    public SetValueTool(int id, String name) {  //qua avrò oltre a id un array con dentro i dati
        this.price = 1;                  // necessari a usare i metodi
        this.id = id;
        this.name=name;
    }

    @Override
    public void use() throws IllegalStepException {
        dadiera = adapter.getDadiera();
        switch (id)
        {
            case 1:
                addSub();
                break;
            case 6:
                relaunchDice();
                break;
            case 7:
                relaunchAllDadiera();
                break;
            case 10:
                turnDice();
                break;
            default:
                break;
        }
    }

    private void setDiceValueDadera (int val , Dice d) throws IllegalStepException
    {
        try {
            dadiera.setDiceValue(val, d);
        } catch (IllegalDiceException ide) {
            throw new IllegalStepException();
        }
    }

    /**
     * Tool nr. 1 function
     */
    private void addSub() throws IllegalStepException
    {
        if(instruction == null || d1 == null)
            throw new IllegalStepException();
        int value = d1.getValue();

        if(instruction.equals("inc"))
            setDiceValueDadera(value + 1, d1);
        else
            setDiceValueDadera(value + -1, d1);

        setPrice();
    }

    /**
     * Tool nr. 6 function
     */
    private void relaunchDice() throws IllegalStepException
    {
        if(d1 == null)
            throw new IllegalStepException();
        int v = new Random().nextInt(6) + 1;

        setDiceValueDadera(v,d1);

        remember = new Dice(v,d1.getColor());
        finished = false;
        setPrice();
    }

    /**
     * Tool nr.10 function
     */
    private void turnDice() throws IllegalStepException
    {
        if(d1 == null)
            throw new IllegalStepException();

        int value = 7 - d1.getValue();
        setDiceValueDadera(value,d1);
        setPrice();
    }

    /**
     * Tool nr.7
     */
    private void relaunchAllDadiera () throws IllegalStepException
    {
        token = adapter.getToken();
        String user = adapter.getUser();
        if (!token.isMySecondRound(user))
            throw new IllegalStepException("Can't use tool in your first round");

        for (int i = 0; i < dadiera.getListaDadi().size() ; i++)
        {
            int v = new Random().nextInt(6) + 1;
            setDiceValueDadera(v,dadiera.getListaDadi().get(i));
        }
        setPrice();
    }

    private void DadieraToDiceBag() throws IllegalStepException {
        /*if(d1 == null)
            throw new IllegalStepException();

        try {
            dadiera.deleteDice(d1);
            Dice d = dadiera.getBag().pickADie();
            dadiera.getBag().putADie(d1);
            dadiera.addDice(d);
        } catch (NotEnoughDiceException nede) {
            throw new IllegalStepException();
        }
        finished = false;
        setPrice();*/

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