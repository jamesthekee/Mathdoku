package sample;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import javax.swing.*;


//gridpane of stackpanes

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {

        GridPane layout = new GridPane();
        layout.setHgap(0);
        layout.setVgap(0);
        layout.setGridLinesVisible(true);


        // Title
        Text title = new Text("Mathdoku");
        GridPane.setHalignment(title, HPos.CENTER);
        layout.add(title, 3, 1);

        // Menu stuff
        MenuBar menubar = new MenuBar();
        Menu menufile = new Menu("File");
        menubar.getMenus().add(menufile);
        layout.add(menubar, 0, 0, 5, 1);

        MenuItem loadFromFile = new MenuItem("Load game from file");
        MenuItem loadFromText = new MenuItem("Load game from text");

        menufile.getItems().addAll(loadFromFile, loadFromText);

        Text credit = new Text("A Mathdoku game by James Kee");
        GridPane.setHalignment(credit, HPos.RIGHT);
        layout.add(credit, 3, 3);



        // Undo and redo buttons
        Button undo = new Button("Undo");
        Button redo = new Button("Redo");
        Button clear = new Button("Clear");
        layout.add(undo, 0, 3);
        layout.add(redo, 1, 3);
        layout.add(clear, 2, 3);


        CheckBox showMistakes = new CheckBox("Show mistakes");
        layout.add(showMistakes, 4, 3);

        GridPane.setValignment(undo, VPos.BOTTOM);
        GridPane.setValignment(redo, VPos.BOTTOM);
        GridPane.setValignment(clear, VPos.BOTTOM);
        GridPane.setValignment(showMistakes, VPos.BOTTOM);

        // Column constraints
        ColumnConstraints column1 = new ColumnConstraints();
        column1.setHgrow(Priority.NEVER);
        ColumnConstraints column2 = new ColumnConstraints();
        column2.setHgrow(Priority.NEVER);
        ColumnConstraints column3 = new ColumnConstraints();
        column3.setHgrow(Priority.NEVER);
        ColumnConstraints column4 = new ColumnConstraints();
        column4.setHgrow(Priority.ALWAYS);
        ColumnConstraints column5 = new ColumnConstraints();
        column5.setHgrow(Priority.NEVER);
        layout.getColumnConstraints().addAll(column1, column2, column3, column4, column5);

        // Row constraints
        RowConstraints row1 = new RowConstraints();
        row1.setVgrow(Priority.NEVER);
        RowConstraints row2 = new RowConstraints();
        row2.setVgrow(Priority.NEVER);
        RowConstraints row3 = new RowConstraints();
        row3.setVgrow(Priority.ALWAYS);
        RowConstraints row4 = new RowConstraints();
        row4.setVgrow(Priority.NEVER);
        layout.getRowConstraints().addAll(row1, row2, row3, row4);


        Scene scene = new Scene(layout, 800, 600);
        stage.setScene(scene);
        stage.show();
    }

}
