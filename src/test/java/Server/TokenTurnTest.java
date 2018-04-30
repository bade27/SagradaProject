package Server;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

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
            assertTurnA();
            token.nextTurn();
            assertTurnB();
            token.nextTurn();
            assertTurnC();
            token.nextTurn();
            assertTurnD();
            token.nextTurn();
            assertTurnD();
            token.nextTurn();
            assertTurnC();
            token.nextTurn();
            assertTurnB();
            token.nextTurn();
            assertTurnA();


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

        //B - A - A - B
        token.nextTurn();
        assertTurnB();
        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        assertTurnA();
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