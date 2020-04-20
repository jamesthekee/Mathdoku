package sample;

import javafx.scene.control.Alert;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;


class GameBoard {
    private GridPane gameGrid;
    private Cell[] cells;
    private Cage[] cages;
    private Cell selected;
    private boolean showMistakes;
    private int n;

    private Mathdoku mathdokuParent;

    private Stack<Action> actionStack;
    private Stack<Action> undoStack;

    GameBoard(GridPane gameGrid, Mathdoku mathdokuParent){
        this(gameGrid, mathdokuParent, 6, new Cage[]{
                new Cage(Operator.ADD, 11, new int[]{0, 6}, 6),
                new Cage(Operator.DIVIDE, 2, new int[]{1, 2}, 6),
                new Cage(Operator.MULTIPLY, 20, new int[]{3, 9}, 6),
                new Cage(Operator.MULTIPLY, 6, new int[]{4, 5, 11, 17}, 6),
                new Cage(Operator.SUBTRACT, 3, new int[]{7, 8}, 6),
                new Cage(Operator.DIVIDE, 3, new int[]{10, 16}, 6),
                new Cage(Operator.MULTIPLY, 240, new int[]{12, 13, 18, 19}, 6),
                new Cage(Operator.MULTIPLY, 6, new int[]{14, 15}, 6),
                new Cage(Operator.ADD, 7, new int[]{21, 27, 28}, 6),
                new Cage(Operator.MULTIPLY, 30, new int[]{22, 23}, 6),
                new Cage(Operator.MULTIPLY, 6, new int[]{24, 25}, 6),
                new Cage(Operator.ADD, 9, new int[]{29, 35}, 6),
                new Cage(Operator.ADD, 8, new int[]{30, 31, 32}, 6),
                new Cage(Operator.DIVIDE, 2, new int[]{33, 34}, 6),
                new Cage(Operator.MULTIPLY, 6, new int[]{20, 26}, 6)});

    }

    GameBoard(GridPane gameGrid, Mathdoku mathdokuParent, int n, Cage[] cages){
        this.gameGrid = gameGrid;
        this.mathdokuParent = mathdokuParent;
        this.n = n;
        this.cages = cages;
        this.cells = new Cell[n*n];
        this.selected = null;

        actionStack = new Stack<>();
        undoStack = new Stack<>();

        gameGrid.getStyleClass().add("game-grid");

        setConstraints();
        display();
    }

    void solve(){
        for(Cage c: cages){
            if(c.getOp() == Operator.NONE)
                cells[c.getCellIndexes()[0]].value = c.getValue();
        }

        // Do more filling in
        recursiveSolve(0);

        for(Cell c: cells) {
            c.setValue(c.value);
            c.update();
        }
    }

    void hint(){
        int[] backup = new int[cells.length];
        for(int i=0; i<cells.length; i++){
            backup[i] = cells[i].value;
        }
        solve();

        boolean first = false;
        for(int i=0; i<cells.length; i++){
            if(!first) {
                if (backup[i] != cells[i].value)
                    first = true;
            }else {
                if (backup[i] != cells[i].value) {
                    cells[i].value = backup[i];
                }
            }
        }
        for(Cell c: cells) {
            c.setValue(c.value);
            c.update();
        }
    }

    private boolean recursiveSolve(int index){
        for(int i=1; i<=n; i++){
            cells[index].value = i;
            if(quickCheck()){
                if(index < n*n - 1) {
                    boolean solved = recursiveSolve(index + 1);
                    if (solved)
                        return true;
                    else
                        cells[index+1].value = 0;
                }
                else if(index == n*n -1)
                    return true;
            }

        }
        return false;
    }

    private boolean quickCheck(){
        // Check if board satisfies row, col constraints
        Set<Integer> curRowSet;
        Set<Integer> curColSet;
        for(int i=0; i<n; i++){
            curColSet = new HashSet<>();
            curRowSet = new HashSet<>();

            // Check each row
            for(int k=0; k<n; k++){
                if (curRowSet.contains(cells[k + i*n].value) && cells[k+i*n].value != 0)
                    return false;
                curRowSet.add(cells[k + i*n].value);
            }

            // Check each col
            for(int k=0; k<n; k++){
                if (curColSet.contains(cells[i + k*n].value) && cells[i+k*n].value != 0)
                    return false;
                curColSet.add(cells[i + k*n].value);
            }
        }

        for(Cage cage: cages){
            if(cage.isFull(cells)){
                if(!cage.correctValues(cells))
                    return false;
            }else if(!cage.couldBeCorrect(cells))
                return false;
        }
        return true;
    }

    private boolean checkForWin(){
        boolean gameWon = true;

        // Reset correctness of each cell
        for(Cell c: cells)
            c.correct = true;

        // Check if board satisfies cage constraints
        for(Cage cage: cages){
            if(!cage.correctValues(cells)) {
                gameWon = false;
                if(cage.isFull(cells))
                    cage.setIncorrect(cells);
            }
        }

        // Check if board satisfies row, col constraints
        Set<Integer> curRowSet;
        Set<Integer> curColSet;
        for(int i=0; i<n; i++){
            curColSet = new HashSet<>();
            curRowSet = new HashSet<>();

            // Check each col
            for(int k=0; k<n; k++){
                boolean isDupe = curColSet.contains(cells[i + k*n].value) && cells[i + k*n].value != 0;
                boolean isErroneous = cells[i + k*n].value > n;
                if (isDupe || isErroneous) {
                    gameWon = false;
                    //highlight col i
                    for (int j = 0; j < n; j++) {
                        cells[i + j * n].correct = false;
                    }
                    break;
                }
                curColSet.add(cells[i + k*n].value);
            }

            // Check each row
            for(int k=0; k<n; k++){
                boolean isDupe = curRowSet.contains(cells[k + i*n].value) && cells[k + i*n].value != 0;
                boolean isErroneous = cells[k + i*n].value > n;
                if (isDupe || isErroneous) {
                    gameWon = false;
                    //highlight col i
                    for (int j = 0; j < n; j++) {
                        cells[j + i * n].correct = false;
                    }
                    break;
                }
                curRowSet.add(cells[k + i*n].value);
            }
        }
        return gameWon;
    }

    private void showHighlighting(){
        for(Cell c : cells)
            c.showHighlighting();
    }

    private void checkBoard(){
        if(checkForWin()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Congratulations!");
            alert.setHeaderText(null);
            alert.setContentText("Congratulations! You solved this mathdoku.");

            alert.showAndWait();

            mathdokuParent.winAnimation();
        }
        if(showMistakes)
            showHighlighting();
    }

    void toggleShowMistakes(){
        showMistakes = !showMistakes;
        if(showMistakes)
            showHighlighting();
        else{
            for(Cell c: cells)
                c.setHighlighting(false);
        }
    }

    private void setConstraints(){
        //Set row and col constraints
        for(int i=0; i<n; i++){
            RowConstraints rowConstraint = new RowConstraints();
            rowConstraint.setPercentHeight(100.0/n);
            rowConstraint.setVgrow(Priority.ALWAYS);
            gameGrid.getRowConstraints().add(rowConstraint);

            ColumnConstraints columnConstraint = new ColumnConstraints();
            columnConstraint.setPercentWidth(100.0/n);
            columnConstraint.setHgrow(Priority.ALWAYS);
            gameGrid.getColumnConstraints().add(columnConstraint);
        }
    }

    private void display(){
        // Create board
        for(int x=0; x<n; x++){
            for(int y=0; y<n; y++){
                int index = x+y*n;
                cells[index] = new Cell();

                // MouseEvent for clicking inside a cell, deselects other Cells
                cells[index].addEventHandler(MouseEvent.MOUSE_PRESSED, mouseEvent -> {
                    if(selected == null){// If hadn't any previously selected cell
                        selected = cells[index];
                        selected.setSelected(true);
                    }else{
                        int prev = selected.value;
                        selected.setSelected(false);
                        recordAction(selected, selected.value - prev);

                        if(selected == cells[index]){ // If clicked same cell [so deselect it]
                            selected = null;
                        }
                        else{ // If selected new cell
                            selected = cells[index];
                            selected.setSelected(true);
                        }
                        checkBoard();
                    }
                });

                // KeyEvent handling for enter, which deselects current cell
                cells[index].textField.addEventHandler(KeyEvent.KEY_PRESSED, keyEvent -> {
                    if(keyEvent.getCode() == KeyCode.ENTER){
                        int prev = cells[index].value;
                        cells[index].setSelected(false);

                        recordAction(cells[index], cells[index].value - prev);
                        checkBoard();
                    }
                });
                gameGrid.add(cells[x + y*n], x, y);
            }
        }
        // For each cage draw label
        for(Cage cage: cages){
            cage.showLabel(cells);
            cage.showWalls(cells);
        }
    }

    void setFont(int size){
        for(Cell c: cells)
            c.setFont(size);

        for(Cage c: cages)
            c.setFont(size);
    }

    void clear(){
        for(Cell c: cells){
            c.setValue(0);
        }

        actionStack.clear();
        undoStack.clear();

        mathdokuParent.setUndoButtonState(false);
        mathdokuParent.setRedoButtonState(true);
    }

    void insert(int i){
        if(selected != null) {
            recordAction(selected, i-selected.value);

            selected.setValue(i);
            checkBoard();
        }
    }

    private void recordAction(Cell changedCell, int changeValue){
        if(changeValue != 0) {
            actionStack.push(new Action(changedCell, changeValue));
            undoStack.clear();

            mathdokuParent.setUndoButtonState(true);
            mathdokuParent.setRedoButtonState(false);
        }
    }

    void undo(){
        if(!actionStack.empty()) {
            Action temp = actionStack.pop();
            undoStack.push(temp);

            mathdokuParent.setRedoButtonState(true);

            temp.cell.value -= temp.value;
            temp.cell.updateTextBoxes();
        }

        if(actionStack.empty()){
            mathdokuParent.setUndoButtonState(false);
        }
    }

    void redo() {
        if(!undoStack.empty()) {
            Action temp = undoStack.pop();
            actionStack.push(temp);

            mathdokuParent.setUndoButtonState(true);

            temp.cell.value += temp.value;
            temp.cell.updateTextBoxes();
        }

        if(undoStack.empty()){
            mathdokuParent.setRedoButtonState(false);
        }
    }
}