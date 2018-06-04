package it.polimi.ingsw.utilities;

import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Placement;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
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
            int id = Integer.parseInt(document.getElementsByTagName("id").item(0).getTextContent());
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
                        if (currentColor.equals("rosso"))
                            board[i][j] = new Cell(new Placement(currentValue, ColorEnum.RED));
                        if (currentColor.equals("verde"))
                            board[i][j] = new Cell(new Placement(currentValue,ColorEnum.GREEN));
                        if (currentColor.equals("giallo"))
                            board[i][j] = new Cell(new Placement(currentValue,ColorEnum.YELLOW));
                        if (currentColor.equals("azzurro"))
                            board[i][j] = new Cell(new Placement(currentValue,ColorEnum.BLUE));
                        if (currentColor.equals("viola"))
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
}
