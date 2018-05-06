package Objectives.Public;

import Model.Cell;
import Model.Placement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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

    }

    @Test
    void calcScoreOdd() {
    }

    @Test
    void calcScoreZero() {
    }

}