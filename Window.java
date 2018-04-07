package Test;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class Window
{
    private int difficult;
    private int id;
    private Cell board [][];
    private String name;
    private final int rows = 4;
    private final int cols = 5;

    public Window ()
    {
        board =new Cell [rows][cols];
        difficult = 0;
        name = "";
        id = 0;
        for (int i =0;i < rows ; i++)
            for (int j = 0;j < cols;j++)
                board[i][j] = new Cell ();
    }

    public Window (String path)
    {
        board =new Cell [rows][cols];
        readXML(path);
    }

    private void readXML (String path)
    {
        /*List<String> id = new ArrayList<>(),
                value = new ArrayList<>(),
                color = new ArrayList<>();*/
        try
        {
            File file = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);

            NodeList nodeList = document.getElementsByTagName("*");

            //l'elemento 0 è il root, che non ci interessa
            id =   Integer.parseInt(nodeList.item(1).getTextContent());
            name = nodeList.item(2).getTextContent();
            difficult = Integer.parseInt(nodeList.item(3).getTextContent());

            System.out.println("---" + id + " " + name + " " + difficult + "---");

            ///Servirebbe il codice Da qui in poi

            /*for(int i = 4; i < nodeList.getLength(); i++) {
                Node currentNode = nodeList.item(i);
                switch(currentNode.getNodeName()) {
                    case "grid":
                        break;
                    case "cell":
                        id.add(String.valueOf(currentNode.getAttributes().getNamedItem("number").getNodeValue()));
                        break;
                    case "value":
                        value.add(String.valueOf(currentNode.getTextContent()));
                        break;
                    case "color":
                        color.add(String.valueOf(currentNode.getTextContent()));
                        break;
                    default:
                        break;
                }

            }

            for(int i = 0, v = 0; i < id.size(); i++) {
                System.out.println("cella numero: " + id.get(i) +
                        "\nvalore:\t" + value.get(i) +
                        "\ncolore:\t" + color.get(i) + "\n");
            }*/


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
