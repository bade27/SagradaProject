package Obbiettivi.Pubblici;

import Model.Cell;
import Model.Dice;
import Model.Window;

import java.util.HashSet;
import java.util.Set;

public class PunteggioRiga implements PunteggioInterface {

	String tag;

	public PunteggioRiga(String tag) {
		this.tag = tag;
    }

    /**
     *
     * @param valore
     * @param vetrata
     * @return *il punteggio totalizzato dal giocatore*
     */
	public int calcola(int valore, Window vetrata) {
        int righe_valide = 0;
        boolean flag;
        Cell[][] grid = vetrata.getGrid();
        for (int row = 0; row < grid.length; row++) {
            flag = true;
            Set<Integer> foundElem = new HashSet<>();
            for (int column = 0; column < grid[0].length; column++) {
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
                righe_valide++;
        }
        return valore * righe_valide;
    }

    /**
     *
     * @param d
     * @return *la sfumatura o il valore del dado corrente a seconda dell'obbiettivo*
     */
	private int getElement(Dice d) {
		return tag.equals("shade") ? d.getValue()
                : d.getColor() != null ? d.getColor().getRGB() : -1;
	}

}
