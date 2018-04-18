package Obbiettivi;

import Obbiettivi.Privati.ObbiettivoPrivato;
import Obbiettivi.Pubblici.*;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class ObbiettiviFactory {

    public static ObbiettivoPrivato getObbiettivoPrivato(String path) {

        try {

            File pathF = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document card = documentBuilder.parse(pathF);

            String name = card.getElementsByTagName("name").item(0).getTextContent();
            String target = card.getElementsByTagName("target").item(0).getTextContent();
            String description = card.getElementsByTagName("description").item(0).getTextContent();

            return new ObbiettivoPrivato(name, target, description);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public static ObbiettivoPubblico getObbiettivoPubblico(String path) {

        try {

            File pathF = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document card = documentBuilder.parse(pathF);

            String name = card.getElementsByTagName("name").item(0).getTextContent();
            int value = Integer.parseInt(card.getElementsByTagName("value").item(0).getTextContent());
            String patterns = card.getElementsByTagName("pattern").item(0).getAttributes()
                    .getNamedItem("p").getNodeValue();
            String tag = card.getElementsByTagName("tag").item(0).getTextContent();
            String description = card.getElementsByTagName("description").item(0).getTextContent();

            switch (patterns) {
                case "row":
                    return new ObbiettivoPubblico(name, description, value, new PunteggioRiga(tag));
                case "col":
                    return new ObbiettivoPubblico(name, description, value, new PunteggioColonna(tag));
                case "pair":
                    return new ObbiettivoPubblico(name, description, value, new PunteggioCoppia(tag));
                case "var":
                    return new ObbiettivoPubblico(name, description, value, new PunteggioVarietà(tag));
                case "diag":
                    return new ObbiettivoPubblico(name, description, value, new PunteggioDiagonale(tag));
                default:
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

}
