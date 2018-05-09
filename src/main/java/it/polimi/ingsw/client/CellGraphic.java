package it.polimi.ingsw.client;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Dice;

import javax.swing.*;
import java.awt.*;

public class CellGraphic extends JButton
{
    //private Dice dice;
    private Cell cella;
    private int x;
    private int y;

    public CellGraphic (Cell c, int i, int j)
    {
        cella = c;
        x = i;
        y = j;
    }

    public CellGraphic (Cell c)
    {
        cella = c;
    }

    /**
     * Funzione che si occupa di aggiornare la grafica corrispondente al currentDice dell'oggetto cell
     */
    public void updateGrpahic ()
    {
        Dice d;
        if (cella.getFrontDice() == null)
            d = new Dice(cella.getPlacement().getValue(),cella.getPlacement().getColor());
        else
            d = cella.getFrontDice();

        Integer aa = d.getValue();
        if (aa != 0)
            this.setText(aa.toString());
        this.setBackground(d.getColor());
        if (cella.getFrontDice() != null)
            this.setFont(new Font("TimesRoman", Font.BOLD, 50));
    }


    public Dice getCurrentDice ()
    {
        Dice d;
        if (cella.getFrontDice() != null)
            d = new Dice(cella.getPlacement().getValue(),cella.getPlacement().getColor());
        else
            d = cella.getFrontDice();
        return d;
    }

    public int getPosX ()
    {
        return x;
    }

    public int getPosY()
    {
        return y;
    }

}
