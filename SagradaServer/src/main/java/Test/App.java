package Test;

import javax.swing.*;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        Window w  = new Window("kaleidoscopic_dream.xml");
        Dadiera d = new Dadiera(5);

        Partita p = new Partita(w,d);
    }
}
