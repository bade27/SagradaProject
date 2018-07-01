package it.polimi.ingsw.model.objectives.Public;

import it.polimi.ingsw.model.Cell;

import java.util.Arrays;
import java.util.OptionalInt;

public class PairScore extends Score {

    private int[] couple;

    public PairScore() {
    }

    public PairScore(String tag) {
        setTag(tag);
    }

    /**
     * calculates the player's score
     * @param value of the objective
     * @param grid of the player
     * @return the number of the pairs found time the value of the objective
     */
    public int calcScore(int value, Cell[][] grid) {
        int[] frequency = new int[2];

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
        return min.getAsInt() * value;
    }

    @Override
    public void setTag(String tag) {
        couple = new int[2];
        String[] result = tag.split("\\s");
        couple[0] = Integer.parseInt(result[0]);
        couple[1] = Integer.parseInt(result[1]);
    }
}
