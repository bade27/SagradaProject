package it.polimi.ingsw.client;

import it.polimi.ingsw.remoteInterface.Coordinates;
import it.polimi.ingsw.remoteInterface.Pair;
import it.polimi.ingsw.remoteInterface.ServerRemoteInterface;

import java.rmi.RemoteException;

public class MoveAction {
    private static Coordinates coord;
    private static Pair pair;


    public static void setCoord(Coordinates c) {
        MoveAction.coord = c;
    }

    public static void setPair(Pair pair) {
        MoveAction.pair = pair;
    }

    public static void clearMove ()
    {
        MoveAction.coord = null;
        MoveAction.pair = null;
    }

    public static boolean canMove ()
    {
        if (MoveAction.coord != null && MoveAction.pair != null)
            return true;
        return false;
    }

    public static String perfromMove (ServerRemoteInterface comm) throws RemoteException
    {
        return comm.makeMove(MoveAction.coord,MoveAction.pair);
    }
}
