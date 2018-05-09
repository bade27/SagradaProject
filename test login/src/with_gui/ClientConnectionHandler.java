package with_gui;

import java.io.*;
import java.net.Socket;
import java.util.concurrent.*;

public class ClientConnectionHandler implements Runnable {

    private static final String address = "localhost";
    //private static final String address = "192.168.1.5";
    private static final int PORT = 8000;

    private Object bufferLock = new Object();
    private String[] buffer = null;

    private Socket socket;
    private BufferedReader inSocket;
    private PrintWriter outSocket;
    private Thread demon;

    public ClientConnectionHandler() {
        System.out.println("connecting...");
        try{
            socket = new Socket(address, PORT);
            inSocket = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(socket.getOutputStream())));
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
        //System.out.println("started");
        try {
            while( (action = inSocket.readLine()) != "stop") {
                //System.out.println("entered");
                switch (action) {
                    case "choose":
                        //chooseNumber();
                        continue;
                    case "login":
                    	/*new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String s = "";
                                //System.out.println("i'm a thread");
                                synchronized (bufferLock) {
                                    //System.out.println("synch block");
                                    while (buffer == null){
                                        try {
                                            //System.out.println("exiting synch");
                                            bufferLock.wait();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                        //System.out.println("done synch");
                                    }
                                    s = buffer[0] + ", " + buffer[1];
                                    bufferLock.notifyAll();
                                }
                                login(s);
                            }
                        }).start();*/

                    	//---------------------------
                        ExecutorService executor = Executors.newCachedThreadPool();
                        Callable<Boolean> task = new Callable<Boolean>() {
                            public Boolean call() {
                                String s = "";
                                synchronized (bufferLock) {
                                    while (buffer == null){
                                        try {
                                            bufferLock.wait();
                                        } catch (InterruptedException e) {
                                            System.out.println("i'm dead");
                                        }
                                    }
                                    s = buffer[0] + ", " + buffer[1];
                                    bufferLock.notifyAll();
                                }
                                return login(s);
                            }
                        };
                        Future future = executor.submit(task);
                        try {
                            future.get(30, TimeUnit.SECONDS);
                        } catch (TimeoutException te) {
                            //System.out.println(te.getMessage());
                            System.out.println("too late to reply");
                        } catch (InterruptedException ie) {
                            //System.out.println(ie.getMessage());
                        } catch (ExecutionException ee) {
                            //System.out.println(ee.getMessage());
                        } finally {
                            future.cancel(true);
                        }
                        continue;
                    	//---------------------------
                    case "ping":
                        //System.out.println("hi");
                        outSocket.write("pong\n");
                    	outSocket.flush();
                    	continue;
                    default:continue;
                }
            }
        } catch (IOException e) {
            //e.printStackTrace();
        }
    }

    public Boolean login(String s) {
        StringBuilder sb = new StringBuilder(s);
        sb.append("\n");
        outSocket.write(sb.toString());
        return outSocket.checkError();
    }

    public boolean setBuffer(String[] buff) {
        boolean ok = false;
        synchronized (bufferLock) {
            buffer = new String[2];
            for(int i = 0; i < buff.length; i++)
                buffer[i] = new String(buff[i]);
            ok = true;
            bufferLock.notifyAll();
        }
        return ok;
    }


}
