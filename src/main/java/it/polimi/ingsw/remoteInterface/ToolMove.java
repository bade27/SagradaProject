package it.polimi.ingsw.remoteInterface;

import it.polimi.ingsw.model.Dadiera;

import java.io.Serializable;

public class ToolMove implements Serializable {

    private int id; //non serve
    private Pair p;
    private int instruction; //per il primo tool memorizza inc o dec, in base a quello si usa la funzionalità opportuna
    private int i, j;
    private Dadiera d;

    public Pair getP() {
        return p;
    }

    public void setP(Pair p) {
        this.p = p;
    }

    public int getInstruction() {
        return instruction;
    }

    public void setInstruction(int instruction) {
        this.instruction = instruction;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public void setIJ(int i, int j) {
        this.i = i;
        this.j = j;
    }

    public int getId() {
        return id;
    } //non serve

    public void setId(int id) {
        this.id = id;
    } //non serve

    @Override
    public String toString() {
        return "ToolMove{" +
                "id=" + id +
                ", p=" + p +
                ", instruction='" + instruction + '\'' +
                ", i=" + i +
                ", j=" + j +
                '}';
    }

    public Dadiera getDadiera() {
        return d;
    }

    public void setDadiera(Dadiera d) {
        this.d = d;
    }
}
