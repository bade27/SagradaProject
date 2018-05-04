package Server;

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


    public static void addLog (String s)
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
}
