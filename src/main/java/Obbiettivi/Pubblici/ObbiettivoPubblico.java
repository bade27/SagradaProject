package Obbiettivi.Pubblici;

import Test.Model.Window;

public class ObbiettivoPubblico {

    private String nome;
    private String descrizione;
    private int valore;
    PunteggioInterface punteggio;

    public ObbiettivoPubblico(String nome, String descrizione,
                              int valore, PunteggioInterface punteggio) {
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
    public int calcolaPunteggio(Window vetrata) {
        return punteggio.calcola(valore, vetrata);
    }

    /**
     *
     * i getter seguenti restituiscono i campi privati della classe (punteggio escluso)
     */
    public String getNome() {
        return nome;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public int getValore() {
        //punteggio diagonale non ha un valore predefinito, ma varia da partita a partita
        return punteggio.getClass().getSimpleName().equals("PunteggioDiagonale") ?
                0 : valore;
    }
}
