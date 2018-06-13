package it.polimi.ingsw.model.objectives.Public;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.Placement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DiagonalScoreTest {

    static Map<Integer, Pattern> map;
    static Cell[][] grid;

    private static void initializeGrid() {
        for (int i = 0; i < grid.length; i++)
            for (int j = 0; j < grid[0].length; j++)
                grid[i][j] = new Cell(new Placement(0, null));
    }

    @BeforeAll
    static void setUp() {
        grid = new Cell[4][5];

        map = new HashMap<>();
        map.put(0, new Pattern(new int[][] {{1, 0, 1, 0, 0},
                                               {0, 1, 0, 1, 0},
                                               {0, 0, 0, 0, 1},
                                               {0, 0, 0, 1, 0}},
                6));
        map.put(1, new Pattern(new int[][] {{1, 0, 0, 1, 0},
                                               {1, 1, 0, 0, 1},
                                               {1, 0, 0, 0, 0},
                                               {0, 0, 0, 1, 0}},
                5));
        map.put(2, new Pattern(new int[][] {{0, 0, 1, 0, 1},
                                               {0, 0, 1, 1, 0},
                                               {1, 0, 1, 0, 1},
                                               {0, 1, 0, 1, 0}},
                8));
        map.put(3, new Pattern(new int[][] {{1, 0, 1, 0, 0},
                                               {0, 0, 0, 0, 0},
                                               {0, 0, 0, 0, 1},
                                               {0, 0, 0, 0, 0}},
                0));
    }

    @Test()
    void calcScore() {
        int value = 1;
        DiagonalScore diagonalScore = new DiagonalScore();
        for(int k = 0; k < map.size(); k++) {

            initializeGrid();
            Pattern p = map.get(k);
            int[][] patternGrid = p.getPattern();

            for (int i = 0; i < grid.length; i++)
                for (int j = 0; j < grid[0].length; j++)
                    if (patternGrid[i][j] == 1)
                        grid[i][j].setFrontDice(new Dice(1, ColorEnum.RED));

            int s = diagonalScore.calcScore(value, grid);
            assertEquals(p.expected * value, s);
        }
    }




    private static class Pattern {
        int[][] pattern;
        int expected;

        public Pattern(int[][] pattern, int expected) {
            this.pattern = pattern;
            this.expected = expected;
        }

        public int getExpected() {
            return expected;
        }

        public int[][] getPattern() {
            return pattern;
        }
    }

}