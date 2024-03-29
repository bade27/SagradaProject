package it.polimi.ingsw.model.objectives.Public;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.Placement;

import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ColRowScoreTest {

    static Cell[][] grid;
    static int rows;
    static int cols;
    static int value;

    @org.junit.jupiter.api.BeforeAll
    static void setup() {
        rows = 4;
        cols = 5;
        value = 5;
    }

    @org.junit.jupiter.api.BeforeEach
    void setupGrid() {
        grid = new Cell[rows][cols];
        for(int i = 0; i < rows; i++)
            for(int j = 0; j < cols; j++)
                grid[i][j] = new Cell(new Placement(0, null));
    }

    //non zero

    //shade
    @org.junit.jupiter.api.Test
    void calcScoreRowShadeNonZero() {
        ColRowScore score = new ColRowScore("row", "shade");

        int numRows = new Random().nextInt(rows) + 1;
        ArrayList<Integer> rs = new ArrayList<>();
        while(rs.size() < numRows) {
            int r = new Random().nextInt(rows);
            if(!rs.contains(r))
                rs.add(r);
        }

        for(int n = 0; n < numRows; n++) {
            for (int i = 0; i < cols; i++)
                grid[rs.get(n)][i].setDice(new Dice((i + 1), ColorEnum.RED));
        }


        for(int i = 0; i < rows; i++)
            for(int j = 0; j < cols; j++)
                if(!rs.contains(i))
                    grid[i][j].setDice(new Dice(3, ColorEnum.YELLOW));

        assertEquals(value * numRows, score.calcScore(value, grid));
    }

    @org.junit.jupiter.api.Test
    void calcScoreColShadeNonZero() {
        ColRowScore score = new ColRowScore("col", "shade");

        int numCols = new Random().nextInt(cols) + 1;
        ArrayList<Integer> cs = new ArrayList<>();
        while(cs.size() < numCols) {
            int c = new Random().nextInt(cols);
            if(!cs.contains(c))
                cs.add(c);
        }

        for(int n = 0; n < numCols; n++) {
            for (int i = 0; i < rows; i++)
                grid[i][cs.get(n)].setDice(new Dice((i + 1), ColorEnum.RED));
        }


        for(int i = 0; i < rows; i++)
            for(int j = 0; j < cols; j++)
                if(!cs.contains(j))
                    grid[i][j].setDice(new Dice(3, ColorEnum.YELLOW));

        assertEquals(value * numCols, score.calcScore(value, grid));
    }

    //color
    @org.junit.jupiter.api.Test
    void calcScoreRowColorNonZero() {
        ColRowScore score = new ColRowScore("row", "color");

        ColorEnum[] arr = {ColorEnum.RED, ColorEnum.PURPLE, ColorEnum.YELLOW, ColorEnum.BLUE, ColorEnum.GREEN};

        int numRows = new Random().nextInt(rows) + 1;
        ArrayList<Integer> rs = new ArrayList<>();
        while(rs.size() < numRows) {
            int r = new Random().nextInt(rows);
            if(!rs.contains(r))
                rs.add(r);
        }

        for(int n = 0; n < numRows; n++) {
            for (int i = 0; i < cols; i++)
                grid[rs.get(n)][i].setDice(new Dice(2, arr[i]));
        }


        for(int i = 1; i < rows; i++)
            for(int j = 0; j < cols; j++)
                if(!rs.contains(i))
                    grid[i][j].setDice(new Dice(3, ColorEnum.YELLOW));

        assertEquals(value * numRows, score.calcScore(value, grid));
    }

    @org.junit.jupiter.api.Test
    void calcScoreColColorNonZero() {
        ColRowScore score = new ColRowScore("col", "color");

        ColorEnum[] arr = {ColorEnum.RED, ColorEnum.PURPLE, ColorEnum.YELLOW, ColorEnum.BLUE, ColorEnum.GREEN};

        int numCols = new Random().nextInt(cols) + 1;
        ArrayList<Integer> cs = new ArrayList<>();
        while(cs.size() < numCols) {
            int r = new Random().nextInt(cols);
            if(!cs.contains(r))
                cs.add(r);
        }

        for(int n = 0; n < numCols; n++) {
            for (int i = 0; i < rows; i++)
                grid[i][cs.get(n)].setDice(new Dice(2, arr[i]));
        }

        for(int i = 0; i < rows; i++)
            for(int j = 0; j < cols; j++)
                if(!cs.contains(j))
                    grid[i][j].setDice(new Dice(3, ColorEnum.YELLOW));

        assertEquals(value * numCols, score.calcScore(value, grid));

    }

    //zero
    @org.junit.jupiter.api.Test
    void calcScoreZero() {
        String[] tag = {"shade", "color"};
        String[] pattern = {"row", "col"};

        boolean allZero = false;
        for(int p = 0; p < pattern.length; p++) {
            for (int t = 0; t < tag.length; t++) {
                ColRowScore score = new ColRowScore(pattern[p], tag[t]);

                for (int i = 0; i < rows; i++)
                    for (int j = 0; j < cols; j++)
                        grid[i][j].setDice(new Dice(3, ColorEnum.YELLOW));

                if (score.calcScore(value, grid) == 0)
                    allZero = true;
            }
        }

        assertTrue(allZero);

    }
}