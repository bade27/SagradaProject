package Objectives.Public;

import Model.Dice;
import Model.Window;
import Model.Cell;

import java.awt.*;
import java.util.Arrays;
import java.util.OptionalInt;

public class VarietyScore implements ScoreInterface {

    private String tag;

    public VarietyScore(String tag) {
        this.tag = tag;
    }

    /**
     *
     * @param valore
     * @param grid
     * @return *il punteggio totalizzato dal giocatore*
     */
    public int calcScore(int valore, Cell[][] grid) {
        int[] frequency = tag.equals("color") ? new int[5]
                : new int[6];

        for (int column = 0; column < grid[0].length; column++) {
            for (int row = 0; row < grid.length; row++) {
                Cell current_cell = grid[row][column];
                if(current_cell.getFrontDice() != null) {
                    Integer element = getElement(current_cell.getFrontDice());
                    if(tag.equals("color"))
                        incrementColor(element, frequency);
                    else incrementShade(element, frequency);
                }
            }
        }
        OptionalInt min = Arrays.stream(frequency).min();
        return min.getAsInt() * valore;
    }

    /**
     *
     * @param d
     * @return *la sfumatura o il colore del dado corrente a seconda dell'obbiettivo*
     */
    private int getElement(Dice d) {
        return tag.equals("shade") ? d.getValue()
                : d.getColor() != null ? d.getColor().getRGB() : -1;
    }

    /**
     *
     * @param value
     * @param f
     * se l'obbiettivo è relativo alle sfumature, incrementa la frequenza della sfumatura corrente
     */
    private void incrementShade(Integer value, int[] f) {
        for(int i = 0; i < f.length; i++)
            if(value == (i + 1))
                f[i]++;
    }

    /**
     *
     * @param value
     * @param f
     * se l'obbiettivo è relativo ai colori, incrementa la frequenza del colore corrente
     */
    private void incrementColor(Integer value, int[] f) {

        if(value == Color.red.getRGB())
            f[0]++;
        if(value == Color.green.getRGB())
            f[1]++;
        if(value == Color.blue.getRGB())
            f[2]++;
        if(value == Color.yellow.getRGB())
            f[3]++;
        if(value == Color.magenta.getRGB())
            f[4]++;

    }

}
