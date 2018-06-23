package it.polimi.ingsw.server;

import it.polimi.ingsw.exceptions.ClientOutOfReachException;
import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.remoteInterface.ClientRemoteInterface;
import it.polimi.ingsw.utilities.FileLocator;
import it.polimi.ingsw.utilities.LogFile;
import it.polimi.ingsw.utilities.ParserXML;
import it.polimi.ingsw.utilities.UsersEntry;

import javax.management.modelmbean.XMLParseException;
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


    //<editor-fold desc="Server Bootstrap">
    public boolean initializeServer ()
    {
        LogFile.createLogFile();

        progressive = 0;
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
            LogFile.addLog("{MAIN SERVER} " + e.getMessage(),e.getStackTrace());
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
        MatchHandler m = new MatchHandler(this,userList);
        matches.add(m);
        new Thread(m).start();
    }

    /**
     * Update RMI's registry after an RMI's connection and call clientRegistration
     * @param cli communicator object, used for interact with client
     */
    public void setClient (ClientRemoteInterface cli)
    {
        ServerRmiHandler rmiCon;
        progressive++;
        LogFile.addLog("Client accepted with RMI connection");
        //RMI Registry creation and bind server name
        try {
            rmiCon = new ServerRmiHandler(this);

            String bindLocation = "rmi://" + HOSTNAME + ":" + RMI_REGISTRY_PORT + "/sagrada" + progressive;
            try{
                java.rmi.registry.LocateRegistry.createRegistry(RMI_REGISTRY_PORT);
            }catch (Exception ex){}

            Naming.bind(bindLocation, rmiCon );

            LogFile.addLog("Server RMI waiting for client on port  " + RMI_REGISTRY_PORT);
        }catch (Exception e) {
            LogFile.addLog("RMI Bind failed" , e.getStackTrace());
        }

        dynamicMathChoosing(cli);
    }


    private void dynamicMathChoosing (ClientRemoteInterface cli)
    {
        for (int i = 0 ; i < matches.size() ; i++)
            if (matches.get(i).clientRegistration(cli))
                return;

        startNewMatchHandler();
        try
        {
            Thread.sleep(500); //I need time before a new Match Handler will be created
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }

        dynamicMathChoosing(cli);
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
            LogFile.addLog("{MAIN SERVER} Impossible to start, server Aborted");
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

                LogFile.addLog("Server RMI waiting for client on port  " + RMI_REGISTRY_PORT);
            }catch (Exception e) {
                LogFile.addLog("RMI Bind failed" , e.getStackTrace());
            }

            //Socket creation and accept
            while (true)
            {
                try{
                    ServerSocketHandler socketCon = new ServerSocketHandler(SOCKET_PORT);
                    socketCon.createConnection();
                    if (socketCon.isConnected()) {
                        LogFile.addLog("Client accepted with Socket connection");
                    }
                    dynamicMathChoosing(socketCon);
                }
                catch (ClientOutOfReachException e) {
                    LogFile.addLog(e.getMessage() , e.getStackTrace());
                }
            }
        }
    }
    //</editor-fold>

}
