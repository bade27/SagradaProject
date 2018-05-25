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
}
