package Obbiettivi.Pubblici;

import Model.Cell;
import Model.Window;

import java.util.Arrays;
import java.util.OptionalInt;

public class PunteggioCoppia implements PunteggioInterface {

    private int[] couple;

    public PunteggioCoppia(String tag) {
        couple = new int[2];
        String[] result = tag.split("\\s");
        couple[0] = Integer.parseInt(result[0]);
        couple[1] = Integer.parseInt(result[1]);
    }

    /**
     *
     * @param valore
     * @param vetrata
     * @return *il numero delle coppie trovate moltiplicato per il valore della carta*
     */
    public int calcola(int valore, Window vetrata) {
        int[] frequency = new int[2];
        Cell[][] grid = vetrata.getGrid();

        for (int column = 0; column < grid[0].length; column++) {
            for (int row = 0; row < grid.length; row++) {
                Cell current_cell = grid[row][column];
                if(current_cell.getFrontDice() != null) {
                    int current_value = current_cell.getFrontDice().getValue();
                    if(current_value == couple[0])
                        frequency[0]++;
                    else if(current_value == couple[1])
                        frequency[1]++;
                }
            }
        }
        OptionalInt min = Arrays.stream(frequency).min();
        return min.getAsInt() * valore;
    }
}
