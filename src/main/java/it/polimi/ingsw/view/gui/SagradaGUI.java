package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.UI;
import it.polimi.ingsw.client.ClientPlayer;
import it.polimi.ingsw.client.ToolAction;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.remoteInterface.Pair;
import it.polimi.ingsw.utilities.ParserXML;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.rmi.RemoteException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class SagradaGUI extends Application implements UI {


    private DadieraGUI dadieraG;
    private GridGUI gridG;
    private PlayersGUI plaG;
    private TokenGUI tokenG;
    private ToolsGUI toolsG;
    private RoundsGUI roundsG;
    private TargetGUI targetG;
    private Stage stage;
    private Scene scene;
    private MessageBox msgb;
    private UseToolButton useTool;
    private ClientPlayer clientPlayer;
    private boolean enableBoard;
    private boolean toolPhase;

    public SagradaGUI() {
        enableBoard = false;
        toolPhase = false;
        //initailizeComunication();
    }

    @Override
    public void start(Stage welcomeStage){
        stage=welcomeStage;
        VBox welcomeRoot= new VBox();
        Label l=new Label("Benvenuto!");
        l.setFont(Font.font("verdana",  FontWeight.BOLD, FontPosture.REGULAR,30));
        Button b=new Button("gioca");


        b.setPrefSize(120,50);
        welcomeRoot.getChildren().add(l);
        welcomeRoot.getChildren().add(b);
        b.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                try {
                    login("inserisci nome, indirizzo e connessione");                                       ///////////
                }catch(Exception e){
                    e.printStackTrace();
                }
            } });

        scene=new Scene(welcomeRoot,500,450);
        welcomeRoot.setAlignment(Pos.CENTER);
        welcomeRoot.setSpacing(10);
        stage.setTitle("Sagrada");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
        closeWindow();
    }

    @Override
    public void login(String s){
        VBox loginRoot=new VBox();
        GridPane login=new GridPane();
        loginRoot.getChildren().add(login);
        //fascia del titolo
        Label l=new Label("LOGIN");
        l.setFont(Font.font("verdana",  FontWeight.BOLD, FontPosture.REGULAR,30));
        //fascia dove inserirò il nome
        GridPane name=new GridPane();

        //fascia dove inseriro l'ip
        GridPane ip=new GridPane();

        //RMI-Socket
        GridPane c=new GridPane();

        //fascia dove inserirò il bottone invio
        Button st=new Button("INVIO");
        loginRoot.getChildren().add(st);
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
        connection.setPromptText("RMI...");
        c.add(connection,0,0);
        Text txcon=new Text("tipo di connessione");
        txcon.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
        c.add(txcon,1,0);
        c.setHgap(30);

        login.add(c,0,3);

        login.setVgap(15);
        name.setHgap(10);
        ip.setHgap(10);
        c.setHgap(20);
        c.setVgap(5);

        Text t=new Text(s);
        t.setDisable(false);
        t.setFill(Color.INDIANRED);
        loginRoot.getChildren().add(t);
        SagradaGUI sagradaGUI=this;
        st.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                t.setText("");
                String n=textname.getText();
                String p=textip.getText();
                int typeOfConnection=-1;
                if(connection.getValue()=="Socket")
                    typeOfConnection=0;
                else if (connection.getValue()=="RMI" || connection.getValue()==null)
                    typeOfConnection=1;
                if(!(n.contentEquals("")) && isIPAddressValid(p) && isNameValid(n)) {
                    try {
                        if (clientPlayer == null)
                            clientPlayer = new ClientPlayer(typeOfConnection,sagradaGUI, p);
                        if (!clientPlayer.isConnected())
                            clientPlayer.connect();

                        clientPlayer.setClientName(n);
                        t.setFill(Color.FORESTGREEN);
                        t.setText("attendere l'ingresso degli altri giocatori");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                }else{
                    t.setFill(Color.INDIANRED);
                    t.setText("valori in input non validi!");
                }
            }
        });

        login.setAlignment(Pos.CENTER);
        loginRoot.setAlignment(Pos.CENTER);
        loginRoot.setSpacing(35);
        //scene=new Scene(root,500,450);
        //stage.setTitle("Sagrada");
        stage.getScene().getWindow().setWidth(500);
        stage.getScene().getWindow().setHeight(450);
        stage.getScene().setRoot(loginRoot);
        //stage.show();
        stage.setResizable(false);
        closeWindow();
    }

    @Override
    public void maps(String[] s1,String[] s2){
        VBox mapsRoot=new VBox();
        Label title=new Label("Seleziona mappa da voler usare");
        title.setFont(Font.font("Verdana",  FontWeight.BOLD, FontPosture.REGULAR,25));
        mapsRoot.getChildren().add(title);
        GridPane mapsgrid=new GridPane();
        mapsRoot.getChildren().add(mapsgrid);


        String n1 = "",n2 = "",n3 = "",n4 = "";
        int d1 = 0,d2 = 0,d3 = 0,d4 = 0;
        try
        {
            n1= ParserXML.readWindowName(s1[0]);
            d1 = ParserXML.readBoardDifficult(s1[0]);
            n2 = ParserXML.readWindowName(s1[1]);
            d2 = ParserXML.readBoardDifficult(s1[1]);
            n3 = ParserXML.readWindowName(s2[0]);
            d3 = ParserXML.readBoardDifficult(s2[0]);
            n4 = ParserXML.readWindowName(s2[1]);
            d4 = ParserXML.readBoardDifficult(s2[1]);
        }catch (ParserXMLException e){
            e.printStackTrace();
        }

        Button map1f=new Button(n1 + "\n\nDifficoltà: " + d1);
        Button map1r=new Button(n2 + "\n\nDifficoltà: " + d2);
        Button map2f=new Button(n3 + "\n\nDifficoltà: " + d3);
        Button map2r=new Button(n4 + "\n\nDifficoltà: " + d4);

        mapsgrid.add(map1f,0,0);
        mapsgrid.add(map1r,0,1);
        mapsgrid.add(map2f,1,0);
        mapsgrid.add(map2r,1,1);


        map1f.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clientPlayer.setChooseMap(s1[0]);
            }
        });

        map1r.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clientPlayer.setChooseMap(s1[1]);
            }
        });

        map2f.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clientPlayer.setChooseMap(s2[0]);
            }
        });

        map2r.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                clientPlayer.setChooseMap(s2[1]);
            }
        });
        mapsRoot.setAlignment(Pos.TOP_CENTER);
        mapsgrid.setAlignment(Pos.CENTER);
        mapsgrid.setHgap(20);
        mapsgrid.setVgap(5);
        mapsgrid.setPrefSize(100,500);
        map1f.setPrefSize(450,200);
        map1r.setPrefSize(450,200);
        map2f.setPrefSize(450,200);
        map2r.setPrefSize(450,200);

        //scene=new Scene(root,700,500);
        stage.getScene().setRoot(mapsRoot);
        //stage.show();
        stage.setResizable(false);
        closeWindow();
    }
    @Override
    public void game() {

        //root
        BorderPane gameRoot = new BorderPane();

        //initialization of the main space
        //it's the main space, it contains everything except for the end turn button and the status message

        GridPane mainContent=new GridPane();
        GridPane pcenter=new GridPane();
        GridPane pleft=new GridPane();
        GridPane pright=new GridPane();
        GridPane roundContent = new GridPane();

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
        //roundContent.setAlignment(Pos.CENTER);

        mainContent.setStyle("-fx-background-color: #000000;");
        roundContent.setStyle("-fx-background-color: #000000;");


        pleft.setMaxWidth(220);
        pright.setMaxWidth(220);

        useTool = new UseToolButton(this);

        //placing the different gui components
        toolsG = new ToolsGUI(mainContent, this,useTool);
        dadieraG =new DadieraGUI(pcenter, 5, this);
        gridG = new GridGUI(pcenter, this);
        plaG=new PlayersGUI(pcenter,this);
        tokenG=new TokenGUI(pcenter,this);
        targetG = new TargetGUI(mainContent,this);
        roundsG = new RoundsGUI(roundContent, this);

        BorderPane bottom = new BorderPane();
        msgb = new MessageBox("Benvenuto!");
        bottom.setCenter(msgb);



        //Automatic centering
        roundContent.setPadding(new Insets(0,0,0,200));

        //putting it all together
        gameRoot.setCenter(mainContent);
        gameRoot.setBottom(bottom);
        gameRoot.setTop(roundContent);

        stage.getScene().setRoot(gameRoot);
        stage.getScene().getWindow().setWidth(1000);
        stage.getScene().getWindow().setHeight(670);
        stage.setResizable(false);

        closeWindow();
    }

    @Override
    public void loading(){
        GridPane loadingRoot=new GridPane();
        Label msg=new Label("Attendere...");
        msg.setFont(Font.font("verdana",  FontWeight.NORMAL,15));
        ProgressIndicator pi=new ProgressIndicator();
        loadingRoot.add(msg,0,0);
        loadingRoot.add(pi,0,1);
        /*Button b=new Button();
        root.add(b,0,2);
        b.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                maps(new String[2],new String[2]);
            }
        });*/
        loadingRoot.setAlignment(Pos.CENTER);
        loadingRoot.setVgap(10);
        //scene=new Scene(root,500,500);
        //stage.show();
        stage.getScene().setRoot(loadingRoot);
        stage.setResizable(false);
        closeWindow();
    }

    @Override
    public void disconnection(String s){
        VBox discRoot= new VBox();
        VBox text=new VBox();
        Label title=new Label("ATTENZIONE!!!");
        Label subtitle=new Label(s);
        subtitle.setFont(Font.font("verdana",  FontWeight.BOLD, FontPosture.REGULAR,13));
        title.setTextFill(Color.INDIANRED);
        title.setFont(Font.font("verdana",  FontWeight.BOLD, FontPosture.REGULAR,30));

        Button b=new Button("Riconnetti");
        b.setPrefSize(100,50);
        text.getChildren().add(title);
        text.getChildren().add(subtitle);
        discRoot.getChildren().add(text);
        discRoot.getChildren().add(b);

        b.setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent event) {
                clientPlayer = null;
                login("Ritorna in partita!");
            } });
        discRoot.setAlignment(Pos.CENTER);
        discRoot.setSpacing(30);
        text.setAlignment(Pos.CENTER);
        text.setSpacing(10);

        stage.getScene().setRoot(discRoot);
        stage.setResizable(false);
        closeWindow();
    }

    @Override
    public void fatalDisconnection(String s){
        VBox fatDicRoot= new VBox();
        Label title=new Label("Spiacenti");
        Label subtitle=new Label(s);
        subtitle.setFont(Font.font("verdana",  FontWeight.BOLD, FontPosture.REGULAR,20));
        title.setTextFill(Color.INDIANRED);
        title.setFont(Font.font("verdana",  FontWeight.BOLD, FontPosture.REGULAR,35));

        fatDicRoot.getChildren().add(title);
        fatDicRoot.getChildren().add(subtitle);

        fatDicRoot.setAlignment(Pos.CENTER);
        fatDicRoot.setSpacing(10);
        stage.getScene().setRoot(fatDicRoot);
        stage.setResizable(false);
        closeWindow();
    }

    @Override
    public void endGame(String [] name, int [] record){
        int max=0;
        String tempName;
        int tempRecord;
        Label title1=new Label("FINE PARTITA!!!");
        title1.setFont(Font.font("verdana",  FontWeight.BOLD, FontPosture.REGULAR,40));
        VBox resultsRoot=new VBox();
        GridPane players=new GridPane();
        VBox winner=new VBox();
        resultsRoot.getChildren().add(title1);
        resultsRoot.getChildren().add(players);
        resultsRoot.getChildren().add(winner);

        for(int i=0;i<name.length-1;i++){
            for(int j=i+1;j<name.length;j++) {
                if (record[j] > record[i]) {

                    tempName=name[j];
                    name[j]=name[i];
                    name[i]=tempName;

                    tempRecord=record[j];
                    record[j]=record[i];
                    record[i]=tempRecord;
                }
            }
        }

        for(int k=0;k<name.length;k++){
            Label index=new Label(""+(k+1)+")");
            Label n;
            if(record[k]!=-1) {
                n = new Label(name[k] + ": " + record[k]);
            }else{
                n=new Label(name[k]+ ": "+ "RITIRATO");
            }
            players.add(index,0,k);
            players.add(n,1,k);
        }

        Text title2=new Text("Il vincitore è:");
        title2.setFont(Font.font("verdana",  FontWeight.BOLD, FontPosture.REGULAR,20));
        title2.setFill(Color.INDIANRED);
        winner.getChildren().add(title2);
        Label playerOne= new Label(name[0]+":\t"+record[0]);

        winner.getChildren().add(playerOne);
        Text title3=new Text("Congratulazioni "+name[0]+" !!!");
        title3.setFill(Color.FORESTGREEN);
        winner.getChildren().add(title3);

        winner.setSpacing(10);
        winner.setAlignment(Pos.CENTER);
        playerOne.setAlignment(Pos.CENTER);
        players.setAlignment(Pos.CENTER);
        resultsRoot.setAlignment(Pos.CENTER);
        players.setHgap(30);
        players.setVgap(10);
        resultsRoot.setSpacing(40);
        stage.getScene().setRoot(resultsRoot);
        stage.setResizable(false);
        closeWindow();
    }


    //<editor-fold desc="Popup">
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
                    choices.addAll(IntStream.range(1, 7).mapToObj(n -> n).collect(Collectors.toList()));
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
    //</editor-fold>

    //<editor-fold desc="Update Client">
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
        toolsG.updateTools(toolNames);
    }

    public void updateOpponents(Pair[][] pair, String user , boolean b) {
        plaG.updateGraphic(pair, user,b);
    }

    public void updateRoundTrace(ArrayList<Pair>[] trace) {
        roundsG.updateRoundTrace(trace);
    }

    public void updatePublicTarget(String [] s){
        targetG.updatePublicTarget(s);
    }

    public void updatePrivateTarget(String[] s){
        targetG.updatePrivateTarget(s);
    }

    public void updateTokens(int n) {
        System.out.println("num of remaining tokens: " + n);
        tokenG.updateTockens(n);
    }
    //</editor-fold>

    //<editor-fold desc="Utilities">
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
     * if the user doesn'gridPane insert an address, a default one is loaded from the setting file
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

            return !ip.endsWith(".");
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
    private boolean isNameValid(String name){
        return !name.isEmpty();
    }

    public void deletePlayer() {
        clientPlayer = null;
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
        toolsG.setEnable(enableBoard);
    }

    /**
     * changes the behaviour of the gui's components in order to perform a tool move
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

    private void closeWindow ()
    {
        stage.setOnCloseRequest(e -> {
            if (clientPlayer != null)
                clientPlayer.disconnect();
            Platform.exit();
            System.exit(0);
        });
    }
    //</editor-fold>

    public static void main(String[] args) {
        GraphicDieHandler.loadDieImages();
        launch(args);
    }


}