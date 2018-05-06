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
    static PairScore score;

    @org.junit.jupiter.api.BeforeAll
    static void setup() {
        rows = 4;
        cols = 5;
        value = 5;
        score = new PairScore("pair");
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

        boolean rightScore = false;
        for(int p = 0; p < 6; p++) {

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
                        grid[i][j].setDice(new Dice((p + 1), Color.green));
                    else grid[i][j].setDice(new Dice(p, Color.green));
                    cont++;
                }
            }


        }

    }

    @Test
    void calcScoreOdd() {
    }

    @Test
    void calcScoreZero() {
    }

}