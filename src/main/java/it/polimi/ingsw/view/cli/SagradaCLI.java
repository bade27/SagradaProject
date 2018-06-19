package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.UI;
import it.polimi.ingsw.client.ClientPlayer;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Pair;

import java.io.*;
import java.util.ArrayList;

public class SagradaCLI implements UI {


    private boolean enableBoard;
    private boolean toolPhase;
    private ClientPlayer clientPlayer;


    public SagradaCLI() {
        enableBoard = false;
        toolPhase = false;
            startGame();
    }

    public  void startGame(){
        Color color=Color.ANSI_RED;
        printbyFile("resources/titleCli/Sagrada.txt",color);
            login("Inserire dati per inizializzazione della partita");
    }

    @Override
    public void login(String message){
        Color color=Color.ANSI_BLUE;
        String connection;
        String ip;
        String name;
        printbyFile("resources/titleCli/Login.txt",color);
        System.out.println("\n\n"+message+"\n");
        do {
            System.out.println("Come ti vuoi connettere? \n(se non sarà messo nulla verrà impostati di default RMI)\n0. Socket\n1. RMI");
            connection = readbyConsole().trim();
            if(connection.equals("")){
                connection="1";
            }
        }while(!(connection.equals("0")) && !(connection.equals("1")));

        do{
            System.out.println("\nInserire l'indirizzo IP del server: \n(se non sarà messo nulla ne verrà messo uno di defauld)");
            ip=readbyConsole();

        }while(!(isIPAddressValid(ip)));

        do{
            System.out.println("\nInserire un Nome:");
            name=readbyConsole().trim();
            System.out.println("\n");
        }while(!(isNameValid(name)));

        SagradaCLI sagradaCLI=this;
        try {
            if (clientPlayer == null)
                clientPlayer = new ClientPlayer(Integer.parseInt(connection), sagradaCLI, ip);
            clientPlayer.setClientName(name);
        }catch(Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void maps(String[] s1, String[] s2) {
        Color color=Color.ANSI_GREEN;
        String map;
        printbyFile("resources/titleCli/Scegli_la_mappa.txt",color);
        String [] vecname;
        String name1, name2,name3,name4;

        vecname=s1[0].split("\\/");
        name1=(vecname[vecname.length-1].split("\\."))[0];

        vecname=s1[1].split("\\/");
        name2=(vecname[vecname.length-1].split("\\."))[0];

        vecname=s2[0].split("\\/");
        name3=(vecname[vecname.length-1].split("\\."))[0];

        vecname=s2[1].split("\\/");
        name4=(vecname[vecname.length-1].split("\\."))[0];


        do{
            System.out.println("\nSono state estratte queste mappe! Scegline una digitando il norrispettivo numero:");
            System.out.println("\n\n1.\t"+name1+"\n");
            //printGrid();
            System.out.println("\n\n2.\t"+name2+"\n");
            //printGrid();
            System.out.println("\n\n3.\t"+name3+"\n");
            //printGrid();
            System.out.println("\n\n4.\t"+name4+"\n");
            //printGrid();
            map=readbyConsole();
        }while(!(map.equals("1")||map.equals("2")||map.equals("3")||map.equals("4")));

        switch (map){
            case "1":
                clientPlayer.setChooseMap(s1[0]);
                break;
            case "2":
                clientPlayer.setChooseMap(s1[1]);
                break;
            case "3":
                clientPlayer.setChooseMap(s2[0]);
                break;
            case "4":
                clientPlayer.setChooseMap(s2[1]);
                break;
        }
    }

    @Override
    public void game() {

    }

    @Override
    public void endGame(String[] name, int[] record) {

    }

    @Override
    public void disconnection(String s) {

    }

    @Override
    public void fatalDisconnection(String s) {

    }


    @Override
    public void loading() {

    }

    @Override
    public void updateDadiera(Pair[] dadiera) {

    }

    @Override
    public void updateWindow(Pair[][] window) {

    }

    @Override
    public void setEnableBoard(boolean enableBoard) {

    }

    @Override
    public void updateMessage(String msg) {

    }

    @Override
    public void passTurn() {

    }

    @Override
    public void makeMove() {

    }

    @Override
    public void toolPermission(int i) {

    }

    @Override
    public void setToolPhase(boolean toolPhase) {

    }

    @Override
    public void makeToolMove() {

    }


    @Override
    public void updateTools(String[] toolNames) {

    }

    @Override
    public void updateOpponents(Pair[][] pair, String user) {

    }

    @Override
    public void updateTokens(int n) {

    }

    @Override
    public void updatePublicTarget(String[] s) {

    }

    @Override
    public void updatePrivateTarget(String[] s) {

    }

    @Override
    public void updateRoundTrace(ArrayList<Pair>[] trace) {

    }

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
    private boolean isNameValid(String name){
        if(name.isEmpty())
            return false;
        /*if(nome esiste gia)
            return false;*/
        return true;
    }

    /*private void printGrid(){
        System.out.println(" _____________________________");
        System.out.println("|     |     |     |     |     |");
        System.out.println("|     |     |     |     |     |");
        System.out.println("|_____|_____|_____|_____|_____|");
        System.out.println("|     |     |     |     |     |");
        System.out.println("|     |     |     |     |     |");
        System.out.println("|_____|_____|_____|_____|_____|");
        System.out.println("|     |     |     |     |     |");
        System.out.println("|     |     |     |     |     |");
        System.out.println("|_____|_____|_____|_____|_____|");
        System.out.println("|     |     |     |     |     |");
        System.out.println("|     |     |     |     |     |");
        System.out.println("|_____|_____|_____|_____|_____|");
    }*/

    private void printbyFile (String s, Color color){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(s));
            String line = bufferedReader.readLine();
            while (line != null) {
                System.out.println(color.escape()+""+line+ Color.RESET);
                line = bufferedReader.readLine();
            }
        }catch(IOException e){
            e.printStackTrace();
            System.err.println("errore nel caricamento del file di testo");
        }
    }

    private String readbyConsole() {
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
            return bufferedReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("errore nella lettura da cli");
            return "";
        }
    }

    public static void main(String[] args) {
        new SagradaCLI();
    }
}
