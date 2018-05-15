package it.polimi.ingsw.model.tools;

public abstract class Tools {

    int price;

    /**
     * If it is the first time i use the tool set tool's price at 2
     */
    abstract void setPrice();

    /**
     * return the price of the tool
     * @return *price of the tool*
     */
    abstract public int getPrice();


}
