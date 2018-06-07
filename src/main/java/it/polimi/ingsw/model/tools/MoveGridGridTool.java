package it.polimi.ingsw.model.tools;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.IllegalStepException;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.Window;

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
        {   //case 2 e case 3 si chiamano la stessa funzione, che si comporta in modo diverso a seconda del id
            case 2:
                //...
            case 3:
                moveOneDieTool();
                break;
            case 4:
                moveTwoDieTool();
                break;
            /*case 12:
                moveTwoDieRestriction();
                break;*/
            default:
                break;

        }
    }

    /**
     * Sposta un dado presente nella griglia di gioco da una cella ad un'altra ignorando le restrizioni in base al valore di level
     *
     */


    public void moveOneDieTool(/*Window w, int[] pos_in, int[] pos_end, int level*/) throws IllegalStepException {
        if(pos_iniz1 == null || pos_fin1 == null)
            throw new IllegalStepException();

        Dice d = window.getCell(pos_iniz1.getI(), pos_iniz1.getJ()).getFrontDice();
        try {
            window.moveDice(pos_iniz1,pos_fin1, level);
        } catch (Exception ex) {
            throw new IllegalStepException();
        }

        if (window.getCell(pos_iniz1.getI(), pos_iniz1.getJ()).getFrontDice() == null &&
                window.getCell(pos_fin1.getI(), pos_fin1.getJ()).getFrontDice() == d &&
                d != null)
            setPrice();
    }

    /**
     * Sposto esattamente 2 dadi presenti nella griglia di gioco dalle rispettive celle iniziali a quelle finali
     *
     * @throws IllegalStepException
     * @throws IllegalDiceException
     */
    public void moveTwoDieTool() throws IllegalStepException, IllegalDiceException {   //perche mi son fatto passare dado?

        if(pos_iniz1 == null || pos_fin1 == null || pos_iniz2 == null || pos_fin2 == null)
            throw new IllegalStepException();

        Dice d1 = window.getCell(pos_iniz1.getI(), pos_iniz1.getJ()).getFrontDice();
        Dice d2 = window.getCell(pos_iniz2.getI(), pos_iniz2.getJ()).getFrontDice();
        try {
            window.moveDice(pos_iniz1, pos_fin1, 0);
        } catch (Exception ex) {
            throw new IllegalStepException();
        }
        if (window.getCell(pos_iniz1.getI(), pos_iniz1.getJ()).getFrontDice() == null &&
                window.getCell(pos_fin1.getI(), pos_fin1.getJ()).getFrontDice() == d1 &&
                d1 != null) {
            try {
                window.moveDice(pos_iniz2, pos_fin2, 0);
            } catch (Exception ex) {
                window.moveDice(pos_fin1, pos_iniz1, 1);
                throw new IllegalStepException();
            }
            if (window.getCell(pos_iniz2.getI(), pos_iniz2.getJ()).getFrontDice() == null &&
                    window.getCell(pos_fin2.getI(), pos_fin2.getJ()).getFrontDice() == d2 &&
                    d2 != null)
                setPrice();
        }
    }

    //tool 12
    /*public void moveTwoDieRestriction () throws IllegalStepException
    {
        if(window == null || pos_iniz1 == null || pos_fin1 == null || pos_iniz2 == null || pos_fin2 == null || d1 == null)
            throw new IllegalStepException();

        Dice dOne = window.getCell(pos_iniz1.getI(), pos_iniz1.getJ()).getFrontDice();
        Dice dTwo = window.getCell(pos_iniz2.getI(), pos_iniz2.getJ()).getFrontDice();

        if (d1.getColor() == dOne.getColor() && d1.getColor() == dTwo.getColor())
        {
            //to be continued..
        }
    }*/

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
