package it.polimi.ingsw.model.objectives.Public;

import it.polimi.ingsw.model.Cell;

public interface ScoreInterface {

    int calcScore(int value, Cell[][] grid);
}
