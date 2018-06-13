package it.polimi.ingsw.view;

import it.polimi.ingsw.GUI;
import it.polimi.ingsw.client.ClientPlayer;
import it.polimi.ingsw.client.ToolAction;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.application.Application;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.awt.*;
import java.rmi.RemoteException;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SagradaGUI extends Application implements GUI {


    private DadieraGUI dadieraG;
    private GridGUI gridG;
    private PlayersGUI plaG;
    private TokenGUI tokenG;
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
            String serverIP;
            do {
                System.out.println("Insert server address");
                serverIP = cli.nextLine();
            } while (!isIPAddressValid(serverIP));
            String typeOfConnection;
            System.out.println("Select connection mode: 0=Socket ; 1=RMI");
            do{
                typeOfConnection = cli.nextLine();
            }while (!typeOfConnection.equals("1") && !typeOfConnection.equals("0"));
            clientPlayer = new ClientPlayer(Integer.parseInt(typeOfConnection),this, serverIP);
        }
        catch (RemoteException e){
            Thread.currentThread().interrupt();
        }
    }


    @Override
    public void start(Stage loginStage){

        /*boolean log;
        //clientConnectionHandler =new ClientSocketHandler();
        VBox root=new VBox();
        GridPane login=new GridPane();
        root.getChildren().add(login);
        //fascia del titolo
        Label l=new Label("LOGIN");
        l.setFont(Font.font("verdana",  FontWeight.BOLD, FontPosture.REGULAR,30));
        //fascia dove inserirò il nome
        GridPane name=new GridPane();

        //fascia dove inseriro l'ip
        GridPane ip=new GridPane();

        //RMI-Socket


        //fascia dove inserirò il bottone invio
        Button st=new Button("INVIO");
        root.getChildren().add(st);
        st.setAlignment(Pos.TOP_RIGHT);

        //inserimento degli elementi nella scena
        login.add(l,0,0);
        login.add(name,0,1);
        login.add(ip,0,2);

        final TextField textname= new TextField();
        name.add(textname,0,0);
        Text txnome=new Text("Nome");
        txnome.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        name.add(txnome,1,0);

        final TextField textip= new TextField();
        ip.add(textip,0,0);
        Text txip=new Text("Indirizzo ip Server");
        txip.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        ip.add(txip,1,0);

        ObservableList<String> type = FXCollections.observableArrayList("RMI", "Socket");
        ComboBox connection=new ComboBox(type);
        connection.setPromptText("Scegliere tipo di connessione...");
        login.add(connection,0,3);

        login.setVgap(10);
        name.setHgap(5);
        ip.setHgap(5);
        log=false;

        Text t=new Text();
        t.setDisable(false);
        t.setFill(Color.INDIANRED);
        root.getChildren().add(t);

        st.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                String n=textname.getText();
                String p=textip.getText();
                System.out.println(connection.getValue());
                if(!(n.contentEquals("")) && !(p.contentEquals("")) && (connection.getValue()!=null) && log==false) {
                    //String[] buffer = {n,p};
                    //boolean b= clientConnectionHandler.setBuffer(buffer);
                    //if(b==true)
                        //log=true;
                    game(loginStage);
                    System.out.println(n);
                    System.out.println(p);
                }else{
                    t.setText("valori in input non validi!");
                }
            }
        });

        login.setAlignment(Pos.CENTER);
        root.setAlignment(Pos.CENTER);
        root.setSpacing(30);
        Scene scene=new Scene(root,500,500);
        loginStage.setTitle("Sagrada");
        loginStage.setScene(scene);
        loginStage.show();
        loginStage.setResizable(false);*/
        game(loginStage);
    }

    public void game(Stage primaryStage) {

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
        tokenG=new TokenGUI(pcenter,this);
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

        Scene scene=new Scene(root,800,500);
        primaryStage.setTitle("Sagrada");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
        primaryStage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }

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
        if("redyellowgreenbluepurple".contains(msg)) {
            Map<String, String> colorTranslation = tranlsateColors();
            String col = colorTranslation.get(msg);
            popUPMessage(11, col);
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

    /**
     * changes the behaviour of the GUI's components in order to perform a tool move
     * @param toolPhase
     */
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

    /**
     * asks to the server if the tool can be used
     * @param i
     */
    public void toolPermission(int i) {
        toolPhase = clientPlayer.toolPermission(i);
        if(toolPhase) {
            setToolPhase(true);
            msgb.updateGraphic("Using a tool");
            if(i == 1)
                popUPMessage(i, "");
        }
    }

    /**
     * handles to the client class the command to perform a move
     */
    public void makeToolMove() {
        clientPlayer.useTool();
    }

    /**
     * handles to the client class the command to end the turn
     */
    @Override
    public void passTurn() {
        clientPlayer.pass();
    }

    /**
     * retrieve necessary information for tools 1 and 11
     * @param toolID
     * @param str
     */
    public void popUPMessage(int toolID, String str) {
        switch (toolID) {
            case 1:
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setHeaderText("Cambia il valore");
                    alert.setContentText("Come desideri cambiare il valore del dado?");

                    ButtonType buttonType1 = new ButtonType("Incrementa");
                    ButtonType buttonType2 = new ButtonType("Decrementa");

                    alert.getButtonTypes().setAll(buttonType1, buttonType2, ButtonType.CLOSE);
                    Optional<ButtonType> result = alert.showAndWait();

                    if (result.get() == buttonType1)
                        ToolAction.setInstruction("inc");
                    else if (result.get() == buttonType2)
                        ToolAction.setInstruction("dec");
                });
                break;
            case 11:
                Platform.runLater(() -> {
                    dadieraG.setEnable(false);
                    gridG.setEnable(false);
                    List<Integer> choices = new ArrayList<>();
                    choices.addAll(IntStream.range(1, 7).mapToObj(n -> (Integer) n).collect(Collectors.toList()));
                    ChoiceDialog<Integer> dialog = new ChoiceDialog<>(1, choices);
                    dialog.setTitle("Selezione valore");
                    dialog.setHeaderText("Seleziona il numero del dado!");
                    dialog.setContentText("Il tuo dado è " + str);
                    Optional<Integer> result = dialog.showAndWait();
                    if(result.isPresent())
                        ToolAction.setDadieraPair(new Pair(result.get()));
                    else ToolAction.setDadieraPair(null);
                    makeToolMove();
                    dadieraG.setEnable(true);
                    gridG.setEnable(true);
                });
                break;
        }
    }

    /**
     * translates color names to italian
     * @return the map with the translation
     */
    private Map<String, String> tranlsateColors() {
        Map<String, String> map = new HashMap<>();
        map.put("red", "rosso");
        map.put("green", "verde");
        map.put("yellow", "giallo");
        map.put("blue", "blu");
        map.put("purple", "viola");
        return map;
    }

    /**
     * checks if the ip address given by the user is valid.
     * if the user doesn't insert an address, a default one is loaded from the setting file
     * @param ip server address
     * @return weather the address is valid or not
     */
    private boolean isIPAddressValid(String ip) {
        if(ip.isEmpty())
            return true;

        try{
            String[] parts = ip.split("\\.");

            if(parts.length != 4)
                return false;

            for (String s : parts) {
                int i = Integer.parseInt(s);
                if(i < 0 || i > 255)
                    return false;
            }

            if(ip.endsWith("."))
                return false;

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
