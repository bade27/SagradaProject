package it.polimi.ingsw.utilities;

import it.polimi.ingsw.exceptions.ParserXMLException;
import java.util.ArrayList;

public class UsersEntry
{
    ArrayList<Users> userList = new ArrayList<>();

    public UsersEntry () throws ParserXMLException
    {
        ArrayList <String> nameList = ParserXML.readUserNames(FileLocator.getUsersDatabasePath());
        for (int i = 0; i < nameList.size() ; i++)
            userList.add(new Users(nameList.get(i)));
    }

    public boolean loginCheck (String user) throws ParserXMLException
    {
        for (int i = 0; i < userList.size() ; i++)
        {
            if (userList.get(i).getUsername().equals(user))
            {
                if (userList.get(i).isInGame())
                    return false;
                userList.get(i).setInGame();
                return true;
            }
        }
        Users u = new Users(user);
        u.addPlayerToDB();
        userList.add(new Users(user));
        return true;
    }

    private class Users
    {
        String username;
        boolean inGame;

        private Users(String username) {
            this.username = username;
            inGame = false;
        }

        private boolean isInGame() {
            return inGame;
        }

        private void setInGame() {
            this.inGame = false;
        }

        private String getUsername() {
            return username;
        }

        private void addPlayerToDB () throws ParserXMLException
        {
            ParserXML.addUserNames(FileLocator.getUsersDatabasePath(),username);
        }
    }
}
