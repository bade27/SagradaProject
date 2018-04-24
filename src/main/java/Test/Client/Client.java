package Test.Client;

import java.util.ArrayList;

public class Client {

    private ClientConnectionHandler cch;

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

    public static void main(String[] args) {
        new Client();
    }

}
