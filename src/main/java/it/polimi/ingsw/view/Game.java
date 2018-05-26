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


    private int num=4;
    private ClientPlayer clientPlayer;
    private boolean enableBoard = true;

    public Game() {
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
            clientPlayer = new ClientPlayer(Integer.parseInt(s),this);
        }
        catch (RemoteException e){
            Thread.currentThread().interrupt();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        GridPane root = new GridPane();
        DadieraGUI d = new DadieraGUI(root, num, this);
        GridGUI g = new GridGUI(root, d,this);
        Scene scene = new Scene(root, 400, 400);
        primaryStage.setTitle("Sagrada");
        primaryStage.setScene(scene);
        primaryStage.show();
        dimWindows.dim(root);
    }

    public void modPair(Pair pair){
        clientPlayer.setMovePair(pair);
    }

    public void modIJ(int i,int j){
        clientPlayer.setMoveIJ(i,j);
    }

    @Override
    public void initGraphic(ClientModelAdapter giocatore) {
        System.out.println("ecco la grafica!");
    }

    @Override
    public void setEnableBoard(boolean enableBoard) {
        this.enableBoard = enableBoard;
    }


    public static void main(String[] args) {
        launch(args);
    }
}
