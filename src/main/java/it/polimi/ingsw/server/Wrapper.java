package it.polimi.ingsw.server;

import it.polimi.ingsw.model.tools.Tools;

public class Wrapper<T> {

    T param;

    public Wrapper(T param) {
        this.param = param;
    }

    public void myFunction() {
        if(Tools.map.containsKey(param.getClass()))
            Tools.map.get(param.getClass()).function(param);
    }

}
