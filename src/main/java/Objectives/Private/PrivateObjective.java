package Objectives.Private;

import Model.Window;
import Model.Cell;

import java.awt.*;

public class PrivateObjective {

    private String name;
    private String description;
    private Color color;

    public PrivateObjective(String name, String colore, String descrizione) {
        this.name = name;
        this.description = descrizione;
        switch (colore) {
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
    public int calcScore(Window vetrata) {
        Cell[][] grid = vetrata.getGrid();
        Color current_color = null;
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
