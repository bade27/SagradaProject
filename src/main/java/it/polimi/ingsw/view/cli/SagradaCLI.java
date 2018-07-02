package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.UI;
import it.polimi.ingsw.client.ClientPlayer;
import it.polimi.ingsw.client.MoveAction;
import it.polimi.ingsw.utilities.FileLocator;
import it.polimi.ingsw.client.ToolAction;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Coordinates;
import it.polimi.ingsw.remoteInterface.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SagradaCLI extends Thread implements UI {

    final int cellHeight = 3;
    final int cellWidth = 7;
    private boolean enableBoard;
    private boolean toolPhase;
    private ClientPlayer clientPlayer;
    private HashMap<ColorEnum, Color> hashMap = new HashMap<>();
    private String msg = null;
    private Pair[] dadiera;
    private Pair[][] window;
    private ArrayList<Pair>[] trace;
    private String[] tools;
    private ArrayList<Player> opponents;
    private int token;
    private String[] privateTarget;
    private String[] publicTarget;

    private InputStreamReader inputStream;
    private BufferedReader bufferedReader;

    //read operation
    private Thread readOperation;
    private String content;
    private Object contentLock = new Object();

    //all other operations
    private Thread task;

    public SagradaCLI() {
        inputStream = new InputStreamReader(System.in);
        bufferedReader = new BufferedReader(inputStream);
        readOperation = new Thread(new Reader(inputStream, bufferedReader));
        readOperation.start();
        enableBoard = false;
        toolPhase = false;
        opponents = new ArrayList<Player>();
        createMap();
        startGame();
    }

    public void startGame() {
        Color color = Color.ANSI_RED;
        printbyFile("resources/titleCli/Sagrada.txt", color);
        login("Inserire dati per inizializzazione della partita");
    }

    @Override
    public void login(String message) {
        Color color = Color.ANSI_BLUE;
        String connection;
        String ip;
        String name;
        printbyFile("resources/titleCli/Login.txt", color);
        System.out.println("\n\n" + message + "\n");
        do {
            System.out.println("Come ti vuoi connettere? \n(se non sarà messo nulla verrà impostati di default RMI)\n0. Socket\n1. RMI");
            connection = readFromConsole().trim();
            if (connection.equals("")) {
                connection = "1";
            }
        } while (!(connection.equals("0")) && !(connection.equals("1")));

        do {
            System.out.println("\nInserire l'indirizzo IP del server: \n(se non sarà messo nulla ne verrà messo uno di defauld)");
            ip = readFromConsole();

        } while (!(isIPAddressValid(ip)));

        do {
            System.out.println("\nInserire un Nome:");
            name = readFromConsole().trim();
            System.out.println("\n");
        } while (!(isNameValid(name)));

        SagradaCLI sagradaCLI = this;
        try {
            if (clientPlayer == null)
                clientPlayer = new ClientPlayer(Integer.parseInt(connection), sagradaCLI, ip);
            if (!clientPlayer.isConnected())
                clientPlayer.connect();

            clientPlayer.setClientName(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void maps(String[] s1, String[] s2) {
        task = new Thread(new Runnable() {
            @Override
            public void run() {
                Color color = Color.ANSI_GREEN;
                String map;
                printbyFile("resources/titleCli/Scegli_la_mappa.txt", color);
                String[] vecname;
                String name1, name2, name3, name4;

                vecname = s1[0].split("\\/");
                name1 = (vecname[vecname.length - 1].split("\\."))[0];

                vecname = s1[1].split("\\/");
                name2 = (vecname[vecname.length - 1].split("\\."))[0];

                vecname = s2[0].split("\\/");
                name3 = (vecname[vecname.length - 1].split("\\."))[0];

                vecname = s2[1].split("\\/");
                name4 = (vecname[vecname.length - 1].split("\\."))[0];


                do {
                    System.out.println("\nSono state estratte queste mappe! Scegline una digitando il norrispettivo numero:");
                    System.out.println("\n\n1.\t" + name1 + "\n");
                    //printGrid();
                    System.out.println("\n\n2.\t" + name2 + "\n");
                    //printGrid();
                    System.out.println("\n\n3.\t" + name3 + "\n");
                    //printGrid();
                    System.out.println("\n\n4.\t" + name4 + "\n");
                    //printGrid();
                    map = readFromConsole();
                } while (!(map.equals("1") || map.equals("2") || map.equals("3") || map.equals("4")));

                switch (map) {
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
        });
        task.start();
    }

    @Override
    public void game() {
        Color color = Color.ANSI_PURPLE;
        printbyFile("resources/titleCli/Il_gioco_comincia.txt", color);
        System.out.println("\n\n\n Attendere il proprio turno");
    }

    @Override
    public void endGame(String[] name, int[] record) {
        String tempName;
        int tempRecord;
        Color color = Color.ANSI_RED;
        printbyFile("resources/titleCli/finePartita.txt", color);
        System.out.println("\n");

        for (int i = 0; i < name.length - 1; i++) {
            for (int j = i + 1; j < name.length; j++) {
                if (record[j] > record[i]) {

                    tempName = name[j];
                    name[j] = name[i];
                    name[i] = tempName;

                    tempRecord = record[j];
                    record[j] = record[i];
                    record[i] = tempRecord;
                }
            }
        }
        for (int k = 0; k < name.length; k++) {
            if(record[k]!=-1) {
                System.out.println(name[k] + ":\t" + record[k] + "\n");
            }else{
                System.out.println(name[k] + ":\t" + Color.ANSI_RED.escape() + "RITIRATO" + Color.ANSI_NOCOLOR.escape() + "\n");
            }
        }

        color = Color.ANSI_GREEN;
        printbyFile("resources/titleCli/Vincitore.txt", color);
        System.out.println("\n" + name[0] + ":\t" + record[0] + "\n");
    }

    @Override
    public void disconnection(String s) {
        task.interrupt();
        task = new Thread(new Runnable() {
            @Override
            public void run() {
                String i;
                Color color = Color.ANSI_RED;
                printbyFile("resources/titleCli/Attenzione.txt", color);
                System.out.println("\n" + s);
                do {
                    System.out.println("Premi 'i' per riprovare a connetterti:");
                    i = readFromConsole();
                } while (!i.equals("i"));
                clientPlayer = null;
                login("Ritorna in partita!");
            }
        });
        task.start();
    }

    @Override
    public void fatalDisconnection(String s) {
        Color color = Color.ANSI_RED;
        printbyFile("resources/titleCli/Spiacenti.txt", color);
        System.out.println("\n" + s);
    }

    @Override
    public void loading() {
        Color color = Color.ANSI_BLUE;
        printbyFile("resources/titleCli/Attendere.txt", color);
        System.out.println("\n\nAttendere l'arrivo di altri giocatori");
    }

    @Override
    public void updateDadiera(Pair[] dadiera) {
        this.dadiera = dadiera;
        viewDadiera();
    }

    private void viewDadiera() {
        Color color = Color.ANSI_NOCOLOR;
        printbyFile("resources/titleCli/Dadiera.txt", color);
        printPair(dadiera);
    }

    @Override
    public void updateWindow(Pair[][] window) {
        this.window = window;
        viewWindow();
    }

    private void viewWindow() {
        Color color = Color.ANSI_NOCOLOR;
        printbyFile("resources/titleCli/Griglia.txt", color);
        printPair(window);
    }

    @Override
    public void updateTools(String[] toolNames) {
        tools = toolNames;

    }

    private void viewTools() {
        Color color = Color.ANSI_NOCOLOR;
        printbyFile("resources/titleCli/Strumenti.txt", color);
        for (int i = 0; i < tools.length; i++) {
            System.out.println(tools[i]);
        }
    }

    @Override
    public void updateOpponents(Pair[][] pair, String user, boolean active) {
        boolean exist = false;
        if(!active)
            user=user+Color.ANSI_RED.escape()+" (non in partita)"+Color.ANSI_NOCOLOR.escape();

        for (int i = 0; i < opponents.size() && !exist; i++) {

            if (((opponents.get(i)).getName()).equals(user)) {
                exist = true;
                (opponents.get(i)).setGrid(pair);
            }
        }
        if (!exist) {
            Player newOpponent = new Player();
            newOpponent.setName(user);
            newOpponent.setGrid(pair);
            opponents.add(newOpponent);
        }
    }

    public void viewOpponents () {
        String user;
        Pair[][] pair;
        Color color = Color.ANSI_NOCOLOR;
        printbyFile("resources/titleCli/Avversari.txt", color);
        for (int k = 0; k < opponents.size(); k++) {
            user = (opponents.get(k)).getName();
            pair = (opponents.get(k)).getGrid();
            for (int i = 0; i < ((cellWidth * 5) - (user.length()) + 4) / 2; i++)
                System.out.print("-");
            System.out.print(user);
            for (int i = 0; i < ((cellWidth * 5) - (user.length()) + 4) / 2; i++)
                System.out.print("-");
            System.out.println("\n");
            printPair(pair);
            System.out.println("\n\n");
        }
    }

    @Override
    public void updateTokens ( int token){
        this.token = token;
    }

    private void viewToken () {
        System.out.println("Token:\t" + token);
    }

    @Override
    public void updatePublicTarget (String[]s){
        this.publicTarget = s;
    }

    @Override
    public void updatePrivateTarget (String[]s){
        this.privateTarget = s;
    }

    private void viewTarget () {
        String name;
        String[] vecname;
        Color color = Color.ANSI_NOCOLOR;
        printbyFile("resources/titleCli/Obiettivi.txt", color);

        System.out.println("\nObiettivo privato:");
        for (int i = 0; i < privateTarget.length; i++) {
            vecname = privateTarget[i].split("\\/");
            name = (vecname[vecname.length - 1].split("\\."))[0];
            System.out.println(name);
        }
        System.out.println("\nObiettivi pubblici:");
        for (int i = 0; i < publicTarget.length; i++) {
            vecname = publicTarget[i].split("\\/");
            name = (vecname[vecname.length - 1].split("\\."))[0];
            System.out.println(name);
        }

    }

    @Override
    public void updateRoundTrace (ArrayList < Pair >[]trace){
        this.trace = trace;
    }

    private void viewRoundTrace () {
        Color color = Color.ANSI_NOCOLOR;
        printbyFile("resources/titleCli/Tracciato_round.txt", color);
        try {
            for (int i = 0; i < trace.length; i++) {
                if (trace[i].size() != 0) {
                    System.out.println("-----------------------Round" + (i + 1) + "-----------------------\n");
                    Pair[] p = new Pair[trace[i].size()];
                    for (int j = 0; j < p.length; j++) {
                        p[j] = trace[i].get(j);
                    }
                    printPair(p);
                }
            }
        } catch (Exception e) {
            System.out.println("problemi con il tracciato round");
            e.printStackTrace();
        }
    }

    @Override
    public void updateMessage (String msg){
        this.msg = msg;
        Thread t=new Thread(new Runnable() {
            @Override
            public void run() {
                if("redyellowgreenbluepurple".contains(msg)) {
                    String col = msg;
                    popUPMessage(11, col);
                }
            }
        });
    }

    private void viewMessage ()
    {
        System.out.println(msg + "\n");
    }
    @Override
    public void setEnableBoard ( boolean enableBoard){
        this.enableBoard = enableBoard;
        new Thread(new Runnable() {
            @Override
            public void run() {
                turn();
            }
        }).start();
    }

    private void turn () {
        String action = "";
        String end_turn;
        boolean pass = false;
        /*if (msg.equals("My turn")||
                msg.equals("Impossibile piazzare il dado: Dado posizionato su una cella incompatibile")||
                        msg.equals("Move ok")){*/
        if (enableBoard == true) {
            viewMessage();
            viewTarget();
            viewTools();
            viewOpponents();
            viewRoundTrace();
            viewDadiera();
            viewWindow();
            viewToken();
            printTurn();
            do {
                do {
                    System.out.println("Vuoi fare una mossa [m], usare una carta strumento [c], vedere gli elementi del gioco [e] o passare il turno [p]?");
                    action = readFromConsole();
                    System.out.println();
                    if (action.equals("m")) {
                        doMovement();
                    } else if (action.equals("c")) {
                        doTool();
                    } else if (action.equals("e")) {
                        viewElements();
                    } else if (action.equals("p")) {
                        pass = true;
                        passTurn();
                    }
                } while (!action.equals("m") && !action.equals("c") && !action.equals("p"));
                if (!action.equals("p")) {
                    do {
                        System.out.println("Vuoi fare dell'altro? [S/n]");
                        end_turn = readFromConsole();
                        if (end_turn.equals("n")) {
                            pass = true;
                            passTurn();
                        } else if (end_turn.equals("S")) {
                            pass = false;
                        }
                    } while (!end_turn.equals("S") && !end_turn.equals("n"));
                }
            } while (!pass);
        } else {
            System.out.println(msg);
        }

    }

    private void doMovement () {

        String mossa;
        String[] vecmove;
        int[] cell = new int[2];
        System.out.println("MOSSA:\n");
        ColorEnum color = null;
        int value = -1;
        do {
            viewDadiera();
            viewWindow();
            System.out.println("Seleziona un dado della dadiera: \n[esempio: 1 RED]");
            mossa = readFromConsole();
            vecmove = mossa.split("\\ ");
            try {
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
            }catch (NumberFormatException nfe){
                value=-1;
                color=null;
            }catch (ArrayIndexOutOfBoundsException ofb){
                value=-1;
                color=null;
            }
        } while (!pairExist(new Pair(value, color), dadiera));

        MoveAction.setPair(new Pair(value, color));
        System.out.println("Seleziona una cella della griglia: \n");
        do {
            System.out.println("ascissa: [crescente da sinistra verso destra]");
            try {
                cell[1] = Integer.parseInt(readFromConsole());
            }catch(NumberFormatException nfe){
                cell[1]=-1;
            }
        } while (cell[1] < 1 || cell[1] > 5);

        do {
            System.out.println("ordinata: [crescente dall' alto verso il basso]");
            try {
                cell[0] = Integer.parseInt(readFromConsole());
            }catch (NumberFormatException nfe){
                cell[0]=-1;
            }
        } while (cell[0] < 1 || cell[0] > 4);

        System.out.println("");

        MoveAction.setCoord(new Coordinates(cell[0] - 1, cell[1] - 1));
        makeMove();
        viewDadiera();
        viewWindow();
        viewMessage();
    }

    private void doTool () {
        System.out.println("STRUMENTO:\n");
        String t;
        String element;
        String dad;
        String[] vecmove;
        Pair[] vecTrace;
        int[] cell = new int[2];
        int numtool = -1;
        int value = -1;
        ColorEnum color = null;
        int round;
        //capisco quale tool vuole utilizzare
        do {
            System.out.println("Quale strumento vuoi utilizzare? [digitare il numero corrispondente]");
            for (int i = 0; i < tools.length; i++) {
                System.out.println((i + 1) + ". " + tools[i]);
            }
            t = readFromConsole();
        } while (!t.equals("1") && !t.equals("2") && !t.equals("3"));
        try {
            numtool = CLIFactory.getToolnumberFromName(FileLocator.getToolsListPath(), tools[Integer.parseInt(t) - 1]);
        } catch (Exception e) {
            System.out.println("Errore nel file degli strumenti");
            e.getStackTrace();
        }
        toolPermission(numtool);
        if(toolPhase==true) {
            //setto i parametri necessari per il tool scelto
            System.out.println("Settare i parametri necessari per utilizzare " + tools[Integer.parseInt(t) - 1] + ":\n - Se si ha finito digitare 'fine'");
            do {
                do {
                    System.out.println("\n Cosa vuoi settare?");
                    System.out.println("1. dado dalla dadiera\n2. cella dalla griglia\n3. dado dal tracciato round");
                    element = readFromConsole();
                }
                while (!element.equals("1") && !element.equals("2") && !element.equals("3") && !element.equals("fine"));
                if (element.equals("1")) {

                    viewDadiera();
                    do {
                        System.out.println("Seleziona un dado della dadiera: \n[esempio: 1 RED]");
                        dad = readFromConsole();
                        vecmove = dad.split("\\ ");
                        try {
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
                        }catch (NumberFormatException nfe){
                            value=-1;
                            color=null;
                        }catch (ArrayIndexOutOfBoundsException ofb){
                            value=-1;
                            color=null;
                        }
                    } while (!pairExist(new Pair(value, color), dadiera));
                    ToolAction.setDadieraPair(new Pair(value, color));

                } else if (element.equals("2")) {
                    viewWindow();
                    System.out.println("Seleziona una cella della griglia: \n");
                    do {
                        System.out.println("ascissa: [crescente da sinistra verso destra]");
                        try {
                            cell[1] = Integer.parseInt(readFromConsole());
                        }catch (NumberFormatException nfe) {
                            cell[1]=-1;
                        }
                    } while (cell[1] < 1 || cell[1] > 5);

                    do {
                        System.out.println("ordinata: [crescente dall' alto verso il basso]");
                        try{
                            cell[0] = Integer.parseInt(readFromConsole());
                        }catch(NumberFormatException nfe){
                            cell[0]=-1;
                        }
                    } while (cell[0] < 1 || cell[0] > 4);
                    ToolAction.setPosition(new Coordinates(cell[0] - 1, cell[1] - 1));

                } else if (element.equals("3")) {
                    viewRoundTrace();
                    do {
                        System.out.println("Selezionare il round del tracciato (che non sia vuoto) dal quale estrarre il dado");
                        String result = readFromConsole();
                        try {
                            round = Integer.parseInt(result);
                        }catch(NumberFormatException nfe){
                            round=-1;
                        }
                    } while ((round) < 1 || round > 9 || trace[round - 1].size() == 0);
                    System.out.println("");
                    do {
                            System.out.println("Selezionare un dado appartenente al round scelto");
                            dad = readFromConsole();
                            vecmove = dad.split("\\ ");
                        try {
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
                            vecTrace = new Pair[trace[round - 1].size()];
                            for (int i = 0; i < trace[round - 1].size(); i++) {
                                vecTrace[i] = trace[round - 1].get(i);
                            }
                        }catch (NumberFormatException nfe){
                            value=-1;
                            vecTrace=null;
                        }catch (ArrayIndexOutOfBoundsException ofb){
                            value=-1;
                            vecTrace=null;
                        }
                    } while (!pairExist(new Pair(value, color), vecTrace));
                    ToolAction.setTracePair(new Pair(value, color));
                    ToolAction.setTracePosition(round);

                } else if (element.equals("fine")) {


                    if (enableBoard) {

                        makeToolMove();

                    } else{
                        System.out.println("il tool non è stato utilizzato\n");
                    }
                        viewRoundTrace();
                        viewDadiera();
                        viewWindow();
                        System.out.println("\n");
                        viewMessage();

                }

            } while (!element.equals("fine"));
        } else
        {
            System.out.println("Richiesta utilizzo strumento respinta");
        }
    }

    private void viewElements () {
        String v;
        String response;
        do {
            do {
                System.out.println("Quale elemento vuoi vedere?\n[d] dadiera\n[g] griglia\n[t] tracciato round\n[a] avversari\n[c] carte strumento\n[o] obiettivi\n[s] segnalini\n[tutto] tutti gli elementi\n[niente] nessuno degli elementi");
                v = readFromConsole();
            }
            while (!v.equals("d") && !v.equals("g") && !v.equals("t") && !v.equals("a") && !v.equals("s") && !v.equals("o") && !v.equals("c") && !v.equals("tutto") && !v.equals("niente"));
            switch (v) {
                case "d":
                    viewDadiera();
                    break;
                case "g":
                    viewWindow();
                    break;
                case "t":
                    viewRoundTrace();
                    break;
                case "a":
                    viewOpponents();
                    break;
                case "c":
                    viewTools();
                    break;
                case "o":
                    viewTarget();
                    break;
                case "s":
                    viewToken();
                    break;
                case "tutto":
                    viewTarget();
                    viewTools();
                    viewOpponents();
                    viewRoundTrace();
                    viewDadiera();
                    viewWindow();
                    viewToken();
                    break;
                case "niente":
                    break;
            }
            do {
                System.out.println("\n\nVuoi vedere altri elementi? [S/n]");
                response = readFromConsole();
            } while (!response.equals("S") && !response.equals("n"));
        } while (response.equals("S"));
    }

    @Override
    public void passTurn () {
        clientPlayer.pass();
        System.out.println("\n\n\n\n\nAttendere il proprio turno\n\n\n\n");
    }

    @Override
    public void makeMove () {
        clientPlayer.myMove();
    }

    @Override
    public void makeToolMove () {
        clientPlayer.useTool();
    }

    @Override
    public void toolPermission ( int i){
        toolPhase = clientPlayer.toolPermission(i);
        if (toolPhase) {
            setToolPhase(true);
            if (i == 1) {
                popUPMessage(i, "");
            }
        }
    }

    @Override
    public void setToolPhase ( boolean toolPhase){
        this.toolPhase = toolPhase;
    }

    private void printTurn(){
        Color color=Color.ANSI_RED;
        if(numTurn()!=-1){
            printbyFile("resources/titleCli/Round"+(numTurn()+1)+".txt",color);
        }
    }

    private int numTurn(){
        for(int i=0;i<trace.length;i++)
            if (trace[i].size()==0) {
                return i;
            }
        return -1;
    }

    private boolean isIPAddressValid (String ip){
        if (ip.isEmpty())
            return true;

        try {
            String[] parts = ip.split("\\.");

            if (parts.length != 4)
                return false;

            for (String s : parts) {
                int i = Integer.parseInt(s);
                if (i < 0 || i > 255)
                    return false;
            }

            if (ip.endsWith("."))
                return false;

            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private boolean isNameValid (String name){
        if (name.isEmpty())
            return false;
        /*if(nome esiste gia)
            return false;*/
        return true;
    }

    //print pair array
    private void printPair (Pair[]p){
        //parte superione
        printPariArray(p);

        //parte centrale
        for (int j = 0; j < p.length; j++) {
            System.out.print(hashMap.get(p[j].getColor()).escape() + "");
            for (int k = 0; k < cellWidth / 2; k++) {
                System.out.print(" ");
            }
            System.out.print(Color.ANSI_BLACK.escape() + hashMap.get(p[j].getColor()).escape() + p[j].getValue());

            for (int k = 0; k < cellWidth / 2; k++) {
                System.out.print(" ");
            }
            System.out.print(Color.ANSI_NOCOLOR.escape() + "  ");
        }
        System.out.println("" + Color.ANSI_NOCOLOR.escape());

        //parte inferiore
        printPariArray(p);
        System.out.println("\n");
        for (int i = 0; i < p.length; i++) {
            System.out.print("" + p[i].getValue() + " " + p[i].getColor() + "\t");
        }
        System.out.println("\n");

    }

    private void printPariArray(Pair[] p) {
        for (int i = 0; i < cellHeight / 2; i++) {
            for (int j = 0; j < p.length; j++) {
                System.out.print(hashMap.get(p[j].getColor()).escape() + "");
                for (int k = 0; k < cellWidth; k++) {
                    System.out.print(" ");
                }
                System.out.print(Color.ANSI_NOCOLOR.escape() + "  ");
            }
            System.out.println("" + Color.ANSI_NOCOLOR.escape());
        }
    }

    //print pair matrix
    private void printPair (Pair[][]mp){
        for (int w = 0; w < mp.length; w++) {
            //parte superione
            printPairMatrix(mp, w);

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
            printPairMatrix(mp, w);
            System.out.println("");
        }

        System.out.println("");
    }

    private void printPairMatrix(Pair[][] mp, int w) {
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
    }

    private void printbyFile (String s, Color color){
        System.out.println("\n\n");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(s));
            String line = reader.readLine();
            while (line != null) {
                System.out.println(color.escape() + "" + line + Color.ANSI_NOCOLOR.escape());
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("errore nel caricamento del file di testo");
        }
    }

    private String readFromConsole() {
        String reply = "";
        synchronized (contentLock) {
            try {
                contentLock.wait();
            } catch (InterruptedException e) {
                    return null;
            }
            reply = new String(content);
            content = null;
        }
        return reply;
    }

    private boolean pairExist (Pair p, Pair[]vecPair){
        for (int i = 0; i < vecPair.length; i++) {
            if ((vecPair[i].getValue()).equals(p.getValue()) && (vecPair[i].getColor()).equals(p.getColor()))
                return true;
        }
        return false;
    }

    private boolean pairExist (Pair p, Pair[][]matPair){
        for (int i = 0; i < matPair.length; i++) {
            for (int j = 0; j < matPair[0].length; j++)
                if ((matPair[i][j].getValue()).equals(p.getValue()) && (matPair[i][j].getColor()).equals(p.getColor()))
                    return true;
        }
        return false;
    }

    public void popUPMessage ( int toolID, String str){
        String result;
        ColorEnum color=null;
        switch (toolID) {
            case 1:
                System.out.println("\n Cambia il valore:\n");
                System.out.println("Come desideri cambiare il valore del dado?");
                do {
                    System.out.println("1. Incrementa\n2. Decrementa\n");
                    result = readFromConsole();
                } while (!result.equals("1") && !result.equals("2"));
                if (result.equals("1"))
                    ToolAction.setInstruction("inc");
                else if (result.equals("2"))
                    ToolAction.setInstruction("dec");
                break;
            case 11:
                System.out.println("Selezione valore\n");
                System.out.println("Il tuo dado è " + str);
                do {
                    System.out.println("Seleziona il numero del dado!");
                    result = readFromConsole();
                } while (Integer.parseInt(result) > 6 || Integer.parseInt(result) < 1);

                switch (str.toUpperCase()) {
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
                ToolAction.setDadieraPair(new Pair(Integer.parseInt(result),color));
                makeToolMove();
                break;
        }
    }

    @Override
    public void deletePlayer () {
        clientPlayer = null;
    }

    public static void main (String[]args){
        new SagradaCLI();
    }

    private void createMap(){
        hashMap.put(ColorEnum.RED, Color.ANSI_BACK_RED);
        hashMap.put(ColorEnum.BLUE, Color.ANSI_BACK_BLUE);
        hashMap.put(ColorEnum.PURPLE, Color.ANSI_BACK_PURPLE);
        hashMap.put(ColorEnum.YELLOW, Color.ANSI_BACK_YELLOW);
        hashMap.put(ColorEnum.GREEN, Color.ANSI_BACK_GREEN);
        hashMap.put(ColorEnum.WHITE, Color.ANSI_NOCOLOR);
        hashMap.put(null, Color.ANSI_NOCOLOR);
    }


    private class Player{
        String name;
        Pair[][] grid;

        public void setName(String name){
            this.name=name;
        }

        public void setGrid(Pair[][] grid){
            this.grid=grid;
        }

        public Pair[][] getGrid() {
            return grid;
        }

        public String getName() {
            return name;
        }
    }

    private class Reader implements Runnable {

        private InputStreamReader inputStream;
        private BufferedReader bufferedReader;
        private boolean condition;

        public Reader(InputStreamReader inputStream, BufferedReader bufferedReader) {
            this.inputStream = inputStream;
            this.bufferedReader = bufferedReader;
            this.condition = true;
        }

        @Override
        public synchronized void run() {
            while(condition) {
                try {
                    content = bufferedReader.readLine();
                } catch (IOException e) {
                    return;
                }
                synchronized (contentLock) {
                    contentLock.notifyAll();
                }
            }
        }

        public synchronized void setCondition(boolean condition) {
            this.condition = condition;
        }
    }

}


