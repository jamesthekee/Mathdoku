package sample;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;

class Cell extends StackPane {
    private Label text;
    private boolean selected;

    int value;
    TextField textField;
    boolean correct;

    Cell(){
        super();
        getStyleClass().add("game-grid-cell");
        selected = false;
        correct = true;

        if(value == 0) {
            this.text = new Label("");
        }
        else {
            this.text = new Label(String.format("%d", value));
        }

        text.getStyleClass().add("cell-label-medium");
        StackPane.setAlignment(text, Pos.CENTER);

        textField = new TextField();
        textField.setVisible(false);
        textField.setMaxWidth(48);
        textField.setPrefWidth(48);
        textField.getStyleClass().add("text-field");

        getChildren().addAll(text, textField);
    }

    void showHighlighting(){
        setHighlighting(!correct);
    }

    void setHighlighting(boolean state){
        getStyleClass().clear();
        if(state)
            getStyleClass().add("game-grid-incorrect-cell");
        else
            getStyleClass().add("game-grid-cell");
    }

    void setValue(int i){
        value = i;
        if(value == 0)
            textField.setText("");
        else
            textField.setText(Integer.toString(value));
        update();
    }

    void setSelected(Boolean state) {
        this.selected = state;
        update();
    }

    void update(){
        // Swap visibility of text and textfield

        text.setVisible(!selected);
        textField.setVisible(selected);

        // If cell is deselected
        if(!selected){
            String textFieldText = textField.getText();
            try {
                value = Integer.parseInt(textFieldText);
            } catch (NumberFormatException nfm){
                value = 0;
            }

            updateTextBoxes();
        }
    }

    void updateTextBoxes(){
        if(value <= 0) {
            value = 0;
            text.setText("");
            textField.setText("");
        }
        else {
            text.setText(Integer.toString(value));
            textField.setText(Integer.toString(value));
        }
    }

    void setFont(int size){
        text.getStyleClass().clear();
        switch(size){
            case 1:
                text.getStyleClass().add("cell-label-small");
                break;
            case 2:
                text.getStyleClass().add("cell-label-medium");
                break;
            case 3:
                text.getStyleClass().add("cell-label-large");
                break;
        }
    }
}
