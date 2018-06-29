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

    }

    public static Image getImageDie (Pair p)
    {
        if (p.getValue() != 0 && p.getColor() == null)
            return placementNumberMap.get(p.getValue());
        else if (p.getValue() == 0 && p.getColor() != null)
            return placementColorMap.get(p.getColor());
        else if (p.getValue() != 0 && p.getColor() != null)
            return  new Image("file:resources/vetrate/Images/Placement/quad_bianco.jpg");

        return new Image("file:resources/vetrate/Images/Placement/quad_bianco.jpg");
    }
}
