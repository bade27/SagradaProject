package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.UI;
import it.polimi.ingsw.client.ClientPlayer;
import it.polimi.ingsw.client.MoveAction;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.utilities.FileLocator;
import it.polimi.ingsw.client.ToolAction;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Coordinates;
import it.polimi.ingsw.remoteInterface.Pair;
import it.polimi.ingsw.utilities.ParserXML;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public class SagradaCLI implements UI {

    final int cellHeight = 3;
    final int cellWidth = 7;
    public final int rows = 4;
    public final int cols = 5;
    private boolean enableBoard;
    private boolean toolPhase;
    private ClientPlayer clientPlayer;
    private HashMap<ColorEnum, Color> hashMapEc = new HashMap<>();
    private String msg = null;
    private Pair[] dadiera;
    private Pair[][] window;
    private ArrayList<Pair>[] trace;
    private String[] tools;
    private ArrayList<Player> opponents;
    private int token;
    private String[] privateTarget;
    private String[] publicTarget;

    private PrintStream printStream;
    private InputStreamReader inputStream;
    private BufferedReader bufferedReader;

    //read operation
    private Thread readOperation;
    private String content;
    private Object contentLock = new Object();
    private Reader reader;

    //all other operations
    private Thread task;

    public SagradaCLI() {
        try {
            printStream = new PrintStream(System.out, true, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            return;
        }
        inputStream = new InputStreamReader(System.in);
        bufferedReader = new BufferedReader(inputStream);
        reader = new Reader(inputStream, bufferedReader);
        readOperation = new Thread(reader);
        readOperation.start();
        enableBoard = false;
        toolPhase = false;
        opponents = new ArrayList<Player>();
        createMap();
        startGame();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                if (clientPlayer != null)
                    clientPlayer.disconnect();
            }
        }, "Shutdown-thread"));
    }

    /**
     * Start Sagrada title and login screen
     */
    public void startGame() {
        Color color = Color.ANSI_RED;
        printbyFile("resources/titleCli/Sagrada.txt", color);
        login("Inserire dati per inizializzazione della partita");
    }

    /**
     * Creates login screen. Login accept name, ip and type of connection
     * Default ip is uploaded from file and default type of connection is RMI
     *
     * @param message string that login print at the bottom
     */
    @Override
    public void login(String message) {
        Color color = Color.ANSI_BLUE;
        String connection;
        String ip;
        String name;
        printbyFile("resources/titleCli/Login.txt", color);
        printStream.println(Color.ANSI_NOCOLOR.escape()+"\n\n" + message + "\n"+Color.ANSI_NOCOLOR.escape());
        do {
            printStream.println(Color.ANSI_NOCOLOR.escape()+"Come ti vuoi connettere? \n(se non sarà messo nulla verrà impostati di default RMI)\n0. Socket\n1. RMI"+Color.ANSI_NOCOLOR.escape());
            connection = readFromConsole().trim();
            if (connection.equals("")) {
                connection = "1";
            }
        } while (!(connection.equals("0")) && !(connection.equals("1")));

        do {
            printStream.println(Color.ANSI_NOCOLOR.escape()+"\nInserire l'indirizzo IP del server: \n(se non sarà messo nulla ne verrà messo uno di defauld)"+Color.ANSI_NOCOLOR.escape());
            ip = readFromConsole();

        } while (!(isIPAddressValid(ip)));

        do {
            printStream.println(Color.ANSI_NOCOLOR.escape()+"\nInserire un Nome:"+Color.ANSI_NOCOLOR.escape());
            name = readFromConsole().trim();
            printStream.println("\n");
        } while (!(isNameValid(name)));

        SagradaCLI sagradaCLI = this;
        try {
            if (clientPlayer == null)
                clientPlayer = new ClientPlayer(Integer.parseInt(connection), sagradaCLI, ip);
            if (!clientPlayer.isConnected())
                clientPlayer.connect();

            clientPlayer.setClientName(name);
        } catch (Exception e) {
            printStream.println(Color.ANSI_NOCOLOR.escape()+"Errore nella creazione del giocatore"+Color.ANSI_NOCOLOR.escape());
            e.printStackTrace();
        }
    }

    /**
     * Creates choose map screen, it use 4 number for choose the map
     * @param s1 first pair of map (first card)
     * @param s2 second pair of map (second card)
     */
    @Override
    public void maps(String[] s1, String[] s2) {
        task = new Thread(new Runnable() {
            @Override
            public void run() {
                Color color = Color.ANSI_GREEN;
                String map;
                printbyFile("resources/titleCli/Scegli_la_mappa.txt", color);

                String name1 = "";
                Integer price1 = null;
                Pair[][] grid1 = new Pair[rows][cols];
                String name2 = "";
                Integer price2 = null;
                Pair[][] grid2 = new Pair[rows][cols];
                String name3 = "";
                Integer price3 = null;
                Pair[][] grid3 = new Pair[rows][cols];
                String name4 = "";
                Integer price4 = null;
                Pair[][] grid4 = new Pair[rows][cols];

                try {
                    name1 = ParserXML.readWindowName(s1[0]);
                    price1 = ParserXML.readBoardDifficult(s1[0]);
                    grid1 = ParserXML.readWindowFromPath(s1[0], grid1);
                } catch (ParserXMLException e) {
                    printStream.println(Color.ANSI_NOCOLOR.escape()+"Errore nel caricamento della mappa: "+s1[0]+Color.ANSI_NOCOLOR.escape());
                    e.printStackTrace();
                }

                try {
                    name2 = ParserXML.readWindowName(s1[1]);
                    price2 = ParserXML.readBoardDifficult(s1[1]);
                    grid2 = ParserXML.readWindowFromPath(s1[1], grid2);

                } catch (ParserXMLException e) {
                    printStream.println(Color.ANSI_NOCOLOR.escape()+"Errore nel caricamento della mappa: "+s1[1]+Color.ANSI_NOCOLOR.escape());
                    e.printStackTrace();
                }

                try {
                    name3 = ParserXML.readWindowName(s2[0]);
                    price3 = ParserXML.readBoardDifficult(s2[0]);
                    grid3 = ParserXML.readWindowFromPath(s2[0], grid3);
                } catch (ParserXMLException e) {
                    printStream.println(Color.ANSI_NOCOLOR.escape()+"Errore nel caricamento della mappa: "+s2[0]+Color.ANSI_NOCOLOR.escape());
                    e.printStackTrace();
                }

                try {
                    name4 = ParserXML.readWindowName(s2[1]);
                    price4 = ParserXML.readBoardDifficult(s2[1]);
                    grid4 = ParserXML.readWindowFromPath(s2[1], grid4);
                } catch (ParserXMLException e) {
                    printStream.println(Color.ANSI_NOCOLOR.escape()+"Errore nel caricamento della mappa: "+s2[1]+Color.ANSI_NOCOLOR.escape());
                    e.printStackTrace();
                }


                do {
                    printStream.println(Color.ANSI_NOCOLOR.escape()+"\n\n1.\t" + name1 + "\n"+Color.ANSI_NOCOLOR.escape());
                    printPair(grid1);
                    printStream.println(Color.ANSI_NOCOLOR.escape()+"\n\t" + "prezzo: " + price1 + "\n"+Color.ANSI_NOCOLOR.escape());

                    printStream.println(Color.ANSI_NOCOLOR.escape()+"\n\n2.\t" + name2 + "\n"+Color.ANSI_NOCOLOR.escape());
                    printPair(grid2);
                    printStream.println(Color.ANSI_NOCOLOR.escape()+"\n\t" + "prezzo: " + price2 + "\n"+Color.ANSI_NOCOLOR.escape());

                    printStream.println(Color.ANSI_NOCOLOR.escape()+"\n\n3.\t" + name3 + "\n"+Color.ANSI_NOCOLOR.escape());
                    printPair(grid3);
                    printStream.println(Color.ANSI_NOCOLOR.escape()+"\n\t" + "prezzo: " + price3 + "\n"+Color.ANSI_NOCOLOR.escape());

                    printStream.println(Color.ANSI_NOCOLOR.escape()+"\n\n4.\t" + name4 + "\n"+Color.ANSI_NOCOLOR.escape());
                    printPair(grid4);
                    printStream.println(Color.ANSI_NOCOLOR.escape()+"\n\t" + "prezzo: " + price4 + "\n"+Color.ANSI_NOCOLOR.escape());

                    printStream.println(Color.ANSI_NOCOLOR.escape()+"\nSono state estratte queste mappe! Scegline una digitando il corrispettivo numero:"+Color.ANSI_NOCOLOR.escape());
                    map = readFromConsole();
                    if (map == null) {
                        return;
                    }
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

    /**
     * Load "il gioco comincia" text
     */
    @Override
    public void game() {
        Color color = Color.ANSI_PURPLE;
        printbyFile("resources/titleCli/Il_gioco_comincia.txt", color);
        printStream.println(Color.ANSI_NOCOLOR.escape()+"\n\n\n Attendere il proprio turno"+Color.ANSI_NOCOLOR.escape());
    }

    /**
     *Creates end game screen with players, scores and winner. If a player is disconnected it write "ritirato"
     * @param name  Vector with players name
     * @param record Vector with scores
     */
    @Override
    public void endGame(String[] name, int[] record) {
        String tempName;
        int tempRecord;
        Color color = Color.ANSI_RED;
        printbyFile("resources/titleCli/finePartita.txt", color);
        printStream.println("\n");

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
            if (record[k] != -1) {
                printStream.println(Color.ANSI_NOCOLOR.escape()+name[k] + ":\t" + record[k] + "\n"+Color.ANSI_NOCOLOR.escape());
            } else {
                printStream.println(Color.ANSI_NOCOLOR.escape()+name[k] + ":\t" + Color.ANSI_RED.escape() + "RITIRATO" + Color.ANSI_NOCOLOR.escape() + "\n");
            }
        }

        color = Color.ANSI_GREEN;
        printbyFile("resources/titleCli/Vincitore.txt", color);
        printStream.println(Color.ANSI_NOCOLOR.escape()+"\n" + name[0] + ":\t" + record[0] + "\n"+Color.ANSI_NOCOLOR.escape());
    }

    /**
     * Creates disconnection screen with reconnection possibility
     * @param s string that disconnection print
     */
    @Override
    public void disconnection(String s) {
        task.interrupt();
        task = new Thread(new Runnable() {
            @Override
            public void run() {
                String i;
                Color color = Color.ANSI_RED;
                printbyFile("resources/titleCli/Attenzione.txt", color);
                printStream.println(Color.ANSI_NOCOLOR.escape()+"\n" + s+Color.ANSI_NOCOLOR.escape());
                do {
                    printStream.println(Color.ANSI_NOCOLOR.escape()+"Premi 'i' per riprovare a connetterti:"+Color.ANSI_NOCOLOR.escape());
                    i = readFromConsole();
                } while (!i.equals("i"));
                deletePlayer();
                login("Ritorna in partita!");
            }
        });
        task.start();
    }

    /**
     * Creates disconnection screen without reconnection possibility
     * @param s string that fatalDisconnection print
     */
    @Override
    public void fatalDisconnection(String s) {
        Color color = Color.ANSI_RED;
        printbyFile("resources/titleCli/Spiacenti.txt", color);
        reader.setCondition(false);
        printStream.println(Color.ANSI_NOCOLOR.escape()+"\n" + s+Color.ANSI_NOCOLOR.escape());
    }

    /**
     * set dadiera with new dadiera
     * @param dadiera new dadiera
     */
    @Override
    public void updateDadiera(Pair[] dadiera) {
        this.dadiera = dadiera;
    }

    /**
     * view dadiera with its title
     */
    private void viewDadiera() {
        Color color = Color.ANSI_NOCOLOR;
        printbyFile("resources/titleCli/Dadiera.txt", color);
        printPair(dadiera);
    }

    /**
     * set window with new grid
     * @param window new grid
     */
    @Override
    public void updateWindow(Pair[][] window) {
        this.window = window;
    }

    /**
     * view grid with its title
     */
    private void viewWindow() {
        Color color = Color.ANSI_NOCOLOR;
        printbyFile("resources/titleCli/Griglia.txt", color);
        printPair(window);
    }

    /**
     * set toolNames with new tools names
     * @param toolNames new tools names
     */
    @Override
    public void updateTools(String[] toolNames) {
        tools = toolNames;

    }

    /**
     * view tools with its title
     */
    private void viewTools() {
        Color color = Color.ANSI_NOCOLOR;
        printbyFile("resources/titleCli/Strumenti.txt", color);
        /*for (int i = 0; i < tools.length; i++) {
            printStream.println(tools[i]);
        }*/
        for (int i = 0; i < tools.length; i++) {
            try {
                String name = tools[i];
                String description = CLIFactory.getTooldescriptionFromName(FileLocator.getToolsListPath(), tools[i]);
                printStream.println(Color.ANSI_NOCOLOR.escape()+"- " + name + ": " + description+Color.ANSI_NOCOLOR.escape());
            } catch (ParserXMLException e) {
                printStream.println(Color.ANSI_NOCOLOR.escape()+"Errore nel file dei tools"+Color.ANSI_NOCOLOR.escape());
                e.getStackTrace();
            }
        }
    }

    /**
     * set one Player with new player's user and corrispective grif. if active is false his name become name+(non in partita)
     * @param pair player grid
     * @param user player user
     * @param active if active is false the player is not in game
     */
    @Override
    public void updateOpponents(Pair[][] pair, String user, boolean active) {
        boolean exist = false;

        for (int i = 0; i < opponents.size() && !exist; i++) {
            if (((opponents.get(i)).getName()).equals(user)) {
                exist = true;
                (opponents.get(i)).setB(active);
                (opponents.get(i)).setGrid(pair);
            }
        }
        if (!exist) {
            Player newOpponent = new Player();
            newOpponent.setB(active);
            newOpponent.setName(user);
            newOpponent.setGrid(pair);
            opponents.add(newOpponent);
        }
    }

    /**
     * view all opponents with its title
     */
    public void viewOpponents() {
        String user;
        Pair[][] pair;
        Color color = Color.ANSI_NOCOLOR;
        printbyFile("resources/titleCli/Avversari.txt", color);
        for (int k = 0; k < opponents.size(); k++) {
            if((opponents.get(k)).isB()==true)
                user = (opponents.get(k)).getName();
            else
                user = (opponents.get(k)).getName()+ " (non in partita)";

            pair = (opponents.get(k)).getGrid();

            for (int i = 0; i < ((cellWidth * 5) - (user.length()) + 4) / 2; i++)
                printStream.print(Color.ANSI_NOCOLOR.escape()+"-"+Color.ANSI_NOCOLOR.escape());

                printStream.print(Color.ANSI_NOCOLOR.escape()+user+Color.ANSI_NOCOLOR.escape());

            for (int i = 0; i < ((cellWidth * 5) - (user.length()) + 4) / 2; i++)
                printStream.print(Color.ANSI_NOCOLOR.escape()+"-"+Color.ANSI_NOCOLOR.escape());

            printStream.println("\n");
            printPair(pair);
            printStream.println("\n\n");
        }
    }

    /**
     * set tokens with new tokens
     * @param token new tokens
     */
    @Override
    public void updateTokens(int token) {
        this.token = token;
    }

    /**
     * view tokens
     */
    private void viewToken() {
        printStream.println(Color.ANSI_NOCOLOR.escape()+"Token:\t" + token+Color.ANSI_NOCOLOR.escape());
    }

    /**
     * set public targets with new public targets
     * @param s new public targets
     */
    @Override
    public void updatePublicTarget(String[] s) {
        this.publicTarget = s;
    }

    /**
     * set private target with new private target
     * @param s new private target
     */
    @Override
    public void updatePrivateTarget(String[] s) {
        this.privateTarget = s;
    }

    /**
     * view private target with its title and public targets with its title
     */
    private void viewTarget() {
        String name;
        String description;
        String[] vecname;
        Color color = Color.ANSI_NOCOLOR;
        printbyFile("resources/titleCli/Obiettivi.txt", color);

        printStream.println(Color.ANSI_NOCOLOR.escape()+"\nObiettivo privato:\n"+Color.ANSI_NOCOLOR.escape());
        for (int i = 0; i < privateTarget.length; i++) {
            try {
                name = ParserXML.readObjectiveName(privateTarget[i]);
                description = ParserXML.readObjectiveDescription(privateTarget[i]);
                printStream.println(Color.ANSI_NOCOLOR.escape()+"- " + name + ": " + description+Color.ANSI_NOCOLOR.escape());
            } catch (ParserXMLException ex) {
                printStream.println(Color.ANSI_NOCOLOR.escape()+"Errore nel file degli obiettivi privati"+Color.ANSI_NOCOLOR.escape());
                ex.printStackTrace();
            }
        }

        printStream.println(Color.ANSI_NOCOLOR.escape()+"\n\nObiettivi pubblici:"+Color.ANSI_NOCOLOR.escape());
        for (int i = 0; i < publicTarget.length; i++) {
            try {
                name = ParserXML.readObjectiveName(publicTarget[i]);
                description = ParserXML.readObjectiveDescription(publicTarget[i]);
                printStream.println(Color.ANSI_NOCOLOR.escape()+name + ": " + description+Color.ANSI_NOCOLOR.escape());
            } catch (ParserXMLException ex) {
                printStream.println(Color.ANSI_NOCOLOR.escape()+"Errore nel file degli obiettivi pubblici"+Color.ANSI_NOCOLOR.escape());
                ex.printStackTrace();
            }
        }
    }

    /**
     * set trace with new trace
     * @param trace new trace
     */
    @Override
    public void updateRoundTrace(ArrayList<Pair>[] trace) {
        this.trace = trace;
    }

    /**
     * view round trace with its title
     */
    private void viewRoundTrace() {
        Color color = Color.ANSI_NOCOLOR;
        printbyFile("resources/titleCli/Tracciato_round.txt", color);
        try {
            for (int i = 0; i < trace.length; i++) {
                if (trace[i].size() != 0) {
                    printStream.println(Color.ANSI_NOCOLOR.escape()+"-----------------------Round" +
                            (i + 1) + "-----------------------\n"+Color.ANSI_NOCOLOR.escape());
                    Pair[] p = new Pair[trace[i].size()];
                    for (int j = 0; j < p.length; j++) {
                        p[j] = trace[i].get(j);
                    }
                    printPair(p);
                }
            }
        } catch (Exception e) {
            printStream.println(Color.ANSI_NOCOLOR.escape()+"Errore nel tracciato round"+Color.ANSI_NOCOLOR.escape());
            e.printStackTrace();
        }
    }

    /**
     * set msg with new message. If message is one color it creates popUPMessage
     * @param msg new message
     */
    @Override
    public void updateMessage(String msg) {
        this.msg = msg;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                if ("redyellowgreenbluepurple".contains(msg)) {
                    String col = msg;
                    popUPMessage(11, col);
                }
            }
        });
    }

    /**
     * view string message
     */
    private void viewMessage() {
        printStream.println(Color.ANSI_NOCOLOR.escape()+msg + "\n"+Color.ANSI_NOCOLOR.escape());
    }

    /**
     * set enable or disable the board
     * @param enableBoard boolean value, if true set enable the board else set disable
     */
    @Override
    public void setEnableBoard(boolean enableBoard) {
        this.enableBoard = enableBoard;
        if (task != null && task.isAlive())
            task.interrupt();
        task = new Thread(new Runnable() {
            @Override
            public void run() {
                turn();
            }
        });
        task.start();
    }

    /**
     * the turn begins, and the player can use simple movement, tools, view the game elements or pass the turn
     * when the turn begin the player view all game elements
     * after the move you can decide whether do an other move or pass the turn
     **/
    private void turn() {
        String action = "";
        String end_turn;
        boolean pass = false;
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
                    printStream.println("\n"+Color.ANSI_NOCOLOR.escape()
                            +"Vuoi fare una mossa [m], usare una carta strumento [c], " +
                            "vedere gli elementi del gioco [e] o passare il turno [p]?"+Color.ANSI_NOCOLOR.escape());

                    action = readFromConsole();
                    if (action == null)
                        return;

                    printStream.println();
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
                        printStream.println(Color.ANSI_NOCOLOR.escape()+"\nVuoi fare dell'altro? [S/n]"+Color.ANSI_NOCOLOR.escape());
                        end_turn = readFromConsole();

                        if (end_turn == null)
                            return;

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
            printStream.println(Color.ANSI_NOCOLOR.escape()+msg+Color.ANSI_NOCOLOR.escape());
        }

    }

    /**
     * when the player choose [m] in turn method this method allows him to choose a dadiera dice and a window cell
     * and if the dice and cell are correct it apply the move
     */
    private void doMovement() {

        String mossa;
        String[] vecmove;
        int[] cell = new int[2];
        printStream.println(Color.ANSI_NOCOLOR.escape()+"MOSSA:\n"+Color.ANSI_NOCOLOR.escape());
        ColorEnum color = null;
        int value = -1;
        do {
            viewDadiera();
            viewWindow();
            printStream.println(Color.ANSI_NOCOLOR.escape()+"Seleziona un dado della dadiera: [esempio: 1 RED] \n" +
                    "[digitare 'annulla' per annullare l'operazione]"+Color.ANSI_NOCOLOR.escape());
            mossa = readFromConsole();

            if (mossa == null || mossa.equals("annulla"))
                return;
            vecmove = mossa.split("\\ ");
            try {
                value = Integer.parseInt(vecmove[0]);
                color=setColor(vecmove[1].toUpperCase());
            } catch (NumberFormatException nfe) {
                value = -1;
                color = null;
            } catch (ArrayIndexOutOfBoundsException ofb) {
                value = -1;
                color = null;
            }
        } while (!pairExist(new Pair(value, color), dadiera));

        MoveAction.setPair(new Pair(value, color));
        printStream.println(Color.ANSI_NOCOLOR.escape()+"\nSeleziona una cella della griglia: "+Color.ANSI_NOCOLOR.escape());
        do {
            printStream.println(Color.ANSI_NOCOLOR.escape()+"ascissa: [crescente da sinistra verso destra]"+Color.ANSI_NOCOLOR.escape());
            try {
                cell[1] = Integer.parseInt(readFromConsole());
            } catch (NumberFormatException nfe) {
                cell[1] = -1;
            } catch (NullPointerException npe) {
                return;
            }
        } while (cell[1] < 1 || cell[1] > 5);

        do {
            printStream.println(Color.ANSI_NOCOLOR.escape()+"ordinata: [crescente dall' alto verso il basso]"+Color.ANSI_NOCOLOR.escape());
            try {
                cell[0] = Integer.parseInt(readFromConsole());
            } catch (NumberFormatException nfe) {
                cell[0] = -1;
            } catch (NullPointerException npe) {
                return;
            }
        } while (cell[0] < 1 || cell[0] > 4);

        printStream.println();

        MoveAction.setCoord(new Coordinates(cell[0] - 1, cell[1] - 1));
        makeMove();
        viewDadiera();
        viewWindow();
        viewMessage();
    }

    /**
     * when the player choose [c] in turn method this method allows him to choose corresponding number
     * and the player must be set the elements that he uses for the tool (dadiera, round trace or window )
     * when the player write 'fine' this method try to use tool and if the sets are wrong the tool is not used
     **/
    private void doTool() {
        printStream.println(Color.ANSI_NOCOLOR.escape()+"STRUMENTO:\n"+Color.ANSI_NOCOLOR.escape());
        String t;
        String element;
        String dad;
        String[] vecmove;
        Pair[] vecTrace;
        String description="";
        int[] cell = new int[2];
        int numtool = -1;
        int value = -1;
        ColorEnum color = null;
        int round;
        //capisco quale tool vuole utilizzare
        do {
            printStream.println(Color.ANSI_NOCOLOR.escape()+"Quale strumento vuoi utilizzare? " +
                    "[digitare il numero corrispondente]\n" +
                    "[digitare 'annulla' per annullare l'operazione]\n");
            for (int i = 0; i < tools.length; i++) {
                try {
                    description = CLIFactory.getTooldescriptionFromName(FileLocator.getToolsListPath(), tools[i]);
                }catch(ParserXMLException ex){
                    ex.printStackTrace();
                }
                printStream.println(Color.ANSI_NOCOLOR.escape()+(i + 1) + ". " + tools[i]+": "+description+Color.ANSI_NOCOLOR.escape());
            }
            t = readFromConsole();

            if (t == null ||t.equals("annulla"))
                return;

        } while (!t.equals("1") && !t.equals("2") && !t.equals("3"));
        try {
            numtool = CLIFactory.getToolnumberFromName(FileLocator.getToolsListPath(), tools[Integer.parseInt(t) - 1]);
        } catch (Exception e) {
            printStream.println(Color.ANSI_NOCOLOR.escape()+"Errore nel file degli strumenti"+Color.ANSI_NOCOLOR.escape());
            e.getStackTrace();
        }
        toolPermission(numtool);
        if (toolPhase == true) {
            //setto i parametri necessari per il tool scelto
            printStream.println(Color.ANSI_NOCOLOR.escape()+"Settare i parametri necessari per utilizzare " + tools[Integer.parseInt(t) - 1] +
                    ":\n - Se si ha finito digitare 'fine'"+Color.ANSI_NOCOLOR.escape());
            do {
                do {
                    printStream.println(Color.ANSI_NOCOLOR.escape()+"\n Cosa vuoi settare?"+Color.ANSI_NOCOLOR.escape());
                    printStream.println(Color.ANSI_NOCOLOR.escape()+"1. dado dalla dadiera\n2. cella dalla griglia\n3. dado dal tracciato round\n[fine]. terminare il settaggio dei parametri"+Color.ANSI_NOCOLOR.escape());
                    element = readFromConsole();

                    if (element == null)
                        return;

                }
                while (!element.equals("1") && !element.equals("2") && !element.equals("3") && !element.equals("fine"));
                if (element.equals("1")) {

                    viewDadiera();
                    do {
                        printStream.println(Color.ANSI_NOCOLOR.escape()+"Seleziona un dado della dadiera: [esempio: 1 RED]\n" +
                                "[digitare 'annulla' per annullare l'operazione]"+Color.ANSI_NOCOLOR.escape());
                        dad = readFromConsole();

                        if (dad == null|| dad.equals("annulla"))
                            return;

                        vecmove = dad.split("\\ ");
                        try {
                            value = Integer.parseInt(vecmove[0]);
                            color=setColor(vecmove[1].toUpperCase());
                        } catch (NumberFormatException nfe) {
                            value = -1;
                            color = null;
                        } catch (ArrayIndexOutOfBoundsException ofb) {
                            value = -1;
                            color = null;
                        }
                    } while (!pairExist(new Pair(value, color), dadiera));
                    ToolAction.setDadieraPair(new Pair(value, color));

                } else if (element.equals("2")) {
                    viewWindow();
                    printStream.println(Color.ANSI_NOCOLOR.escape()+"Seleziona una cella della griglia: \n"+Color.ANSI_NOCOLOR.escape());
                    do {
                        printStream.println(Color.ANSI_NOCOLOR.escape()+"ascissa: [crescente da sinistra verso destra]"+Color.ANSI_NOCOLOR.escape());
                        try {
                            cell[1] = Integer.parseInt(readFromConsole());
                        } catch (NumberFormatException nfe) {
                            cell[1] = -1;
                        } catch (NullPointerException npe) {
                            return;
                        }
                    } while (cell[1] < 1 || cell[1] > 5);

                    do {
                        printStream.println(Color.ANSI_NOCOLOR.escape()+"ordinata: [crescente dall' alto verso il basso]"+Color.ANSI_NOCOLOR.escape());
                        try {
                            cell[0] = Integer.parseInt(readFromConsole());
                        } catch (NumberFormatException nfe) {
                            cell[0] = -1;
                        } catch (NullPointerException npe) {
                            return;
                        }
                    } while (cell[0] < 1 || cell[0] > 4);
                    ToolAction.setPosition(new Coordinates(cell[0] - 1, cell[1] - 1));

                } else if (element.equals("3")) {
                    viewRoundTrace();
                    do {
                        printStream.println(Color.ANSI_NOCOLOR.escape()+"Selezionare il round del tracciato (che non sia vuoto) dal quale estrarre il dado" +
                                "\n[digitare 'annulla' per annullare l'operazione]"+Color.ANSI_NOCOLOR.escape());
                        String result = readFromConsole();

                        if (result == null||result.equals("annulla"))
                            return;

                        try {
                            round = Integer.parseInt(result);
                        } catch (NumberFormatException nfe) {
                            round = -1;
                        }
                    } while ((round) < 1 || round > 9 || trace[round - 1].size() == 0);
                    printStream.println();
                    do {
                        printStream.println(Color.ANSI_NOCOLOR.escape()+"Selezionare un dado appartenente al round scelto"+Color.ANSI_NOCOLOR.escape());
                        dad = readFromConsole();

                        if (dad == null||dad.equals("annulla"))
                            return;

                        vecmove = dad.split("\\ ");
                        try {
                            value = Integer.parseInt(vecmove[0]);
                            color=setColor(vecmove[1].toUpperCase());
                            vecTrace = new Pair[trace[round - 1].size()];
                            for (int i = 0; i < trace[round - 1].size(); i++) {
                                vecTrace[i] = trace[round - 1].get(i);
                            }
                        } catch (NumberFormatException nfe) {
                            value = -1;
                            vecTrace = null;
                        } catch (ArrayIndexOutOfBoundsException ofb) {
                            value = -1;
                            vecTrace = null;
                        }
                    } while (!pairExist(new Pair(value, color), vecTrace));
                    ToolAction.setTracePair(new Pair(value, color));
                    ToolAction.setTracePosition(round);

                } else if (element.equals("fine")) {


                    if (enableBoard) {

                        makeToolMove();

                    } else {
                        printStream.println(Color.ANSI_NOCOLOR.escape()+"il tool non è stato utilizzato\n"+Color.ANSI_NOCOLOR.escape());
                    }
                    viewRoundTrace();
                    viewDadiera();
                    viewWindow();
                    printStream.println("\n");
                    viewMessage();

                }

            } while (!element.equals("fine"));
        } else {
            printStream.println(Color.ANSI_NOCOLOR.escape()+"Richiesta utilizzo strumento respinta"+Color.ANSI_NOCOLOR.escape());
        }
    }

    /**
     * It view the elements that the player wants to see
     */
    private void viewElements() {
        String v;
        String response;
        printStream.println(Color.ANSI_NOCOLOR.escape()+"ELEMENTI:\n"+Color.ANSI_NOCOLOR.escape());
        do {
            do {
                printStream.println(Color.ANSI_NOCOLOR.escape()+
                        "Quale elemento vuoi vedere?\n\n" +
                        "[d] dadiera\n" +
                        "[g] griglia\n" +
                        "[t] tracciato round\n" +
                        "[a] avversari\n" +
                        "[c] carte strumento\n" +
                        "[o] obiettivi\n" +
                        "[s] segnalini\n" +
                        "[tutto] tutti gli elementi\n" +
                        "[niente] nessuno degli elementi"+
                        Color.ANSI_NOCOLOR.escape());
                v = readFromConsole();
                if (v == null||v.equals("niente"))
                    return;
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
                printStream.println(Color.ANSI_NOCOLOR.escape()+"\n\nVuoi vedere altri elementi? [S/n]"+Color.ANSI_NOCOLOR.escape());
                response = readFromConsole();
                if (response == null)
                    return;
            } while (!response.equals("S") && !response.equals("n"));
        } while (response.equals("S"));
    }

    /**
     * pass the turn
     */
    @Override
    public void passTurn() {
        clientPlayer.pass();
        printStream.println(Color.ANSI_NOCOLOR.escape()+"\n\n\n\n\nAttendere il proprio turno\n\n\n\n"+Color.ANSI_NOCOLOR.escape());
    }

    /**
     * it send the movement to the server
     */
    @Override
    public void makeMove() {
        clientPlayer.myMove();
    }

    /**
     * it send the tool action to the server
     */
    @Override
    public void makeToolMove() {
        clientPlayer.useTool();
    }

    /**
     * it warn the server that the player wants to use the tool
     * @param i tool's number
     */
    @Override
    public void toolPermission(int i) {
        toolPhase = clientPlayer.toolPermission(i);
        if (toolPhase) {
            setToolPhase(true);
            if (i == 1) {
                popUPMessage(i, "");
            }
        }
    }

    /**
     * if this is a tool phase set true else set false
     * @param toolPhase boolean paramenter
     */
    @Override
    public void setToolPhase(boolean toolPhase) {
        this.toolPhase = toolPhase;
    }

    /**
     * print a big title with the number of current round
     */
    private void printTurn() {
        Color color = Color.ANSI_RED;
        if (numTurn() != -1) {
            printbyFile("resources/titleCli/Round" + (numTurn() + 1) + ".txt", color);
        }
    }

    /**
     * it count the not empty rounds in roundTrace and this is the number of current round
     * @return number of current round
     */
    private int numTurn() {
        for (int i = 0; i < trace.length; i++)
            if (trace[i].size() == 0) {
                return i;
            }
        return -1;
    }

    /**
     * verify the ip validity
     * @param ip ip address
     * @return false if it is not valid, else true
     */
    private boolean isIPAddressValid(String ip) {
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

    /**
     * verify the name validity
     * @param name player name
     * @return false if it is an empty string, else true
     */
    private boolean isNameValid(String name) {
        if (name.isEmpty())
            return false;
        return true;
    }

    /**
     * print a pairs vector
     * @param p pairs vector
     */
    //print pair array
    private synchronized void printPair(Pair[] p) {
        printStream.print(Color.ANSI_NOCOLOR.escape());
        //parte superione
        printPairArray(p);
        printStream.print(Color.ANSI_NOCOLOR.escape());
        //parte centrale
        for (int j = 0; j < p.length; j++) {
            printStream.print(hashMapEc.get(p[j].getColor()).escape() + "");
            for (int k = 0; k < cellWidth / 2; k++) {
                printStream.print(" ");
            }
            printStream.print(Color.ANSI_BLACK.escape() + hashMapEc.get(p[j].getColor()).escape() + p[j].getValue());

            for (int k = 0; k < cellWidth / 2; k++) {
                printStream.print(" ");
            }
            printStream.print(Color.ANSI_NOCOLOR.escape() + "  ");
        }
        printStream.println("" + Color.ANSI_NOCOLOR.escape());
        printStream.print(Color.ANSI_NOCOLOR.escape());
        //parte inferiore
        printPairArray(p);
        printStream.println("\n");
        for (int i = 0; i < p.length; i++) {
            printStream.print("" + p[i].getValue() + " " + p[i].getColor() + "\t");
        }
        printStream.println("\n");
        printStream.print(Color.ANSI_NOCOLOR.escape());
    }

    /**
     * print higher and lower vector part
     * @param p pairs vector
     */
    private void printPairArray(Pair[] p) {
        for (int i = 0; i < cellHeight / 2; i++) {
            for (int j = 0; j < p.length; j++) {
                printStream.print(hashMapEc.get(p[j].getColor()).escape() + "");
                for (int k = 0; k < cellWidth; k++) {
                    printStream.print(" ");
                }
                printStream.print(Color.ANSI_NOCOLOR.escape() + "  ");
            }
            printStream.println("" + Color.ANSI_NOCOLOR.escape());
        }
    }

    /**
     * print a pairs matrix
     * @param mp pairs matrix
     */
    //print pair matrix
    private synchronized void printPair(Pair[][] mp) {

        printStream.print(Color.ANSI_NOCOLOR.escape());

        for (int w = 0; w < mp.length; w++) {

            //parte superione
            printPairMatrix(mp, w);
            printStream.print(Color.ANSI_NOCOLOR.escape());
            //parte centrale
            for (int j = 0; j < mp[0].length; j++) {
                printStream.print(hashMapEc.get(mp[w][j].getColor()).escape() + "");
                for (int k = 0; k < cellWidth / 2; k++) {
                    printStream.print(" ");
                }
                printStream.print(Color.ANSI_BLACK.escape() + hashMapEc.get(mp[w][j].getColor()).escape() + mp[w][j].getValue());

                for (int k = 0; k < cellWidth / 2; k++) {
                    printStream.print(" ");
                }
                printStream.print(Color.ANSI_NOCOLOR.escape() + "  ");
            }
            printStream.println("" + Color.ANSI_NOCOLOR.escape());
            printStream.print(Color.ANSI_NOCOLOR.escape());
            //parte inferiore
            printPairMatrix(mp, w);
            printStream.println();
        }

        printStream.println();
        printStream.print(Color.ANSI_NOCOLOR.escape());
    }

    /**
     * print higher and lower pairs matrix row part
     * @param mp pairs matrix
     * @param w row index
     */
    private void printPairMatrix(Pair[][] mp, int w) {
        for (int i = 0; i < cellHeight / 2; i++) {
            for (int j = 0; j < mp[0].length; j++) {
                printStream.print(hashMapEc.get(mp[w][j].getColor()).escape() + "");
                for (int k = 0; k < cellWidth; k++) {
                    printStream.print(" ");
                }
                printStream.print(Color.ANSI_NOCOLOR.escape() + "  ");
            }
            printStream.println("" + Color.ANSI_NOCOLOR.escape());
        }
    }

    /**
     * print a title from txt file with a specific color
     * @param s file path
     * @param color specific color
     */
    private synchronized void printbyFile(String s, Color color) {
        printStream.println("\n\n");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(s));
            String line = reader.readLine();
            while (line != null) {
                printStream.println(color.escape() + "" + line + Color.ANSI_NOCOLOR.escape());
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("errore nel caricamento del file di testo");
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                return;
            }
        }
    }

    /**
     * this method handles the read from console.
     *
     * @return the value of the global variable in which the input is stored
     */
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

    /**
     * control pair presence in a pairs vector
     * @param p pair
     * @param vecPair pairs vector
     * @return if it exist return true else false
     */
    private boolean pairExist(Pair p, Pair[] vecPair) {
        for (int i = 0; i < vecPair.length; i++) {
            if ((vecPair[i].getValue()).equals(p.getValue()) && (vecPair[i].getColor()).equals(p.getColor()))
                return true;
        }
        return false;
    }

    /**
     * it is a message that appear when the player use tool 1 for set value+1 or value-1 and tool 11 for set die value
     *
     * @param toolID tool number
     * @param str die color selection in tool 11
     */
    public void popUPMessage(int toolID, String str) {
        String result;
        ColorEnum color = null;
        switch (toolID) {
            case 1:
                printStream.println(Color.ANSI_NOCOLOR.escape()+"\n Cambia il valore:\n"+Color.ANSI_NOCOLOR);
                printStream.println(Color.ANSI_NOCOLOR.escape()+"Come desideri cambiare il valore del dado?"+Color.ANSI_NOCOLOR.escape());
                do {
                    printStream.println(Color.ANSI_NOCOLOR.escape()+"1. Incrementa\n2. Decrementa\n"+Color.ANSI_NOCOLOR.escape());
                    result = readFromConsole();
                    if (result == null)
                        return;
                } while (!result.equals("1") && !result.equals("2"));
                if (result.equals("1"))
                    ToolAction.setInstruction("inc");
                else if (result.equals("2"))
                    ToolAction.setInstruction("dec");
                break;
            case 11:
                printStream.println(Color.ANSI_NOCOLOR.escape()+"Selezione valore\n"+Color.ANSI_NOCOLOR.escape());
                printStream.println(Color.ANSI_NOCOLOR.escape()+"Il tuo dado è " + str+Color.ANSI_NOCOLOR.escape());
                do {
                    printStream.println(Color.ANSI_NOCOLOR.escape()+"Seleziona il numero del dado!"+Color.ANSI_NOCOLOR.escape());
                    result = readFromConsole();
                    if (result == null)
                        return;
                } while (Integer.parseInt(result) > 6 || Integer.parseInt(result) < 1);
                color=setColor(str.toUpperCase());
                ToolAction.setDadieraPair(new Pair(Integer.parseInt(result), color));
                makeToolMove();
                break;
        }
    }

    /**
     * delete player in client
     */
    @Override
    public void deletePlayer() {
        clientPlayer = null;
    }

    /**
     * create corresponce ColorEnum-->Color with hashmap
     */
    private void createMap() {
        hashMapEc.put(ColorEnum.RED, Color.ANSI_BACK_RED);
        hashMapEc.put(ColorEnum.BLUE, Color.ANSI_BACK_BLUE);
        hashMapEc.put(ColorEnum.PURPLE, Color.ANSI_BACK_PURPLE);
        hashMapEc.put(ColorEnum.YELLOW, Color.ANSI_BACK_YELLOW);
        hashMapEc.put(ColorEnum.GREEN, Color.ANSI_BACK_GREEN);
        hashMapEc.put(ColorEnum.WHITE, Color.ANSI_NOCOLOR);
        hashMapEc.put(null, Color.ANSI_NOCOLOR);
    }

    /**
     * if the input color is valid return corresponding ColorEnum
     * @param s imput string
     * @return corresponding ColorEnum. Ifinput is not valid return null
     */
    private ColorEnum setColor(String s){
        switch (s.toUpperCase()) {
            case "RED":
                return ColorEnum.RED;
            case "GREEN":
                return ColorEnum.GREEN;
            case "YELLOW":
                return ColorEnum.YELLOW;
            case "BLUE":
                return ColorEnum.BLUE;
            case "PURPLE":
                return ColorEnum.PURPLE;
            default:
                return null;
        }
    }

    public static void main(String[] args) {
        new SagradaCLI();
    }



    private class Player {
        String name;
        Pair[][] grid;
        boolean b;

        public void setName(String name) {
            this.name = name;
        }

        public void setGrid(Pair[][] grid) {
            this.grid = grid;
        }

        public Pair[][] getGrid() {
            return grid;
        }

        public String getName() {
            return name;
        }

        public void setB(boolean b) {
            this.b = b;
        }

        public boolean isB() {
            return b;
        }


    }


    /**
     * this thread constantly waits for an input from console
     * and stores it in a global variable
     */
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
            while (condition) {
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


