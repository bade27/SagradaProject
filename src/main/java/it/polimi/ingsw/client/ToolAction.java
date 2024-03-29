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
    private static Pair genericDie;

    private static Coordinates fstDieStartPosition;
    private static Coordinates fstDiePlacePosition;
    private static Coordinates sndDieStartPosition;
    private static Coordinates sndDiePlacePosition;

    private static String instruction;

    private static int tracePosition;

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

    public static String performTool (ServerRemoteInterface comm) throws RemoteException {
        if (idTool == -1)
            return "Tool not selected";
        else {

                if (idTool == 1 || idTool == 6 || idTool == 10)
                    return comm.useTool(dadieraDie,instruction);
                if (idTool == 2 || idTool == 3)
                    return comm.useTool(fstDieStartPosition, fstDiePlacePosition);
                if (idTool == 4 || idTool == 12)
                    return comm.useTool(traceDie ,fstDieStartPosition,fstDiePlacePosition,sndDieStartPosition,sndDiePlacePosition);
                if (idTool == 5)
                    return comm.useTool(dadieraDie,traceDie,tracePosition);
                if (idTool == 8 || idTool == 7)
                    return comm.useTool();
                if (idTool == 9)
                    return comm.useTool(dadieraDie,fstDieStartPosition);
                if(idTool == 11)
                    return comm.useTool(dadieraDie, instruction);


        }
        return "Tool not yet implemented";
    }


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

    public static void setTracePosition(int tracePosition){ ToolAction.tracePosition=tracePosition; }

    public static void setDadieraPair(Pair dadieraDie) {

        //the first branch guarantees the correct functionality of tool 11
        if(ToolAction.dadieraDie != null && dadieraDie != null && ToolAction.dadieraDie.getValue() == null && ToolAction.dadieraDie.getColor() != null) {
            ToolAction.dadieraDie.setValue(dadieraDie.getValue());
        } else ToolAction.dadieraDie = dadieraDie;
    }

    public static void setTracePair(Pair traceDie) {
        ToolAction.traceDie = traceDie;
    }

}
