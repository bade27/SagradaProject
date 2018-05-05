package with_cli;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Random;
import java.util.stream.IntStream;

public class Handler {

    private Socket client;
    private final int PING_TIMEOUT = 10000;
    private final int ACTION_TIMEOUT = 300000;
    private boolean isAlive = true;
    private BufferedReader inSocket;
    private PrintWriter outSocket;
    private final int myTurn;
    private boolean logged = false;

    public Handler(Socket client, int t) {
        this.client = client;
        myTurn = t;
        try {
            inSocket = new BufferedReader(new InputStreamReader(client.getInputStream()));
            outSocket = new PrintWriter(new BufferedWriter(
                    new OutputStreamWriter(client.getOutputStream())), true);
        }
        catch(Exception e) {
            System.out.println(e);
        }
    }

    public void doStuff() {
        if (!logged)
            login();
        //chooseNumber();
    }

    public boolean isAlive() {
        return isAlive;
    }

    private boolean ping() {
    	//setting up ping timeout
    	try {
            client.setSoTimeout(PING_TIMEOUT);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    	
    	//ping-pong communication
    	boolean reply = false;
    	try {
    		outSocket.write("ping\n");
    		String r = inSocket.readLine();
    		reply = r.equals("pong");
    	} catch (SocketTimeoutException ste) {
    		reply = false;
    	} catch (IOException e) {
    		e.getMessage();
    	}
    	return reply;
    	
    }
    
    public String login() {
    	
    	boolean setup = ping();
    	String user = null;
    	
    	if(setup) {
    		
    		//setting up ping timeout
        	try {
                client.setSoTimeout(ACTION_TIMEOUT);
            } catch (SocketException e) {
                e.printStackTrace();
            }
        	
    		System.out.println("I'm client " + myTurn + ", my globalTurn is " + myTurn + " and I'm loggin");
    		outSocket.write("login\n");
    		try {
    			user = inSocket.readLine();
    			Server.add(user);
    			logged = true;
    			isAlive = true;
    		} catch (SocketTimeoutException ste) {
    			boolean alive = ping();
    			if(!alive) {
    				isAlive = false;
    			} else {
    				isAlive = true;
    				System.out.println("time's up");
    			}
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	} else isAlive = false;
    	
        return user;
    }

    public int chooseNumber() {
        int c = -1;
        int[] array = IntStream.range(0, 5)
                .map(n -> new Random().nextInt(50) +1)
                .toArray();
        outSocket.println("choose");
        outSocket.println(array.length);
        for(Integer i : array)
            outSocket.println(i);
        try {
            c = Integer.parseInt(inSocket.readLine());
            System.out.println("I'm client " + myTurn + " and i've chosen " + c);
        } catch (SocketTimeoutException ste) {
            isAlive = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return c;
    }

}
