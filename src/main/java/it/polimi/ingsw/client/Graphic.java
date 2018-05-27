package it.polimi.ingsw.client;

import it.polimi.ingsw.GUI;
import it.polimi.ingsw.model.Dice;
import it.polimi.ingsw.model.Window;
import it.polimi.ingsw.remoteInterface.Pair;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Scanner;

public class Graphic extends JFrame implements GUI
{
    private JPanel boardPanel,textPanel,dicePanel;
    private int rows,cols;
    private CellGraphic board [][];
    private CellGraphic dices[];
    private ClientPlayer player;


    //Dado selezionato da quelli sopra
    private Dice selectedDice;


    private boolean enableBoard;

    public Graphic()
    {
        initailizeComunication();
    }

    private void initailizeComunication ()
    {
        try {
            //1:RMI     0:Socket
            Scanner cli = new Scanner(System.in);
            String s;
            System.out.println("Select connection mode: 0=Socket ; 1=RMI");
            do{
                s = cli.nextLine();
            }while (!s.equals("1") && !s.equals("0"));
            player = new ClientPlayer(Integer.parseInt(s),this);
        }
        catch (RemoteException e){
            Thread.currentThread().interrupt();
        }
    }

    public String myPrivateObj(String obj) {
        System.out.println(obj);
        return "ok";
    }




    /**
     * Inizializza la grafica di partita
     */
    public void initGraphic (ClientModelAdapter giocatore)
    {
        Window finestra = giocatore.getWindow();
        //Dadiera dadiera = giocatore.getDadiera();


        rows = finestra.getRows();
        cols = finestra.getCols();
        boardPanel = new JPanel();
        textPanel = new JPanel();
        dicePanel = new JPanel();
        board = new CellGraphic[rows][cols];
        //dices = new CellGraphic[dadiera.getDim()];


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

        /*for (int i=0;i<dadiera.getDim();i++)
        {
            dices[i] = new CellGraphic(new Cell(dadiera.getDice(i)),i,0);
            dices [i].updateGrpahic();
            //dices[i].addActionListener(new DicesListener(dices[i]));
            dicePanel.add(dices[i]);
        }*/


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

    public boolean isEnableBoard() {
        return enableBoard;
    }

    @Override
    public void updateDadiera(Pair[] dadiera) {

    }

    @Override
    public void updateWindow(Pair[][] window) {

    }

    public void setEnableBoard(boolean enableBoard) {
        this.enableBoard = enableBoard;
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
            if (enableBoard)
            {
                enableBoard = false;
                player.sendMove("Cell clicked: x=" + cellGraph.getPosX() +   " y=" + cellGraph.getPosY());
            }
        }
    }

    /*class DicesListener implements ActionListener
    {
        private CellGraphic cellGraph;
        public DicesListener( CellGraphic c )
        {
            cellGraph = c;
        }

        public void actionPerformed(ActionEvent arg)
        {
            /*if (selectedDice != null)
                JOptionPane.showMessageDialog(null, "Dice already selected", "Error", JOptionPane.INFORMATION_MESSAGE);
            else
                selectedDice = cellGraph.getCurrentDice();
        }
    }*/

    public static void main(String[] args)
    {
        Graphic g1 = new Graphic();
        //Graphic g2 = new Graphic();
    }
}
