//MAIN IN BETA NON FUNZIONANTE
package Test;
import java.net.*;
import java.util.concurrent.TimeUnit;
import java.awt.Color;
import java.io.*;


public class ServerMain implements Runnable
{
    private final static int PORT=5000;
    private ServerSocket server;

    public void run ()
    {
        System.out.println(">>>Server Started");
        try
        {
            server = new ServerSocket(PORT);


            System.out.println(">>>Waiting for player one");
            Socket newConnection = null;
            try
            {
                newConnection = server.accept();

            }
            catch (Exception e)
            {
                System.out.println("Server Stopped.") ;
                return;
            }

        }
        catch (Exception e)
        {
            System.out.println("Exception: "+e);
            e.printStackTrace();

            // Always close it:
            try {
                server.close();
            } catch(IOException ex) {
                System.err.println("Socket not closed");
            }
        }
    }



    public static void main(String[] args)
    {
        (new Thread(new ServerMain())).start();

    }
}
