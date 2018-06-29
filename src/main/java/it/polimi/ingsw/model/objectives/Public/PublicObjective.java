package it.polimi.ingsw.model.objectives.Public;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Window;

public class PublicObjective {

    private String name;
    private String path;
    private String description;
    private int value;
    Score score;

    public PublicObjective(String name, String path, String description,
                           int value, Score score) {
        this.name = name;
        this.path = path;
        this.description = description;
        this.value = value;
        this.score = score;
    }

    /**
     *  calculate the player's score
     * @param window of the player
     *@return gets the player's score
     */
    public int getScore(Window window) {
        Cell[][] grid = window.getGrid();
        return score.calcScore(value, grid);
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getValue() {
        return score.getClass().getSimpleName().equals("DiagonalScore") ?
                0 : value;

    }

    public String getPath() {
        return path;
    }

    public Score getScoreObjbect() {
        return score;
    }
}
