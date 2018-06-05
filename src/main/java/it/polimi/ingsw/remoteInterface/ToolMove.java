package it.polimi.ingsw.remoteInterface;

import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.model.RoundTrace;
import it.polimi.ingsw.model.Window;

import java.io.Serializable;
import java.util.ArrayList;

public class ToolMove implements Serializable {

    private Pair p;
    private String instruction;
    private Integer i_start, j_start;
    private Integer i_end, j_end;
    private Dadiera d;
    private Window w;
    private RoundTrace roundTrace;

    private ArrayList<int[]> first = new ArrayList<>();
    private ArrayList<int[]> second = new ArrayList<>();
    private ArrayList<Pair> pair = new ArrayList<>();

    public ArrayList<Pair> getPair() {
        return pair;
    }

    public void setPair(Pair pair) {
        this.pair.add(pair);
    }

    public ArrayList<int[]> getFirst() {
        return first;
    }

    public void setFirst(int[] pos) {
        this.first.add(pos);
    }

    public ArrayList<int[]> getSecond() {
        return second;
    }

    public void setSecond(int[] pos) {
        this.second.add(pos);
    }


    public Pair getP() {
        return p;
    }

    public void setP(Pair p) {
        this.p = p;
    }

    public String getInstruction() {
        return instruction;
    }

    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    public Integer getI_start() {
        return i_start;
    }

    public Integer getJ_start() {
        return j_start;
    }

    public void setIJStart(int i, int j) {
        this.i_start = i;
        this.j_start = j;
    }

    public Integer getI_end() {
        return i_end;
    }

    public Integer getJ_end() {
        return j_end;
    }

    public void setIJEnd(int i, int j) {
        this.i_end = i;
        this.j_end = j;
    }



    public Dadiera getDadiera() {
        return d;
    }

    public void setDadiera(Dadiera d) {
        this.d = d;
    }

    public Window getW() {
        return w;
    }

    public void setW(Window w) {
        this.w = w;
    }


    public void setRoundTrace(RoundTrace roundTrace) {
        this.roundTrace = roundTrace;
    }

    public RoundTrace getRoundTrace() {
        return roundTrace;
    }
}
