package json.prova;

import json.prova.necessity.Dice;

public class Move {
    private Pair p;
    private Dice d;
    private String playerName;

    public Move(Pair p, Dice d, String player) {
        this.p = p;
        this.d = d;
        this.playerName = player;
    }

    public Pair getPair() {
        return p;
    }

    public Dice getDice() {
        return d;
    }

    public String getPlayerName() {
        return playerName;
    }
}
