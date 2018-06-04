package it.polimi.ingsw.view;

import javafx.scene.control.Button;

public class GraphButton extends Button
{
    private String nameTool,imgPath;
    private int idTool;
    public GraphButton (String n , int i , String iP )
    {
        nameTool = n;
        idTool = i;
        imgPath = iP;
    }

    public String getNameTool() {
        return nameTool;
    }

    public String getImgPath() {
        return imgPath;
    }

    public int getIdTool() {
        return idTool;
    }
}
