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
    
        try
        {
            File file = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            

            //ora è un po' più efficiente anche qui
            int id = Integer.parseInt(document.getElementsByTagName("id").item(0).getTextContent());
            String name = document.getElementsByTagName("name").item(0).getTextContent();
            int difficult = Integer.parseInt(document.getElementsByTagName("difficulty").item(0).getTextContent());

            System.out.println("---" + id + " " + name + " " + difficult + "---");
            
            //sono due liste che contengono l'elenco0 dei tag del file (hanno stessa lunghezza)
            NodeList values = document.getElementsByTagName("value");
            NodeList colors = document.getElementsByTagName("color");
            //NodeList imgs = document.getElementsByTagName("img_source");
            
            //il parametro k mi serve come indice delle due liste valori e colori
            for(int i = 0, k = 0; i < rows; i++) 
            	for(int j = 0; j < cols; j++) {
            		int currentValue = Integer.parseInt(values.item(k).getTextContent());
                    String currentColor = colors.item(k).getTextContent();
                    board[i][j] = new Cell(new Dice(currentValue, currentColor));
                    k++;
            	}
            		


        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
}
