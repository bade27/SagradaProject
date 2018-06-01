package it.polimi.ingsw.model.tools;

import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.IllegalStepException;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.Window;

import javax.tools.Tool;

public class MoveGridGridTool extends Tools {

    private int price;

    public MoveGridGridTool() {
        this.price = 1;
    }


    /**
     * Sposta un dado presente nella griglia di gioco da una cella ad un'altra ignorando le restrizioni in base al valore di level
     *
     * @param w       griglia di gioco
     * @param pos_in  posizione del dado da spostare
     * @param pos_end posizione nella quale spostare il dado
     * @throws IllegalStepException
     */
    public void moveOneDieTool(Window w, int[] pos_in, int[] pos_end, int level) throws IllegalStepException {
        Dice d = w.getCell(pos_in[0], pos_in[1]).getFrontDice();
        try {
            w.moveDice(pos_in,pos_end, level);
        } catch (Exception ex) {
            throw new IllegalStepException();
        }

        if (w.getCell(pos_in[0], pos_in[1]).getFrontDice() == null &&
                w.getCell(pos_end[0], pos_end[1]).getFrontDice() == d &&
                d != null)
            setPrice();
    }

    /**
     * Sposto esattamente 2 dadi presenti nella griglia di gioco dalle rispettive celle iniziali a quelle finali
     * @param w griglia di gioco
     * @param pos_in1 posizione iniziale primo dado
     * @param pos_end1 posizione finale primo dado
     * @param pos_in2 posizione iniziale secondo dado
     * @param pos_end2 posizione finale secondo dado
     * @throws IllegalStepException
     * @throws IllegalDiceException
     */
    public void useTool(Window w, int[] pos_in1, int[] pos_end1, int[] pos_in2, int[] pos_end2) throws IllegalStepException, IllegalDiceException {   //perche mi son fatto passare dado?
        Dice d1 = w.getCell(pos_in1[0], pos_in1[1]).getFrontDice();
        Dice d2 = w.getCell(pos_in2[0], pos_in2[1]).getFrontDice();
        try {
            w.moveDice(pos_in1, pos_end1, 0);
        } catch (Exception ex) {
            throw new IllegalStepException();
        }
        if (w.getCell(pos_in1[0], pos_in1[1]).getFrontDice() == null &&
                w.getCell(pos_end1[0], pos_end1[1]).getFrontDice() == d1 &&
                d1 != null) {
            try {
                w.moveDice(pos_in2, pos_end2, 0);
            } catch (Exception ex) {
                w.moveDice(pos_end1, pos_in1, 1);
                throw new IllegalStepException();
            }
            if (w.getCell(pos_in2[0], pos_in2[1]).getFrontDice() == null &&
                    w.getCell(pos_end2[0], pos_end2[1]).getFrontDice() == d2 &&
                    d2 != null)
                setPrice();
        }
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

}
