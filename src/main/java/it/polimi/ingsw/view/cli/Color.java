package it.polimi.ingsw.view.cli;

public enum Color {
    ANSI_NOCOLOR("\u001B[0m"),
    ANSI_RED("\u001B[31m"),
    ANSI_GREEN("\u001B[32m"),
    ANSI_YELLOW("\u001B[33m"),
    ANSI_BLUE("\u001B[34m"),
    ANSI_PURPLE("\u001B[35m"),
    ANSI_BLACK("\u001B[90m"),
    ANSI_BACK_RED("\u001B[41m"),
    ANSI_BACK_GREEN("\u001B[42m"),
    ANSI_BACK_YELLOW("\u001B[43m"),
    ANSI_BACK_BLUE("\u001B[44m"),
    ANSI_BACK_PURPLE("\u001B[45m"),
    ANSI_BACK_WHITE("\u001B[47m");


    //static final String RESET ="\u001B[0m";

    private String escape;

    Color(String escape){
        this.escape=escape;
    }

    public String escape(){
        return escape;
    }
}
