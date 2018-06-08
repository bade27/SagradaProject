package it.polimi.ingsw.client;

import it.polimi.ingsw.remoteInterface.Coordinates;
import it.polimi.ingsw.remoteInterface.Pair;
import it.polimi.ingsw.remoteInterface.ServerRemoteInterface;

import java.rmi.RemoteException;

public class ToolAction
{
    private static int idTool;

    private static Pair dadieraDie;
    private static Pair traceDie;

    private static Coordinates fstDieStartPosition;
    private static Coordinates fstDiePlacePosition;
    private static Coordinates sndDieStartPosition;
    private static Coordinates sndDiePlacePosition;

    private static String instruction;

    public static void clearTool ()
    {
        idTool = -1;
        dadieraDie = null;

        fstDiePlacePosition = null;
        sndDiePlacePosition = null;
        fstDieStartPosition = null;
        sndDieStartPosition = null;
    }

    public static String askToolPermission (ServerRemoteInterface comm, int id) throws RemoteException
    {
        idTool = id;
        return comm.askToolPermission(idTool);
    }

    public static String performTool (ServerRemoteInterface comm) throws RemoteException
    {
        if (idTool == -1)
            return "Tool not selected";
        else {
            try
            {
                if (idTool == 1 || idTool == 6 || idTool == 7|| idTool == 10)
                    return comm.useTool(dadieraDie,instruction);
                if (idTool == 2 || idTool == 3)
                    return comm.useTool(fstDieStartPosition, fstDiePlacePosition);
                if (idTool == 4)
                    return comm.useTool(fstDieStartPosition,fstDiePlacePosition,sndDieStartPosition,sndDiePlacePosition);
                if (idTool == 5)
                    return comm.useTool(dadieraDie,traceDie,1);//Uno fisso momentaneo
                if (idTool == 8)
                    return comm.useTool();
                if (idTool == 9)
                    return comm.useTool(dadieraDie,fstDieStartPosition);

            }catch (Exception e){//Poi da togliere una volta tolto toolMove
                return "Invalid Input";
            }

        }
        return "Tool not yet implemented";
    }


    //3)use tool unico per tool 2-3-4? ci stuh
    //5)tool 5 non funzionante
    public static void setPosition (Coordinates coord)
    {
        if (fstDieStartPosition == null)
            fstDieStartPosition = coord;
        else if (fstDiePlacePosition == null)
            fstDiePlacePosition = coord;
        else if (sndDieStartPosition == null)
            sndDieStartPosition = coord;
        else if (sndDiePlacePosition == null)
            sndDiePlacePosition = coord;

    }

    public static void setInstruction(String instruction) {
        ToolAction.instruction = instruction;
    }

    public static void setDadieraPair(Pair dadieraDie) {
        ToolAction.dadieraDie = dadieraDie;
    }

    public static void setTracePair(Pair traceDie) {
        ToolAction.traceDie = traceDie;
    }
}
