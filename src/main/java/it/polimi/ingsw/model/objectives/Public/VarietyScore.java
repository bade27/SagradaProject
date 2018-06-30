package it.polimi.ingsw.model.objectives.Public;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dice;

import java.util.Arrays;
import java.util.OptionalInt;

public class VarietyScore extends Score {

    public VarietyScore() {
    }

    public VarietyScore(String tag) {
        this.tag = tag;
    }

    /**
     * calculates the player's score
     * @param value
     * @param grid
     * @return the player's score
     */
    public int calcScore(int value, Cell[][] grid) {
        int[] frequency = tag.equals("color") ? new int[5]
                : new int[6];

        for (int column = 0; column < grid[0].length; column++) {
            for (int row = 0; row < grid.length; row++) {
                Cell current_cell = grid[row][column];
                if(current_cell.getFrontDice() != null) {
                    String element = getElement(current_cell.getFrontDice());
                    if(tag.equals("color"))
                        incrementColor(element, frequency);
                    else incrementShade(element, frequency);
                }
            }
        }
        OptionalInt min = Arrays.stream(frequency).min();
        return min.getAsInt() * value;
    }

    /**
     * @param d
     * @return the shade or the color of the element
     */
    private String getElement(Dice d) {
        return tag.equals("shade") ? String.valueOf(d.getValue())
                : d.getColor() != null ? d.getColor().toString() : "missing";
    }

    /**
     * increments the shades' frequency
     * @param value
     * @param f
     */
    private void incrementShade(String value, int[] f) {
        for(int i = 0; i < f.length; i++)
            if(Integer.parseInt(value) == (i + 1))
                f[i]++;
    }

    /**
     * increments the colors' frequency
     * @param value
     * @param f
     */
    private void incrementColor(String value, int[] f) {

        if(value == ColorEnum.RED.toString())
            f[0]++;
        if(value == ColorEnum.GREEN.toString())
            f[1]++;
        if(value == ColorEnum.BLUE.toString())
            f[2]++;
        if(value == ColorEnum.YELLOW.toString())
            f[3]++;
        if(value == ColorEnum.PURPLE.toString())
            f[4]++;

    }

}
