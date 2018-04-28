package Model;

import Exceptions.NotEnoughDiceException;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class DiceBagTest {

    static DiceBag diceBag;
    static final int N_OF_DICE = 90;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        diceBag = new DiceBag();
    }

    @org.junit.jupiter.api.Test
    void pickDices_noException() throws NotEnoughDiceException {
        int n = 50;
        assertEquals(n, diceBag.pickDices(n).size());
    }

    @org.junit.jupiter.api.Test
    void pickDices_withException() throws NotEnoughDiceException {
        int n = 1000;
        assertThrows(NotEnoughDiceException.class, () -> { diceBag.pickDices(n); });
    }

    @org.junit.jupiter.api.Test
    void getRemainingDice() {
        int pick = 10;
        diceBag.pickDices(pick);
        assertEquals(N_OF_DICE - pick, diceBag.getRemainingDice());
    }

    @org.junit.jupiter.api.Test
    void toStringTest() {
        assertNotNull(diceBag.toString());
    }

    @org.junit.jupiter.api.Test
    void noIllegalValues() {
        int pick = N_OF_DICE;
        ArrayList<Dice> list = diceBag.pickDices(pick);
        assertTrue(list.stream()
                .map(dice -> dice.getValue())
                .allMatch(value -> value > 0 && value <= 6));
    }
}