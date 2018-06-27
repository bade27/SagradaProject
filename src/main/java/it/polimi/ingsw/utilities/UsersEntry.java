package it.polimi.ingsw.utilities;

import it.polimi.ingsw.exceptions.ParserXMLException;
import java.util.ArrayList;

public class UsersEntry
{
    private ArrayList<Users> userList = new ArrayList<>();

    public UsersEntry () throws ParserXMLException
    {
        ArrayList <String> nameList = ParserXML.readUserNames(FileLocator.getUsersDatabasePath());
        for (int i = 0; i < nameList.size() ; i++)
            userList.add(new Users(nameList.get(i)));
    }

    public synchronized boolean loginCheck (String user) throws ParserXMLException
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
        u.setInGame();
        userList.add(u);
        return true;
    }

    public synchronized boolean setUserGameStatus(String user, boolean status) {
        boolean exit = false;
        for(int i = 0; i < userList.size(); i++)
        {
            if(userList.get(i).username.equals(user))
            {
                if(status)
                    userList.get(i).setInGame();
                else
                {
                    System.out.println("Set not " + userList.get(i).getUsername());
                    userList.get(i).setNotInGame();
                }
            }
//            exit = true; ???why bade???
  //          break;
        }
        return exit;
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
            this.inGame = true;
        }

        private void setNotInGame() {
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
