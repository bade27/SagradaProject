package Objectives.Private;

import Model.Window;
import Model.Cell;

import java.awt.*;

public class PrivateObjective {

    private String nome;
    private String descrizione;
    private Color colore;

    public PrivateObjective(String nome, String colore, String descrizione) {
        this.nome = nome;
        this.descrizione = descrizione;
        switch (colore) {
            case "rosso":
                this.colore = Color.RED;
                break;
            case "verde":
                this.colore = Color.GREEN;
                break;
            case "giallo":
                this.colore = Color.YELLOW;
                break;
            case "viola":
                this.colore = Color.MAGENTA;
                break;
            case "blu":
                this.colore = Color.BLUE;
                break;
        }

    }

    /**
     *
     * @param vetrata
     * @return *il punteggio totalizzato dal giocatore (=numero dei dadi del colore obbiettivo posizionati)*
     */
    public int calcolaPunteggio(Window vetrata) {
        Cell[][] grid = vetrata.getGrid();
        Color current_color = null;
        int total = 0;
        for(int i = 0; i < grid.length; i++)
            for(int j = 0; j < grid[0].length; j++) {
                Cell current_cell = grid[i][j];
                if(current_cell.getFrontDice() != null) {
                    current_color = current_cell.getFrontDice().getColor();
                    if (current_color.equals(colore))
                        total++;
                }
            }
        return total;
    }

    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }
}
