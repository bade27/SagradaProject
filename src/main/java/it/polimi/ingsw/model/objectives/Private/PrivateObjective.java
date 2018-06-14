package it.polimi.ingsw.model.objectives.Private;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Window;

import java.util.Optional;
import java.util.stream.Stream;

public class PrivateObjective {

    private String name;
    private String path;
    private String description;
    private ColorEnum color;

    public PrivateObjective(String name, String path, String color, String description) {

        this.name = name;
        this.path = path;
        this.description = description;
        Optional<ColorEnum> result = Stream.of(ColorEnum.values())
                .filter(e -> e.toString().toLowerCase().equals(color))
                .findAny();
        result.ifPresent(c -> this.color = c);

    }

    /**
     * Calculates and returns points about window passed
     * @param window window passed
     * @return total points of board (=numero dei dadi del color obbiettivo posizionati)*
     */
    public int getScore (Window window) {
        Cell[][] grid = window.getGrid();
        ColorEnum current_color;
        int total = 0;
        for(int i = 0; i < grid.length; i++)
            for(int j = 0; j < grid[0].length; j++) {
                Cell current_cell = grid[i][j];
                if(current_cell.getFrontDice() != null) {
                    current_color = current_cell.getFrontDice().getColor();
                    if (current_color.equals(color))
                        total += current_cell.getFrontDice().getValue();
                }
            }
        return total;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public String getDescription() {
        return description;
    }

    public ColorEnum getColor() {
        return color;
    }
}
