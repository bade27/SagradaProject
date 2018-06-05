package it.polimi.ingsw.model;

import java.util.ArrayList;

public class RoundTrace {
    //private ArrayList<Object> trace;
    private ArrayList[] trace;             //vettore di arrayList

    public RoundTrace(){
        //trace=new ArrayList<Object>(10);
        trace = new ArrayList [10];
        for(int i=0;i<10;i++) {
            ArrayList<Dice> listdice = new ArrayList<Dice>();       //inizializzo la lista
            //trace.set(i,listdice);
            trace[i]= listdice;                                     //la infilo nell i-esima posizione dell'array
        }
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

    public ArrayList[] getTrace() {
        return trace;
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
