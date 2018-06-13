package it.polimi.ingsw.model.objectives.Public;

import it.polimi.ingsw.model.Cell;
import it.polimi.ingsw.model.ColorEnum;

import java.util.*;

public class DiagonalScore extends Score {

    /**
     *
     * @param valore
     * @param grid
     * @return the total score for this objective
     */
    public int calcScore(int valore, Cell[][] grid) {

        Map<Integer, ColorEnum> colors = new HashMap<>();
        colors.put(0, ColorEnum.RED);
        colors.put(1, ColorEnum.GREEN);
        colors.put(2, ColorEnum.BLUE);
        colors.put(3, ColorEnum.YELLOW);
        colors.put(4, ColorEnum.PURPLE);

        int points = 0;

        for(int i = 0; i < colors.size(); i++) {

            //this list contains the lists of adjacent cells (diagonally)
            ArrayList<ArrayList<Tuple>> root = new ArrayList<>();
            ColorEnum current_color = colors.get(i);

            //create root
            for(int h = 0; h < grid.length; h++) {
                for (int k = 0; k < grid[0].length; k++) {
                    Cell current_cell = grid[h][k];
                    if (current_cell.getFrontDice() != null) {
                        if(current_cell.getFrontDice().getColor() == current_color)
                            compileRoot(root, new Tuple(k, h));
                    }
                }
            }

            points += sumOfDiagonals(root);

        }
        return points;
    }


    /**
     *
     * @param lp
     * @param p
     * @return true if the cell is reachable from the current one
     * (only diagonal steps are valid)
     */
    private boolean isReachable(ArrayList<Tuple> lp, Tuple p) {
        boolean reachable = false;
        int c = p.getX();
        int r = p.getY();
        for(int i = 0; i < lp.size() && !reachable; i++) {
            Tuple current = lp.get(i);
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
     * @return the total number of diagonal cells
     */
    private int sumOfDiagonals(ArrayList<ArrayList<Tuple>> root) {
        ArrayList<ArrayList<Tuple>> foo = new ArrayList<>();
        int len = 0;
        if(root.size() != 0) {
            if (root.size() == 1)
                len = root.get(0).size() <= 1 ? 0 : root.get(0).size();
            else {
                for (int k = 0; k < root.size() - 1; k++) {
                    for (int h = k + 1; h < root.size(); h++) {
                        ArrayList<Tuple> l1 = root.get(k), l2 = root.get(h);
                        if (shareElement(l1, l2)) {
                            root.set(k, merge(l1, l2));
                            root.set(h, merge(l1, l2));
                        }
                    }

                }

                root.forEach(l -> {if(!isContained(l, foo)) foo.add(l);});

                len = foo.stream()
                        .mapToInt(list -> list.size())
                        .filter(x -> x != 1)
                        .sum();
            }
        }
        return len;
    }


    /**
     *
     * @param el
     * @param list
     * @return weather the list el is contained inside list
     */
    private boolean isContained(ArrayList<Tuple> el, ArrayList<ArrayList<Tuple>> list) {
        for(int i = 0; i < list.size(); i++)
            for(int j = 0; j < el.size(); j++)
                if(list.get(i).contains(el.get(j)))
                    return true;
        return false;
    }

    /**
     *
     * @param l1
     * @param l2
     * @return weather the two lists share an element or not
     */
    private boolean shareElement(ArrayList<Tuple> l1, ArrayList<Tuple> l2) {
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
     * @return merges lists with common element(s)
     */
    private ArrayList<Tuple> merge(ArrayList<Tuple> l1, ArrayList<Tuple> l2) {
        Set<Tuple> fooSet = new LinkedHashSet<>(l1);
        fooSet.addAll(l2);
        ArrayList<Tuple> finalFoo = new ArrayList<>(fooSet);
        return finalFoo;
    }

    /**
     *
     * @param root
     * @param p
     * initializes root
     */
    private void compileRoot(ArrayList<ArrayList<Tuple>> root, Tuple p) {
        boolean add = true;
        for(int k =  0; k < root.size(); k++) {
            if(!root.get(k).contains(p))
                if(isReachable(root.get(k), p)) {
                    root.get(k).add(p);
                    add = false;
                }
        }
        if(add) {
            ArrayList<Tuple> new_list = new ArrayList<>();
            new_list.add(p);
            root.add(new_list);
        }
    }


    private class Tuple {
        int x, y;
        Tuple(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public boolean isEqual(Tuple t) {
            return this.x == t.getX() && this.y == t.getY();
        }

        @Override
        public String toString() {
            return "Tuple{" +
                    "x=" + x +
                    ", y=" + y +
                    '}';
        }
    }
}
