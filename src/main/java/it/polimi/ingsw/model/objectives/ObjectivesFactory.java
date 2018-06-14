package it.polimi.ingsw.model.objectives;

import it.polimi.ingsw.exceptions.ModelException;
import it.polimi.ingsw.model.objectives.Private.PrivateObjective;
import it.polimi.ingsw.model.objectives.Public.*;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ObjectivesFactory {

    private static DocumentBuilderFactory documentBuilderFactory;
    private static File pathF;
    private static DocumentBuilder documentBuilder;
    private static Document card;
    private static String name;
    private static String target;
    private static String description;
    private static int value;
    private static String patterns;
    private static String tag;
    private static Map<String, Score> map;

    private static void initMap() {
        map = new HashMap<>();
        map.put("row", new ColRowScore());
        map.put("col", new ColRowScore());
        map.put("pair", new PairScore());
        map.put("var", new VarietyScore());
        map.put("diag", new DiagonalScore());
    }

    public static PrivateObjective getPrivateObjective(String path) throws ModelException
    {

        try {

            pathF = new File(path);
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            card = documentBuilder.parse(pathF);

            name = card.getElementsByTagName("name").item(0).getTextContent();
            target = card.getElementsByTagName("target").item(0).getTextContent();
            description = card.getElementsByTagName("description").item(0).getTextContent();

            return new PrivateObjective(name, path, target, description);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ModelException("Impossible to read Private Objectives XML");
        }

    }

    public static PublicObjective getPublicObjective(String path) throws ModelException {

        initMap();

        PublicObjective publicObjective;
        try {

            pathF = new File(path);
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            card = documentBuilder.parse(pathF);

            name = card.getElementsByTagName("name").item(0).getTextContent();
            value = Integer.parseInt(card.getElementsByTagName("value").item(0).getTextContent());
            patterns = card.getElementsByTagName("pattern").item(0).getAttributes()
                    .getNamedItem("p").getNodeValue();
            tag = card.getElementsByTagName("tag").item(0).getTextContent();
            description = card.getElementsByTagName("description").item(0).getTextContent();

            Score score = map.get(patterns);
            score.setPattern(patterns);
            score.setTag(tag);
            publicObjective = new PublicObjective(name, path, description, value, score);

        } catch (Exception e) {
            e.printStackTrace();
            throw new ModelException("Impossible to read Public Objectives XML");
        }
        return publicObjective;
    }

}
