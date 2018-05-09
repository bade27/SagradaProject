package Objectives;

import Objectives.Private.PrivateObjective;
import Objectives.Public.*;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

public class ObjectivesFactory {

    public static PrivateObjective getPrivateObjective(String path) {

        try {

            File pathF = new File(path);
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document card = documentBuilder.parse(pathF);

            String name = card.getElementsByTagName("name").item(0).getTextContent();
            String target = card.getElementsByTagName("target").item(0).getTextContent();
            String description = card.getElementsByTagName("description").item(0).getTextContent();

            return new PrivateObjective(name, target, description);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public static PublicObjective getPublicObjective(String path) {

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
                    return new PublicObjective(name, description, value, new ColRowScore(patterns, tag));
                case "col":
                    return new PublicObjective(name, description, value, new ColRowScore(patterns, tag));
                case "pair":
                    return new PublicObjective(name, description, value, new PairScore(tag));
                case "var":
                    return new PublicObjective(name, description, value, new VarietyScore(tag));
                case "diag":
                    return new PublicObjective(name, description, value, new DiagonalScore(tag));
                default:
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

}
