package it.polimi.ingsw.model.tools;

//Tool nr. 1-6-7-10-11

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.IllegalStepException;
import it.polimi.ingsw.exceptions.NotEnoughDiceException;
import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.server.TokenTurn;

import java.util.Random;

public class SetValueTool extends Tools {

    private Dice remember;
    private Dadiera dadiera;
    private TokenTurn token;

    //temporaryDie stores a die to be removed from dadiera - prevents misuse of the tool
    private Dice temporaryDie;

    public SetValueTool(int id, String name) {  //qua avrò oltre a id un array con dentro i dati
        this.price = 1;                  // necessari a usare i metodi
        this.id = id;
        this.name=name;
        completeDice = true;
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
            case 11:
                dadieraToDiceBag();
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
            throw new IllegalStepException("Impossibile eseguire il tool coi parametri selezionati");
        }
    }

    /**
     * Tool nr. 1 function
     * @throws IllegalStepException if the action does not end well
     */
    private void addSub() throws IllegalStepException
    {
        if(instruction == null || d1 == null)
            throw new IllegalStepException("Riprova selezionando tutti i parametri del tool");
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
            throw new IllegalStepException("Riprova selezionando tutti i parametri del tool");
        int v = new Random().nextInt(6) + 1;

        setDiceValueDadera(v,d1);

        remember = new Dice(v,d1.getColor());
        finished = false;
        setPrice();
    }

    /**
     * Tool nr.10 function
     * @throws IllegalStepException if the action does not end well
     */
    private void turnDice() throws IllegalStepException
    {
        if(d1 == null)
            throw new IllegalStepException("Riprova selezionando tutti i parametri del tool");

        int value = 7 - d1.getValue();
        setDiceValueDadera(value,d1);
        setPrice();
    }

    /**
     * Tool nr.7
     * @throws IllegalStepException if the action does not end well
     */
    private void relaunchAllDadiera () throws IllegalStepException
    {
        token = adapter.getToken();
        String user = adapter.getUser();
        if (!token.isMySecondRound(user))
            throw new IllegalStepException("tool utilizzabile durante il secondo giro del turno");

        for (int i = 0; i < dadiera.getDiceList().size() ; i++)
        {
            int v = new Random().nextInt(6) + 1;
            setDiceValueDadera(v,dadiera.getDiceList().get(i));
        }
        setPrice();
    }

    /**
     * Tool nr. 11
     * @throws IllegalStepException if the action does not end well
     */
    private void dadieraToDiceBag() throws IllegalStepException {
        if (d1 == null) {
            if(temporaryDie != null)
                dadiera.addDice(temporaryDie);
            remember = null;
            throw new IllegalStepException("Impossibile eseguire il tool coi parametri selezionati");
        }


        if(completeDice) {

            try {
                temporaryDie = d1.cloneDice();
                dadiera.deleteDice(d1);
                Dice d = dadiera.getBag().pickADie();
                dadiera.getBag().putADie(d1);
                color = d.getColor().toString().toLowerCase();
            } catch (NotEnoughDiceException nede) {
                throw new IllegalStepException("Impossibile eseguire il tool coi parametri selezionati");
            }
            completeDice = false;
        } else {
            dadiera.addDice(d1);
            remember = new Dice(d1.getValue(), d1.getColor());
            completeDice = true;
            finished = false;
            setPrice();
        }

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
        if (id == 6 || id == 11)
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