package Objectives.Public;

import Model.Cell;
import Model.Dice;
import Model.Placement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PairScoreTest {

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

    @Test
    void calcScoreEven() {

        int[] pair = new int[2];
        int first = -1;
        boolean validPair = false;
        while(first == 2 && first == 4) {
            first = new Random().nextInt(5) + 1;
        }
        pair[0] = first;
        pair[1] = first + 1 ;

        PairScore score = new PairScore(pair[0] + " " + pair[1]);

        int numCells = 0;
        do {
            numCells = new Random().nextInt(rows * cols);
        } while ((numCells % 2) != 0);

        HashSet<Integer> cells = new HashSet<>();
        for (int i = 0; i < numCells; i++) {
            int n = new Random().nextInt(rows * cols);
            cells.add(n);
        }

        for (int i = 0, cont = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (cells.contains(cont))
                    if(cont % 2 == 0) {
                        grid[i][j].setDice(new Dice(pair[0], Color.green));
                    } else grid[i][j].setDice(new Dice(pair[1], Color.green));
                else grid[i][j].setDice(new Dice(7, Color.green));
                cont++;
            }
        }

        assertEquals(value * numCells / 2, score.calcScore(value, grid));

    }

    @Test
    void calcScoreOdd() {
    }

    @Test
    void calcScoreZero() {
    }

}