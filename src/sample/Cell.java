package sample;

import java.util.ArrayList;

public class Cell {
    private int value = 0;
    private ArrayList<Integer> valuesAvailable = new ArrayList<>(9);//todo
    private boolean visible = true;

    private ArrayList<Integer> guessValues = new ArrayList<>(9);

    private final int row, column;



    public Cell(int column, int row) {
        this.row = row;
        this.column = column;

        replenishValuesAvailable();
        replenishGuessValues();
    }

    public void replenishValuesAvailable(){
        for (int i = 1; i <10 ; i++) {
            valuesAvailable.add(i);
        }
    }
    public void replenishGuessValues(){
        for (int i = 1; i <10 ; i++) {
            guessValues.add(i);
        }
    }

    public ArrayList<Integer> getValuesAvailable() {
        return valuesAvailable;
    }

    public ArrayList<Integer> getGuessValues() {
        return guessValues;
    }
    //    public void clearValuesAvailable(){
//        valuesAvailable.clear();
//    }
//
//    public void removeAvailableValue(Integer num){
//        valuesAvailable.remove(num);
//    }
//    public void removeAvailableValue(int num){
//        valuesAvailable.remove(num);
//    }



    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }
}
