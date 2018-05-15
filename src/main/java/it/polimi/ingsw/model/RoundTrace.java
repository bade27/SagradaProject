package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.IllegalStepException;

import java.util.ArrayList;

public class RoundTrace {
    ArrayList<Dice>[] trace;

    public RoundTrace(){
        trace=new ArrayList[10];
    }

    /**
     * Aggiunge il dado d nella posizione n del tracciato
     * @param n cella del tracciato round nella quale inserire il dado
     * @param d dado da aggiungere
     */
    public void addDice(int n,Dice d){
        trace[n-1].add(d);
    }

    /**
     * Toglie il dado d dalla posizione n del tracciato
     * @param n cella del tracciato round dalla quale togliere il dado
     * @param d dado da togliere
     */
    public void deleteDice(int n,Dice d) throws IllegalStepException {
        boolean found=false;
        for(int i=0;i<trace[n-1].size() && found==false;i++){
            if (d.equals(trace[n-1].get(i))){
                trace[n-1].remove(i);
                found=true;
            }
        }
        if (found==false)
            throw new IllegalStepException();
    }

    /**
     * Genera la lista dei dadi presenti in una determinata cella del tracciato round
     * @param n cella del tracciato round
     * @return lista di dadi
     */
    public ArrayList<Dice> getListDice(int n){
        return trace[n-1];
    }
}
