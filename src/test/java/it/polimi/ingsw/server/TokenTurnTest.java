package it.polimi.ingsw.server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenTurnTest {
    TokenTurn token;

    @BeforeEach
    void setUp() {
        token = new TokenTurn();

        token.addPlayer("A");
        token.addPlayer("B");
        token.addPlayer("C");
        token.addPlayer("D");
    }

    @Test
    void checkInitTest()
    {
        assertFalse(token.isMyTurn("A"));
        assertFalse(token.isMyTurn("B"));
        assertFalse(token.isMyTurn("C"));
        assertFalse(token.isMyTurn("D"));
    }

    @Test
    void roundTestWithoutExits ()
    {
        for (int i = 0 ; i < 7 ; i++)
        {
            //A - B - C - D - D - C - B - A
            token.nextTurn();
            assertFalse(token.isMySecondRound("A"));
            assertTurnA();
            token.nextTurn();
            assertFalse(token.isMySecondRound("B"));
            assertTurnB();
            token.nextTurn();
            assertFalse(token.isMySecondRound("C"));
            assertTurnC();
            token.nextTurn();
            assertFalse(token.isMySecondRound("D"));
            assertTurnD();
            token.nextTurn();
            assertTrue(token.isMySecondRound("D"));
            assertTurnD();
            token.nextTurn();
            assertTrue(token.isMySecondRound("C"));
            assertTurnC();
            token.nextTurn();
            assertTrue(token.isMySecondRound("B"));
            assertTurnB();
            token.nextTurn();
            assertTrue(token.isMySecondRound("A"));
            assertTurnA();

            assertTrue(token.isEndRound());

            //B - C - D - A - A - D - C - B
            token.nextTurn();
            assertTurnB();
            token.nextTurn();
            assertTurnC();
            token.nextTurn();
            assertTurnD();
            token.nextTurn();
            assertTurnA();
            token.nextTurn();
            assertTurnA();
            token.nextTurn();
            assertTurnD();
            token.nextTurn();
            assertTurnC();
            token.nextTurn();
            assertTurnB();

            assertTrue(token.isEndRound());

            //C - D - A - B - B - A - D - C
            token.nextTurn();
            assertTurnC();
            token.nextTurn();
            assertTurnD();
            token.nextTurn();
            assertTurnA();
            token.nextTurn();
            assertTurnB();
            token.nextTurn();
            assertTurnB();
            token.nextTurn();
            assertTurnA();
            token.nextTurn();
            assertTurnD();
            token.nextTurn();
            assertTurnC();

            assertTrue(token.isEndRound());

            //D - A - B - C - C - B - A - D
            token.nextTurn();
            assertTurnD();
            token.nextTurn();
            assertTurnA();
            token.nextTurn();
            assertTurnB();
            token.nextTurn();
            assertTurnC();
            token.nextTurn();
            assertTurnC();
            token.nextTurn();
            assertTurnB();
            token.nextTurn();
            assertTurnA();
            token.nextTurn();
            assertTurnD();

            assertTrue(token.isEndRound());
        }
    }

    @Test
    void roundTestWithExits ()
    {
        //A - B - C - delete(D) - C - B - A
        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        assertTurnB();
        token.nextTurn();
        assertTurnC();

        token.deletePlayer("D");

        token.nextTurn();
        assertTurnC();
        token.nextTurn();
        assertTurnB();
        token.nextTurn();
        assertTurnA();

        assertTrue(token.isEndRound());

        //B - C - A - A - C - B
        token.nextTurn();
        assertTurnB();
        token.nextTurn();
        assertTurnC();
        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        assertTurnC();
        token.nextTurn();
        assertTurnB();

        assertTrue(token.isEndRound());

        //delete(C) A - B - B - A
        token.deletePlayer("C");

        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        assertTurnB();
        token.nextTurn();
        assertTurnB();
        token.nextTurn();
        assertTurnA();

        assertTrue(token.isEndRound());

        //B - A - A - B
        token.nextTurn();
        assertTurnB();
        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        assertTurnB();

        assertTrue(token.isEndRound());

        //delete(A) B - B - ....
        token.deletePlayer("A");

        token.nextTurn();
        assertTurnB();
        token.nextTurn();
        assertTurnB();
    }



    //System.out.println(token.toString());




    void assertTurnA ()
    {
        assertTrue(token.isMyTurn("A"));
        assertFalse(token.isMyTurn("B"));
        assertFalse(token.isMyTurn("C"));
        assertFalse(token.isMyTurn("D"));
    }

    void assertTurnB ()
    {
        assertTrue(token.isMyTurn("B"));
        assertFalse(token.isMyTurn("A"));
        assertFalse(token.isMyTurn("C"));
        assertFalse(token.isMyTurn("D"));
    }

    void assertTurnC ()
    {
        assertTrue(token.isMyTurn("C"));
        assertFalse(token.isMyTurn("B"));
        assertFalse(token.isMyTurn("A"));
        assertFalse(token.isMyTurn("D"));
    }

    void assertTurnD ()
    {
        assertTrue(token.isMyTurn("D"));
        assertFalse(token.isMyTurn("B"));
        assertFalse(token.isMyTurn("C"));
        assertFalse(token.isMyTurn("A"));
    }
}