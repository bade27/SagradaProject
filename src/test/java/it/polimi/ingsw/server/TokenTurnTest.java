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
        token.setOnGame(true);
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
    void checkRoundTwoPlayers ()
    {
        token = new TokenTurn();

        token.addPlayer("A");
        token.addPlayer("B");

        for (int i = 0 ; i < 10 ; i++)
        {
            token.nextTurn();
            assertTurnA();
            token.nextTurn();
            assertTurnB();
            token.nextTurn();
            assertTurnB();
            token.nextTurn();
            assertTurnA();

            assertTrue(token.isEndRound());

            token.nextTurn();
            assertTurnB();
            token.nextTurn();
            assertTurnA();
            token.nextTurn();
            assertTurnA();
            token.nextTurn();
            assertTurnB();

            assertTrue(token.isEndRound());
        }
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
    void roundTestWithExits1 ()
    {
        //A - B - C - delete(D) - C - B - A
        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        assertTurnB();
        token.nextTurn();
        assertTurnC();

        token.nextTurn();
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

        //delete(C) - A - B - B - A
        token.nextTurn();
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
        token.nextTurn();
        token.deletePlayer("A");

        token.nextTurn();
        assertTurnB();
        token.nextTurn();
        assertTurnB();
    }

    @Test
    void roundTestWithExits2 ()
    {
        //A - B - C - delete(D) - C - B
        token.nextTurn();
        assertTurnA();
        assertFalse(token.isEndRound());
        token.nextTurn();

        assertTurnB();
        assertFalse(token.isEndRound());
        token.nextTurn();
        assertTurnC();
        assertFalse(token.isEndRound());
        token.nextTurn();
        token.deletePlayer("D");
        token.nextTurn();

        assertTurnC();
        assertFalse(token.isEndRound());
        token.nextTurn();
        assertTurnB();
        assertFalse(token.isEndRound());
        token.nextTurn();
        assertTurnA();

        assertTrue(token.isEndRound());

        //B - C - A - A - C - B
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnB();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnC();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnC();
        token.nextTurn();
        assertTurnB();

        assertTrue(token.isEndRound());

        //C - A - B - B - delete(A) - C
        token.nextTurn();
        assertTurnC();
        assertFalse(token.isEndRound());
        token.nextTurn();
        assertTurnA();
        assertFalse(token.isEndRound());
        token.nextTurn();
        assertTurnB();
        assertFalse(token.isEndRound());
        token.nextTurn();
        assertTurnB();
        assertFalse(token.isEndRound());

        token.nextTurn();
        token.deletePlayer("A");
        token.nextTurn();
        assertTurnC();

        assertTrue(token.isEndRound());

        //B - C - C - B
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnB();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnC();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnC();
        token.nextTurn();
        assertTurnB();

        assertTrue(token.isEndRound());

        //delete(A) B - B - ....
        token.nextTurn();
        token.deletePlayer("A");

        token.nextTurn();

        assertTurnB();
    }

    @Test
    void roundTestWithExits3 ()
    {
        //A - B - C - delete(D) - C - B - A
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnB();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnC();

        token.nextTurn();
        assertFalse(token.isEndRound());
        token.deletePlayer("D");
        token.nextTurn();

        assertFalse(token.isEndRound());
        assertTurnC();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnB();
        token.nextTurn();
        assertTurnA();

        assertTrue(token.isEndRound());

        //B - C - A - A - C - B
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnB();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnC();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnC();
        token.nextTurn();
        assertTurnB();

        assertTrue(token.isEndRound());

        //C - A - B - B - A - delete(C)
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnC();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnB();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnB();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();

        token.nextTurn();
        token.deletePlayer("C");

        assertTrue(token.isEndRound());

        //A - B - B - A
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnB();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnB();
        token.nextTurn();
        assertTurnA();

        assertTrue(token.isEndRound());

        //delete(A) B - B - ....
        token.nextTurn();
        token.deletePlayer("A");

        token.nextTurn();

        assertTurnB();
    }

    @Test
    void checkTool8WithoutExits ()
    {
        //A - tool8(A) - A - B - C - D - D - C - B
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        assertTrue (token.useToolNumber8("A"));
        assertTurnA();

        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnB();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnC();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnD();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnD();
        token.nextTurn();
        assertTurnC();
        token.nextTurn();
        assertTurnB();

        assertTrue(token.isEndRound());

        //(no tools in use) B - C - D - A - A - D - C - B
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnB();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnC();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnD();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnD();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnC();
        token.nextTurn();
        assertTurnB();

        assertTrue(token.isEndRound());

        //C - D - tool8(D) - D - A - B - B - A - C
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnC();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnD();
        assertTrue (token.useToolNumber8("D"));
        assertTurnD();
        assertFalse(token.isEndRound());
        token.nextTurn();
        assertTurnD();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnB();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnB();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertTurnC();

        assertTrue(token.isEndRound());

        //D - A - B tool8(B) - B - C - C - A - D
        token.nextTurn();
        assertTurnD();
        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        assertTurnB();
        assertTrue (token.useToolNumber8("B"));
        assertTurnB();

        token.nextTurn();
        assertTurnB();
        token.nextTurn();
        assertTurnC();
        token.nextTurn();
        assertTurnC();
        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        assertTurnD();


        assertTrue(token.isEndRound());

        //A - tool8(A) - A - B - C - D - D - C - B
        token.nextTurn();
        assertTurnA();
        assertTrue (token.useToolNumber8("A"));
        assertTurnA();

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

        assertTrue(token.isEndRound());

    }

    @Test
    void checkTool8WithEveryNumberOfPlayers()
    {
        token.nextTurn();
        token.deletePlayer("A");

        //B - C - tool8 - C - D - D - B
        token.nextTurn();
        assertTurnB();
        token.nextTurn();

        assertTurnC();
        token.useToolNumber8("C");
        assertTurnC();

        token.nextTurn();
        assertTurnC();
        token.nextTurn();
        assertTurnD();
        token.nextTurn();
        assertTurnD();
        token.nextTurn();
        assertTurnB();

        assertTrue(token.isEndRound());

        //delete B - C - tool8 - C - D - D
        token.nextTurn();
        token.deletePlayer("B");
        token.nextTurn();

        assertTurnC();
        token.useToolNumber8("C");
        assertTurnC();

        token.nextTurn();
        assertTurnC();
        token.nextTurn();
        assertTurnD();
        token.nextTurn();
        assertTurnD();

        assertTrue(token.isEndRound());

        //D - C - C -D
        token.nextTurn();
        assertTurnD();
        token.nextTurn();
        assertTurnC();
        token.nextTurn();
        assertTurnC();
        token.nextTurn();
        assertTurnD();

        assertTrue(token.isEndRound());
    }

    @Test
    void checkTool8WithExits ()
    {
        //A - tool8(A) - A - delete B - C - D - D - C
        token.nextTurn();
        assertTurnA();
        assertFalse(token.isEndRound());
        assertTrue (token.useToolNumber8("A"));
        assertTurnA();

        token.nextTurn();
        assertTurnA();
        assertFalse(token.isEndRound());
        token.nextTurn();
        assertFalse(token.isEndRound());
        token.deletePlayer("B");
        token.nextTurn();

        assertTurnC();
        assertFalse(token.isEndRound());
        token.nextTurn();
        assertTurnD();
        assertFalse(token.isEndRound());
        token.nextTurn();
        assertTurnD();
        assertFalse(token.isEndRound());
        token.nextTurn();
        assertTurnC();

        assertTrue(token.isEndRound());


        //C - D- A - A - D - C
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnC();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnD();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnD();
        token.nextTurn();
        assertTurnC();

        assertTrue(token.isEndRound());

        //D - tool8(D) - D - delete A - C - C

        token.nextTurn();
        assertTurnD();
        assertFalse(token.isEndRound());
        assertTrue (token.useToolNumber8("D"));
        assertTurnD();

        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnD();

        token.nextTurn();
        token.deletePlayer("A");
        token.nextTurn();

        assertFalse(token.isEndRound());
        assertTurnC();
        token.nextTurn();
        assertTurnC();

        assertTrue(token.isEndRound());

        //C - D - D - C
        token.nextTurn();
        assertTurnC();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnD();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnD();
        token.nextTurn();
        assertTurnC();

        assertTrue(token.isEndRound());

        //D - tool8(D) - C - C
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnD();
        assertTrue (token.useToolNumber8("D"));
        assertTurnD();

        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnD();
        token.nextTurn();
        assertTurnC();
        token.nextTurn();
        assertTurnC();
        assertFalse(token.useToolNumber8("C"));
        assertTurnC();

        assertTrue(token.isEndRound());

        //C - D - D - C
        token.nextTurn();
        assertTurnC();
        token.nextTurn();
        assertTurnD();
        token.nextTurn();
        assertTurnD();
        token.nextTurn();
        assertTurnC();

        assertTrue(token.isEndRound());
    }


    @Test
    void roundTestWithExitsAndEntrances ()
    {
        //A - B - C - delete(D) - C - B - A
        token.nextTurn();
        assertTurnA();
        assertFalse(token.isEndRound());
        token.nextTurn();
        assertTurnB();
        assertFalse(token.isEndRound());
        token.nextTurn();
        assertTurnC();
        assertFalse(token.isEndRound());
        token.nextTurn();
        token.deletePlayer("D");
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnC();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnB();
        token.nextTurn();
        assertTurnA();

        assertTrue(token.isEndRound());

        //B - C - A - add(D) - A - C - B
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnB();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnC();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertFalse(token.isEndRound());
        token.addPlayer("D");
        assertTurnA();
        assertFalse(token.isEndRound());
        token.nextTurn();
        assertTurnC();
        assertFalse(token.isEndRound());
        token.nextTurn();
        assertTurnB();

        assertTrue(token.isEndRound());

        //C - A - D - B - B - D - A - C
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnC();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnD();
        token.nextTurn();
        assertTurnB();
        token.nextTurn();
        assertTurnB();
        token.nextTurn();
        assertTurnD();
        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        assertTurnC();

        assertTrue(token.isEndRound());

        //A - D - B - C - delete(C) - B - D - A
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnD();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnB();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnC();

        token.nextTurn();
        assertFalse(token.isEndRound());
        token.deletePlayer("C");
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnB();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnD();
        token.nextTurn();
        assertTurnA();

        assertTrue(token.isEndRound());


        //D - B - A - A - B - D
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnD();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnB();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnB();
        token.nextTurn();
        assertTurnD();

        assertTrue(token.isEndRound());


        //B - A - D - D - A - delete (B)
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnB();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertTurnD();
        assertFalse(token.isEndRound());
        token.nextTurn();
        assertTurnD();
        assertFalse(token.isEndRound());
        token.nextTurn();
        assertTurnA();
        assertFalse(token.isEndRound());

        token.nextTurn();
        token.deletePlayer("B");

        assertTrue(token.isEndRound());

        //A - D - D - A
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnA();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnD();
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnD();
        token.nextTurn();
        assertTurnA();

        assertTrue(token.isEndRound());

        //D - A - A - D
        token.nextTurn();
        assertFalse(token.isEndRound());
        assertTurnD();
        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        assertTurnD();

        assertTrue(token.isEndRound());

        //A add(C) - D - D - A
        token.nextTurn();
        assertTurnA();
        token.addPlayer("C");
        token.nextTurn();
        assertTurnD();
        token.nextTurn();
        assertTurnD();
        token.nextTurn();
        assertTurnA();

        assertTrue(token.isEndRound());

        //D - C - A - A - C - D
        token.nextTurn();
        assertTurnD();
        token.nextTurn();
        assertTurnC();
        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        assertTurnC();
        token.nextTurn();
        assertTurnD();

        assertTrue(token.isEndRound());
    }


    @Test
    void doubleDisconnectionAndReconnection ()
    {
        //A - B - C - delete(D) - C - delete(B) - A
        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        assertTurnB();
        token.nextTurn();
        assertTurnC();

        token.nextTurn();
        token.deletePlayer("D");
        token.nextTurn();

        assertTurnC();

        token.nextTurn();
        token.deletePlayer("B");
        token.nextTurn();

        assertTurnA();

        assertTrue(token.isEndRound());

        //C - A - A - C
        token.nextTurn();
        assertTurnC();
        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        assertTurnC();

        assertTrue(token.isEndRound());

        //A add(B) - C - C - A add(D)
        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        token.addPlayer("B");
        assertTurnC();
        token.nextTurn();
        assertTurnC();
        token.addPlayer("D");
        token.nextTurn();
        assertTurnA();

        assertTrue(token.isEndRound());

        //C - B - D - A - A - D - B - C
        token.nextTurn();
        assertTurnC();
        token.nextTurn();
        assertTurnB();
        token.nextTurn();
        assertTurnD();
        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        assertTurnA();
        token.nextTurn();
        assertTurnD();
        token.nextTurn();
        assertTurnB();
        token.nextTurn();
        assertTurnC();

        assertTrue(token.isEndRound());
    }

    private void assertTurnA ()
    {
        assertTrue(token.isMyTurn("A"));
        assertFalse(token.isMyTurn("B"));
        assertFalse(token.isMyTurn("C"));
        assertFalse(token.isMyTurn("D"));
    }

    private void assertTurnB ()
    {
        assertTrue(token.isMyTurn("B"));
        assertFalse(token.isMyTurn("A"));
        assertFalse(token.isMyTurn("C"));
        assertFalse(token.isMyTurn("D"));
    }

    private void assertTurnC ()
    {
        assertTrue(token.isMyTurn("C"));
        assertFalse(token.isMyTurn("B"));
        assertFalse(token.isMyTurn("A"));
        assertFalse(token.isMyTurn("D"));
    }

    private void assertTurnD ()
    {
        assertTrue(token.isMyTurn("D"));
        assertFalse(token.isMyTurn("B"));
        assertFalse(token.isMyTurn("C"));
        assertFalse(token.isMyTurn("A"));
    }
}