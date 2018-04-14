package Obbiettivi.Pubblici;

import Test.Model.Cell;
import Test.Model.Window;

import java.awt.*;
import java.util.*;

public class PunteggioDiagonale implements PunteggioInterface {

    private String tag;

    public PunteggioDiagonale(String tag) {
        this.tag = tag;
    }

    /**
     *
     * @param valore
     * @param vetrata
     * @return **il punteggio totalizzato dal giocatore
     */
    public int calcola(int valore, Window vetrata) {
        //0 red, 1 green, 2 blue, 3 yellow, 4 magenta
        Map<Integer, Color> colors = new HashMap<>();
        colors.put(0, Color.red);
        colors.put(1, Color.green);
        colors.put(2, Color.blue);
        colors.put(3, Color.yellow);
        colors.put(4, Color.magenta);

        int points = 0;

        Cell[][] grid = vetrata.getGrid();
        for(int i = 0; i < colors.size(); i++) {

            //lista che contiene le liste di celle (adiacenti in diagonale)
            // che contengono dadi dello stesso colore
            ArrayList<ArrayList<Coppia>> root = new ArrayList<>();
            Color current_color = colors.get(i);

            //compilo root
            for(int h = 0; h < grid.length; h++) {
                for (int k = 0; k < grid[0].length; k++) {
                    Cell current_cell = grid[h][k];
                    if (current_cell.getFrontDice() != null) {
                        if(current_cell.getFrontDice().getColor() == current_color)
                            compileRoot(root, new Coppia(k, h));
                    }
                }
            }

            //calcolo la sequenza più lunga per quella root
            //per ogni colore viene considerata solo la sequenza più lunga di
            // colori uguali adiacenti in diagonale
            points += longestSequence(root);

        }
        return points;
    }


    /**
     *
     * @param lp
     * @param p
     * @return *true se la cella è raggiungibile da quella attuale tramite
     * spostamento in diagonale (nelle quattro direzioni possibili)
     */
    private boolean isReachable(ArrayList<Coppia> lp, Coppia p) {
        boolean reachable = false;
        int c = p.getX();
        int r = p.getY();
        for(int i = 0; i < lp.size() && !reachable; i++) {
            Coppia current = lp.get(i);
            int x = current.getX(), y = current.getY();
            if( (y + 1) == r) {
                if( x == 0)
                    reachable = (x + 1) == c;
                else if( x == 4 )
                    reachable = (x - 1) == c;
                else reachable = (x + 1) == c || (x - 1) == c;
            }
        }
        return reachable;
    }

    /**
     *
     * @param root
     * @return *la lunghezza della più lunga sequenza di celle diagonali adiacenti
     * con dadi dello stesso colore*
     */
    private int longestSequence(ArrayList<ArrayList<Coppia>> root) {
        int len = 0;
        if(root.size() != 0) {
            if (root.size() == 1)
                len = root.get(0).size() <= 1 ? 0 : root.get(0).size();
            else {
                for (int k = 0; k < root.size() - 1; k++) {
                    for (int h = k + 1; h < root.size(); h++) {
                        ArrayList<Coppia> l1 = root.get(k), l2 = root.get(h);
                        if (shareElement(l1, l2)) {
                            root.set(k, merge(l1, l2));
                            root.set(h, merge(l1, l2));
                        }
                    }

                }

                Optional<Integer> l = root.stream()
                        .map(list -> list.size())
                        .max(Comparator.comparing(i -> i));
                len = l.isPresent() ? l.get() : 0;
            }
        }
        return len;
    }


    private boolean shareElement(ArrayList<Coppia> l1, ArrayList<Coppia> l2) {
        for(int i = 0; i < l1.size(); i++)
            for(int j = 0; j < l2.size(); j++)
                if(l1.get(i).isEqual(l2.get(j))) {
                    l1.remove(l1.get(i));
                    return true;
                }
        return false;
    }

    /**
     *
     * @param l1
     * @param l2
     * @return *il merge di due liste che hanno almeno un elemento in comune*
     */
    private ArrayList<Coppia> merge(ArrayList<Coppia> l1, ArrayList<Coppia> l2) {
        Set<Coppia> fooSet = new LinkedHashSet<>(l1);
        fooSet.addAll(l2);
        ArrayList<Coppia> finalFoo = new ArrayList<>(fooSet);
        return finalFoo;
    }

    /**
     *
     * @param root
     * @param p
     * inizializza il parametro root
     */
    private void compileRoot(ArrayList<ArrayList<Coppia>> root, Coppia p) {
        boolean add = true;
        for(int k =  0; k < root.size(); k++) {
            if(!root.get(k).contains(p))
                if(isReachable(root.get(k), p)) {
                    root.get(k).add(p);
                    add = false;
                }
        }
        if(add) {
            ArrayList<Coppia> new_list = new ArrayList<>();
            new_list.add(p);
            root.add(new_list);
        }
    }


    /**
     * memorizza informazioni relative alle celle della griglia
     */
    private class Coppia {
        int x, y;
        Coppia(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public boolean isEqual(Coppia p) {
            return this.x == p.getX() && this.y == p.getY();
        }
    }
}
