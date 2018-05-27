package it.polimi.ingsw.model.objectives.Private;

import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.model.Window;
import it.polimi.ingsw.model.Cell;

import java.awt.*;

public class PrivateObjective {

    private String name;
    private String description;
    private Color color;

    public PrivateObjective(String name, String color, String description) {
        this.name = name;
        this.description = description;
        switch (color) {
            case "rosso":
                this.color = Color.RED;
                break;
            case "verde":
                this.color = Color.GREEN;
                break;
            case "giallo":
                this.color = Color.YELLOW;
                break;
            case "viola":
                this.color = Color.MAGENTA;
                break;
            case "blu":
                this.color = Color.BLUE;
                break;
        }

    }

    /**
     *
     * @param vetrata
     * @return *il punteggio totalizzato dal giocatore (=numero dei dadi del color obbiettivo posizionati)*
     */
    public int calcolaPunteggio(Window vetrata) {
        Cell[][] grid = vetrata.getGrid();
        ColorEnum current_color = null;
        int total = 0;
        for(int i = 0; i < grid.length; i++)
            for(int j = 0; j < grid[0].length; j++) {
                Cell current_cell = grid[i][j];
                if(current_cell.getFrontDice() != null) {
                    current_color = current_cell.getFrontDice().getColor();
                    if (current_color.equals(color))
                        total++;
                }
            }
        return total;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
