package it.polimi.ingsw.remoteInterface;

import java.io.Serializable;

public class ToolMove implements Serializable {

    private int id;
    private Pair p;
    String instruction; //per il primo tool memorizza inc o dec, in base a quello si usa la funzionalità opportuna
    int i, j;

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
    }

    public void setId(int id) {
        this.id = id;
    }

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
}
