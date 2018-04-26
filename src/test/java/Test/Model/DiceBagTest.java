package Test.Model;

import Test.Client.NotEnoughDiceException;

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
    void getRemaining_dice() {
        int pick = 10;
        diceBag.pickDices(pick);
        assertEquals(N_OF_DICE - pick, diceBag.getRemaining_dice());
    }

}