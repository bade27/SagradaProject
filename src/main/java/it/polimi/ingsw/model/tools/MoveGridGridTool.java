package it.polimi.ingsw.model.tools;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.IllegalStepException;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.Window;

//Tool nr. 2-3-4-12


public class MoveGridGridTool extends Tools {

    private int level;
    private Window window;

    public MoveGridGridTool(int id, String name) {
        this.level = 0;
        this.price = 1;
        this.id =id;
        this.name=name;
    }

    @Override
    public void use() throws IllegalStepException, IllegalDiceException {

        window = adapter.getBoard();

        if(id == 2)
            level = 1;
        else if(id == 3)
            level = 2;

        switch (id)
        {
            //case 2 and 3 call the same function, what changes is the behaviour
            case 2:
                //...
            case 3:
                moveOneDieTool();
                break;
            case 4:
                moveTwoDieTool();
                break;
            case 12:
                moveTwoDieRestriction();
                break;
            default:
                break;

        }
    }

    /**
     * Moves one die according to the restrictions of level
     * @param lev level of control performed by the model
     * @throws IllegalStepException if is impossible to move the die
     */
    private void moveFirstDieWindow (int lev) throws IllegalStepException
    {
        try {
            window.moveDice(pos_iniz1,pos_fin1, lev);
        } catch (Exception ex) {
            throw new IllegalStepException("Impossibile eseguire il tool coi parametri selezionati");
        }
    }

    /**
     * Moves one die according to the restrictions of level
     * @param lev level of control performed by the model
     * @throws IllegalStepException if is impossible to move the dices
     * @throws IllegalDiceException if the dices are invalid
     */
    private void moveSecondDieWindow (int lev) throws IllegalStepException , IllegalDiceException
    {
        try {
            window.moveDice(pos_iniz2, pos_fin2, lev);
        } catch (Exception ex) {
            window.moveDice(pos_fin1, pos_iniz1, -1);
            throw new IllegalStepException("Impossibile eseguire il tool coi parametri selezionati");
        }
    }

    /**
     * Function of tools 2 and 3 (the difference between the two lies in the level value, aka restrictions control)
     * @throws IllegalStepException if the tool is used incorrectly
     */
    private void moveOneDieTool() throws IllegalStepException {
        if(pos_iniz1 == null || pos_fin1 == null)
            throw new IllegalStepException("Riprova selezionando tutti i parametri del tool");

        moveFirstDieWindow(level);
        setPrice();
    }

    /**
     * Function of tool 4
     * @throws IllegalStepException if the tool is used incorrectly
     * @throws IllegalDiceException if an invalid die is used
     */
    private void moveTwoDieTool() throws IllegalStepException, IllegalDiceException
    {
        if(pos_iniz1 == null || pos_fin1 == null || pos_iniz2 == null || pos_fin2 == null)
            throw new IllegalStepException("Riprova selezionando tutti i parametri del tool");
        moveFirstDieWindow(0);
        moveSecondDieWindow(0);
        setPrice();
    }

    //tool 12
    private void moveTwoDieRestriction () throws IllegalStepException,IllegalDiceException
    {
        if(pos_iniz1 == null || pos_fin1 == null || d1 == null)
            throw new IllegalStepException("Riprova selezionando tutti i parametri del tool");

        Dice dOne = window.getCell(pos_iniz1.getI(), pos_iniz1.getJ()).getFrontDice();

        if (d1.getColor() == dOne.getColor())
        {
            moveFirstDieWindow(0);
            if (pos_iniz2 != null && pos_fin2 != null)
            {
                Dice dTwo = window.getCell(pos_iniz2.getI(), pos_iniz2.getJ()).getFrontDice();
                if (dTwo.getColor() == d1.getColor())
                    moveSecondDieWindow(0);
            }

        }
        else
            throw new IllegalStepException("I colori dei dadi devono essere compatibili");
    }

    void setPrice(){
        if(price==1)
            price++;
    }


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
