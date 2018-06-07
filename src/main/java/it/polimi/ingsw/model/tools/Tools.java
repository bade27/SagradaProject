package it.polimi.ingsw.model.tools;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.IllegalStepException;
import it.polimi.ingsw.model.Dadiera;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.RoundTrace;
import it.polimi.ingsw.model.Window;
import it.polimi.ingsw.remoteInterface.ToolMove;

import java.util.HashMap;
import java.util.Map;

public abstract class Tools {

    protected int price;
    protected int id;
    protected String name;

    protected RoundTrace rt;
    protected Dadiera dadiera;
    protected Window window;

    protected static Dice d1;
    protected static Dice d2;
    protected static Integer pos_rt;
    protected static Integer [] pos_iniz1;
    protected static Integer [] pos_fin1;
    protected static Integer [] pos_iniz2;
    protected static Integer [] pos_fin2;
    protected static String instruction;
    protected Integer level;
    protected boolean finished;



    public interface SetParameter {
        public void function(Object o);
    }

    public static final Map<Class, SetParameter> map;

    static {
        map = new HashMap<>();
        map.put(Dice.class, new SetParameter() {
            @Override
            public void function(Object o) {
                if(d1 == null)
                    setD1((Dice)o);
                else setD2((Dice)o);
            }
        });
        map.put(String.class, o -> setInstruction((String)o));
        map.put(Integer[].class, new SetParameter() {
            @Override
            public void function(Object o) {
                if(pos_iniz1 == null) {
                    setPos_iniz1((Integer[]) o);
                    return;
                }
                if(pos_fin1 == null) {
                    setPos_fin1((Integer[]) o);
                    return;
                }
                if(pos_iniz2 == null) {
                    setPos_iniz2((Integer[]) o);
                    return;
                }
                if(pos_fin2 == null) {
                    setPos_fin2((Integer[]) o);
                    return;
                }
            }
        });
    }




    public Tools() {
        finished = true;
    }

    //abstract functions
    abstract void setPrice();

    abstract public int getPrice();

    abstract public int getId();

    abstract public boolean canPlaceDie (Dice d);

    abstract public void use() throws IllegalStepException, IllegalDiceException;


    //others
    public boolean isToolFinished (){ return finished; }

    public void setToolFinished (boolean f){ finished=f; }

    //recupera tutti i valori memorizzati in toolmove. sono poi i singoli figli a controllare che
    //ci sia tutto ciò che serve
    public void setToolMove (ToolMove tm) {
        //d1 = tm.getP() !=null ?new Dice(tm.getP().getValue(), tm.getP().getColor()) : null;
        d1 = tm.getPair().size() > 0 ? new Dice(tm.getPair().get(0).getValue(), tm.getPair().get(0).getColor()) : null;
        d2 = tm.getPair().size() == 2 ? new Dice(tm.getPair().get(1).getValue(), tm.getPair().get(1).getColor()) : null;
        instruction = tm.getInstruction();
        dadiera = tm.getDadiera();
        window = tm.getW();
        /*pos_iniz1 = (tm.getI_start() != null || tm.getJ_start() != null)
                ? new int[] {tm.getI_start(), tm.getJ_start()}
                : null;
        pos_fin1 = (tm.getI_end() != null || tm.getJ_end() != null)
                ? new int[] {tm.getI_end(), tm.getJ_end()}
                : null;*/
        pos_iniz1 = tm.getFirst().size() > 0 ? tm.getFirst().get(0) : null;
        pos_fin1 = tm.getFirst().size() == 2 ? tm.getFirst().get(1) : null;

        pos_iniz2 = tm.getSecond().size() > 0 ? tm.getSecond().get(0) : null;
        pos_fin2 = tm.getSecond().size() == 2 ? tm.getSecond().get(1) : null;
        pos_rt = 1;//To modify
        rt = tm.getRoundTrace();
    }


    //setters----------------------------------------------------------------------------------------------
    public void setRt(RoundTrace rt) {
        this.rt = rt;
    }

    public static void setD1(Dice d1) {
        Tools.d1 = d1;
    }

    public static void setD2(Dice d2) {
        Tools.d2 = d2;
    }

    public void setDadiera(Dadiera dadiera) {
        this.dadiera = dadiera;
    }

    public void setWindow(Window window) {
        this.window = window;
    }

    public static void setPos_iniz1(Integer[] pos_iniz1) {
        Tools.pos_iniz1 = pos_iniz1;
    }

    public static void setPos_fin1(Integer[] pos_fin1) { Tools.pos_fin1 = pos_fin1; }

    public static void setPos_fin2(Integer[] pos_fin2) { Tools.pos_fin2 = pos_fin2; }

    public static void setPos_iniz2(Integer[] pos_iniz2) {
        Tools.pos_iniz2 = pos_iniz2;
    }

    public void setPos_rt(int pos_rt){
        this.pos_rt=pos_rt;
    }

    public void setLevel(int level){ this.level=level; }

    public static void setInstruction(String instruction) {
        Tools.instruction = instruction;
    }

    public static void setAllToNull() {
        d1 = null;
        d2 = null;
        pos_iniz1 = null;
        pos_fin1 = null;
        pos_iniz2 = null;
        pos_fin2 = null;
    }

    //getters-----------------------------------------------------------------------------------------------
    public RoundTrace getRt() {
        return rt;
    }

    public Dice getD1() {
        return d1;
    }

    public Dice getD2() {
        return d2;
    }

    public Dadiera getDadiera() {
        return dadiera;
    }

    public Window getWindow() {
        return window;
    }

    public Integer[] getPos_iniz1() {
        return pos_iniz1;
    }

    public Integer[] getPos_fin1() {
        return pos_fin1;
    }

    public Integer[] getPos_iniz2() {
        return pos_iniz2;
    }

    public Integer[] getPos_fin2() {
        return pos_fin2;
    }

    public int getPos_rt(){
        return pos_rt;
    }

    public int getLevel(){ return level;}

    //do not delete, this is needed
    public String getName() {
        return name;
    }
}
