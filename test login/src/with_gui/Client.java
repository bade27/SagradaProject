package with_gui;

import java.io.*;
import java.net.Socket;

public class Client implements Runnable {

    private static final String address = "localhost";
    private static final int PORT = 8000;

    private Object bufferLock = new Object();
    private String[] buffer = null;

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
        System.out.println("started");
        try {
            while( (action = inSocket.readLine()) != "stop") {
                System.out.println("entered");
                switch (action) {
                    case "choose":
                        //chooseNumber();
                        continue;
                    case "login":
                    	new Thread(new Runnable() {
                            @Override
                            public void run() {
                                String s = "";
                                synchronized (bufferLock) {
                                    while (buffer == null){
                                        try {
                                            bufferLock.wait();
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    s = buffer[0] + ", " + buffer[1];
                                    bufferLock.notifyAll();
                                }
                                login(s);
                            }
                        }).start();
                        continue;
                    case "ping":
                        System.out.println("hi");
                        //outSocket.write("pong\n");
                    	//outSocket.flush();
                    	continue;
                    default:continue;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
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
            buffer.notifyAll();
        }
        return ok;
    }

    public static void main(String[] args) {
        Client c1 = new Client();
    }

}
