package it.polimi.ingsw.view.cli;

import it.polimi.ingsw.exceptions.ParserXMLException;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class CLIFactory {

    /**
     *
     * @param path XML file location
     * @param name tool's name
     * @return tool's number
     * @throws ParserXMLException
     */
    public static int getToolnumberFromName (String path , String name) throws ParserXMLException
    {
        try
        {
            File file = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);

            int nCard = Integer.parseInt(document.getElementsByTagName("numberOfCards").item(0).getTextContent());

            for (int i = 0; i < nCard;i++)
            {
                String s = "tool" + (i + 1);
                int numtool = Integer.parseInt(document.getElementsByTagName(s).item(0).getAttributes().getNamedItem("num").getTextContent());
                String nm = document.getElementsByTagName("name").item(numtool-1).getTextContent();
                if (nm.equals(name))
                    return numtool;//n/d andrà sostiutito dal path immagine
            }
            throw new ParserXMLException("Name: " + name + " Not founded in path: " + path);
        }
        catch (Exception ex){
            throw new ParserXMLException("Impossible to read file: " + path);
        }
    }

    /**
     *
     * @param path XML file location
     * @param name tool's name
     * @return tool's effect
     * @throws ParserXMLException
     */
    public  static String getTooldescriptionFromName(String path , String name) throws ParserXMLException{
        try{
            File file = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);

            int nCard = Integer.parseInt(document.getElementsByTagName("numberOfCards").item(0).getTextContent());

            for (int i = 0; i < nCard;i++)
            {
                String s = "tool" + (i + 1);
                int numtool = Integer.parseInt(document.getElementsByTagName(s).item(0).getAttributes().getNamedItem("num").getTextContent());
                String nm = document.getElementsByTagName("name").item(numtool-1).getTextContent();
                String desTool = document.getElementsByTagName("effect").item(i).getTextContent();
                if (nm.equals(name))
                    return desTool;//n/d andrà sostiutito dal path immagine
            }
            throw new ParserXMLException("Name: " + name + " Not founded in path: " + path);
        }
        catch (Exception ex){
            throw new ParserXMLException("Impossible to read file: " + path);
        }
    }

}
