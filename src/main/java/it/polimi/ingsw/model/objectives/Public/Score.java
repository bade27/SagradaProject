package it.polimi.ingsw.model.objectives.Public;

import it.polimi.ingsw.model.Cell;

public abstract class Score {

    protected String pattern;
    protected String tag;

    abstract int calcScore(int value, Cell[][] grid);

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}
