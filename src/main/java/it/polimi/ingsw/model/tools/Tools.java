package it.polimi.ingsw.model.tools;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.IllegalStepException;
import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.RoundTrace;
import it.polimi.ingsw.model.Window;
import it.polimi.ingsw.remoteInterface.ToolMove;

public abstract class Tools {

    protected int price;
    protected int type;
    protected String name;

    protected RoundTrace rt;
    protected Dadiera dadiera;
    protected Window window;

    protected Dice d1;
    protected Dice d2;
    protected int pos_rt;
    protected int [] pos_iniz1;
    protected int [] pos_fin1;
    protected int [] pos_iniz2;
    protected int [] pos_fin2;
    protected int index;

    public Tools() { }

    abstract void setPrice();

    abstract public int getPrice();

    abstract public int getType();

    public void setToolMove (ToolMove tm)
    {
        d1 = new Dice(tm.getP().getValue(),tm.getP().getColor());
        index = tm.getInstruction();
        dadiera = tm.getDadiera();
    }



    public RoundTrace getRt() {
        return rt;
    }

    public void setRt(RoundTrace rt) {
        this.rt = rt;
    }

    /*public Dice getD1() {
        return d1;
    }

    public void setD1(Dice d1) {
        this.d1 = d1;
    }*/

    public Dice getD2() {
        return d2;
    }

    public void setD2(Dice d2) {
        this.d2 = d2;
    }

    public Dadiera getDadiera() {
        return dadiera;
    }

    public void setDadiera(Dadiera dadiera) {
        this.dadiera = dadiera;
    }

    public Window getWindow() {
        return window;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public int[] getPos_iniz1() {
        return pos_iniz1;
    }

    public void setPos_iniz1(int[] pos_iniz1) {
        this.pos_iniz1 = pos_iniz1;
    }

    public int[] getPos_fin1() {
        return pos_fin1;
    }

    public void setPos_fin1(int[] pos_fin1) {
        this.pos_fin1 = pos_fin1;
    }

    public int [] getPos_iniz2() {
        return pos_iniz2;
    }

    public void setPos_iniz2(int [] pos_iniz2) {
        this.pos_iniz2 = pos_iniz2;
    }

    public int[] getPos_fin2() {
        return pos_fin2;
    }

    public void setPos_fin2(int [] pos_fin2) {
        this.pos_fin2 = pos_fin2;
    }

    public int getPos_rt(){
        return pos_rt;
    }

    public void setPos_rt(int pos_rt){
        this.pos_rt=pos_rt;
    }

    /*public int getIndex(){ return index;}

    public void setIndex(int index){ this.index=index; }*/

    abstract public void use() throws IllegalStepException, IllegalDiceException;


}
