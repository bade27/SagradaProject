package it.polimi.ingsw.model.tools;

import it.polimi.ingsw.exceptions.ParserXMLException;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class ToolsFactory
{
    private static final String path = "resources/carte/tools/tools.xml";
    public static Tools getTools(String name) throws ParserXMLException
    {
        try {

            File xmlTool = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document tool = documentBuilder.parse(xmlTool);

            int nCard = Integer.parseInt(tool.getElementsByTagName("numberOfCards").item(0).getTextContent());

            for (int i = 0 ; i < nCard ; i++)
            {
                String s = "tool" + (i+1);
                int numtool = Integer.parseInt(tool.getElementsByTagName(s).item(0).getAttributes().getNamedItem("num").getTextContent());
                String nm = tool.getElementsByTagName("name").item(numtool-1).getTextContent();
                String effect = tool.getElementsByTagName("class").item(numtool-1).getTextContent();

                if (name.equals(nm))
                {
                    switch (effect) {
                        case "SetDiceValue":
                            return new SetValueTool(s,name);
                        case "MoveGridGrid":
                            return new MoveGridGridTool(s,name);
                        case "MoveDadieraTrace":
                            return new MoveDadieraTraceTool(s,name);
                        //...
                        default:
                    }
                }

            }
        } catch (Exception e) {
            throw new ParserXMLException("Impossible to read xml tools");
        }
        return null;
    }
}
