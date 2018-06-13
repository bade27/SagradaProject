package it.polimi.ingsw.model.objectives.Public;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Dice;

import java.util.HashSet;
import java.util.Set;

public class ColRowScore extends Score {

    int maxi;
    int maxj;

    public ColRowScore() {
    }

    public ColRowScore(String pattern, String tag) {
        this.pattern = pattern;
        this.tag = tag;
    }

    /**
     *
     * @param value
     * @param grid
     * @return *il score totalizzato dal giocatore*
     */
    public int calcScore(int value, Cell[][] grid) {
        int valid_cols = 0;
        boolean flag;
        switch (pattern) {
            case "row":
                maxi = 4;
                maxj = 5;
                break;
            case "col" :
                maxi = 5;
                maxj = 4;
                break;
        }

        for (int i = 0; i < maxi; i++) {
            flag = true;
            Set<String> foundElem = new HashSet<>();
            for (int j = 0; j < maxj; j++) {
                Cell current;
                if(pattern.equals("row"))
                    current = grid[i][j];
                else current = grid[j][i];
                if(current.getFrontDice() != null) {
                    String element = getElement(current.getFrontDice());
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
                valid_cols++;
        }
        return value * valid_cols;
    }

    /**
     *
     * @param d
     * @return *the shade or color of the current die (depending on the objective)*
     */
    private String getElement(Dice d) {
        return tag.equals("shade") ? String.valueOf(d.getValue())
                : d.getColor() != null ? d.getColor().toString() : "missing";
    }



}
