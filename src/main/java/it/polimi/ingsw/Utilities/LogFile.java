package it.polimi.ingsw.Utilities;

import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogFile
{
    public static void cleanFile ()
    {
        FileWriter f;
        try {
            f = new FileWriter("LogFile.log");
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }


    public synchronized static void addLog (String s)
    {
        FileWriter f;
        try {
            f = new FileWriter("LogFile.log",true);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            f.write(dateFormat.format(date) + " " + s + "\r\n");
            f.flush();
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }

    public synchronized static void addLog (String s, StackTraceElement[] st)
    {
        FileWriter f;
        try {
            f = new FileWriter("LogFile.log",true);
            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            Date date = new Date();
            if (s.equals(""))
                s = "it.polimi.ingsw.Server Error";
            f.write(dateFormat.format(date) + " " + s + "\r\n");
            for (int i = 0; i < st.length ; i++)
                f.write("\t" + st[i].toString() + "\r\n");
            f.flush();
        }
        catch (IOException ex){
            System.out.println(ex.getMessage());
        }
    }
}
