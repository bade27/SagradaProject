package with_cli;

import java.io.*;
import java.net.Socket;
import java.util.Random;
import java.util.concurrent.*;

public class Client implements Runnable {



    //for test only
    private final static String[] names = {"paul", "john", "logan",
            "george", "lucas", "peter"};
    //

    private static final String address = "localhost";
    private static final int PORT = 8000;

    private Socket socket;
    private BufferedReader inSocket;
    private PrintWriter outSocket;
    private Thread demon;

    public Client() {
        System.out.println("connecting...");
        try{
            socket = new Socket(address, PORT);
            inSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())), true);
        } catch (Exception e) {
            System.out.println("initialization gone wrong");
            e.printStackTrace();
        }
        demon = new Thread(this);
        demon.start();
        System.out.println("connected");
    }

    @Override
    public void run() {
        String action = "";
        try {
            while( (action = inSocket.readLine()) != "stop") {
                switch (action) {
                    case "choose":
                        chooseNumber();
                        continue;
                    case "login":
                    	ExecutorService executor = Executors.newCachedThreadPool();
                        Callable<Boolean> task = new Callable<Boolean>() {
                            public Boolean call() {
                                login();
                                return null;
                            }
                        };
                    	Future future = executor.submit(task);
                    	try {
                    	    future.get(5, TimeUnit.MINUTES);
                        } catch (TimeoutException te) {
                            System.out.println(te.getMessage());
                        } catch (InterruptedException ie) {
                            System.out.println(ie.getMessage());
                        } catch (ExecutionException ee) {
                            System.out.println(ee.getMessage());
                        } finally {
                    	    future.cancel(true);
                        }
                        continue;
                    case "ping":
                    	outSocket.write("pong\n");
                    	continue;
                    default:continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void chooseNumber() {
        try {

            if(new Random().nextBoolean())
                TimeUnit.SECONDS.sleep(15000);

            int arrayLen = Integer.parseInt(inSocket.readLine());
            int[] array = new int[arrayLen];
            for(int i = 0; i < arrayLen; i++)
                array[i] = Integer.parseInt(inSocket.readLine());
            int n = array[new Random().nextInt(arrayLen)];
            outSocket.println(n);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Boolean login() {
        StringBuilder sb = new StringBuilder(names[new Random().nextInt(6)]);
        sb.append("\n");
        outSocket.write(sb.toString());
        return outSocket.checkError();
    }

    public static void main(String[] args) {
        Client c1 = new Client();
    }

}
