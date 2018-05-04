package Obbiettivi.Pubblici;

import Model.Cell;
import Model.Dice;
import Model.Window;

import java.util.HashSet;
import java.util.Set;

public class PunteggioColonna implements PunteggioInterface {

    String tag;

    public PunteggioColonna(String tag) {
        this.tag = tag;
    }

    /**
     *
     * @param valore
     * @param vetrata
     * @return *il punteggio totalizzato dal giocatore*
     */
    public int calcola(int valore, Window vetrata) {
        int colonne_valide = 0;
        boolean flag;
        Cell[][] grid = vetrata.getGrid();

        for (int column = 0; column < grid[0].length; column++) {
            flag = true;
            Set<Integer> foundElem = new HashSet<>();
            for (int row = 0; row < grid.length; row++) {
                Cell current = grid[row][column];
                if(current.getFrontDice() != null) {
                    Integer element = getElement(current.getFrontDice());
                    if (foundElem.contains(element)) {
                        flag = false;
                        break;
                    }
                    foundElem.add(element);
                } else {
                    flag = false;
                    break;
                }
            }
            if(flag)
                colonne_valide++;
        }
        return valore * colonne_valide;
    }

    /**
     *
     * @param d
     * @return *la sfumatura o il colore del dado attuale a seconda del tipo di obbiettivo*
     */
    private int getElement(Dice d) {
        return tag.equals("shade") ? d.getValue()
                : d.getColor() != null ? d.getColor().getRGB() : -1;
    }

}
