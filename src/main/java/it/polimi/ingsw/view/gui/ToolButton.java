package it.polimi.ingsw.view.gui;

import javafx.scene.control.Button;


public class ToolButton extends Button
{
    private String nameTool,imgPath;
    private int idTool;
    public ToolButton(String n , int i , String iP )
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
