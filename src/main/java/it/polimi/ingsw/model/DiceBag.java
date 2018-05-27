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
     * initialize dice and fill dicebag (aka dice)
     * N.B.: the absence of 'break' in switch-case code is intentional because if I created all dice for a color,
     * I pass to the next one.
     */
    private void init() {
        int num_of_red, num_of_green,
                num_of_blue, num_of_yellow,
                num_of_magenta;
        num_of_red = num_of_green  = num_of_blue
                = num_of_yellow = num_of_magenta = DICE_PER_COLOR;
        Random value = new Random();
        Random color = new Random();
        ColorEnum col = null;
        for(int k = 0; k < N_OF_DICE; k++) {
            repeat_switch:
            switch (color.nextInt(5)) {
                case 0:
                    if(num_of_red > 0) {
                        col = ColorEnum.RED;
                        num_of_red--;
                        break;
                    }
                    break repeat_switch;
                case 1:
                    if(num_of_green > 0) {
                        col = ColorEnum.GREEN;
                        num_of_green--;
                        break;
                    }
                    break repeat_switch;
                case 2:
                    if(num_of_blue > 0) {
                        col = ColorEnum.BLUE;
                        num_of_blue--;
                        break;
                    }
                    break repeat_switch;
                case 3:
                    if(num_of_yellow > 0) {
                        col = ColorEnum.YELLOW;
                        num_of_yellow--;
                        break;
                    }
                    break repeat_switch;
                case 4:
                    if(num_of_magenta > 0) {
                        col = ColorEnum.PURPLE;
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

    /**
     * @param col color to check
     * @return number of dice per color created
     */
    private int numberOfDicePerColor(Color col) {
        return (int)dice.stream()
                .map(d -> d.getColor())
                .filter(c -> c.equals(col))
                .count();
    }

    /**
     *  check if sum of dice per color is equals to sum of dice inside dicebag
     */
    private void checkNumDice() {
        assert numberOfDicePerColor(Color.red) + numberOfDicePerColor(Color.green)
                + numberOfDicePerColor(Color.blue) + numberOfDicePerColor(Color.yellow)
                + numberOfDicePerColor(Color.magenta) == dice.size();
    }

    /**
     * check of rep after constructor
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
     * @return list of dice extract from dicebag
     * (dice extract will be removed form dicebag)
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
     * @return number of dice remaining
     */
    public int getRemainingDice() {
        return dice.size();
    }

    /**
     *
     * @return Dicebag on string representation
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
