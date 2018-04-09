package Test;
import java.awt.*;
import javax.swing.*;

import java.awt.event.*;
import javax.swing.border.Border;

public class Partita extends JFrame
{
    private Window finestra;
    private Dadiera dadiera;
    private JPanel boardPanel,textPanel,dicePanel;
    private JLabel title;
    private int rows,cols;
    private CellGraphic board [][];
    private CellGraphic dices[];

    //Dado selezionato da quelli sopra
    private Dice selectedDice;

    public Partita (Window w,Dadiera d)
    {
        finestra = w;
        dadiera = d;
        initGraphic();
    }


    private void initGraphic ()
    {
        rows = finestra.getRows();
        cols = finestra.getCols();
        boardPanel = new JPanel();
        textPanel = new JPanel();
        dicePanel = new JPanel();
        title = new JLabel();
        board = new CellGraphic[rows][cols];
        dices = new CellGraphic[dadiera.getLength()];


        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GridLayout layout = new GridLayout(rows, cols, 0, 0 );
        boardPanel.setLayout( layout );

        for (int i=0;i < rows;i++)
        {
            for (int j = 0; j < cols; j++)
            {
                board[i][j] = new CellGraphic(finestra.getCell(i,j),i,j);
                board[i][j].updateGrpahic();
                board[i][j].addActionListener(new BoardListener(board[i][j]));
                boardPanel.add(board[i][j]);
            }
        }

        for (int i=0;i<dadiera.getLength();i++)
        {
            dices[i] = new CellGraphic(new Cell (dadiera.getDice(i)),i,0);
            dices [i].updateGrpahic();
            dices[i].addActionListener(new DicesListener(dices[i]));
            dicePanel.add(dices[i]);
        }
        title.setText("Wecocme to Forza Quattro... Waiting for opponent player");
        textPanel.add(title);

        this.add(boardPanel);
        this.add(textPanel,BorderLayout.NORTH);
        this.add(dicePanel,BorderLayout.NORTH);
        this.setSize(400, 400);
        this.setVisible(true);
    }

    public void updateGraphic ()
    {
        System.out.println("aas");
        for (int i=0;i < rows;i++)
            for (int j = 0; j < cols; j++)
                board[i][j].updateGrpahic();

        for (int i=0;i < rows;i++)
            dices[i].updateGrpahic();

    }

    class BoardListener implements ActionListener
    {
        private CellGraphic cell;
        public BoardListener( CellGraphic c )
        {
            cell = c;
        }

        public void actionPerformed(ActionEvent arg)
        {
            if (selectedDice != null)
            {
                try
                {
                    finestra.addDice(cell.getPosX(),cell.getPosY(),selectedDice);
                    System.out.println("-------------");
                    updateGraphic();
                }
                catch (IllegalDiceException ex)
                {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Error", JOptionPane.INFORMATION_MESSAGE);
                }
                selectedDice = null;

            }
            else
                JOptionPane.showMessageDialog(null, "No dice selected", "Error", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    class DicesListener implements ActionListener
    {
        private CellGraphic cell;
        public DicesListener( CellGraphic c )
        {
            cell = c;
        }

        public void actionPerformed(ActionEvent arg)
        {
            if (selectedDice != null)
                JOptionPane.showMessageDialog(null, "Dice already selected", "Error", JOptionPane.INFORMATION_MESSAGE);
            else
                selectedDice = cell.getCurrentDice();
        }
    }
}
