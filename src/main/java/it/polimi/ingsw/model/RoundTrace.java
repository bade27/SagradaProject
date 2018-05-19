package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.IllegalStepException;

import java.util.ArrayList;

public class RoundTrace {
    ArrayList<Dice>[] trace;

    public RoundTrace(){
        trace=new ArrayList[10];
    }

    /**
     * Add d die in the n position
     * @param n track's cell
     * @param d die
     */
    public void addDice(int n,Dice d){
            trace[n-1].add(d);
    }

    /**
     * Delete d die from the n position
     * @param n track's cell
     * @param d die
     */
    public void deleteDice(int n,Dice d){
        for(int i=0;i<trace[n-1].size();i++){
            if (d.equals(trace[n-1].get(i))){
                trace[n-1].remove(i);
                break;
            }
        }
    }

    /**
     * Return the list of dice in a specific track cell
     * @param n cell
     * @return list of dice
     */
    public ArrayList<Dice> getListDice(int n){
        return trace[n-1];
    }
}
