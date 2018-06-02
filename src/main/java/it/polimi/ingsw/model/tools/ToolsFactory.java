package it.polimi.ingsw.model.tools;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class ToolsFactory {
    public static Tools getTools(String path) {

        try {

            File xmlTool = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document tool = documentBuilder.parse(xmlTool);

            String s = "tool" + 6;
            int numtool = Integer.parseInt(tool.getElementsByTagName(s).item(0).getAttributes().getNamedItem("num").getTextContent());
            String name = tool.getElementsByTagName("name").item(numtool-1).getTextContent();
            String effect = tool.getElementsByTagName("class").item(numtool-1).getTextContent();

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

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    static public void main(String[] args){
        Tools t=getTools("resources/tools.xml");
        
    }
}
