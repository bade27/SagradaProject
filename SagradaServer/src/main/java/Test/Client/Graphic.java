package Test.Client;
import Test.Exceptions.ModelException;
import Test.Model.Cell;
import Test.Model.Dadiera;
import Test.Model.Dice;
import Test.Model.Window;

import java.awt.*;
import javax.swing.*;

import java.awt.event.*;

public class Graphic extends JFrame
{
    ClientModelAdapter giocatore;
    private JPanel boardPanel,textPanel,dicePanel;
    private int rows,cols;
    private CellGraphic board [][];
    private CellGraphic dices[];

    //Dado selezionato da quelli sopra
    private Dice selectedDice;

    public Graphic()
    {
        giocatore = new ClientModelAdapter();
        initailizeComunication();
        initGraphic();
    }

    private void initailizeComunication ()
    {

    }

    /**
     * Inizializza la grafica di partita
     */
    private void initGraphic ()
    {
        Window finestra = giocatore.getWindow();
        Dadiera dadiera = giocatore.getDadiera();

        rows = finestra.getRows();
        cols = finestra.getCols();
        boardPanel = new JPanel();
        textPanel = new JPanel();
        dicePanel = new JPanel();
        board = new CellGraphic[rows][cols];
        dices = new CellGraphic[dadiera.getDim()];


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

        for (int i=0;i<dadiera.getDim();i++)
        {
            dices[i] = new CellGraphic(new Cell(dadiera.getDice(i)),i,0);
            dices [i].updateGrpahic();
            dices[i].addActionListener(new DicesListener(dices[i]));
            dicePanel.add(dices[i]);
        }

        this.add(boardPanel);
        this.add(textPanel,BorderLayout.NORTH);
        this.add(dicePanel,BorderLayout.NORTH);
        this.setSize(400, 400);
        this.setVisible(true);
    }

    /**
     * Aggiorna la grafica di partita
     */
    private void updateGraphic ()
    {
        //Aggiorna la grafica della board coi nuovi dadi selezionati
        for (int i=0;i < rows;i++)
            for (int j = 0; j < cols; j++)
                board[i][j].updateGrpahic();

        //Aggiorna la grafica della dadiera eliminando i dadi scelti
        //In teoria quando viene chiamata la funzione selectedDice non è stato ancora rimesso a null
        for (int i=0;i<dices.length;i++)
            if (dices[i].getCurrentDice().equals(selectedDice))
                dicePanel.remove(dices[i]);

        dicePanel.updateUI();
    }

    class BoardListener implements ActionListener
    {
        private CellGraphic cellGraph;
        public BoardListener( CellGraphic c )
        {
            cellGraph = c;
        }

        public void actionPerformed(ActionEvent arg)
        {
            if (selectedDice != null)
            {
                try
                {
                    giocatore.addDiceToBoard(cellGraph.getPosX(),cellGraph.getPosY(),selectedDice);
                    giocatore.deleteDiceFromDadiera(selectedDice);

                    updateGraphic();
                }
                catch (ModelException ex)
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
        private CellGraphic cellGraph;
        public DicesListener( CellGraphic c )
        {
            cellGraph = c;
        }

        public void actionPerformed(ActionEvent arg)
        {
            if (selectedDice != null)
                JOptionPane.showMessageDialog(null, "Dice already selected", "Error", JOptionPane.INFORMATION_MESSAGE);
            else
                selectedDice = cellGraph.getCurrentDice();
        }
    }

    public static void main(String[] args)
    {
        Graphic g = new Graphic();
    }
}
