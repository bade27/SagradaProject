package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.NotEnoughDiceException;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DiceBag {

    private final int DICE_PER_COLOR = 18;
    private final int N_OF_DICE = 90;
    private List<Dice> dice;

    public DiceBag() {
        dice = new ArrayList<>();
        init();
        //checkConstructor();
    }

    /**
     * inizializza i dadi e li inserisce nel sacchetto (aka dice)
     * N.B.: l'assenza di break nei vari branch è intenzionale, in quanto se ho esaurito i dadi di
     * un colore passo a quello successivo
     */
    private void init() {
        int num_of_red, num_of_green,
                num_of_blue, num_of_yellow,
                num_of_magenta;
        num_of_red = num_of_green  = num_of_blue
                = num_of_yellow = num_of_magenta = DICE_PER_COLOR;
        Random value = new Random();
        Random color = new Random();
        Color col = null;
        for(int k = 0; k < N_OF_DICE; k++) {
            repeat_switch:
            switch (color.nextInt(5)) {
                case 0:
                    if(num_of_red > 0) {
                        col = Color.red;
                        num_of_red--;
                        break;
                    }
                    break repeat_switch;
                case 1:
                    if(num_of_green > 0) {
                        col = Color.green;
                        num_of_green--;
                        break;
                    }
                    break repeat_switch;
                case 2:
                    if(num_of_blue > 0) {
                        col = Color.blue;
                        num_of_blue--;
                        break;
                    }
                    break repeat_switch;
                case 3:
                    if(num_of_yellow > 0) {
                        col = Color.yellow;
                        num_of_yellow--;
                        break;
                    }
                    break repeat_switch;
                case 4:
                    if(num_of_magenta > 0) {
                        col = Color.magenta;
                        num_of_magenta--;
                        break;
                    }
                    break repeat_switch;
                default:
                    break;
            }
            Dice d = new Dice(value.nextInt(6) + 1, col);
            dice.add(d);
        }
    }

    //medoti per il check del rep

    /**
     *
     * @param col
     * @return *numero di elementi del colore scelto*
     */
    private int numberOfDicePerColor(Color col) {
        return (int)dice.stream()
                .map(d -> d.getColor())
                .filter(c -> c.equals(col))
                .count();
    }

    /**
     * controlla che la somma dei dadi per colore sia uguale
     * al numero di dadi presenti nel sacchetto
     */
    private void checkNumDice() {
        assert numberOfDicePerColor(Color.red) + numberOfDicePerColor(Color.green)
                + numberOfDicePerColor(Color.blue) + numberOfDicePerColor(Color.yellow)
                + numberOfDicePerColor(Color.magenta) == dice.size();
    }

    /**
     * controllo del rep dopo il costruttore
     */
    private void checkConstructor() {
        assert dice.size() == N_OF_DICE;
        assert numberOfDicePerColor(Color.red) == DICE_PER_COLOR;
        assert numberOfDicePerColor(Color.green) == DICE_PER_COLOR;
        assert numberOfDicePerColor(Color.blue) == DICE_PER_COLOR;
        assert numberOfDicePerColor(Color.yellow) == DICE_PER_COLOR;
        assert numberOfDicePerColor(Color.magenta) == DICE_PER_COLOR;
    }

    //public methods

    /**
     *
     * @param n
     * @return *una lista di n dadi estratti dal sacchetto*
     * (i dadi estratti vengono rimossi)
     */
    public ArrayList<Dice> pickDices(int n) {
        if(n > dice.size())
            throw new NotEnoughDiceException("non sono rimasti abbastanza dadi");
        ArrayList<Dice> choosenDice = new ArrayList<>();
        while(n > 0) {
            choosenDice.add(dice.get(0));
            dice.remove(0);
            n--;
        }
        //checkNumDice();
        return choosenDice;
    }

    /**
     *
     * @return *il numero di dadi rimasti*
     */
    public int getRemainingDice() {
        return dice.size();
    }

    /**
     *
     * @return *la rappresentazione in formato String del contenuto del sacchetto*
     */
    @Override
    public String toString() {
        String str =  "DiceBag{";
        for(int i = 0; i < dice.size(); i++) {
            str += dice.get(i).toString();
            if (i != (dice.size() - 1))
                str += ", ";
        }
        str += '}';
        return str;
    }
}
