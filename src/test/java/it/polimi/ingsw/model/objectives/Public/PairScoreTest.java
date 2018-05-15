package it.polimi.ingsw.model.objectives.Public;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.Placement;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PairScoreTest {

    static Cell[][] grid;
    static int rows;
    static int cols;
    static int value;
    static int[] pair;
    static PairScore score;

    @org.junit.jupiter.api.BeforeAll
    static void setup() {
        rows = 4;
        cols = 5;
        value = 5;
        pair = new int[2];
        int first = 2;
        boolean validPair = false;
        while(first == 2 || first == 4) {
            first = new Random().nextInt(5) + 1;
        }
        pair[0] = first;
        pair[1] = first + 1 ;

        score = new PairScore(pair[0] + " " + pair[1]);
    }

    @org.junit.jupiter.api.BeforeEach
    void setupGrid() {
        grid = new Cell[rows][cols];
        for(int i = 0; i < rows; i++)
            for(int j = 0; j < cols; j++) {
                grid[i][j] = new Cell(new Placement(0, null));
            }
    }

    @Test
    void calcScore() {
        int numCells = new Random().nextInt(rows * cols) + 1;
        int fh = numCells / 2;
        int sh = numCells - fh;

        ArrayList<Coordinates> cells = new ArrayList<>();

        while (cells.size() < numCells) {
            Coordinates c = new Coordinates(new Random().nextInt(rows), new Random().nextInt(cols));
            if (!cells.contains(c))
                cells.add(c);
        }

        int here = 0;
        for (int i = 0; i < fh; i++) {
            Coordinates current = cells.get(i);
            grid[current.getI()][current.getJ()].setDice(new Dice(pair[0], Color.green));
            here = i;
        }
        here++;
        for (int i = 0; i < sh; i++) {
            Coordinates current = cells.get(i + here);
            grid[current.getI()][current.getJ()].setDice(new Dice(pair[1], Color.green));
        }

        for(int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Cell current = grid[i][j];
                if(current.getFrontDice() == null)
                    current.setDice(new Dice(0, Color.green));
            }
        }

        int min = fh < sh ? fh : sh;
        assertEquals(value * min, score.calcScore(value, grid));

    }

    @Test
    void calcScoreZero() {

        if(new Random().nextBoolean()) {
            //no value of the pair
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    Cell current = grid[i][j];
                    if (current.getFrontDice() == null)
                        current.setDice(new Dice(0, Color.green));
                }
            }

        } else {
            //just one value of the pair
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    Cell current = grid[i][j];
                    if (i == 0)
                        current.setDice(new Dice(pair[0], Color.green));
                    if (current.getFrontDice() == null)
                        current.setDice(new Dice(0, Color.green));
                }
            }

        }

        assertEquals(0, score.calcScore(value, grid));

    }


    class Coordinates {

        int i;
        int j;

        public Coordinates(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public int getI() {
            return i;
        }

        public int getJ() {
            return j;
        }

        @Override
        public boolean equals(Object p) {
            Coordinates c = (Coordinates)p;
            return i == c.getI() && j == c.getJ();
        }
    }

}