package it.polimi.ingsw.model;

public enum ColorEnum {

    RED("Red"),
    GREEN("Green"),
    YELLOW("Yellow"),
    BLUE("Blue"),
    PURPLE("Purple"),
    WHITE("White");

    private String color;

    ColorEnum(String s) {
        color = s;
    }

    public static ColorEnum getColor (String s)
    {
        if (s.equals("RED"))
            return RED;
        else if (s.equals("GREEN"))
            return GREEN;
        else if (s.equals("YELLOW"))
            return YELLOW;
        else if (s.equals("BLUE"))
            return BLUE;
        else if (s.equals("PURPLE"))
            return PURPLE;
        else if (s.equals("WHITE"))
            return WHITE;
        else
            return null;
    }

}
