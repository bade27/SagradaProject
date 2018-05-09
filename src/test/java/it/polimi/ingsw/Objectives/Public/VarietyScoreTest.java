package it.polimi.ingsw.Objectives.Public;

import it.polimi.ingsw.Model.Cell;
import it.polimi.ingsw.Model.Dice;
import it.polimi.ingsw.Model.Placement;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VarietyScoreTest {

    static Cell[][] grid;
    static int rows;
    static int cols;

    VarietyScore score;
    @BeforeAll
    static void setUp() {
        rows = 4;
        cols = 5;
    }

    @BeforeEach
    void setupGrid() {
        grid = new Cell[rows][cols];
        for(int i = 0; i < rows; i++)
            for(int j = 0; j < cols; j++) {
                grid[i][j] = new Cell(new Placement(0, null));
            }
    }

    @Test
    void ShadeVariety() {
        int value = 5;
        VarietyScore score = new VarietyScore("shade");
        int numClust = new Random().nextInt(3) + 1;
        for (int i = 0, n = 1, cont = 1;  i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if(cont <= 6) {
                    grid[i][j].setDice(new Dice(cont++, Color.gray));
                } else {
                    n++;
                    if(n <= numClust)
                        cont = 1;
                    grid[i][j].setDice(new Dice(0, Color.gray));
                }
            }
        }

        assertEquals(value * numClust, score.calcScore(value, grid));
    }

    @Test
    void ColorVariety() {
        int value = 4;
        VarietyScore score = new VarietyScore("color");
        Color[] colors = {Color.red, Color.magenta, Color.yellow, Color.blue, Color.green};
        int numClust = new Random().nextInt(rows * cols / colors.length) + 1;
        int n = 1;
        int i = 0, j = 0;
        int cont = 0;
        while(n <= numClust) {
            while(cont < colors.length) {
                grid[i][j].setDice(new Dice(0, colors[cont++]));
                j++;
                if(j == cols) {
                    j = 0;
                    i++;
                }
            }
            n++;
            cont = 0;
        }
        for (i = 0;  i < rows; i++) {
            for (j = 0; j < cols; j++) {
                Cell current = grid[i][j];
                if(current.getFrontDice() == null)
                    grid[i][j].setDice(new Dice(0, Color.gray));
            }
        }

        assertEquals(value * numClust, score.calcScore(value, grid));
    }

    @Test
    void noVariety() {
        int value = 4;
        VarietyScore score;
        String[] tag = {"color", "shade"};
        boolean bothZero = false;
        for(String s : tag) {
            score = new VarietyScore(s);
            for(int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    grid[i][j].setDice(new Dice(0, Color.gray));
                }
            }
            bothZero = score.calcScore(value, grid) == 0 ? true : false;
        }
        assertTrue(bothZero);
    }

}