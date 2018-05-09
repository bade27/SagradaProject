package it.polimi.ingsw.view;

import javafx.scene.Node;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.RowConstraints;

public class dimWindows {
    public static void dim(GridPane pane){
        for(int rowIndex=0;rowIndex<getRowCount(pane);rowIndex++){
            RowConstraints rc=new RowConstraints();
            rc.setVgrow(Priority.ALWAYS);
            rc.setFillHeight(true);
            pane.getRowConstraints().add(rc);
        }
        for(int colIndex=0;colIndex<getColumnCount(pane);colIndex++){
            ColumnConstraints cc=new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setFillWidth(true);
            pane.getColumnConstraints().add(cc);
        }
    }
    public static void dimHeight(GridPane pane, double dim){
        for(int rowIndex=0;rowIndex<getRowCount(pane);rowIndex++){
            RowConstraints rc=new RowConstraints();
            rc.setPrefHeight(dim);
            pane.getRowConstraints().add(rc);
        }
        for(int colIndex=0;colIndex<getColumnCount(pane);colIndex++){
            ColumnConstraints cc=new ColumnConstraints();
            cc.setHgrow(Priority.ALWAYS);
            cc.setFillWidth(true);
            pane.getColumnConstraints().add(cc);
        }
    }
    public static void dimWidth(GridPane pane, double dim){
        for(int rowIndex=0;rowIndex<getRowCount(pane);rowIndex++){
            RowConstraints rc=new RowConstraints();
            rc.setVgrow(Priority.ALWAYS);
            rc.setFillHeight(true);
            pane.getRowConstraints().add(rc);
        }
        for(int colIndex=0;colIndex<getColumnCount(pane);colIndex++){
            ColumnConstraints cc=new ColumnConstraints();
            cc.setPrefWidth(dim);
            pane.getColumnConstraints().add(cc);
        }
    }
    public static int getRowCount(GridPane pane) {
        int numRows=pane.getRowConstraints().size();
        for(int i=0;i<pane.getChildren().size();i++){
            Node child=pane.getChildren().get(i);
            if(child.isManaged()){
                Integer rowIndex=GridPane.getRowIndex(child);
                if(rowIndex!=null){
                    numRows=Math.max(numRows,rowIndex+1);
                }
            }
        }
        return numRows;
    }
    public static int getColumnCount(GridPane pane) {
        int numColumn=pane.getColumnConstraints().size();
        for(int i=0;i<pane.getChildren().size();i++){
            Node child=pane.getChildren().get(i);
            if(child.isManaged()){
                Integer columnIndex=GridPane.getColumnIndex(child);
                if(columnIndex!=null){
                    numColumn=Math.max(numColumn,columnIndex+1);
                }
            }
        }
        return numColumn;
    }
}
