package it.polimi.ingsw.utilities;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFile
{
    private String name;

    public LogFile (String n)
    {
        name = n + ".log";
        FileWriter f = null;
        try {
            f = new FileWriter(name);
        }
        catch (IOException ex){
            System.out.println("Impossible to create to log file: " + name + ".log");
        }
        finally
        {
            try{
                if (f != null)
                    f.close();
            }catch (IOException ex ){
                System.out.println(ex.getMessage());
            }
        }
    }


    public synchronized void addLog (String s)
    {
        FileWriter f = null;
        try {
            f = new FileWriter(name,true);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            f.write(dateFormat.format(date) + " " + s + "\r\n");
            f.flush();
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        finally
        {
            try{
                if (f != null)
                    f.close();
            }catch (IOException ex ){
                System.out.println(ex.getMessage());
            }
        }
    }

    public synchronized void addLog (String s, StackTraceElement[] st)
    {
        FileWriter f = null;
        try {
            f = new FileWriter(name,true);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            if (s.equals(""))
                s = "it.polimi.ingsw.server Error";
            f.write(dateFormat.format(date) + " " + s + "\r\n");
            for (int i = 0; i < st.length ; i++)
                f.write("\t" + st[i].toString() + "\r\n");
            f.flush();
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        finally
        {
            try{
                if (f != null)
                    f.close();
            }catch (IOException ex ){
                System.out.println(ex.getMessage());
            }
        }
    }
}
