package it.polimi.ingsw.utilities;

import it.polimi.ingsw.model.tools.Tools;

public class Wrapper<T> {

    private T param;

    public Wrapper(T param) {
        this.param = param;
    }

    public void myFunction() {
        if(param != null) {
            if (Tools.map.containsKey(param.getClass()))
                Tools.map.get(param.getClass()).function(param);
        }
    }

    public T getParam () {return this.param;}

}
