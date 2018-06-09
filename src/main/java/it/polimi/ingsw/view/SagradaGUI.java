package it.polimi.ingsw.view;

import it.polimi.ingsw.GUI;
import it.polimi.ingsw.client.ClientPlayer;
import it.polimi.ingsw.client.ToolAction;
import it.polimi.ingsw.exceptions.IllegalDiceException;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SagradaGUI extends Application implements GUI {


    private DadieraGUI dadieraG;
    private GridGUI gridG;
    private PlayersGUI plaG;
    private ToolsGUI tools;
    private RoundsGUI rounds;
    private TargetGUI target;

    private MessageBox msgb;
    private UseToolButton useTool;

    private int num=4;
    private ClientPlayer clientPlayer;
    private boolean enableBoard;
    private boolean toolPhase;


    public SagradaGUI() {
        enableBoard = false;
        toolPhase = false;
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
    public void start(Stage primaryStage) throws IllegalDiceException {
        //root
        BorderPane root = new BorderPane();

        //initialization of the main space
        //it's the main space, it contains everything except for the end turn button and the status message
        GridPane mainContent=new GridPane();
        GridPane pcenter=new GridPane();
        GridPane pleft=new GridPane();
        GridPane pright=new GridPane();
        mainContent.setGridLinesVisible(true);

        mainContent.add(pleft,0,0);
        mainContent.add(pcenter,1,0);
        mainContent.add(pright,2,0);

        DimWindows.dim(mainContent);
        DimWindows.dim(pright);
        DimWindows.dim(pleft);
        DimWindows.dim(pcenter);

        mainContent.setAlignment(Pos.CENTER);
        pcenter.setAlignment(Pos.CENTER);



        //placing the different gui components
        tools = new ToolsGUI(mainContent, this);
        rounds = new RoundsGUI(pcenter, this);
        dadieraG =new DadieraGUI(pcenter, 5, this);
        gridG = new GridGUI(pcenter, this);
        plaG=new PlayersGUI(pcenter,this);
        target = new TargetGUI(mainContent);



        //status message and end of turn button
        BorderPane bottom = new BorderPane();
        msgb = new MessageBox("Wellcome!");
        bottom.setLeft(msgb);
        EndButton pass = new EndButton(this);
        bottom.setRight(pass);
        useTool = new UseToolButton("Usa il tool!", this);
        bottom.setCenter(useTool);

        //putting it all together
        root.setCenter(mainContent);
        root.setBottom(bottom);

        Scene scene=new Scene(root,800,600);
        primaryStage.setTitle("Sagrada");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> Platform.exit());
    }


    /*public void modMovePair(Pair pair){
        clientPlayer.setMovePair(pair);
    }

    public void modMoveIJ(Coordinates coord){
        clientPlayer.setMoveCoordinates(coord);
    }*/


    /**
     * updates the dice displayed on dadiera
     * @param p
     */
    @Override
    public void updateDadiera(Pair[] p) {
        dadieraG.updateGraphic(p);
    }


    /**
     * updates the dice on the grid
     * @param p
     */
    @Override
    public void updateWindow(Pair[][] p) {
        gridG.updateGrid(p);
    }


    /**
     * displays the message relative to the status of the move
     * @param msg
     */
    public void updateMessage(String msg) {
        if(msg.equals("11")) {
            popUPMessage(11);
            msg = "Tool in use";
        }
        msgb.updateGraphic(msg);
    }

    public void updateTools(String[] toolNames) {
        tools.updateTools(toolNames);
    }

    public void updateOpponents(Pair[][] pair, String user) {
        plaG.updateGraphic(pair, user);
    }

    public void updateRoundTrace(ArrayList<Pair>[] trace) {
        rounds.updateRoundTrace(trace);
    }

    public void updateTokens(int n) {
        System.out.println("num of remaining tokens: " + n);
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

    public void setToolPhase(boolean toolPhase) {
        this.toolPhase = toolPhase;
        dadieraG.setTool(toolPhase);
        gridG.setTool(toolPhase);
        useTool.enable(toolPhase);
    }

    /**
     * sends to client player the move made by the user
     */
    public void makeMove() {
        clientPlayer.myMove();
    }


    public void toolPermission(int i) {
        toolPhase = clientPlayer.toolPermission(i);
        if(toolPhase) {
            setToolPhase(true);
            msgb.updateGraphic("Using a tool");
            popUPMessage(i);
        }
    }

    public void makeToolMove() {
        clientPlayer.useTool();
    }

    @Override
    public void passTurn() {
        clientPlayer.pass();
    }

    public void popUPMessage(int toolID) {
        switch (toolID) {
            case 1:
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setContentText("Come desideri cambiare il valore del dado?");

                    ButtonType buttonType1 = new ButtonType("Incrementa");
                    ButtonType buttonType2 = new ButtonType("Decrementa");

                    alert.getButtonTypes().setAll(buttonType1, buttonType2);
                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.get() == buttonType1)
                        ToolAction.setInstruction("inc");
                     else
                         ToolAction.setInstruction("dec");
                 });
                break;
            case 11:
                Platform.runLater(() -> {
                    List<Integer> choices = new ArrayList<>();
                    choices.addAll(IntStream.range(1, 7).mapToObj(n -> (Integer)n).collect(Collectors.toList()));
                    ChoiceDialog<Integer> dialog = new ChoiceDialog<>(1, choices);
                    dialog.setTitle("Selezione valore");
                    dialog.setHeaderText("Seleziona il numero del dado!");
                    Optional<Integer> result = dialog.showAndWait();
                    result.isPresent();
                });
                break;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
