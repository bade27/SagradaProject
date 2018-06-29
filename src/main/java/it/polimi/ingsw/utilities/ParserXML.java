package it.polimi.ingsw.utilities;

import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Placement;
import javafx.scene.image.Image;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ParserXML
{
    //<editor-fold desc="Window XML">
    /**
     * Read from xml window's pattern and initialize an empty board
     * @param path XML file location
     * @param b Board to initialize
     * @return Board initialized
     */
    public static Cell[][] readWindowFromPath (String path, Cell[][] b) throws ParserXMLException
    {
        Cell board [][] = b;
        try
        {
            File file = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);

            //ora è un po' più efficiente anche qui (prendo solo i tag che mi servono, senza fare la lista di tutti
            String name = document.getElementsByTagName("name").item(0).getTextContent();
            int difficult = Integer.parseInt(document.getElementsByTagName("difficulty").item(0).getTextContent());

            //sono due liste che contengono l'elenco dei tag del file (hanno stessa lunghezza)
            //in questo modo posso leggere in parallelo i valori delle celle
            NodeList values = document.getElementsByTagName("value");
            NodeList colors = document.getElementsByTagName("color");
            //NodeList imgs = document.getElementsByTagName("img_source");

            //il parametro k mi serve come indice delle due liste valori e colori
            for(int i = 0, k = 0; i < board.length; i++)
            {
                for(int j = 0; j < board[i].length; j++)
                {
                    int currentValue = Integer.parseInt(values.item(k).getTextContent());
                    String currentColor = colors.item(k).getTextContent();
                    if (currentColor.equals("dc"))
                        board[i][j] = new Cell(new Placement(currentValue,null));
                    else
                    {
                        if (currentColor.equals("red"))
                            board[i][j] = new Cell(new Placement(currentValue, ColorEnum.RED));
                        if (currentColor.equals("green"))
                            board[i][j] = new Cell(new Placement(currentValue,ColorEnum.GREEN));
                        if (currentColor.equals("yellow"))
                            board[i][j] = new Cell(new Placement(currentValue,ColorEnum.YELLOW));
                        if (currentColor.equals("blue"))
                            board[i][j] = new Cell(new Placement(currentValue,ColorEnum.BLUE));
                        if (currentColor.equals("purple"))
                            board[i][j] = new Cell(new Placement(currentValue,ColorEnum.PURPLE));
                    }
                    k++;
                }
            }
        }
        catch (Exception e) {
            throw new ParserXMLException("Impossible to read file: " + path);
        }
        return board;
    }

    /**
     * Read from xml all windows' names and return a list with those
     * @param path XML file location
     * @return list of names
     */
    public static ArrayList readWindowsName (String path) throws ParserXMLException
    {
        try
        {
            ArrayList<String []> arr = new ArrayList<String[]>();
            File file = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);

            int nCard = Integer.parseInt(document.getElementsByTagName("numberOfCards").item(0).getTextContent());
            NodeList cards = document.getElementsByTagName("card");

            for (int i = 0; i < nCard * 2;i++)
            {
                String[] names = new String[2];
                String card = cards.item(i).getTextContent();
                names[0] = card;
                i++;
                card = cards.item(i).getTextContent();
                names[1] = card;
                arr.add(names);
            }
            return arr;
        }
        catch (Exception ex){
            throw new ParserXMLException("Impossible to read file: " + path);
        }

    }

    /**
     * Read and return from xml the difficult about board passed in path
     * @param path XML file location
     * @return difficult of board
     */
    public static int readBoardDifficult (String path) throws ParserXMLException
    {
        try {
            File file = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);

            return Integer.parseInt(document.getElementsByTagName("difficulty").item(0).getTextContent());
        }catch (Exception e){
            throw new ParserXMLException("Impossible to read difficult from XML " + path);
        }
    }

    /**
     * Read and return from xml the name about board passed in path
     * @param path XML file location
     * @return name of board
     */
    public static String readWindowName (String path) throws ParserXMLException
    {
        try {
            File file = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);

            return document.getElementsByTagName("name").item(0).getTextContent();
        }catch (Exception e){
            throw new ParserXMLException("Impossible to read difficult from XML " + path);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Objective XML">
    /**
     * Read from xml all objectives' names and return a list with those
     * @param path XML file location
     * @return list of names
     */
    public static ArrayList readObjectiveNames (String path) throws ParserXMLException
    {
        try
        {
            ArrayList<String> arr = new ArrayList<String>();
            File file = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);

            int nCard = Integer.parseInt(document.getElementsByTagName("numberOfCards").item(0).getTextContent());
            NodeList cards = document.getElementsByTagName("card");

            for (int i = 0; i < nCard;i++)
            {
                String card = cards.item(i).getTextContent();
                arr.add(card);
            }
            return arr;
        }
        catch (Exception ex){
            throw new ParserXMLException("Impossible to read file: " + path);
        }
    }

    //</editor-fold>

    //<editor-fold desc="Tool XML">
    /**
     * Read from xml all tools' names and return a list with those
     * @param path XML file location
     * @return list of names
     */
    public static ArrayList readToolsNames (String path) throws ParserXMLException
    {
        try
        {
            ArrayList<String> arr = new ArrayList<String>();
            File file = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);

            int nCard = Integer.parseInt(document.getElementsByTagName("numberOfCards").item(0).getTextContent());


            for (int i = 0; i < nCard;i++)
            {
                String s = "tool" + (i + 1);
                int numtool = Integer.parseInt(document.getElementsByTagName(s).item(0).getAttributes().getNamedItem("num").getTextContent());
                String name = document.getElementsByTagName("name").item(numtool-1).getTextContent();
                arr.add(name);
            }
            return arr;
        }
        catch (Exception ex){
            throw new ParserXMLException("Impossible to read file: " + path);
        }
    }



    //</editor-fold>

    //<editor-fold desc="User XML">
    /**
     * Read from xml all user names and return a list with those
     * @param path XML file location
     * @return list of usernames
     */
    public static ArrayList<String> readUserNames (String path) throws ParserXMLException
    {
        try
        {
            ArrayList<String> userList = new ArrayList<>();
            File file = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            for (int i = 0 ; i < document.getElementsByTagName("username").getLength() ; i++)
            {
                String s = document.getElementsByTagName("username").item(i).getTextContent();
                userList.add(s);
            }
            return userList;
        }
        catch (Exception ex){
            throw new ParserXMLException("Impossible to read file: " + path);
        }
    }

    /**
     * Write to user name passed
     * @param path XML file location
     */
    public static void addUserNames (String path,String name) throws ParserXMLException
    {
        try {
            File inputFile = new File(path);
            DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            // creating input stream
            Document doc = builder.parse(inputFile );
            Element root =doc.getDocumentElement();
            Node childnode=doc.createElement("username");
            root.appendChild(childnode);
            childnode.setTextContent(name);

            // writing xml file
            Transformer tf = TransformerFactory.newInstance().newTransformer();
            tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            File outputFile = new File(path);
            tf.transform(new DOMSource(doc), new StreamResult(outputFile));


        } catch (Exception e) {
            throw new ParserXMLException("Impossible to read file: " + path);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Setup parameters XML">
    public static class SetupParserXML
    {
        public static String getHostName (String path) throws ParserXMLException
        {
            return retrieveSetting(path,"host_name");
        }

        public static int getSocketPort (String path) throws ParserXMLException
        {
            return Integer.parseInt(retrieveSetting(path,"socket_port"));
        }

        public static int getRmiPort (String path) throws ParserXMLException
        {
            return Integer.parseInt(retrieveSetting(path,"rmi_port"));
        }

        public static int getMaxPlayers (String path) throws ParserXMLException
        {
            return Integer.parseInt(retrieveSetting(path,"max_players"));
        }

        public static int getThresholdPlayers (String path) throws ParserXMLException
        {
            return Integer.parseInt(retrieveSetting(path,"threshold_players"));
        }

        public static int getThresholdTimeLaps (String path) throws ParserXMLException
        {
            return Integer.parseInt(retrieveSetting(path,"threshold_time_laps"));
        }

        public static int getSetupTimeLaps (String path) throws ParserXMLException
        {
            return Integer.parseInt(retrieveSetting(path,"setup_time_laps"));
        }

        public static int getPingTimeLaps (String path) throws ParserXMLException
        {
            return Integer.parseInt(retrieveSetting(path,"ping_time_laps"));
        }

        public static int getTurnTimeLaps (String path) throws ParserXMLException
        {
            return Integer.parseInt(retrieveSetting(path,"turn_time_laps"));
        }

        public static int getTotalTurns (String path) throws ParserXMLException
        {
            return Integer.parseInt(retrieveSetting(path,"num_turns"));
        }

        public static int getStatusTimeLaps (String path) throws ParserXMLException
        {
            return Integer.parseInt(retrieveSetting(path,"server_status_time_laps"));
        }

        private static String retrieveSetting (String path, String setting) throws ParserXMLException
        {
            try
            {
                File file = new File(path);
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(file);
                return document.getElementsByTagName(setting).item(0).getTextContent();

            }catch (SAXException | IOException | ParserConfigurationException e){
                throw new ParserXMLException("Impossible to read file: " + path);
            }
        }
    }
    //</editor-fold>

    public static class LoadImageXMLAtRequest
    {
        public static Image getImageFromPath (String path)
        {
            try {
                File file = new File(path);
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                Document document = documentBuilder.parse(file);

                String imgPath = document.getElementsByTagName("image").item(0).getTextContent();
                return new Image(imgPath);

            }catch (Exception e){

                return new Image("file:resources/carte/obbiettivi/sfondo_obbiettivi.png");

            }
        }

        public static Image getPublicIntestation ()
        {
            try {
                return new Image("file:resources/carte/obbiettivi/obbiettiviPubblici/Images/obbiettivi_pubblici.png");
            }catch (Exception e){
                return new Image("file:resources/carte/obbiettivi/sfondo_obbiettivi.png");
            }
        }

        public static Image getPrivateIntestation ()
        {
            try {
                return new Image("file:resources/carte/obbiettivi/obbiettiviPubblici/Images/obbiettivi_privati.png");
            }catch (Exception e){
                return new Image("file:resources/carte/obbiettivi/sfondo_obbiettivi.png");
            }
        }

        public static Image getObjectivesBackground ()
        {
            return new Image("file:resources/carte/obbiettivi/sfondo_obbiettivi.png");
        }

    }
}
