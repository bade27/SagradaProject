package it.polimi.ingsw.view;

import it.polimi.ingsw.GUI;
import it.polimi.ingsw.client.ClientModelAdapter;
import it.polimi.ingsw.client.ClientPlayer;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.Scanner;

public class Game extends Application implements GUI {


    private DadieraGUI dadieraG;
    private GridGUI gridG;

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
        dadieraG = new DadieraGUI(root, num, this);
        gridG = new GridGUI(root, dadieraG,this);
        Scene scene = new Scene(root, 400, 400);
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

    @Override
    public void updateDadiera(Pair[] p) {
        //Manca l'update della dadiera
        //dadieraG.updateGraphic(p);
        System.out.println("Dadiera:");
        for(int i = 0; i < p.length ; i++)
            System.out.print(p[i].toString() + " ");
        System.out.println();
    }

    @Override
    public void updateWindow(Pair[][] p) {
        //Manca l'update della dadiera
        //gridG.updateGrid();
        System.out.println("Window:");
        for(int i = 0; i < p.length ; i++)
        {
            for (int j = 0; j<p[i].length ;j++)
                System.out.print(p[i][j].toString() + " ");
            System.out.println();
        }
    }


    @Override
    public void setEnableBoard(boolean enableBoard) {
        this.enableBoard = enableBoard;
        dadieraG.setEnable(enableBoard);
        gridG.setEnable(enableBoard);
    }

    public void makeMove() {
        clientPlayer.myMove();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
