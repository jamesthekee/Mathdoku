package sample;

public class Action {
    Cell cell;
    int value;

    Action(Cell cell, int value){
        this.cell = cell;
        this.value = value;
    }

    @Override
    public String toString(){
        return Integer.toString(value);
    }
}
