package with_gui;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Server {

    public static ArrayList<String> users = new ArrayList<>();
    //public static Object token = new Object();
    public static int globalTurn = 0;
    //private static Stack<Integer> stack = new Stack<>();

    public synchronized static void add(String s) {
        users.add(s);
    }

    private static void nextTurn() {
        switch (globalTurn) {
            case 0:
                Server.globalTurn = 1;
                break;
            case 1:
                Server.globalTurn = 2;
                break;
            case 2:
                Server.globalTurn = 0;
        }
    }

    public static void main(String[] args) {
        /*stack.push(2);
        stack.push(1);
        stack.push(3);
        Thread[] t = new Thread[3];
        try(ServerSocket serverSocket = new ServerSocket(8000)) {
            int turn = 0;
            for(int i = 0; i < 3; i++) {
                if(!stack.isEmpty()) {
                    turn = stack.pop();
                    t[i] = new Thread(new Handler(serverSocket.accept(), turn));
                }
            }
        } catch(IOException e) {
            System.out.println("Server exception " + e.getMessage());
        }
        t[2].start();
        t[0].start();
        t[1].start();
        */
        Handler[] h = new Handler[3];
        try(ServerSocket serverSocket = new ServerSocket(8000)) {
            int turn = 0;
            for(int i = 0; i < 1; i++) {
                    h[i] = new Handler(serverSocket.accept(), turn++);
                }
        } catch(IOException e) {
            System.out.println("Server exception " + e.getMessage());
        }

        System.out.println("ok, let's get started");
        int deads = 0;
        /*for(int i = 0; i < 12 && deads < 3; i++) {
            Handler hd = h[globalTurn];
            if(hd.isAlive()) {
                System.out.println("turno di " + globalTurn);
                System.out.println(hd.login());
                nextTurn();
            } else {
                System.out.println("player " + globalTurn + " is dead");
                deads++;
                globalTurn = (globalTurn + 1) % 3;
            }
        }*/

        while(true) System.out.println(h[0].login());

    }

}
