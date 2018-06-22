package it.polimi.ingsw.utilities;

public class FileLocator
{
    private static final String windowListPath = "resources/vetrate/xml/windows_list.xml";
    private static final String privateObjectivesListPath = "resources/carte/obbiettivi/obbiettiviPrivati/xml/privateObjectiveList.xml";
    private static final String publicObjectivesListPath = "resources/carte/obbiettivi/obbiettiviPubblici/xml/publicObjectiveList.xml";
    private static final String toolsListPath = "resources/carte/tools/tools.xml";
    private static final String clientSettingsPath = "resources/client_settings.xml";
    private static final String serverSettingsPath = "resources/server_settings.xml";
    private static final String usersDatabasePath = "resources/username_database.xml";
    private static final String gameSettingsPath = "resources/game_settings.xml";

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

    public static String getServerSettingsPath() {
        return serverSettingsPath;
    }

    public static String getClientSettingsPath() {
        return clientSettingsPath;
    }

    public static String getUsersDatabasePath() {
        return usersDatabasePath;
    }

    public static String getGameSettingsPath() {
        return gameSettingsPath;
    }
}
