package Test;
import java.awt.*;
import javax.swing.JApplet;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SagradaIntefaceTest extends JApplet implements Runnable
{
    private JPanel backgroundPanel;

    public void init ()
    {
        backgroundPanel = new JPanel();
        GridLayout layout = new GridLayout( 0, 3, 0, 0 );
        backgroundPanel.setLayout( layout );



        Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();

        this.add(backgroundPanel);
        this.setSize(dim);
    }


    public void run()
    {

    }
}
