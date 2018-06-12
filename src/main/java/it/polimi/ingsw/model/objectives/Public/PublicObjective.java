package it.polimi.ingsw.model.objectives.Public;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Window;

public class PublicObjective {

    private String name;
    private String description;
    private int value;
    Score score;

    public PublicObjective(String name, String description,
                           int value, Score score) {
        this.name = name;
        this.description = description;
        this.value = value;
        this.score = score;
    }

    /**
     *
     * @param vetrata
     *@return *restituisce il score (relativo all'obbiettivo) che il giocatore ha totalizzato*
     */
    public int getScore(Window vetrata) {
        Cell[][] grid = vetrata.getGrid();
        return score.calcScore(value, grid);
    }

    /**
     *
     * i getter seguenti restituiscono i campi privati della classe (score escluso)
     */
    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getValue() {
        //score diagonale non ha un value predefinito, ma varia da partita a partita
        return score.getClass().getSimpleName().equals("DiagonalScore") ?
                0 : value;

    }
}
