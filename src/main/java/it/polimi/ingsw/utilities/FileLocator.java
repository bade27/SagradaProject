package it.polimi.ingsw.utilities;

public class FileLocator
{
    private static final String windowListPath = "resources/vetrate/xml/windows_list.xml";
    private static final String privateObjectivesListPath = "resources/carte/obbiettivi/obbiettiviPrivati/xml/privateObjectiveList.xml";
    private static final String publicObjectivesListPath = "resources/carte/obbiettivi/obbiettiviPubblici/xml/publicObjectiveList.xml";
    private static final String toolsListPath = "resources/carte/tools/tools.xml";


    public static String getWindowListPath ()
    {
        return windowListPath;
    }

    public static String getPrivateObjectivesListPath ()
    {
        return privateObjectivesListPath;
    }

    public static String getPublicObjectivesListPath ()
    {
        return publicObjectivesListPath;
    }

    public static String getToolsListPath ()
    {
        return toolsListPath;
    }
}
