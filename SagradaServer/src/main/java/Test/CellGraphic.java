package Test;
import javafx.scene.text.FontSmoothingType;

import javax.swing.JButton;
import java.awt.*;

public class CellGraphic extends JButton
{
    //private Dice dice;
    private Cell cella;
    private int x;
    private int y;

    public CellGraphic ()
    {
        cella = new Cell ();
    }

    public CellGraphic (Cell c,int i,int j)
    {
        cella = c;
        x = i;
        y = j;
    }

    public CellGraphic (Cell c)
    {
        cella = c;
    }


    public void updateGrpahic ()
    {
        Integer aa = cella.getCurrentDice().getValue();
        if (aa != 0)
            this.setText(aa.toString());
        this.setBackground(cella.getCurrentDice().getColor());
        if (cella.getFrontDice() != null)
            this.setFont(new Font("TimesRoman", Font.BOLD, 50));
    }


    public Dice getCurrentDice ()
    {
        return cella.getCurrentDice();
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
