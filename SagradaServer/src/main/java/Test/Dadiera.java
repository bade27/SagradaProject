package Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Dadiera
{
    private ArrayList<Dice> listaDadi;
    private final Color [] listaColori = {null,Color.red,Color.blue,Color.magenta,Color.yellow,Color.green};
    private int dim;
    private int length;

    //Da aggiungere la diminuzione dei dadi
    public Dadiera (int n)
    {
        dim = n;
        length = n;
        listaDadi = new ArrayList<Dice>(dim);
        mix();
    }

    /**
     * Genera un nuovo set di dadi casualmente
     */
    public void mix ()
    {
        int v,c;
        for (int i=0;i<dim;i++)
        {
            v = (int)(Math.random()*6) + 1;
            c = (int)(Math.random()*5) + 1;
            listaDadi.add(new Dice (v,listaColori[c]));
        }
    }

    public Dice getDice (int i)
    {
        return listaDadi.get(i);
    }

    public int getLength ()
    {
        return length;
    }
}
