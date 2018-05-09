package it.polimi.ingsw.Objectives.Public;

import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Model.Dice;

import java.util.HashSet;
import java.util.Set;

public class ColRowScore implements ScoreInterface {

    String tag;
    String pattern;
    int maxi;
    int maxj;

    public ColRowScore(String pattern, String tag) {
        this.pattern = pattern;
        this.tag = tag;
    }

    /**
     *
     * @param value
     * @param grid
     * @return *il punteggio totalizzato dal giocatore*
     */
    public int calcScore(int value, Cell[][] grid) {
        int colonne_valide = 0;
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
            Set<Integer> foundElem = new HashSet<>();
            for (int j = 0; j < maxj; j++) {
                Cell current;
                if(pattern.equals("row"))
                    current = grid[i][j];
                else current = grid[j][i];
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
        return value * colonne_valide;
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
