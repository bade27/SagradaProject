package it.polimi.ingsw.model.objectives.Public;

import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.Placement;
import it.polimi.ingsw.model.objectives.ObjectivesFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PairScoreTest {

    static Cell[][] grid;
    static int rows;
    static int cols;
    static int value;
    static int[] pair;
    static PublicObjective obj;
    static Score score;

    @BeforeAll
    static void setup() {
        rows = 4;
        cols = 5;
        pair = new int[2];
    }

    @BeforeEach
    void setupEnvironment() throws ModelException {

        String pa = "/home/matteo/Scrivania/SagradaProject/resources/carte/obbiettivi/obbiettiviPubblici/xml/sfumatura/sfumature_";
        String[] pb = {"chiare.xml", "medie.xml", "scure.xml"};
        int n = new Random().nextInt(3);
        obj = ObjectivesFactory.getPublicObjective(pa + pb[n]);
        switch (n) {
            case 0:
                pair[0] = 1;
                pair[1] = 2;
                break;
            case 1:
                pair[0] = 3;
                pair[1] = 4;
                break;
            case 2:
                pair[0] = 5;
                pair[1] = 6;
                break;
        }
        value = obj.getValue();
        score = obj.getScoreObjbect();

        grid = new Cell[rows][cols];
        for(int i = 0; i < rows; i++)
            for(int j = 0; j < cols; j++) {
                grid[i][j] = new Cell(new Placement(0, null));
            }
    }

    @Test
    void calcScore() {
        int numCells = new Random().nextInt(rows * cols) + 1;
        int first = 0;
        int second = 0;

        for(int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                if ((first + second <= numCells) && new Random().nextBoolean()) {
                    grid[i][j].setFrontDice(new Dice(pair[0], ColorEnum.WHITE));
                    first++;
                } else {
                    grid[i][j].setFrontDice(new Dice(pair[1], ColorEnum.WHITE));
                    second++;
                }
            }
        }

        int min = first < second ? first : second;
        assertEquals(min * value, score.calcScore(value, grid));

    }

    @Test
    void calcScoreZero() {

        if(new Random().nextBoolean()) {
            //no value of the pair
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    Cell current = grid[i][j];
                    if (current.getFrontDice() == null)
                        current.setDice(new Dice(0, ColorEnum.GREEN));
                }
            }

        } else {
            //just one value of the pair
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < cols; j++) {
                    Cell current = grid[i][j];
                    if (i == 0)
                        current.setDice(new Dice(pair[0], ColorEnum.GREEN));
                    if (current.getFrontDice() == null)
                        current.setDice(new Dice(0, ColorEnum.GREEN));
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