package Test.Model;

import java.util.ArrayList;

public class TracciatoRound {
    private ArrayList<Dice>[] rounds = new ArrayList[10];
    public TracciatoRound() {
        for(int i = 0; i < rounds.length; i++)
            rounds[i] = null;
    }
    public void setCurrentRoundDice(ArrayList<Dice> d, int round) {
        rounds[round] = d;
    }

    public ArrayList<Dice> getDice(int round) {
        return rounds[round];
    }
}
