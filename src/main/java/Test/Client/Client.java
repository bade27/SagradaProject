package Test.Client;

import java.util.ArrayList;

public class Client {

    private ClientConnectionHandler cch;
    private Graphic gr;
    private ClientModelAdapter model;

    public Client() {
        cch = new ClientConnectionHandler(this);
    }

    public String chooseWindow(ArrayList<String[]> list) {
        return list.get(0)[0];
    }

    public String myPrivateObj(String obj) {
        System.out.println(obj);
        return "ok";
    }
}
