package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.UI;
import it.polimi.ingsw.client.ClientPlayer;
import it.polimi.ingsw.client.MoveAction;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Coordinates;
import it.polimi.ingsw.remoteInterface.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SagradaCLI extends Thread implements UI{

    final int cellHeight=3;
    final int cellWidth=7;
    private boolean enableBoard;
    private boolean toolPhase;
    private ClientPlayer clientPlayer;
    private HashMap<ColorEnum,Color> hashMap=new HashMap<>();
    private String msg=null;
    private Pair[] dadiera;
    private Pair[][] window;
    private ArrayList<Pair> [] trace;

    public SagradaCLI() {
        enableBoard = false;
        toolPhase = false;
        hashMap.put(ColorEnum.RED,Color.ANSI_BACK_RED);
        hashMap.put(ColorEnum.BLUE,Color.ANSI_BACK_BLUE);
        hashMap.put(ColorEnum.PURPLE,Color.ANSI_BACK_PURPLE);
        hashMap.put(ColorEnum.YELLOW,Color.ANSI_BACK_YELLOW);
        hashMap.put(ColorEnum.GREEN,Color.ANSI_BACK_GREEN);
        hashMap.put(ColorEnum.WHITE,Color.ANSI_BACK_WHITE);
        hashMap.put(null, Color.ANSI_NOCOLOR);

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
        Color color=Color.ANSI_PURPLE;
        printbyFile("resources/titleCli/Il_gioco_comincia.txt",color);
    }

    @Override
    public void endGame(String[] name, int[] record) {
        String tempName;
        int tempRecord;
        Color color=Color.ANSI_RED;
        printbyFile("resources/titleCli/finePartita.txt",color);
        System.out.println("\n");

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
            System.out.println(name[k]+":\t"+record[k]+"\n");
        }

        color=Color.ANSI_GREEN;
        printbyFile("resources/titleCli/Vincitore.txt",color);
        System.out.println("\n"+name[0]+":\t"+record[0]+"\n");
    }

    @Override
    public void disconnection(String s) {
        String r;
        Color color = Color.ANSI_RED;
        printbyFile("resources/titleCli/Attenzione.txt", color);
        System.out.println("\n" + s);
        do {
            System.out.println("premo 'r' per riprovare a connetterti:");
            r = readbyConsole();
        }while(!r.equals("r"));
        login("Ritorna in partita!");
    }

    @Override
    public void fatalDisconnection(String s) {
        Color color = Color.ANSI_RED;
        printbyFile("resources/titleCli/Spiacenti.txt", color);
        System.out.println("\n" + s);
    }

    @Override
    public void loading() {

    }

    @Override
    public void updateDadiera(Pair[] dadiera) {
        this.dadiera=dadiera;
        Color color=Color.ANSI_NOCOLOR;
        printbyFile("resources/titleCli/Dadiera.txt",color);
        try {
            printPair(dadiera);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateWindow(Pair[][] window) {
        this.window=window;
        Color color=Color.ANSI_NOCOLOR;
        printbyFile("resources/titleCli/Griglia.txt",color);
        try {
            printPair(window);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void updateTools(String[] toolNames) {

        String name;
        String [] vecname;
        Color color=Color.ANSI_NOCOLOR;
        printbyFile("resources/titleCli/Strumenti.txt",color);

        for(int i=0;i<toolNames.length;i++) {
            System.out.println(toolNames[i]);
        }
    }

    @Override
    public void updateOpponents(Pair[][] pair, String user,boolean b) {
        Color color=Color.ANSI_NOCOLOR;
        printbyFile("resources/titleCli/Avversari.txt",color);
        for(int i=0;i<((cellWidth*5)-user.length()+4)/2;i++)
            System.out.print("-");
        System.out.print(user);
        for(int i=0;i<((cellWidth*5)-user.length()+4)/2;i++)
            System.out.print("-");
        System.out.println("\n");
        printPair(pair);
        System.out.println("\n\n");
    }

    @Override
    public void updateTokens(int n) {
        System.out.println("Tocken:\t"+n);
    }

    @Override
    public void updatePublicTarget(String[] s) {

        String name;
        String [] vecname;
        Color color=Color.ANSI_NOCOLOR;
        printbyFile("resources/titleCli/Obiettivi_pubblici.txt",color);

        for(int i=0;i<s.length;i++) {
            vecname = s[i].split("\\/");
            name = (vecname[vecname.length - 1].split("\\."))[0];
            System.out.println(name);
        }
    }

    @Override
    public void updatePrivateTarget(String[] s) {

        String name;
        String [] vecname;
        Color color=Color.ANSI_NOCOLOR;
        printbyFile("resources/titleCli/Obiettivo_privato.txt",color);

        for(int i=0;i<s.length;i++) {
            vecname = s[i].split("\\/");
            name = (vecname[vecname.length - 1].split("\\."))[0];
            System.out.println(name);
        }

    }

    @Override
    public void updateRoundTrace(ArrayList<Pair>[] trace) {
/*        this.trace=trace;
        Color color=Color.ANSI_NOCOLOR;
        printbyFile("resources/titleCli/Tracciato_round.txt",color);

        try{
            for(int i=0;i<trace.length;i++){

            }
        }catch(Exception e){
            e.printStackTrace();
        }
*/
    }

    @Override
    public void updateMessage(String msg) {
        this.msg=msg;
    }

    @Override
    public void setEnableBoard(boolean enableBoard) {
        this.enableBoard=enableBoard;
        new Thread(new Runnable() {
            @Override
            public void run() {
                turn();
            }
        }).start();
    }

    private void turn(){
        String action = "";
        String end_turn;
        boolean pass = false;
        /*if (msg.equals("My turn")||
                msg.equals("Impossibile piazzare il dado: Dado posizionato su una cella incompatibile")||
                        msg.equals("Move ok")){*/
        if(enableBoard==true) {
            System.out.println(msg);
            do {
                do {
                    System.out.println("Vuoi fare una mossa [m], usare una carta strumento [t], o passare il turno [p]?");
                    action = readbyConsole();
                    System.out.println();
                    if (action.equals("m")) {
                        doMovement();
                    } else if (action.equals("t")) {
                        doTool();
                    } else if (action.equals("p")) {
                        pass=true;
                        doPassTurn();
                    }
                    System.out.println("\n" + msg + "\n");
                } while (!action.equals("m") && !action.equals("t") && !action.equals("p"));
                if(!action.equals("p")) {
                    do {
                        System.out.println("Vuoi fare dell'altro? [S/n]");
                        end_turn = readbyConsole();
                        if (end_turn.equals("n")) {
                            pass = true;
                            doPassTurn();
                        } else if (end_turn.equals("S")) {
                            pass = false;
                        }
                    } while (!end_turn.equals("S") && !end_turn.equals("n"));
                }
            } while (!pass);
        } else{
            System.out.println(msg);
        }

    }
    private void doMovement(){
        String mossa;

        String [] vecmove;
        int [] cell=new int[2];
        System.out.println("MOSSA:\n");
        ColorEnum color = null;
        int value = -1;
        do {
            System.out.println("Seleziona un dado della dadiera: \n[esempio: 1 RED]");
            mossa = readbyConsole();
            vecmove = mossa.split("\\ ");
            value = Integer.parseInt(vecmove[0]);

            switch (vecmove[1].toUpperCase()) {
                case "RED":
                    color = ColorEnum.RED;
                    break;
                case "GREEN":
                    color = ColorEnum.GREEN;
                    break;
                case "YELLOW":
                    color = ColorEnum.YELLOW;
                    break;
                case "BLUE":
                    color = ColorEnum.BLUE;
                    break;
                case "PURPLE":
                    color = ColorEnum.PURPLE;
                    break;
                default:
                    break;
            }

        } while (!PairExist(new Pair(value, color), dadiera));

        MoveAction.setPair(new Pair(value, color));
        System.out.println("Seleziona una cella della griglia: \n");
        do {
            System.out.println("ascissa: ");
            cell[1] = Integer.parseInt(readbyConsole());
        } while (cell[1] < 1 || cell[1] > 5);

        do {
            System.out.println("ordinata: ");
            cell[0] = Integer.parseInt(readbyConsole());
        } while (cell[0] < 1 || cell[0] > 4);

        System.out.println("");

        MoveAction.setCoord(new Coordinates(cell[0] - 1, cell[1] - 1));
        makeMove();
    }

    private void doTool(){
        System.out.println("tool usato con successo!");
    }

    private void doPassTurn(){
        passTurn();
    }

    @Override
    public void passTurn() {
        clientPlayer.pass();
    }

    @Override
    public void makeMove() {
        clientPlayer.myMove();
    }

    @Override
    public void makeToolMove() {
        clientPlayer.useTool();
    }

    @Override
    public void toolPermission(int i) {
        toolPhase = clientPlayer.toolPermission(i);
        if(toolPhase) {
            setToolPhase(true);
            if (i == 1) {
                popUPMessage(i, "");
            }
        }
    }

    @Override
    public void setToolPhase(boolean toolPhase) {
        this.toolPhase = toolPhase;
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

    private void printPair(Pair [] p){
        //parte superione
        for(int i=0;i<cellHeight/2;i++) {
            for (int j = 0; j < p.length; j++) {
                System.out.print(hashMap.get(p[j].getColor()).escape() + "");
                for (int k = 0; k < cellWidth; k++) {
                    System.out.print(" ");
                }
                System.out.print(Color.ANSI_NOCOLOR.escape()+"  ");
            }
            System.out.println("" + Color.ANSI_NOCOLOR.escape());
        }

        //parte centrale
        for (int j = 0; j < p.length; j++) {
            System.out.print(hashMap.get(p[j].getColor()).escape() + "");
            for (int k = 0; k < cellWidth/2; k++) {
                System.out.print(" ");
            }
            System.out.print(Color.ANSI_BLACK.escape()+hashMap.get(p[j].getColor()).escape()+p[j].getValue());

            for (int k = 0; k < cellWidth/2; k++) {
                System.out.print(" ");
            }
            System.out.print(Color.ANSI_NOCOLOR.escape()+"  ");
        }
        System.out.println("" + Color.ANSI_NOCOLOR.escape());

        //parte inferiore
        for(int i=0;i<cellHeight/2;i++) {
            for (int j = 0; j < p.length; j++) {
                System.out.print(hashMap.get(p[j].getColor()).escape() + "");
                for (int k = 0; k < cellWidth; k++) {
                    System.out.print(" ");
                }
                System.out.print(Color.ANSI_NOCOLOR.escape()+"  ");
            }
            System.out.println("" + Color.ANSI_NOCOLOR.escape());
        }
        System.out.println("\n");
        for(int i=0;i<p.length;i++){
            System.out.print(""+p[i].getValue()+" "+p[i].getColor()+"\t");
        }
        System.out.println("\n");

    }

    private void printPair(Pair[][] mp){
        for(int w=0;w<mp.length;w++) {
            //parte superione
            for (int i = 0; i < cellHeight / 2; i++) {
                for (int j = 0; j < mp[0].length; j++) {
                    System.out.print(hashMap.get(mp[w][j].getColor()).escape() + "");
                    for (int k = 0; k < cellWidth; k++) {
                        System.out.print(" ");
                    }
                    System.out.print(Color.ANSI_NOCOLOR.escape() + "  ");
                }
                System.out.println("" + Color.ANSI_NOCOLOR.escape());
            }

            //parte centrale
            for (int j = 0; j < mp[0].length; j++) {
                System.out.print(hashMap.get(mp[w][j].getColor()).escape() + "");
                for (int k = 0; k < cellWidth / 2; k++) {
                    System.out.print(" ");
                }
                System.out.print(Color.ANSI_BLACK.escape() + hashMap.get(mp[w][j].getColor()).escape() + mp[w][j].getValue());

                for (int k = 0; k < cellWidth / 2; k++) {
                    System.out.print(" ");
                }
                System.out.print(Color.ANSI_NOCOLOR.escape() + "  ");
            }
            System.out.println("" + Color.ANSI_NOCOLOR.escape());

            //parte inferiore
            for (int i = 0; i < cellHeight / 2; i++) {
                for (int j = 0; j < mp[0].length; j++) {
                    System.out.print(hashMap.get(mp[w][j].getColor()).escape() + "");
                    for (int k = 0; k < cellWidth; k++) {
                        System.out.print(" ");
                    }
                    System.out.print(Color.ANSI_NOCOLOR.escape() + "  ");
                }
                System.out.println("" + Color.ANSI_NOCOLOR.escape());
            }
            System.out.println("");
        }
        for(int i=0;i<mp.length;i++){
            for(int j=0;j<mp[0].length;j++) {
                System.out.print("" + mp[i][j].getValue() + " " + mp[i][j].getColor() + "\t");
            }
            System.out.println("");
        }
        System.out.println("");
    }

    private void printbyFile (String s, Color color){
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(s));
            String line = bufferedReader.readLine();
            while (line != null) {
                System.out.println(color.escape()+""+line+ Color.ANSI_NOCOLOR.escape());
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

    private boolean PairExist(Pair p, Pair[] vecPair){
        for(int i=0;i<vecPair.length;i++){
            if((vecPair[i].getValue()).equals(p.getValue()) && (vecPair[i].getColor()).equals(p.getColor()))
                return true;
        }
        return false;
    }

    private boolean PairExist(Pair p, Pair[][] matPair){
        for(int i=0;i<matPair.length;i++){
            for(int j=0;j<matPair[0].length;j++)
                if((matPair[i][j].getValue()).equals(p.getValue()) && (matPair[i][j].getColor()).equals(p.getColor()))
                    return true;
        }
        return false;
    }

    public void popUPMessage(int toolID, String str) {

    }

    @Override
    public void deletePlayer() {
        clientPlayer = null;
    }

    public static void main(String[] args) {
        new SagradaCLI();
    }
}



