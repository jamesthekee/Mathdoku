import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import java.util.Arrays;


enum Operator{
    ADD, SUBTRACT, MULTIPLY, DIVIDE, NONE
}

class Cage {
    private Operator op;
    private int value;
    private int[] cellIndexes;
    private boolean[][] walls;
    private int n;

    private Label cageLabel;

    Cage(Operator op, int value, int[] cellIndexes, int n){
        this.op = op;
        this.value = value;
        this.cellIndexes = cellIndexes;
        this.n = n;
        this.walls = new boolean[cellIndexes.length][4];
        calcWalls();
    }

    public String toString(){
        return Arrays.toString(cellIndexes);
    }

    void setIncorrect(Cell[] cells){
        for(int index: cellIndexes){
            cells[index].correct = false;
        }
    }

    boolean couldBeCorrect(Cell[] cells){
        int total = 0;

        switch(op){
            case NONE:
            case SUBTRACT:
            case DIVIDE:
                return true;

            case ADD:
                for(int i: cellIndexes)
                    total += cells[i].value;
                return total < value;

            case MULTIPLY:
                total = 1;
                for(int i: cellIndexes){
                    if (cells[i].value != 0 && value % cells[i].value != 0)
                        return false;
                    total *= cells[i].value;
                }
                return total < value;
        }
        return false;
    }

    boolean correctValues(Cell[] cells){
        int total = 0;
        int max;
        switch(op){
            case NONE:
                return cells[cellIndexes[0]].value == value;

            case ADD:
                for(int i: cellIndexes)
                    total += cells[i].value;
                return total == value;

            case MULTIPLY:
                total = 1;
                for(int i: cellIndexes)
                    total *= cells[i].value;
                return total == value;

            case SUBTRACT:
                max = 0;
                for(int i: cellIndexes) {
                    total += cells[i].value;
                    if(cells[i].value > max){
                        max = cells[i].value;
                    }
                }
                return 2*max - total == value;

            case DIVIDE:
                total = 1;
                max = 0;
                for(int i: cellIndexes){
                    if(cells[i].value == 0)
                        return false;
                    total *= cells[i].value;
                    if(cells[i].value > max){
                        max = cells[i].value;
                    }
                }
                return max*max/total == value;
        }
        return false;
    }

    Operator getOp(){
        return op;
    }

    int getValue(){
        return value;
    }

    int[] getCellIndexes(){
        return cellIndexes;
    }

    boolean isFull(Cell[] cells){
        for(int index: cellIndexes){
            if(cells[index].value == 0){
                return false;
            }
        }
        return true;
    }

    private String string(){
        String contents = Integer.toString(value);

        switch(op){
            case ADD:
                contents += "+";
                break;
            case SUBTRACT:
                contents += "-";
                break;
            case MULTIPLY:
                contents += "\u00D7";
                break;
            case DIVIDE:
                contents += '\u00F7';
                break;
        }
        return contents;
    }

    void showLabel(StackPane[] panes){
        int first = cellIndexes[0];

        cageLabel = new Label(string());
        cageLabel.getStyleClass().add("cage-label-medium");
        panes[first].getChildren().add(cageLabel);
        StackPane.setAlignment(cageLabel, Pos.TOP_LEFT);
    }

    void setFont(int size){
        cageLabel.getStyleClass().clear();
        switch(size){
            case 1:
                cageLabel.getStyleClass().add("cage-label-small");
                break;
            case 2:
                cageLabel.getStyleClass().add("cage-label-medium");
                break;
            case 3:
                cageLabel.getStyleClass().add("cage-label-large");
                break;
        }
    }

    private void calcWalls(){
        for(int a=0; a<cellIndexes.length; a++) {
            for (int b = a + 1; b < cellIndexes.length; b++) {
                // if index i, j are horizontally adjacent
                if (cellIndexes[a] % n < n - 1 && cellIndexes[a] == cellIndexes[b] - 1) {
                    walls[a][1] = true;
                    walls[b][3] = true;
                } else if (cellIndexes[a] / n < n - 1 && cellIndexes[a] == cellIndexes[b] - n) {
                    walls[a][2] = true;
                    walls[b][0] = true;
                }
            }
        }
    }

    void showWalls(Cell[] cell){
        for(int i=0; i<cellIndexes.length; i++){
            int a = walls[i][0] ? 1: 4;
            int b = walls[i][1] ? 1: 4;
            int c = walls[i][2] ? 1: 4;
            int d = walls[i][3] ? 1: 4;

            cell[cellIndexes[i]].setStyle(String.format("-fx-background-insets: 0, %d %d %d %d ;", a, b, c, d));
        }
    }
}
