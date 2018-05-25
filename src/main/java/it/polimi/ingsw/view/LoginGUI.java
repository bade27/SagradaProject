package it.polimi.ingsw.view;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginGUI extends Application {
    private boolean log;
    //public ClientSocketHandler clientConnectionHandler;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        //clientConnectionHandler =new ClientSocketHandler();
        GridPane root=new GridPane();

        //fascia del titolo
        Label l=new Label("LOGIN");
        l.setFont(Font.font("Tahoma", FontWeight.NORMAL,20));

        //fascia dove inserirò il nome
        GridPane name=new GridPane();

        //fascia dove inseriro la password
        GridPane pw=new GridPane();

        //fascia dove inserirò il bottone invio
        Button st=new Button("INVIO");

        //inserimento degli elementi nella scena
        root.add(l,0,0,1,1);
        root.add(name,0,1,1,1);
        root.add(pw,0,2,1,1);
        root.add(st,0,3,1,1);

        final TextField textname= new TextField();
        name.add(textname,0,0);
        Text txnome=new Text("nome");
        name.add(txnome,1,0);

        final PasswordField textpw= new PasswordField();
        pw.add(textpw,0,0);
        Text txpw=new Text("password");
        pw.add(txpw,1,0);

        log=false;
        st.setOnAction(new EventHandler<ActionEvent>() {

            public void handle(ActionEvent event) {
                String n=textname.getText();
                String p=textpw.getText();
                if(!(n.contentEquals("")) && !(p.contentEquals("")) && log==false) {
                    /*String[] buffer = {n,p};
                    boolean b= clientConnectionHandler.setBuffer(buffer);
                    if(b==true)
                        log=true;*/
                    System.out.println(n);
                    System.out.println(p);
                }else{
                    System.out.println("valori non validi");
                }
            }
        });

        root.setAlignment(Pos.CENTER);
        Scene scene=new Scene(root,400,400);
        primaryStage.setTitle("Sagrada");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(false);
    }
}
