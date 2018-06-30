package it.polimi.ingsw.view.gui;

import it.polimi.ingsw.exceptions.ParserXMLException;
import it.polimi.ingsw.model.ColorEnum;
import it.polimi.ingsw.remoteInterface.Pair;
import it.polimi.ingsw.utilities.FileLocator;
import it.polimi.ingsw.utilities.ParserXML;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;


/**
 * the purpose of this class if purely debug!! it will be deleted soon
 */
public class DisplayWindow extends Application {

    private ArrayList<String[]> list;
    private SimpleGrid simpleGrid;

    @Override
    public void start(Stage stage) throws Exception {
        BorderPane root = new BorderPane();
        simpleGrid = new SimpleGrid();
        root.setCenter(simpleGrid);
        Scene scene=new Scene(root,800,500);
        stage.setTitle("Sagrada");
        stage.setScene(scene);
        stage.show();
        stage.setResizable(false);
        stage.setOnCloseRequest(e -> {
            Platform.exit();
            System.exit(0);
        });
    }


    private class SimpleGrid extends GridPane {

        public SimpleGrid () throws ParserXMLException {
            Pair[][] pair=new Pair[4][5];
            for(int i = 0 ; i < 4; i++)
                for(int j = 0 ; j < 5; j++)
                    pair[i][j]=new Pair(0, ColorEnum.WHITE);
            renderGrid();
            this.setAlignment(Pos.CENTER);
            this.setVisible(true);
        }


        public void renderGrid () throws ParserXMLException {
            list = ParserXML.readWindowsName(FileLocator.getWindowListPath());
            Platform.runLater(() -> {
                Pair board[][] = new Pair[4][5];
                try {
                    File file = new File(list.get(8)[1]);
                    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
                    Document document = documentBuilder.parse(file);

                    NodeList values = document.getElementsByTagName("value");
                    NodeList colors = document.getElementsByTagName("color");

                    for (int i = 0, k = 0; i < board.length; i++) {
                        for (int j = 0; j < board[i].length; j++) {
                            int currentValue = Integer.parseInt(values.item(k).getTextContent());
                            String currentColor = colors.item(k).getTextContent();
                            if (currentColor.equals("dc"))
                                board[i][j] = new Pair(currentValue, ColorEnum.WHITE);
                            else {
                                if (currentColor.equals("red"))
                                    board[i][j] = new Pair(currentValue, ColorEnum.RED);
                                if (currentColor.equals("green"))
                                    board[i][j] = new Pair(currentValue, ColorEnum.GREEN);
                                if (currentColor.equals("yellow"))
                                    board[i][j] = new Pair(currentValue, ColorEnum.YELLOW);
                                if (currentColor.equals("blue"))
                                    board[i][j] = new Pair(currentValue, ColorEnum.BLUE);
                                if (currentColor.equals("purple"))
                                    board[i][j] = new Pair(currentValue, ColorEnum.PURPLE);
                            }
                            k++;
                        }
                    }
                } catch (Exception e) {
                    //throw new ParserXMLException("Impossible to read file: " + list);
                }


                for (int i = 0; i < 4; i++) {
                    for (int j = 0; j < 5; j++) {
                        CellButton b = new CellButton(i, j, board[i][j].getColor(), board[i][j].getValue());
                        b.setPrefSize(100, 80);
                        b.setText("" + board[i][j].getValue());
                        b.setStyle("-fx-background-color: " + board[i][j].getColor());
                        if (board[i][j].getColor() != null && board[i][j].getValue() != 0)
                            b.setFont(Font.font("ComicSans", FontWeight.EXTRA_BOLD, 30));
                        this.add(b, j, i);
                    }
                }
            });

        }

        /**
         * element of the grid
         */
        private class CellButton extends Button {

            private int i;
            private int j;

            private ColorEnum color;
            private int value;

            private CellButton(int i, int j,ColorEnum col , int val){
                this.i=i;
                this.j=j;
                color = col;
                value = val;
            }
        }
    }

}
