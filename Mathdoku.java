import javafx.animation.Transition;
import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Mathdoku extends Application {
    private StackPane overlay;
    private GridPane layout;
    private GridPane gameGrid;
    private GameBoard gameBoard;

    private Button undoButton;
    private Button redoButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        setupGUI(primaryStage);
        Scene scene = new Scene(layout, 800, 640);
        scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

        // Define game grid
        gameGrid = new GridPane();
        GridPane.setHalignment(gameGrid, HPos.CENTER);

        overlay = new StackPane();
        overlay.getChildren().add(gameGrid);

        layout.add(overlay, 3, 1, 1, 1);
        gameBoard = new GameBoard(gameGrid, this);

        primaryStage.setScene(scene);
        primaryStage.setTitle("Mathdoku");
        primaryStage.show();
    }

    private void setupGUI(Stage stage){
        layout = new GridPane();
        layout.setHgap(0);
        layout.setVgap(5);

        createMenus();
        createBottomPanel();
        createInputPanel();

        setConstraints();
    }

    private void createMenus(){
        // Create menu bar
        MenuBar menubar = new MenuBar();
        layout.add(menubar, 0, 0, 5, 1);


        //  Create new game submenu
        Menu menufile = new Menu("Load game");
        MenuItem loadFromFile = new MenuItem("Load game from file");
        MenuItem loadFromText = new MenuItem("Load game from text");
        menufile.getItems().addAll(loadFromFile, loadFromText);

        // New game menu functions
        loadFromFile.setOnAction(actionEvent -> loadFromFile());
        loadFromText.setOnAction(actionEvent -> loadFromText());


        // Create generate menu
        Menu generate = new Menu("Generate");

        for(int i=2; i<=8; i++){
            int finalI = i;
            MenuItem tempGen = new MenuItem(String.format("Generate %dx%d Mathdoku", i, i));
            tempGen.setOnAction(actionEvent -> generateMathdoku(finalI));
            generate.getItems().add(tempGen);
        }

        MenuItem customGen = new MenuItem("Custom size generate");
        generate.getItems().add(customGen);

        customGen.setOnAction(actionEvent -> {
            TextInputDialog dialog = new TextInputDialog("8");
            dialog.setTitle("Generate settings");
            dialog.setHeaderText(null);
            dialog.setContentText("Size:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(name -> {
                try {
                    int size = Integer.parseInt(name);
                    generateMathdoku(size);
                } catch (NumberFormatException e){
                    System.err.println("Invalid size");
                }
            });
        });


        // Create font submenu
        Menu menuFont = new Menu("Font");
        MenuItem smallFont = new MenuItem("Small font");
        MenuItem mediumFont = new MenuItem("Medium font");
        MenuItem largeFont = new MenuItem("Large font");
        menuFont.getItems().addAll(smallFont, mediumFont, largeFont);

        // Font menu functions
        smallFont.setOnAction(actionEvent -> gameBoard.setFont(1));
        mediumFont.setOnAction(actionEvent -> gameBoard.setFont(2));
        largeFont.setOnAction(actionEvent -> gameBoard.setFont(3));

        // Create help submenu
        Menu helpMenu = new Menu("Help");
        MenuItem hint = new MenuItem("Give hint");
        MenuItem solve = new MenuItem("Solve the mathdoku");
        helpMenu.getItems().addAll(hint, solve);

        solve.setOnAction(actionEvent -> gameBoard.solve());
        hint.setOnAction(actionEvent -> gameBoard.hint());

        menubar.getMenus().addAll(menufile, generate, menuFont, helpMenu);
    }

    private void generateMathdoku(int n){
        Generate gen = new Generate();
        Cage[] newCages = gen.generate(n);

        overlay.getChildren().remove(gameGrid);
        gameGrid = new GridPane();
        GridPane.setHalignment(gameGrid, HPos.CENTER);
        overlay.getChildren().add(gameGrid);
        this.gameBoard = new GameBoard(gameGrid, this, n, newCages);
    }

    private void createBottomPanel(){
        // Credit text
        Text credit = new Text("A Mathdoku game by James Kee");
        GridPane.setHalignment(credit, HPos.RIGHT);
        layout.add(credit, 3, 2);

        // Control buttons
        undoButton = new Button("Undo");
        redoButton = new Button("Redo");
        Button clear = new Button("Clear");
        Button showMistakes = new Button("Show mistakes");
        layout.add(undoButton, 0, 2);
        layout.add(redoButton, 1, 2);
        layout.add(clear, 2, 2);
        layout.add(showMistakes, 3, 2);

        undoButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> gameBoard.undo());
        redoButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> gameBoard.redo());

        undoButton.setDisable(true);
        redoButton.setDisable(true);

        clear.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
            // Prompt for confirmation
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "", ButtonType.YES, ButtonType.NO);
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to clear?");
            alert.showAndWait();
            if(alert.getResult() == ButtonType.YES) {
                gameBoard.clear();
            }
        });

        showMistakes.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> gameBoard.toggleShowMistakes());

        GridPane.setValignment(undoButton, VPos.BOTTOM);
        GridPane.setValignment(redoButton, VPos.BOTTOM);
        GridPane.setValignment(clear, VPos.BOTTOM);
        GridPane.setValignment(showMistakes, VPos.BOTTOM);
    }

    private void createInputPanel(){
        // Auxillary input panel on the left
        VBox inputPanel = new VBox(5);
        for(int i=1; i<=9; i++){
            Button temp = new Button(Integer.toString(i));

            temp.setPrefWidth(50);
            temp.setPrefHeight(40);

            int finalI = i;
            temp.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> gameBoard.insert(finalI));
            inputPanel.getChildren().add(temp);
        }
        Button clearButton = new Button("Clear cell");
        clearButton.addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> gameBoard.insert(0));
        inputPanel.getChildren().add(clearButton);

        layout.add(inputPanel, 4, 1, 1, 2);
        GridPane.setHalignment(inputPanel, HPos.CENTER);
    }

    private void loadFromFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Puzzle File", "*.txt"));

        String currentPath = Paths.get(".").toAbsolutePath().normalize().toString();
        fileChooser.setInitialDirectory(new File(currentPath));

        fileChooser.setTitle("Load Puzzle File");
        File selectedFile = fileChooser.showOpenDialog(new Stage());

        if(selectedFile != null) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(selectedFile));
                ArrayList<String> arr = new ArrayList<String>();

                String line = br.readLine();
                while (line != null) {
                    arr.add(line);
                    line = br.readLine();
                }
                br.close();

                String[] lines = new String[arr.size()];
                for (int i = 0; i < arr.size(); i++) {
                    lines[i] = arr.get(i);
                }

                parseGameText(lines);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void loadFromText(){
        Stage popup = new Stage();

        GridPane popGrid = new GridPane();
        popup.setTitle("Load game from text");
        popup.setScene(new Scene(popGrid, 300, 200));
        popup.show();

        TextArea input = new TextArea();
        popGrid.add(input, 0, 0, 2, 1);

        Button load = new Button("Load");
        Button cancel = new Button("Cancel");

        popGrid.add(cancel, 0, 1);
        popGrid.add(load, 1, 1);
        popGrid.setHalignment(load, HPos.RIGHT);

        cancel.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> popup.close());
        load.addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEvent -> {
            String temp = input.getText();
            String[] lines = temp.split("\\r?\\n");
            parseGameText(lines);
            popup.close();
        });
    }

    private void parseGameText(String[] gameText){
        boolean response = loadGame(gameText);
        if(!response){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Error!");
            alert.setHeaderText(null);
            alert.setContentText("The format of this file is invalid");

            alert.showAndWait();
        }
    }

    private boolean loadGame(String[] gameText){
        Operator[] operators = new Operator[gameText.length];
        int[] targets = new int[gameText.length];
        int[][] cageCellIndexes = new int[gameText.length][];

        for(int i=0; i<gameText.length; i++){
            String[] parts = gameText[i].split("\\s+");
            if(parts.length != 2){
                System.err.println("ERROR: invalid format (multiple spaces)");
                return false;
            }

            Pattern pattern = Pattern.compile("\\d+");
            Matcher matcher = pattern.matcher(parts[0]);
            if(!matcher.find()){
                System.err.println("ERROR: invalid format");
                return false;
            }

            int target = Integer.parseInt(parts[0].substring(0, matcher.end()));
            String op = parts[0].substring(matcher.end());

            Operator operator;

            switch(op){
                case "+":
                    operator = Operator.ADD;
                    break;
                case "-":
                    operator = Operator.SUBTRACT;
                    break;
                case "*":
                case "x":
                    operator = Operator.MULTIPLY;
                    break;
                case "/":
                case "\u00F7":
                    operator = Operator.DIVIDE;
                    break;
                case "":
                    operator = Operator.NONE;
                    break;
                default:
                    System.err.println("ERROR: invalid operator");
                    return false;
            }

            String[] strindexes = parts[1].split(",");
            int[] cellindexes = new int[strindexes.length];
            for(int j=0; j<strindexes.length; j++){
                try {
                    cellindexes[j] = Integer.parseInt(strindexes[j]) - 1;
                }catch (NumberFormatException e){
                    System.err.println("ERROR: invalid values");
                    return false;
                }
            }
            Arrays.sort(cellindexes);

            operators[i] = operator;
            targets[i] = target;
            cageCellIndexes[i] = cellindexes;
        }

        // Check cell indexes are valid, and are uniquely assigned to cages
        Set<Integer> indexes = new HashSet<Integer>();
        int count = 0;
        int highest = Integer.MIN_VALUE;
        for(int[] list: cageCellIndexes){
            for(int index: list){
                if(index < 0){
                    System.err.println("ERROR: invalid values");
                    return false;
                }
                if(index > highest)
                    highest = index;
                indexes.add(index);
                count += 1;
            }
        }

        if(indexes.size() != count || highest != count-1){
            System.err.println("ERROR: duplicate or invalid values");
            return false;
            // Duplicate or invalid values
        }
        double sqr = Math.sqrt(count);
        if(sqr != Math.floor(sqr) && sqr >= 2){
            System.err.println("ERROR: Format error bro");
            return false;
        }

        int n = (int) sqr;


        Set<Integer> connectedCells;
        for(int[] list: cageCellIndexes) {
            connectedCells = new HashSet<Integer>();
            for (int a=0; a<list.length-1; a++) {
                boolean rightAdjacent = list[a] % n < n - 1 && list[a] == list[a+1] - 1;
                boolean downAdjacent = false;

                for(int b=a+1; b<list.length; b++){
                    if(list[b] == list[a] + n){
                        downAdjacent = true;
                        break;
                    }
                }

                if(rightAdjacent){
                    connectedCells.add(list[a]);
                    connectedCells.add(list[a]+1);
                }

                if(downAdjacent) {
                    connectedCells.add(list[a]);
                    connectedCells.add(list[a]+n);
                }
            }
            if(connectedCells.size() != list.length && list.length > 1){
                System.err.println("ERROR: not adjacent");
                return false;
            }
        }

        Cage[] newCages = new Cage[gameText.length];
        for(int i=0; i<operators.length; i++){
            newCages[i] = new Cage(operators[i], targets[i], cageCellIndexes[i], n);
        }

        overlay.getChildren().remove(gameGrid);

        gameGrid = new GridPane();
        GridPane.setHalignment(gameGrid, HPos.CENTER);
        overlay.getChildren().add(gameGrid);

        this.gameBoard = new GameBoard(gameGrid, this, n, newCages);
        return true;
    }

    void setUndoButtonState(Boolean state){
        undoButton.setDisable(!state);
    }

    void setRedoButtonState(Boolean state){
        redoButton.setDisable(!state);
    }

    private double smoothStep(double n){
        if(n<=0)
            return 0;
            if(n>=1)
            return 1;
        return 6*Math.pow(n, 5) - 15*Math.pow(n,4) + 10*Math.pow(n, 3);
    }

    void winAnimation(){
        ImageView img = new ImageView(new Image("/victory.png"));
        overlay.getChildren().add(img);

        Transition bounce = new Transition() {
            {
                setCycleDuration(Duration.millis(2500));
            }
            @Override
            protected void interpolate(double v) {
                img.setFitHeight(Math.min(9 + 180*v, 180));
                img.setFitWidth(Math.min(24 + 480*v, 480));

                img.setRotate(4 * 360 * 2 * (smoothStep(v+0.5)-0.5));
            }
        };
        bounce.play();
        bounce.setOnFinished(e -> overlay.getChildren().remove(img));
    }

    private void setConstraints(){
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
        row2.setVgrow(Priority.ALWAYS);
        RowConstraints row3 = new RowConstraints();
        row3.setVgrow(Priority.NEVER);
        layout.getRowConstraints().addAll(row1, row2, row3);
    }
}