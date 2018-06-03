package it.polimi.ingsw.remoteInterface;

import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.model.Window;

import java.io.Serializable;

public class ToolMove implements Serializable {

    private Pair p;
    private String instruction;
    private Integer i_start, j_start;
    private Integer i_end, j_end;
    private Dadiera d;
    private Window w;

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

    @Override
    public String toString() {
        return "ToolMove{" +
                ", p=" + p +
                ", instruction='" + instruction + '\'' +
                ", i_start=" + i_start +
                ", j_start=" + j_start +
                '}';
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
}
