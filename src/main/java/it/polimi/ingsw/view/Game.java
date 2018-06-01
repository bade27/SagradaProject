package it.polimi.ingsw.view;

import it.polimi.ingsw.GUI;
import it.polimi.ingsw.client.ClientPlayer;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.Scanner;

public class Game extends Application implements GUI {


    private DadieraGUI dadieraG;
    private GridGUI gridG;
    private PlayersGUI plaG;

    private MessageBox msgb;

    private int num=4;
    private ClientPlayer clientPlayer;
    private boolean enableBoard;


    public Game() {
        enableBoard = false;
        initailizeComunication();
    }

    //<editor-fold desc="Initialization pahse">
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
            clientPlayer = new ClientPlayer(Integer.parseInt(s),this);
        }
        catch (RemoteException e){
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane root = new GridPane();
        //root.setAlignment(Pos.CENTER);
        root.setVgap(30);
        dadieraG = new DadieraGUI(root, num, this);
        dimWindows.dim(dadieraG);
        //root.add(dadieraG, 0, 1);

        gridG = new GridGUI(root,this);
        dimWindows.dim(gridG);
        //root.add(gridG,0,2);

        plaG= new PlayersGUI(root,this);


        //message and end of turn button
        BorderPane bottom = new BorderPane();
        msgb = new MessageBox("Wellcome!");
        bottom.setLeft(msgb);

        EndButton pass = new EndButton("End Turn", this);
        bottom.setRight(pass);

        root.add(bottom, 0, 3);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setTitle("Sagrada");
        primaryStage.setScene(scene);
        dimWindows.dim(root);
        primaryStage.show();
    }
    //</editor-fold>

    public void modPair(Pair pair){
        clientPlayer.setMovePair(pair);
    }

    public void modIJ(int i,int j){
        clientPlayer.setMoveIJ(i,j);
    }

    /**
     * updates the dice displayed on dadiera
     * @param p
     */
    @Override
    public void updateDadiera(Pair[] p) {
        dadieraG.updateGraphic(p);
        /*System.out.println("Dadiera:");
        for(int i = 0; i < p.length ; i++)
            System.out.print(p[i].toString() + "\t|\t");
        System.out.println();*/
    }

    /**
     * updates the dice on the grid
     * @param p
     */
    @Override
    public void updateWindow(Pair[][] p) {
        gridG.updateGrid(p);
        /*System.out.println("Window:");
        for(int i = 0; i < p.length ; i++)
        {
            for (int j = 0; j<p[i].length ;j++)
                System.out.print(p[i][j].toString() + "\t|\t");
            System.out.println();
        }*/
    }


    /**
     * displays the message relative to the status of the move
     * @param msg
     */
    public void updateMessage(String msg) {
        msgb.updateGraphic(msg);
    }

    /**
     * enables or disables the board
     * @param enableBoard
     */
    @Override
    public void setEnableBoard(boolean enableBoard) {
        this.enableBoard = enableBoard;
        dadieraG.setEnable(enableBoard);
        gridG.setEnable(enableBoard);
    }

    /**
     * sends to client player the move made by the user
     */
    public void makeMove() {
        clientPlayer.myMove();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
