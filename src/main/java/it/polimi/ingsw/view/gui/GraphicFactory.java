package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.exceptions.ParserXMLException;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class GraphicFactory {

    public static GraphButton getToolButtonFromName (String path , String name) throws ParserXMLException
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
                String img = document.getElementsByTagName("image").item(numtool-1).getTextContent();
                //System.out.println(img);
                if (nm.equals(name))
                    return new GraphButton(nm,numtool,img);
            }
            throw new ParserXMLException("Name: " + name + " Not founded in path: " + path);
        }
        catch (Exception ex){
            throw new ParserXMLException("Impossible to read file: " + path);
        }
    }

}
