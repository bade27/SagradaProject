package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.remoteInterface.ClientRemoteInterface;
import it.polimi.ingsw.utilities.FileLocator;
import it.polimi.ingsw.utilities.LogFile;
import it.polimi.ingsw.utilities.ParserXML;
import it.polimi.ingsw.utilities.UsersEntry;

import java.rmi.Naming;
import java.util.ArrayList;

public class MainServerApplication
{
    //connection parameters
    private static int RMI_REGISTRY_PORT;
    private static String HOSTNAME;
    private static int SOCKET_PORT;
    private int progressive;

    private ArrayList<MatchHandler> matches = new ArrayList<>();
    private UsersEntry userList;
    private int handlerProgressive;

    private LogFile mainLog;

    //<editor-fold desc="Server Bootstrap">
    public boolean initializeServer ()
    {
        System.out.println(">>>Server Application Started");
        mainLog = new LogFile();
        mainLog.createLogFile("Main_Server");

        progressive = 0;
        handlerProgressive = 0;
        try {
            parametersSetup();
        }catch (ParserXMLException e){
            return false;
        }

        startClientRegistration();
        return initializeUsers();
    }

    /**
     * Initialize from file users's credential
     * @return true if goes well, false otherwise
     */
    private boolean initializeUsers ()
    {
        try
        {
            userList = new UsersEntry();
            return true;
        }catch (Exception e){
            mainLog.addLog("Impossible to initialize users from db" + e.getMessage(),e.getStackTrace());
            return false;
        }
    }

    private void startClientRegistration ()
    {
        //Starts thread that accept client's connection
        InitializerConnection initializer = new InitializerConnection(this);
        initializer.start();
    }

    /**
     * sets up parameters
     */
    private static void parametersSetup() throws ParserXMLException
    {
        HOSTNAME = ParserXML.SetupParserXML.getHostName(FileLocator.getServerSettingsPath());
        RMI_REGISTRY_PORT = ParserXML.SetupParserXML.getRmiPort(FileLocator.getServerSettingsPath());
        SOCKET_PORT =ParserXML.SetupParserXML.getSocketPort(FileLocator.getServerSettingsPath());
    }
    //</editor-fold>

    private void startNewMatchHandler()
    {
        MatchHandler m = new MatchHandler(this,userList,handlerProgressive);
        matches.add(m);
        new Thread(m).start();
        mainLog.addLog(">>>New Match Handler" + handlerProgressive + " Created");
        handlerProgressive ++ ;
    }

    public void removeMatchHandler (MatchHandler m)
    {
        mainLog.addLog(">>>Match Handler" + m.getId() + " Removed");
        matches.remove(m);
    }

    private synchronized void dynamicMatchChoosing(ClientRemoteInterface cli)
    {
        for (int i = 0 ; i < matches.size() ; i++)
            if (matches.get(i).clientRegistration(cli))
            {
                mainLog.addLog(">>>Match Handler" + matches.get(i).getId() + " client added");
                return;
            }

        startNewMatchHandler();
        try
        {
            Thread.sleep(500); //I need time before a new Match Handler will be created
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
        matches.get(matches.size() - 1).clientRegistration(cli);
    }

    /**
     * Update RMI's registry after an RMI's connection and call clientRegistration
     * @param cli communicator object, used for interact with client
     */
    public void setClient (ClientRemoteInterface cli)
    {
        ServerRmiHandler rmiCon;
        progressive++;
        mainLog.addLog("Client accepted with RMI connection");
        //RMI Registry creation and bind server name
        try {
            rmiCon = new ServerRmiHandler(this);

            String bindLocation = "rmi://" + HOSTNAME + ":" + RMI_REGISTRY_PORT + "/sagrada" + progressive;
            try{
                java.rmi.registry.LocateRegistry.createRegistry(RMI_REGISTRY_PORT);
            }catch (Exception ex){}

            Naming.bind(bindLocation, rmiCon );

            mainLog.addLog("Server RMI waiting for client on port  " + RMI_REGISTRY_PORT);
        }catch (Exception e) {
            mainLog.addLog("RMI Bind failed" , e.getStackTrace());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                dynamicMatchChoosing(cli);
            }
        }).start();
    }

    /**
     * Main method. It starts the server
     * @param args arguments
     */
    public static void main(String[] args)
    {
        MainServerApplication main = new MainServerApplication();

        if (main.initializeServer()) {
            main.startNewMatchHandler();
        }
        else
            System.out.println( "Impossible to start, server Aborted");
    }


    //<editor-fold desc="Initializer connection class">
    /**
     * This thread-class is used to accept client's connection through RMI and Socket in a parallel process
     */
    private class InitializerConnection extends Thread
    {
        MainServerApplication main;
        private InitializerConnection (MainServerApplication m)
        {
            main = m;
        }

        public void run ()
        {
            ServerRmiHandler rmiCon;
            //RMI Registry creation and bind server name
            try {
                rmiCon = new ServerRmiHandler(main);

                String bindLocation = "rmi://" + HOSTNAME + ":" + RMI_REGISTRY_PORT + "/sagrada" + progressive;
                try{
                    java.rmi.registry.LocateRegistry.createRegistry(RMI_REGISTRY_PORT);
                }catch (Exception ex){}

                Naming.bind(bindLocation, rmiCon );

                mainLog.addLog("Server RMI waiting for client on port  " + RMI_REGISTRY_PORT);
            }catch (Exception e) {
                mainLog.addLog("RMI Bind failed" , e.getStackTrace());
            }

            //Socket creation and accept
            while (true)
            {
                try{
                    ServerSocketHandler socketCon = new ServerSocketHandler(SOCKET_PORT);
                    socketCon.createConnection(mainLog);
                    if (socketCon.isConnected()) {
                        mainLog.addLog("Client accepted with Socket connection");
                    }
                    dynamicMatchChoosing(socketCon);
                }
                catch (ClientOutOfReachException e) {
                    mainLog.addLog(e.getMessage() , e.getStackTrace());
                }
            }
        }
    }
    //</editor-fold>

}
