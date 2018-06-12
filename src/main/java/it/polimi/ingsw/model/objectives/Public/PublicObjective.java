package it.polimi.ingsw.model.objectives.Public;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.Window;

public class PublicObjective {

    private String nome;
    private String descrizione;
    private int valore;
    ScoreInterface punteggio;

    public PublicObjective(String nome, String descrizione,
                           int valore, ScoreInterface punteggio) {
        this.nome = nome;
        this.descrizione = descrizione;
        this.valore = valore;
        this.punteggio = punteggio;
    }

    /**
     *
     * @param vetrata
     *@return *restituisce il punteggio (relativo all'obbiettivo) che il giocatore ha totalizzato*
     */
    public int getScore(Window vetrata) {
        Cell[][] grid = vetrata.getGrid();
        return punteggio.calcScore(valore, grid);
    }

    public int getValore() {
        //punteggio diagonale non ha un valore predefinito, ma varia da partita a partita
        return punteggio.getClass().getSimpleName().equals("DiagonalScore") ?
                0 : valore;
    }
}
