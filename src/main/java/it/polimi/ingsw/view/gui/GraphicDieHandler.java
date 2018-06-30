package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Pair;
import javafx.scene.image.Image;

import java.util.HashMap;
import java.util.Map;

public class GraphicDieHandler
{
    private static Map<Integer, Image> placementNumberMap = new HashMap<Integer, Image>();
    private static Map<ColorEnum, Image> placementColorMap = new HashMap<ColorEnum, Image>();

    private static Map<ColorEnum,Map<Integer,Image>> dieMap = new HashMap<ColorEnum,Map<Integer,Image>>();

    public static void loadDieImages ()
    {
        placementNumberMap.put(1 , new Image("file:resources/vetrate/Images/Placement/quad_1.jpg"));
        placementNumberMap.put(2 , new Image("file:resources/vetrate/Images/Placement/quad_2.jpg"));
        placementNumberMap.put(3 , new Image("file:resources/vetrate/Images/Placement/quad_3.jpg"));
        placementNumberMap.put(4 , new Image("file:resources/vetrate/Images/Placement/quad_4.jpg"));
        placementNumberMap.put(5 , new Image("file:resources/vetrate/Images/Placement/quad_5.jpg"));
        placementNumberMap.put(6 , new Image("file:resources/vetrate/Images/Placement/quad_6.jpg"));

        placementColorMap.put(ColorEnum.RED , new Image("file:resources/vetrate/Images/Placement/quad_rosso.jpg"));
        placementColorMap.put(ColorEnum.GREEN , new Image("file:resources/vetrate/Images/Placement/quad_verde.jpg"));
        placementColorMap.put(ColorEnum.BLUE , new Image("file:resources/vetrate/Images/Placement/quad_azzurro.jpg"));
        placementColorMap.put(ColorEnum.YELLOW , new Image("file:resources/vetrate/Images/Placement/quad_giallo.jpg"));
        placementColorMap.put(ColorEnum.PURPLE , new Image("file:resources/vetrate/Images/Placement/quad_viola.jpg"));


        Map<Integer,Image> redDie = new HashMap <Integer,Image>();
        redDie.put(1,new Image("file:resources/vetrate/Images/Die/1_rosso.jpg"));
        redDie.put(2,new Image("file:resources/vetrate/Images/Die/2_rosso.jpg"));
        redDie.put(3,new Image("file:resources/vetrate/Images/Die/3_rosso.jpg"));
        redDie.put(4,new Image("file:resources/vetrate/Images/Die/4_rosso.jpg"));
        redDie.put(5,new Image("file:resources/vetrate/Images/Die/5_rosso.jpg"));
        redDie.put(6,new Image("file:resources/vetrate/Images/Die/6_rosso.jpg"));
        dieMap.put(ColorEnum.RED,redDie);

        Map<Integer,Image> yellowDie = new HashMap <Integer,Image>();
        yellowDie.put(1,new Image("file:resources/vetrate/Images/Die/1_giallo.jpg"));
        yellowDie.put(2,new Image("file:resources/vetrate/Images/Die/2_giallo.jpg"));
        yellowDie.put(3,new Image("file:resources/vetrate/Images/Die/3_giallo.jpg"));
        yellowDie.put(4,new Image("file:resources/vetrate/Images/Die/4_giallo.jpg"));
        yellowDie.put(5,new Image("file:resources/vetrate/Images/Die/5_giallo.jpg"));
        yellowDie.put(6,new Image("file:resources/vetrate/Images/Die/6_giallo.jpg"));
        dieMap.put(ColorEnum.YELLOW,yellowDie);

        Map<Integer,Image> greenDie = new HashMap <Integer,Image>();
        greenDie.put(1,new Image("file:resources/vetrate/Images/Die/1_verde.jpg"));
        greenDie.put(2,new Image("file:resources/vetrate/Images/Die/2_verde.jpg"));
        greenDie.put(3,new Image("file:resources/vetrate/Images/Die/3_verde.jpg"));
        greenDie.put(4,new Image("file:resources/vetrate/Images/Die/4_verde.jpg"));
        greenDie.put(5,new Image("file:resources/vetrate/Images/Die/5_verde.jpg"));
        greenDie.put(6,new Image("file:resources/vetrate/Images/Die/6_verde.jpg"));
        dieMap.put(ColorEnum.GREEN,greenDie);

        Map<Integer,Image> purpleDie = new HashMap <Integer,Image>();
        purpleDie.put(1,new Image("file:resources/vetrate/Images/Die/1_rosa.jpg"));
        purpleDie.put(2,new Image("file:resources/vetrate/Images/Die/2_rosa.jpg"));
        purpleDie.put(3,new Image("file:resources/vetrate/Images/Die/3_rosa.jpg"));
        purpleDie.put(4,new Image("file:resources/vetrate/Images/Die/4_rosa.jpg"));
        purpleDie.put(5,new Image("file:resources/vetrate/Images/Die/5_rosa.jpg"));
        purpleDie.put(6,new Image("file:resources/vetrate/Images/Die/6_rosa.jpg"));
        dieMap.put(ColorEnum.PURPLE,purpleDie);

        Map<Integer,Image> blueDie = new HashMap <Integer,Image>();
        blueDie.put(1,new Image("file:resources/vetrate/Images/Die/1_blu.jpg"));
        blueDie.put(2,new Image("file:resources/vetrate/Images/Die/2_blu.jpg"));
        blueDie.put(3,new Image("file:resources/vetrate/Images/Die/3_blu.jpg"));
        blueDie.put(4,new Image("file:resources/vetrate/Images/Die/4_blu.jpg"));
        blueDie.put(5,new Image("file:resources/vetrate/Images/Die/5_blu.jpg"));
        blueDie.put(6,new Image("file:resources/vetrate/Images/Die/6_blu.jpg"));
        dieMap.put(ColorEnum.BLUE,blueDie);

    }

    public static Image getImageDie (Pair p)
    {
        if (p.getValue() != 0 && p.getColor() == null)
            return placementNumberMap.get(p.getValue());
        else if (p.getValue() == 0 && p.getColor() != null)
            return placementColorMap.get(p.getColor());
        else if (p.getValue() != 0 && p.getColor() != null)
            return  dieMap.get(p.getColor()).get(p.getValue());

        return new Image("file:resources/vetrate/Images/Placement/quad_bianco.jpg");
    }
}
