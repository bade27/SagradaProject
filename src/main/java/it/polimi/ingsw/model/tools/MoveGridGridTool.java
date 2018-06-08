package it.polimi.ingsw.model.tools;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.IllegalStepException;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.Window;
import it.polimi.ingsw.remoteInterface.Coordinates;

//Tool nr. 2-3-4


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

    private void moveFirstDieWindow (int lev) throws IllegalStepException
    {
        try {
            window.moveDice(pos_iniz1,pos_fin1, lev);
        } catch (Exception ex) {
            throw new IllegalStepException();
        }
    }

    private void moveSecondDieWindow (int lev) throws IllegalStepException , IllegalDiceException
    {
        try {
            window.moveDice(pos_iniz2, pos_fin2, lev);
        } catch (Exception ex) {
            window.moveDice(pos_fin1, pos_iniz1, -1);
            throw new IllegalStepException();
        }
    }

    /**
     * Sposta un dado presente nella griglia di gioco da una cella ad un'altra ignorando le restrizioni in base al valore di level
     *
     */
    private void moveOneDieTool() throws IllegalStepException {
        if(pos_iniz1 == null || pos_fin1 == null)
            throw new IllegalStepException();

        moveFirstDieWindow(level);
        setPrice();
    }

    /**
     * Sposto esattamente 2 dadi presenti nella griglia di gioco dalle rispettive celle iniziali a quelle finali
     *
     * @throws IllegalStepException
     * @throws IllegalDiceException
     */
    private void moveTwoDieTool() throws IllegalStepException, IllegalDiceException
    {
        if(pos_iniz1 == null || pos_fin1 == null || pos_iniz2 == null || pos_fin2 == null)
            throw new IllegalStepException();
        moveFirstDieWindow(0);
        moveSecondDieWindow(0);
    }

    //tool 12
    private void moveTwoDieRestriction () throws IllegalStepException,IllegalDiceException
    {
        if(pos_iniz1 == null || pos_fin1 == null || d1 == null)
            throw new IllegalStepException();

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
            throw new IllegalStepException();
    }

    void setPrice(){
        if(price==1)
            price++;
    }

    /**
     * Ritorna il costo del tool
     * @return *il prezzo del tool*
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
