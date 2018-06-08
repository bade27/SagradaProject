package it.polimi.ingsw.model.tools;

import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.exceptions.IllegalStepException;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.remoteInterface.Coordinates;
import it.polimi.ingsw.remoteInterface.Pair;
import it.polimi.ingsw.server.ServerModelAdapter;

import java.util.HashMap;
import java.util.Map;

public abstract class Tools {

    protected int price;
    protected int id;
    protected boolean finished;
    protected String name;

    protected static ServerModelAdapter adapter;

    protected static Dice d1;
    protected static Dice d2;
    protected static Integer pos_rt;
    protected static Coordinates pos_iniz1;
    protected static Coordinates pos_fin1;
    protected static Coordinates pos_iniz2;
    protected static Coordinates pos_fin2;
    protected static String instruction;




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
                    d1 = (Dice)o;
                else d2 = (Dice)o;
            }
        });
        map.put(Pair.class, new SetParameter() {
            @Override
            public void function(Object o) {
                if(d1 == null) {
                    Pair p = (Pair) o;
                    if (p != null)
                        d1 = new Dice(p.getValue(),p.getColor());
                }
                else{
                    Pair p = (Pair) o;
                    if (p != null)
                        d2 = new Dice(p.getValue(),p.getColor());
                }
            }
        });
        map.put(String.class, o -> instruction = ((String)o));
        map.put(Coordinates.class, new SetParameter() {
            @Override
            public void function(Object o) {

                if(pos_iniz1 == null)
                    pos_iniz1 = (Coordinates)o;
                else if(pos_fin1 == null)
                    pos_fin1 = (Coordinates)o;
                else if(pos_iniz2 == null)
                    pos_iniz2 = (Coordinates)o;
                else if(pos_fin2 == null) {
                    pos_fin2 = (Coordinates)o;
                }
            }
        });
        map.put(ServerModelAdapter.class, new SetParameter() {
            @Override
            public void function(Object o) {
                adapter = (ServerModelAdapter)o;
            }
        });
        map.put(Integer.class, new SetParameter() {
            @Override
            public void function(Object o) {
                pos_rt = (Integer)o;
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

    public String getName() {
        return name;
    }

    public static void setAllToNull() {
        d1 = null;
        d2 = null;
        pos_iniz1 = null;
        pos_fin1 = null;
        pos_iniz2 = null;
        pos_fin2 = null;
    }

}
